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
import com.hp.hpl.jena.graph.Triple;
import org.openjena.atlas.lib.Sink;

/**
 * A Sink implementation that adds Triple objects to a provided path. This object is used
 * with the org.openjena.riot.pipeline.SinkTripleNodeTransform and the
 * org.ilrt.mca.rdf.ModifyUriNodeTransform classes to modify URIs to replace
 * placeholder text.
 *
 * @author Mike Jones (mike.a.jones@bristol.ac.uk)
 */
public class ModifyUriSink implements Sink<com.hp.hpl.jena.graph.Triple> {

    /**
     * Default constructor.
     *
     * @param graph the graph to add the triples too.
     */
    public ModifyUriSink(final Graph graph) {
        this.g = graph;
    }

    /**
     * @param triple the triple to be added to the graph.
     */
    @Override
    public void send(Triple triple) {
        g.add(triple);
    }

    /**
     * Do nothing.
     */
    @Override
    public void flush() {
        // do nothing
    }

    /**
     * Do nothing.
     */
    @Override
    public void close() {
        // do nothing
    }

    private Graph g;
}
