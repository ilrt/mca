/*
 * Copyright (c) 2009, University of Bristol
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
package org.ilrt.mca.dao.delegate;

import com.hp.hpl.jena.datatypes.xsd.XSDDatatype;
import com.hp.hpl.jena.query.QuerySolutionMap;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.ResourceFactory;
import com.hp.hpl.jena.vocabulary.RDF;
import org.apache.log4j.Logger;
import org.ilrt.mca.dao.AbstractDao;
import org.ilrt.mca.rdf.QueryManager;
import org.ilrt.mca.vocab.MCA_REGISTRY;
import org.ilrt.mca.vocab.WGS84;

import javax.ws.rs.core.MultivaluedMap;
import java.io.IOException;
import java.util.Properties;

/**
 * @author Mike Jones (mike.a.jones@bristol.ac.uk)
 * @author Jasper Tredgold (jasper.tredgold@bristol.ac.uk)
 */
public class ActiveMapDelegateImpl extends AbstractDao implements Delegate {

    public ActiveMapDelegateImpl(final QueryManager queryManager) {
        this.queryManager = queryManager;
        try {
            activeMapDetailsSparql = loadSparql("/sparql/findActiveMapDetails.rql");
            activeMapGroupSparql = loadSparql("/sparql/findActiveMapGroup.rql");
            activeMapDetailsByGraphSparql = loadSparql("/sparql/findActiveMapDetailsInGraph.rql");

            Properties props = new Properties();
            props.load(this.getClass().getResourceAsStream("/map.properties"));
            defaultLat = props.getProperty("defaultLat");
            defaultLng = props.getProperty("defaultLng");
            defaultMapZoom = props.getProperty("defaultMapZoom");

        } catch (IOException ex) {
            Logger log = Logger.getLogger(ActiveMapDelegateImpl.class);
            log.error("Unable to load SPARQL query: " + ex.getMessage());
            throw new RuntimeException(ex);
        }
    }

    @Override
    public Resource createResource(Resource resource, MultivaluedMap<String, String> parameters) {

        // parameter means we are only interested in a single resource
        if (parameters.containsKey("item")) {

            String item = parameters.getFirst("item");
            Resource itemUri = ResourceFactory.createResource(item);

            Model m = createResourceForSingleMapItem(itemUri);

            Resource r = m.getResource(item);

            // provides a url for the ajax request
            m.add(r, MCA_REGISTRY.markers, "geo/?id=" + item);
            m.add(r, WGS84.latitude, defaultLat, XSDDatatype.XSDdouble);
            m.add(r, WGS84.longitude, defaultLng, XSDDatatype.XSDdouble);
            m.add(r, MCA_REGISTRY.mapZoom, defaultMapZoom, XSDDatatype.XSDinteger);

            return r;
        }


        Model model;

        if (resource.getProperty(RDF.type).getResource().getURI()
                .equals(MCA_REGISTRY.ActiveMapGroup.getURI())) {
            model = createMapNavigationList(resource);
        } else {
            model = queryManager.find("id", resource.getURI(), activeMapDetailsSparql);
        }

        resource.getModel().add(model);

        return resource;
    }

    /**
     * Creates a list of geo items that can be used to create a navigation list. For example,
     * an A-Z of University buildings.
     *
     * @param resource represents the URL for the navigation list.
     * @return a model that represents the navigation list.
     */
    private Model createMapNavigationList(Resource resource) {

        QuerySolutionMap bindings = new QuerySolutionMap();
        bindings.add("id", resource);
        bindings.add("type", resource.getProperty(MCA_REGISTRY.groupType).getResource());

        return queryManager.find(bindings, activeMapGroupSparql);
    }

    /**
     * Creates a model so that a single point can be displayed on a map. For example,
     * display the location of an individual University building.
     *
     * @param item represents the item of interest.
     * @return a model that represents the item so it can be displayed on a map.
     */
    private Model createResourceForSingleMapItem(Resource item) {

        QuerySolutionMap bindings = new QuerySolutionMap();
        bindings.add("id", item);
        return queryManager.find(bindings, activeMapDetailsByGraphSparql);
    }

    private String activeMapDetailsSparql = null;
    private String activeMapDetailsByGraphSparql = null;
    private String activeMapGroupSparql = null;

    private String defaultLat;
    private String defaultLng;
    private String defaultMapZoom;

    private final QueryManager queryManager;
}
