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

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ResIterator;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.vocabulary.DC;
import com.hp.hpl.jena.vocabulary.RDF;
import com.hp.hpl.jena.vocabulary.RDFS;
import com.sun.jersey.core.header.ContentDisposition;
import com.sun.jersey.spi.resource.Singleton;
import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONObject;
import org.ilrt.mca.KmlMediaType;
import org.ilrt.mca.RdfMediaType;
import org.ilrt.mca.dao.GeoDao;
import org.ilrt.mca.rdf.SdbManagerImpl;
import org.ilrt.mca.rest.ex.NotFoundException;
import org.ilrt.mca.vocab.MCA_GEO;
import org.ilrt.mca.vocab.MCA_REGISTRY;
import org.ilrt.mca.vocab.WGS84;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Mike Jones (mike.a.jones@bristol.ac.uk)
 */
@Singleton
@Path("/geo/")
public class GeoResource extends AbstractResource {

    // ---------- Constructors

    public GeoResource() throws IOException {
        super();
        geoDao = new GeoDao(new SdbManagerImpl(manager));
    }

    // ---------- Find resources by type

    @GET
    @Produces({RdfMediaType.APPLICATION_RDF_XML, RdfMediaType.TEXT_RDF_N3})
    @Path("type")
    public Response placesAsRdf(@QueryParam("uri") String type) {

        return Response.ok(createModelByType(type)).build();
    }


    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("type")
    public Response placesAsJson(@QueryParam("uri") String type) {

        return Response.ok(jsonRepresentationOfModel(createModelByType(type)))
                .type(MediaType.APPLICATION_JSON_TYPE).build();
    }

    @GET
    @Produces({MediaType.WILDCARD, KmlMediaType.APPLICATION_KML})
    @Path("type")
    public Response placesAsKml(@QueryParam("uri") String type) {

        ContentDisposition cd = ContentDisposition.type("file").fileName(type + ".kml").build();

        return Response.ok(createModelByType(type))
                .type(KmlMediaType.APPLICATION_KML_TYPE).header("Content-Disposition", cd).build();
    }

    // ---------- Find resources by id

    @GET
    @Produces({RdfMediaType.APPLICATION_RDF_XML, RdfMediaType.TEXT_RDF_N3})
    public Response pointByUri(@QueryParam("id") String id) {

        return Response.ok(createModelByUri(id)).build();
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response pointByUriAsJson(@QueryParam("id") String id) {

        return Response.ok(jsonRepresentationOfModel(createModelByUri(id)))
                .type(MediaType.APPLICATION_JSON_TYPE).build();
    }

    @GET
    @Produces({MediaType.WILDCARD, KmlMediaType.APPLICATION_KML})
    public Response pointByUriAsKml(@QueryParam("id") String id) {

        ContentDisposition kmlCd = ContentDisposition.type("file").fileName(id + ".kml").build();

        return Response.ok(createModelByUri(id))
                .type(KmlMediaType.APPLICATION_KML_TYPE)
                .header("Content-Disposition", kmlCd).build();
    }

    // ---------- Private helper methods

    private Model createModelByType(String type) {

        Model m = geoDao.findGeoPointByType(type);

        m.write(System.out);

        if (m == null || m.size() == 0)
            throw new NotFoundException("Unable to find the requested resource");

        return m;
    }

    private Model createModelByUri(String id) {

        Model m = geoDao.findGeoPointByUri(id);

        if (m == null || m.size() == 0)
            throw new NotFoundException("Unable to find the requested resource");

        m.write(System.out);

        return m;
    }

    private JSONObject jsonRepresentationOfModel(Model m) {

        ResIterator iter = m.listResourcesWithProperty(RDF.type, WGS84.Point);

        JSONArray jsonArray = new JSONArray();

        while (iter.hasNext()) {

            Resource resource = iter.nextResource();

            Map<String, String> map = new HashMap<String, String>();

            map.put("id", resource.getURI());

            if (resource.hasProperty(WGS84.longitude))
                map.put("lng", resource.getProperty(WGS84.longitude).getLiteral().getLexicalForm());

            if (resource.hasProperty(WGS84.latitude))
                map.put("lat", resource.getProperty(WGS84.latitude).getLiteral().getLexicalForm());

            if (resource.hasProperty(RDFS.label)) {
                map.put("label", resource.getProperty(RDFS.label).getLiteral().getLexicalForm());
            }

            if (resource.hasProperty(DC.description)) {
                map.put("description", resource.getProperty(DC.description).getLiteral()
                        .getLexicalForm());
            }

            if (resource.hasProperty(MCA_REGISTRY.icon)) {
                map.put("icon", resource.getProperty(MCA_REGISTRY.icon).getLiteral()
                        .getLexicalForm());
            }

            jsonArray.put(map);
        }

        Map<String, JSONArray> p = new HashMap<String, JSONArray>();
        p.put("markers", jsonArray);

        return new JSONObject(p);
    }


    private GeoDao geoDao;
}
