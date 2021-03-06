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
package org.ilrt.mca.rest.resources;

import com.hp.hpl.jena.query.Dataset;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.sdb.SDBFactory;
import com.hp.hpl.jena.sdb.Store;
import com.hp.hpl.jena.sdb.sql.SDBConnection;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.test.framework.WebAppDescriptor;
import org.ilrt.mca.KmlMediaType;
import org.ilrt.mca.RdfMediaType;
import org.ilrt.mca.rest.providers.JenaModelKmlProvider;
import org.ilrt.mca.rest.providers.JenaModelRdfProvider;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import javax.ws.rs.core.MediaType;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.xpath.XPathExpressionException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertTrue;

/**
 * @author Mike Jones (mike.a.jones@bristol.ac.uk)
 */
public class GeoResourceTest extends AbstractResourceTest {

    @SuppressWarnings("unchecked")
    public GeoResourceTest() {

        super(new WebAppDescriptor.Builder("org.ilrt.mca.rest")
                .initParam("sparqlEnabled", "true").build());

        List supportedClasses = new ArrayList();
        supportedClasses.add(JenaModelRdfProvider.class);
        supportedClasses.add(JenaModelKmlProvider.class);

        this.setSupportedClasses(supportedClasses);
    }

    @Before
    public void setUp() throws Exception {

        super.setUp();

        // create a connection and store
        SDBConnection conn = createConnection();
        Store store = createStore(conn);

        // add the data
        Dataset dataset = SDBFactory.connectDataset(store);

        assertTrue("We don't have data in the graph " + GRAPH_URI,
                dataset.containsNamedModel(GRAPH_URI));

        // clean up
        store.close();
        conn.close();
    }

    @Test
    public void testType() {

        webResource = resource().path("/geo/type").queryParam("uri", "http://vocab.bris.ac.uk/mca/geo#cafe");

        ClientResponse clientResponse = webResource.accept(RdfMediaType.APPLICATION_RDF_XML)
                .get(ClientResponse.class);

        Assert.assertEquals("Unexpected response from the server.", 200, clientResponse.getStatus());
    }


    @Test
    public void testInvalidType() {
        webResource = resource().path("/geo/type").queryParam("uri", "http://vocab.bris.ac.uk/mca/geo#invalid");

        ClientResponse clientResponse = webResource.accept(RdfMediaType.APPLICATION_RDF_XML)
                .get(ClientResponse.class);

        Assert.assertEquals("Unexpected response from the server.", 404, clientResponse.getStatus());
    }


    @Test
    public void testKml() throws ParserConfigurationException, TransformerException, XPathExpressionException {


        webResource = resource().path("/geo/type").queryParam("uri", "http://vocab.bris.ac.uk/mca/geo#cafe");

        ClientResponse clientResponse = webResource.accept(KmlMediaType.APPLICATION_KML)
                .get(ClientResponse.class);

        Assert.assertEquals("Unexpected response from the server.", 200, clientResponse.getStatus());
    }

    @Test
    public void testJson() {

        webResource = resource().path("/geo/type").queryParam("uri", "http://vocab.bris.ac.uk/mca/geo#cafe");

        ClientResponse clientResponse = webResource.accept(MediaType.APPLICATION_JSON_TYPE)
                .get(ClientResponse.class);

        Assert.assertEquals("Unexpected response from the server.", 200, clientResponse.getStatus());

    }

    @Test
    public void testUri() {

        webResource = resource().path("/geo/").queryParam("id", URI_ONE);

        ClientResponse clientResponse = webResource.accept(RdfMediaType.APPLICATION_RDF_XML)
                .get(ClientResponse.class);

        Assert.assertEquals("Unexpected response from the server.", 200, clientResponse.getStatus());
    }

    @Test
    public void testInvalidUri() {

        webResource = resource().path("/geo/").queryParam("id", URI_ONE + "XXXXXXXXXXXXXX");

        ClientResponse clientResponse = webResource.accept(RdfMediaType.APPLICATION_RDF_XML)
                .get(ClientResponse.class);

        Assert.assertEquals("Unexpected response from the server.", 404, clientResponse.getStatus());
    }

        @Test
    public void testKmlByUri() throws ParserConfigurationException, TransformerException, XPathExpressionException {


        webResource = resource().path("/geo/").queryParam("id", URI_ONE);

        ClientResponse clientResponse = webResource.accept(KmlMediaType.APPLICATION_KML)
                .get(ClientResponse.class);

        Assert.assertEquals("Unexpected response from the server.", 200, clientResponse.getStatus());
    }

    @Test
    public void testJsonByUri() {

        webResource = resource().path("/geo/").queryParam("id", URI_ONE);

        ClientResponse clientResponse = webResource.accept(MediaType.APPLICATION_JSON_TYPE)
                .get(ClientResponse.class);

        Assert.assertEquals("Unexpected response from the server.", 200, clientResponse.getStatus());
    }

    @Override
    protected void setUpDatabase() {

        // create a connection and store
        SDBConnection conn = createConnection();
        Store store = createStore(conn);

        // format the database
        store.getTableFormatter().format();
        store.getTableFormatter().truncate();

        // add the data
        Dataset dataset = SDBFactory.connectDataset(store);
        Model model = dataset.getNamedModel(GRAPH_URI);
        model.read(getClass().getResourceAsStream("/geodata.rdf"), null);

        // clean up
        store.close();
        conn.close();
    }

    private String GRAPH_URI = "geo://test_geo_graph1";

    private String URI_ONE = "http://www.openstreetmap.org/api/0.6/node/975730982";
}
