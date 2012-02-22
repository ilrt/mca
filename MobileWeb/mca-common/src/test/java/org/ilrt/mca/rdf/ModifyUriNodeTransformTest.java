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

import com.hp.hpl.jena.graph.Node;
import org.junit.Test;

import static org.junit.Assert.assertEquals;


/**
 * @author Mike Jones (mike.a.jones@bristol.ac.uk)
 */
public class ModifyUriNodeTransformTest {

    @Test
    public void transform() {

        String placeholder = "mca://";
        String replacement = "http:/m.bristol.ac.uk";
        String path = "foo/bar";

        // create a node with the placeholder URI
        Node s = Node.createURI(placeholder + path);

        // convert
        ModifyUriNodeTransform transform = new ModifyUriNodeTransform(placeholder, replacement);
        Node after = transform.convert(s);

        // check the transformation
        assertEquals("Unexpected uri", after.getURI(), replacement + path);

    }
}
