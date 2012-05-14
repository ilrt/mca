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
package org.ilrt.mca.dao.delegate;

import com.hp.hpl.jena.query.QuerySolutionMap;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.ResourceFactory;
import org.ilrt.mca.dao.AbstractDao;
import org.ilrt.mca.rdf.QueryManager;
import org.ilrt.mca.vocab.MCA_REGISTRY;

import javax.ws.rs.core.MultivaluedMap;
import java.io.IOException;

/**
 * @author Mike Jones (mike.a.jones@bristol.ac.uk)
 */
public class DynamicNavigationDelegateImpl extends AbstractDao implements Delegate {

    public DynamicNavigationDelegateImpl(QueryManager queryManager, String domain) {
        try {
            this.queryManager = queryManager;
            findTypeSparqlQuery = loadSparql("/sparql/findByType.rql");
            findDataSparqlQuery = loadSparql("/sparql/findData.rql");
            this.domain = domain;
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }

    @Override
    public Resource createResource(Resource resource, MultivaluedMap<String, String> parameters) {

        // bind the values
        QuerySolutionMap bindings = new QuerySolutionMap();

        // id, so we are drilling down to an item
        if (parameters.containsKey("id")) {

            // query uri
            Resource r = ResourceFactory.createResource(domain + parameters.getFirst("id"));
            bindings.add("s", r);


            Model m = queryManager.find(bindings, findDataSparqlQuery);
            m.add(m.createStatement(resource, MCA_REGISTRY.hasItem, r));
            m.add(m.createStatement(r, MCA_REGISTRY.hasParent, resource));
            m.add(m.createStatement(r, MCA_REGISTRY.template,
                    ResourceFactory.createResource("template://detailView.ftl")));
            m.add(m.createStatement(r, MCA_REGISTRY.shortLabel, "Details"));

            if (resource.hasProperty(MCA_REGISTRY.style)) {
                m.add(m.createStatement(r, MCA_REGISTRY.style,
                        resource.getProperty(MCA_REGISTRY.style).getLiteral()));
            }

            resource.getModel().add(m);

            return resource.getModel().getResource(domain + parameters.getFirst("id"));

        } else {

            // need a type to bind with ...
            Resource type = resource.getProperty(MCA_REGISTRY.hasType).getResource();

            bindings.add("id", resource);
            bindings.add("type", type);

            if (resource.hasProperty(MCA_REGISTRY.style)) {
                bindings.add("style", resource.getProperty(MCA_REGISTRY.style).getLiteral());
            }

            // return the data
            Model model = queryManager.find(bindings, findTypeSparqlQuery);
            resource.getModel().add(model);
        }

        resource.getModel().write(System.out);

        return resource;
    }

    private final QueryManager queryManager;
    private String findTypeSparqlQuery;
    private String findDataSparqlQuery;
    private String domain;
}
