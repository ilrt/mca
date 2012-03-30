/*
 * Copyright (c) 2010, University of Bristol
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * 1) Redistributions of source code must retain the above copyright notice,
 *    this list of conditions and the following disclaimer.
 *
 * 2) Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 *
 * 3) Neither the name of the University of Bristol nor the names of its
 *    contributors may be used to endorse or promote products derived from this
 *    software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 *
 */
package org.ilrt.mca.harvester.geo;

import com.hp.hpl.jena.datatypes.xsd.XSDDatatype;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.reasoner.rulesys.GenericRuleReasoner;
import com.hp.hpl.jena.reasoner.rulesys.Rule;
import com.hp.hpl.jena.sparql.vocabulary.FOAF;
import com.hp.hpl.jena.vocabulary.RDF;
import com.hp.hpl.jena.vocabulary.RDFS;
import com.hp.hpl.jena.vocabulary.VCARD;
import org.ilrt.mca.harvester.ResponseHandler;
import org.ilrt.mca.vocab.MCA_GEO;
import org.ilrt.mca.vocab.WGS84;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

/**
 * Converts OpenStreetMap XML data to RDF.
 *
 * We are interested in amenities and shops that have cash points (atm). The amenities
 * that we are interested in are loaded from an external configuration file. When the
 * RDF is created we use a reasoner to create additional triples that assign
 * RDF types to the different points of interest.
 *
 * @author Mike Jones (mike.a.jones@bristol.ac.uk)
 */
public class OpenStreetMapResponseHandlerImpl extends DefaultHandler implements ResponseHandler {

    /**
     * Entry point to use the harvester on the command line.
     *
     * @param args needs 2 arguments. (1) The OSM XML file (2) The RDF output file.
     * @throws Exception if there is an issue parsing the XML or creating the RDF.
     */
    public static void main(String[] args) throws Exception {

        // get the location of the files from the command line
        if (args.length != 2) {
            System.out.println("Expected arguments: [Input File] [Output File]");
            System.exit(1);
        }

        // The data and output files
        FileInputStream fis = new FileInputStream(new File(args[0]));
        FileOutputStream fos = new FileOutputStream(new File(args[1]));

        // Process the xml and convert to RDF
        OpenStreetMapResponseHandlerImpl handler = new OpenStreetMapResponseHandlerImpl();
        Model m = handler.getModel(null, fis);
        fis.close();
        m.write(fos);
        fos.close();


    }

    /**
     * Default constructor.
     */
    public OpenStreetMapResponseHandlerImpl() {
        model = ModelFactory.createDefaultModel();

        // get the osm.rules
        InputStream is = getClass().getResourceAsStream(rulesFile);
        rules = Rule.parseRules(Rule.rulesParserFromReader(
                new BufferedReader(new InputStreamReader(is))));

    }

    // ---------- ResponseHandler interface methods

    /**
     * Return a Jena Model representation of the harvested data.
     *
     * @param sourceUri the URI of the source.
     * @param is        InputStream of the harvested data source.
     * @return return a RDF Model representation of the harvested file.
     */
    @Override
    public Model getModel(String sourceUri, InputStream is) {
        try {

            // get the parser ready
            SAXParserFactory factory = SAXParserFactory.newInstance();
            SAXParser parser = factory.newSAXParser();

            // create an instance and get the items of interest
            loadItemsOfInterest();

            // parse the xml
            parser.parse(is, this);

            // create additional types
            fireRules();


        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        } catch (SAXException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return model;
    }

    /**
     * Does the harvester handle this media type
     *
     * @param mediaType the media type of the data source
     * @return true if the harvested thinks it can process it, otherwise false.
     */
    @Override
    public boolean isSupportedMediaType(String mediaType) {
        return mediaType.startsWith("text/xml") || mediaType.startsWith("application/xml");
    }


    // ---------- Override SAX handler methods

    /**
     * Receive notification of the start of an element.
     *
     * @param uri        the namespace URI or null.
     * @param localName  the local name.
     * @param qName      the qualified name.
     * @param attributes the attributes attached to the element.
     * @throws SAXException any SAX exception.
     */
    public final void startElement(final String uri, final String localName, final String qName,
                                   final Attributes attributes) throws SAXException {

        // node element
        if (qName.equals(nodeElement)) {

            isNode = true;

            // get the id, lat and lon for the node
            processId(attributes);
        }

        // tag element - should be found within a node element
        if (qName.equals(tagElement)) {

            isTag = true;

            // we are ony interested in tags within a node
            if (isNode || isWay) {

                processNodeType(attributes);

                if (isAmenity || isTourism) {
                    processType(attributes);
                }

                // we have a key attribute in the tag ...
                if (isTag) {
                    processTagElement(attributes);
                }
            }
        }

        if (qName.equals(wayElement)) {
            // get the id for the way
            processId(attributes);
            isWay = true;
        }

        if (qName.equals(ndElement)) {
            isNd = true;
            nodeRef = attributes.getValue(refAttr);
        }
    }

    /**
     * Receive notification of the end of an element.
     *
     * @param uri       the namespace URI or null.
     * @param localName the local name.
     * @param qName     the qualified name.
     * @throws SAXException any SAX exception.
     */
    public final void endElement(final String uri, final String localName, final String qName)
            throws SAXException {

        // node
        if (qName.equals(nodeElement)) {

            // keep track of the point
            nodes.put(id, new Point(lat, lon));

            // we are only interested in shops with an atm
            if (isShop && isAtm) {
                isOfInterest = true;
            }

            // place of interest (but still in use), so create RDF
            if (isOfInterest && !isDisused) {
                processItemOfInterest();
            }

            // reset the state for the node
            resetState();
        }

        // tag
        if (qName.equals(tagElement)) {
            isTag = false;
        }

        if (qName.equals(wayElement)) {
            if (isOfInterest) {
                processItemOfInterest();

            }
            isWay = false;
            nodeRef = null;
            resetState();
        }

        if (qName.equals(ndElement)) {
            isNd = false;
        }

    }

    // ---------- private methods for handling state while processing the XML

    /**
     * Get the id (used in the URI), the latitude and longitude.
     *
     * @param attributes the attributes attached to the element.
     */
    private void processId(final Attributes attributes) {

        if (attributes.getValue(idAttrVal) != null) {
            id = attributes.getValue(idAttrVal);
        }

        if (attributes.getValue(latAttrVal) != null) {
            lat = attributes.getValue(latAttrVal);
        }

        if (attributes.getValue(lonAttrVal) != null) {
            lon = attributes.getValue(lonAttrVal);
        }
    }

    /**
     * Check if we are an amenity or a shop.
     *
     * @param attributes the attributes attached to the element.
     */
    private void processNodeType(final Attributes attributes) {

        if (attributes.getValue(keyAttr) != null) {

            String type = attributes.getValue(keyAttr);

            if (type.equals(amenityAttrVal)) {
                isAmenity = true;
            }

            if (type.equals(shopAttrVal)) {
                isShop = true;
            }

            if (type.equals(tourismAttrVal)) {
                isTourism = true;
            }
        }
    }

    /**
     * Check if we are an atm. Also check if an amenity, are  we interested in it.
     *
     * @param attributes the attributes attached to the element.
     */
    private void processType(final Attributes attributes) {

        String type = attributes.getValue(valAttr);

        if (type.equals(atmAttrVal)) {
            isAtm = true;
        }

        if (itemsOfInterest.contains(type)) {
            isOfInterest = true;
            tag = type;
        }
    }

    /**
     * Process the Tag element, since the key/values might be of interest.
     *
     * @param attributes the attributes attached to the element.
     */
    private void processTagElement(final Attributes attributes) {

        if (attributes.getValue(keyAttr) != null) {

            // get the name
            if (attributes.getValue(keyAttr).equals(nameAttrVal)) {
                name = attributes.getValue(valAttr);
            }

            // the house number
            if (attributes.getValue(keyAttr).equals(houseNoAttrVal)) {
                houseNo = attributes.getValue(valAttr);
            }

            // the street
            if (attributes.getValue(keyAttr).equals(streetAttrVal)) {
                street = attributes.getValue(valAttr);
            }

            // the postcode
            if (attributes.getValue(keyAttr).equals(postCodeAttrVal)) {
                postCode = attributes.getValue(valAttr);
            }

            // phone number
            if (attributes.getValue(keyAttr).equals(phoneAttrVal)) {
                phone = attributes.getValue(valAttr);
            }

            // website
            if (attributes.getValue(keyAttr).equals(websiteAttrVal)) {
                website = attributes.getValue(valAttr);
            }

            // email
            if (attributes.getValue(keyAttr).equals(emailAttrVal)) {
                email = attributes.getValue(valAttr);
            }

            // operator
            if (attributes.getValue(keyAttr).equals(operatorAttrVal)) {
                operator = attributes.getValue(valAttr);
            }

            // does it have an atm?
            if (attributes.getValue(keyAttr).equals(atmAttrVal)) {
                if (attributes.getValue(valAttr).equals(yAttrVal)
                        || attributes.getValue(valAttr).equals(yesAttrVal)) {
                    isAtm = true;
                }
            }

            // is disused?
            if (attributes.getValue(keyAttr).equals(disusedAttrVal)) {
                if (attributes.getValue(valAttr).equals(yAttrVal)
                        || attributes.getValue(valAttr).equals(yesAttrVal)) {
                    isDisused = true;
                }
            }
        }
    }

    /**
     * A node has been marked as containing items of interest - get the state and
     * create some RDF.
     */
    private void processItemOfInterest() {

        // local uri for the point
        Resource resource = model.createResource(uriBase + id);

        // seeAlso - links to the
        if (isNode) {
            resource.addProperty(RDFS.seeAlso, model.createResource(osmNodeUriBase + id));
        } else if (isWay) {
            resource.addProperty(RDFS.seeAlso, model.createResource(osmWayUriBase + id));
        }

        // add the latitude and longitude
        if (isNode) {
            resource.addProperty(WGS84.latitude, lat, XSDDatatype.XSDdouble);
            resource.addProperty(WGS84.longitude, lon, XSDDatatype.XSDdouble);
        } else if (isWay) {
            Point point = nodes.get(nodeRef);
            resource.addProperty(WGS84.latitude, point.getLat(), XSDDatatype.XSDdouble);
            resource.addProperty(WGS84.longitude, point.getLon(), XSDDatatype.XSDdouble);
        }

        // add Point type
        resource.addProperty(RDF.type, WGS84.Point);

        // add tag
        if (tag != null) {
            resource.addProperty(MCA_GEO.hasTag, tag, XSDDatatype.XSDstring);
        }

        // a name becomes a label
        if (name != null) {
            resource.addProperty(RDFS.label, name, XSDDatatype.XSDstring);
        }

        if (houseNo != null && street != null && postCode != null) {
            StringBuilder builder = new StringBuilder(houseNo);
            builder.append(", ").append(street).append(", ").append(postCode);
            resource.addProperty(VCARD.ADR, builder.toString(), XSDDatatype.XSDstring);
        }

        if (phone != null) {
            phone = phone.replaceAll(" ", ""); // remove spaces
            if (phone.startsWith("0")) {
                phone = "+44" + phone.substring(1, phone.length());
            }
            resource.addProperty(FOAF.phone, model.createResource("tel:" + phone));
        }

        // does the node have a website?
        if (website != null) {
            if (website.startsWith("http")) {
                resource.addProperty(FOAF.homepage, model.createResource(website));
            }
        }

        // does the node have an email address?
        if (email != null) {
            resource.addProperty(FOAF.mbox, model.createResource("mailto:" + email));
        }

        // we are interested in amenities
        if (isAmenity) {
            resource.addProperty(RDF.type, MCA_GEO.Amenity);
        }

        // we are interested in shops (with an atm)
        if (isShop) {
            resource.addProperty(RDF.type, MCA_GEO.Shop);
        }

        // an atm
        if (isAtm) {
            resource.addProperty(RDF.type, MCA_GEO.BuildingWithCashPoint);
            if (name == null && operator != null) {
                resource.addProperty(RDFS.label, operator, XSDDatatype.XSDstring);
            }
        }

    }

    /**
     * Reset the state to false (boolean) or null (String).
     */
    public void resetState() {
        isNode = false;
        isTag = false;
        isWay = false;
        isNd = false;
        isAmenity = false;
        isShop = false;
        isTourism = false;
        isAtm = false;
        isOfInterest = false;
        isDisused = false;
        id = null;
        lat = null;
        lon = null;
        name = null;
        houseNo = null;
        street = null;
        postCode = null;
        phone = null;
        website = null;
        email = null;
        operator = null;
        tag = null;
    }

    // ----------- workflow things ...

    /**
     * Load the amenities we are interested from an external file.
     */
    private void loadItemsOfInterest() {

        itemsOfInterest = new ArrayList<String>();

        Scanner scanner = new Scanner(getClass().getResourceAsStream(amenitiesOfInterest));

        while (scanner.hasNextLine()) {
            itemsOfInterest.add(scanner.next().trim());
        }
    }

    /**
     * Fire the reasoner rules against the RDF we have created.
     */
    void fireRules() {

        model.add(ModelFactory.createInfModel(new GenericRuleReasoner(rules), model));
    }

    private List<String> itemsOfInterest;


    private Model model;

    private final List<Rule> rules;

    /**
     * The XML elements that we will want to investigate.
     */
    private final String nodeElement = "node";
    private final String tagElement = "tag";
    private final String wayElement = "way";
    private final String ndElement = "nd";

    /**
     * The attribute names that we will parse.
     */
    private final String keyAttr = "k";
    private final String valAttr = "v";
    private final String refAttr = "ref";

    /**
     * A lot of data is stored in 'key' or 'value' attributes. These are values that we are
     * interested in while parsing data.
     */
    private final String amenityAttrVal = "amenity";
    private final String shopAttrVal = "shop";
    private final String tourismAttrVal = "tourism";
    private final String idAttrVal = "id";
    private final String latAttrVal = "lat";
    private final String lonAttrVal = "lon";
    private final String nameAttrVal = "name";
    private final String houseNoAttrVal = "addr:housenumber";
    private final String streetAttrVal = "addr:street";
    private final String postCodeAttrVal = "addr:postcode";
    private final String phoneAttrVal = "phone";
    private final String websiteAttrVal = "website";
    private final String emailAttrVal = "email";
    private final String operatorAttrVal = "operator";
    private final String atmAttrVal = "atm";
    private final String yAttrVal = "y";
    private final String yesAttrVal = "yes";
    private final String disusedAttrVal = "disused";
    private final String refAttrVal = "val";

    /**
     * We use a generous splattering of boolean variables to keep track of state.
     * What type of tag are we in, what type of thing are we representing etc.
     */
    private boolean isNode = false;
    private boolean isTag = false;
    private boolean isWay = false;
    private boolean isNd = false;
    private boolean isAmenity = false;
    private boolean isShop = false;
    private boolean isTourism = false;
    private boolean isAtm = false;
    private boolean isOfInterest = false;
    private boolean isDisused = false;

    /**
     * Keep track of values that we are interested in. Needs to be reset after hitting a
     * closing Node or Way element.
     */
    private String id = null;
    private String lat = null;
    private String lon = null;
    private String name = null;
    private String houseNo = null;
    private String street = null;
    private String postCode = null;
    private String phone = null;
    private String website = null;
    private String email = null;
    private String operator = null;
    private String tag = null;

    private String nodeRef = null;


    final private String uriBase = "mca://data/osm/node/";
    final private String osmNodeUriBase = "http://www.openstreetmap.org/api/0.6/node/";
    final private String osmWayUriBase = "http://www.openstreetmap.org/api/0.6/way/";
    final private String amenitiesOfInterest = "/osm/items_of_interest.txt";
    final private String rulesFile = "/osm/rules/osmdata.rules";

    private Map<String, Point> nodes = new HashMap<String, Point>();

    // ---------- inner classes

    /**
     * Keep track of a point - something with a latitude and longitude. These objects are
     * created when parsing the Node element, so we can find latitude and longitudes
     * when processing Way elements (which refer back to Nodes). These are stored in-memory
     * in a Map :-(.
     */
    private class Point {

        private Point(String lat, String lon) {
            this.lat = lat;
            this.lon = lon;
        }

        public String getLat() {
            return lat;
        }

        public String getLon() {
            return lon;
        }

        private String lat;
        private String lon;
    }

}

