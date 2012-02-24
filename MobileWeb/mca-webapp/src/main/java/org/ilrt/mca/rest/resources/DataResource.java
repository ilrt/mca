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

/**
 * @author Mike Jones (mike.a.jones@bristol.ac.uk)
 */

import com.hp.hpl.jena.rdf.model.Resource;
import com.sun.jersey.api.view.Viewable;
import com.sun.jersey.spi.container.servlet.WebConfig;
import com.sun.jersey.spi.resource.Singleton;
import org.apache.log4j.Logger;
import org.ilrt.mca.dao.DataDao;
import org.ilrt.mca.dao.DataDaoImpl;
import org.ilrt.mca.rdf.QueryManager;
import org.ilrt.mca.rdf.SdbManagerImpl;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

/**
 * @author Mike Jones (mike.a.jones@bristol.ac.uk)
 */
@Singleton
@Path("/data/{path:.*}")
public class DataResource extends AbstractResource {

    public DataResource() throws Exception {
        super();
        QueryManager queryManager = new SdbManagerImpl(manager);
        dataDao = new DataDaoImpl(queryManager);


    }

    @GET
    @Produces(MediaType.TEXT_HTML)
    public Response getGroupsAsHtml(@PathParam("path") String path, @Context UriInfo ui) {

        Resource resource = dataDao.findData(getDomain() + path);

        if (resource == null || resource.getModel().size() == 0) {
            return Response.status(Response.Status.NOT_FOUND).entity(new Viewable("/404.ftl",
                    "Unable to resolve the requested path: " + path)).build();
        }

        return Response.status(Response.Status.OK)
                .entity(new Viewable("/data.ftl", resource)).build();
    }

    private String getDomain() {

        if (domain == null) {
            domain = config.getServletContext().getInitParameter("domain");

            if (!domain.endsWith("/")) {
                domain += "/";
            }

            domain += "data/";
        }

        return domain;
    }

    @Context
    WebConfig config;

    private DataDao dataDao;
    Logger log = Logger.getLogger(MobileCampusResource.class);
    String domain;

}
