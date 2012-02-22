/*
 * Copyright 2012 University of Bristol
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package org.ilrt.mca.rdf;

import com.hp.hpl.jena.graph.Graph;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.sparql.graph.GraphFactory;
import com.hp.hpl.jena.sparql.graph.NodeTransform;
import org.ilrt.mca.rdf.ModifyUriNodeTransform;
import org.ilrt.mca.rdf.ModifyUriSink;
import org.junit.Test;
import org.openjena.atlas.lib.Sink;
import org.openjena.riot.Lang;
import org.openjena.riot.RiotLoader;
import org.openjena.riot.pipeline.SinkTripleNodeTransform;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * @author Mike Jones (mike.a.jones@bristol.ac.uk)
 */
public class ModifyUriSinkTest {

    @Test
    public void modifyUri() {

        final String placeholder = "mca://";
        final String replacement = "http://after.bristol.ac.uk/";
        final String path = "data/bcc/parkingdata/carpark/22";

        final String uriBefore = placeholder + path;
        final String uriAfter = replacement + path;

        // ---------- create a model of the data without transformation

        Model before = ModelFactory.createDefaultModel();
        before.read(getClass().getResourceAsStream("/dummy-data.ttl"), null, "TTL");

        assertFalse("The model should not be empty", before.isEmpty());
        assertTrue("Expected resource", before.containsResource(before.createResource(uriBefore)));
        assertFalse("Unexpected resource", before.containsResource(before.createResource(uriAfter)));

        // ---------- create a model of the data with transformation

        Graph g = GraphFactory.createDefaultGraph();

        Sink sink = new ModifyUriSink(g);
        NodeTransform transform = new ModifyUriNodeTransform(placeholder, replacement);

        RiotLoader.readTriples(getClass().getResourceAsStream("/dummy-data.ttl"), Lang.TURTLE, null,
                new SinkTripleNodeTransform(sink, transform));

        Model after = ModelFactory.createModelForGraph(g);

        assertFalse("The model should not be empty", after.isEmpty());
        assertFalse("Unexpected resource", after.containsResource(after.createResource(uriBefore)));
        assertTrue("Expected resource", after.containsResource(after.createResource(uriAfter)));

        assertEquals("The models should be the same size", before.size(), after.size());

    }

}
