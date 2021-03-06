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
package org.ilrt.mca.rest.resources;

import com.hp.hpl.jena.rdf.model.Resource;
import com.sun.jersey.api.view.Viewable;
import com.sun.jersey.spi.resource.Singleton;
import org.apache.log4j.Logger;
import org.ilrt.mca.Common;
import org.ilrt.mca.RdfMediaType;
import org.ilrt.mca.dao.ItemDao;
import org.ilrt.mca.dao.ItemDaoImpl;
import org.ilrt.mca.rdf.QueryManager;
import org.ilrt.mca.rdf.SdbManagerImpl;
import org.ilrt.mca.vocab.MCA_REGISTRY;

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
@Path("{path:.*}")
public class MobileCampusResource extends AbstractResource {

    public MobileCampusResource() throws Exception {
        super();
        QueryManager queryManager = new SdbManagerImpl(manager);
        itemDao = new ItemDaoImpl(queryManager);
    }


    @GET
    @Produces(MediaType.TEXT_HTML)
    public Response getGroupsAsHtml(@PathParam("path") String path, @Context UriInfo ui) {

        Resource resource = createResource(path, ui);

        if (resource == null || resource.getModel().size() == 0) {
            return Response.status(Response.Status.NOT_FOUND).entity(new Viewable("/404.ftl",
                    "Unable to resolve the requested path: " + path)).build();
        }

        String template = resolveTemplateFromResource(resource);

        return Response.ok(new Viewable(getTemplatePath(template), resource)).build();
    }


    @GET
    @Produces({RdfMediaType.APPLICATION_RDF_XML, RdfMediaType.TEXT_RDF_N3})
    public Response getModelAsRdf(@PathParam("path") String path, @Context UriInfo ui) {

        Resource resource = createResource(path, ui);

        if (resource.getModel().isEmpty()) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }

        return Response.ok(resource.getModel()).build();
    }


    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getGroupAsJson(@PathParam("path") String path, @Context UriInfo ui) {

        Resource resource = createResource(path, ui);

        if (resource.getModel().isEmpty()) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }

        return Response.ok(resource).build();
    }


    private Resource createResource(@PathParam("path") String path, @Context UriInfo ui) {

        return itemDao.findResource(getDomain("") + path, ui.getQueryParameters(), getDomain(""));
    }


    protected String resolveTemplateFromResource(Resource resource) {

        if (resource.hasProperty(MCA_REGISTRY.template)) {
            return resource.getProperty(MCA_REGISTRY.template).getResource().getURI();
        } else {
            log.warn("Unable to find a template.");
            return "template://fallback.ftl";
        }
    }

    private String getTemplatePath(String templatePath) {
        return "/" + templatePath.substring(Common.TEMPLATE_STUB.length());
    }

    private ItemDao itemDao;
    Logger log = Logger.getLogger(MobileCampusResource.class);
}
