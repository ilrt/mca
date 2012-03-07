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
package org.ilrt.mca.rest.resources;

import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.rdf.model.StmtIterator;
import com.hp.hpl.jena.vocabulary.RDF;
import com.sun.jersey.api.view.Viewable;
import com.sun.jersey.spi.resource.Singleton;
import org.ilrt.mca.RdfMediaType;
import org.ilrt.mca.dao.DataDao;
import org.ilrt.mca.dao.DataDaoImpl;
import org.ilrt.mca.rdf.QueryManager;
import org.ilrt.mca.rdf.SdbManagerImpl;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.HashMap;
import java.util.Map;

/**
 * A lot of harvested data uses minted URIs that start with "/data". This JAX-RS
 * resource proves a way of viewing the data.
 *
 * @author Mike Jones (mike.a.jones@bristol.ac.uk)
 */
@Singleton
@Path("/data/{path:.*}")
public class DataResource extends AbstractResource {

    public DataResource() throws Exception {
        super();
        QueryManager queryManager = new SdbManagerImpl(manager);
        dataDao = new DataDaoImpl(queryManager);

        // TODO - I really should use dependency injection
        templates.put("http://www.geonames.org/ontology#S.PKLT", "/fragments/carparks.ftl");
    }

    /**
     * @param path the request path.
     *
     * @return an HTML representation of the requested URI.
     */
    @GET
    @Produces(MediaType.TEXT_HTML)
    public Response getGroupsAsHtml(@PathParam("path") String path, @QueryParam("type") String type) {

        Resource resource = createResource(path);

        if (resource == null || resource.getModel().size() == 0) {
            return Response.status(Response.Status.NOT_FOUND).entity(new Viewable("/404.ftl",
                    "Unable to resolve the requested path: " + path)).build();
        }

        String template = findTemplate(type, resource);
        
        return Response.status(Response.Status.OK)
                .entity(new Viewable(template, resource)).build();
    }

    /**
     * @param path the request path.
     * @return an RDF representation of the requested URI (RDF/XML or Turtle)
     */
    @GET
    @Produces({RdfMediaType.APPLICATION_RDF_XML, RdfMediaType.TEXT_RDF_N3})
    public Response getModelAsRdf(@PathParam("path") String path) {

        Resource resource = createResource(path);

        if (resource.getModel().isEmpty()) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }

        return Response.ok(resource).build();
    }

    /**
     * @param path the request path.
     * @return a JSON representation of the requested URI.
     */
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getGroupAsJson(@PathParam("path") String path) {

        Resource resource = createResource(path);

        if (resource.getModel().isEmpty()) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }

        return Response.ok(resource).build();
    }

    /**
     * @param path the path parameter.
     * @return the full URI of the requested resource.
     */
    private Resource createResource(String path) {
        return dataDao.findData(getDomain("data/") + path);
    }

    private String findTemplate(String type, Resource resource) {

        String template = null;

        System.out.println("Here!");
        
        if (type != null && type.equals("custom")) {
            if (resource.hasProperty(RDF.type)) {
                StmtIterator iter = resource.listProperties(RDF.type);
                while (iter.hasNext()) {
                    Statement statement = iter.nextStatement();
                    String temp = templates.get(statement.getResource().getURI());
                    if (temp != null) {
                        template = temp;
                    }
                }
            }
        }

        return template == null ? "/data.ftl" : template;
    }

    private DataDao dataDao;

    Map<String, String> templates = new HashMap<String, String>();
}
