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
import com.hp.hpl.jena.sparql.graph.NodeTransform;

/**
 * Inspects a URI to see of there is a placeholder that needs replacing. We don't know
 * what URL the system will be deployed at, so this allows us to use placeholders in the
 * RDF until startup or data harvest time.
 *
 * @author Mike Jones (mike.a.jones@bristol.ac.uk)
 */
public class ModifyUriNodeTransform implements NodeTransform {

    /**
     * Default and only constructor.
     *
     * @param mPlaceholder the placeholder string.
     * @param mReplacement the text we want to replace the placeholder with.
     */
    public ModifyUriNodeTransform(String mPlaceholder, String mReplacement) {
        this.placeholder = mPlaceholder;
        this.replacement = mReplacement;
    }

    /**
     * @param node the node that needs to be inspected for placeholders. We are only
     *             interested in URIs.
     * @return a node that might have been modified.
     */
    @Override
    public Node convert(Node node) {
        if (node.isURI() && node.getURI().startsWith(placeholder)) {
            return Node.createURI(node.getURI().replace(placeholder, replacement));
        }
        return node;
    }

    private String placeholder;
    private String replacement;
}
