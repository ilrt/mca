/*
 * Copyright (c) 2009, 2010, 2011, 2012 University of Bristol
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
package org.ilrt.mca.servlet;

import com.hp.hpl.jena.graph.Graph;
import com.hp.hpl.jena.graph.Triple;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.sparql.graph.GraphFactory;
import com.hp.hpl.jena.sparql.graph.NodeTransform;
import org.apache.log4j.Logger;
import org.ilrt.mca.rdf.ConnPoolStoreWrapperManagerImpl;
import org.ilrt.mca.rdf.DataManager;
import org.ilrt.mca.rdf.DataSourceManager;
import org.ilrt.mca.rdf.ModifyUriNodeTransform;
import org.ilrt.mca.rdf.ModifyUriSink;
import org.ilrt.mca.rdf.SdbManagerImpl;
import org.ilrt.mca.rdf.StoreWrapper;
import org.ilrt.mca.rdf.StoreWrapperManager;
import org.openjena.atlas.lib.Sink;
import org.openjena.riot.Lang;
import org.openjena.riot.RiotLoader;
import org.openjena.riot.pipeline.SinkTripleNodeTransform;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

/**
 * Loads the registry and data into the database on the application startup.
 *
 * @author Mike Jones (mike.a.jones@bristol.ac.uk)
 */
public class RegistryInitServlet extends HttpServlet {

    /**
     * Default constructor.
     */
    public RegistryInitServlet() {
    }

    //---------- PUBLIC SERVLET-RELATED METHODS

    /**
     * We do things on initialisation:
     *
     * (1) Load the registry data which defines pages and navigation.
     * (2) Load the application data that is not harvested.
     *
     * The URIs are modified on startup so that a place holder (mca://) is replaced in the RDF
     * with the domain name of the application. For example, mca://foo/bar is replaced with
     * http://m.bristol.ac.uk/foo/bar.
     *
     * @param config the servlet configuration.
     * @throws ServletException if we cannot initialize the servlet.
     */
    @Override
    public void init(ServletConfig config) throws ServletException {

        log.info("RegistryInitServlet started.");

        super.init(config);

        // file that helps to locate data files
        dataLocation = config.getInitParameter("dataLocation");

        // database configuration
        configLocation = config.getInitParameter("configLocation");

        // prefix used in the RDF data (default is mca://)
        prefix = config.getInitParameter("prefix");

        if (prefix == null) {
            prefix = "mca://";
        }

        // domain for the application
        domain = config.getInitParameter("domain");

        if (domain != null && !domain.endsWith("/")) {
            domain = domain + "/";
        }

        // find the data sources
        findDataSources();

        // load the data and save it to the RDF store
        loadData();
    }

    /**
     * Destroy the registry initialization servlet.
     */
    @Override
    public void destroy() {
        super.destroy();
        log.info("RegistryInitServlet shutdown.");
    }

    /**
     * We don't accept POST requests. Return a Forbidden message.
     *
     * @param request  the request object.
     * @param response the response object.
     * @throws IOException if something goes wrong.
     */
    @Override
    public void doPost(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        response.sendError(HttpServletResponse.SC_FORBIDDEN);
    }

    /**
     * We don't accept GET requests. Return a Forbidden message.
     *
     * @param request  the request object.
     * @param response the response object.
     * @throws IOException if something goes wrong.
     */
    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        response.sendError(HttpServletResponse.SC_FORBIDDEN);
    }

    //---------- PRIVATE HELPER METHODS

    /**
     * A list of available data files is found in the automatically generated
     * WEB-INF/classes/data-manifest.txt file. The locations are prefixed
     * with "registry:" (app configuration) or "graph:" (app data).
     *
     * @throws ServletException if there is a problem reading the manifest file.
     */
    private void findDataSources() throws ServletException {

        // get a stream for data manifest file.
        InputStream is = this.getClass().getResourceAsStream(dataLocation);

        // populate arrays of registry and manifest files
        if (is != null) {
            try {
                BufferedReader reader = new BufferedReader(new InputStreamReader(is));

                String line;

                while ((line = reader.readLine()) != null) {
                    if (!line.startsWith("#")) {
                        String[] temp = line.split(":");
                        if (temp[0].equals("registry")) {
                            registryFiles.add(temp[1]);
                        } else {
                            dataFiles.add(temp[1]);
                        }
                    }
                }

            } catch (IOException e) {
                throw new ServletException("Unable to process " + dataLocation + ": "
                        + e.getMessage());
            }
        } else {
            throw new ServletException("Unable to locate configuration file with location of "
                    + "registry or data files.");
        }
    }

    /**
     * Load the registry files into the default graph and the data files into named graphs.
     */
    private void loadData() {

        // create the dataManager
        DataSourceManager dataSourceManager = new DataSourceManager();
        StoreWrapperManager manager =
                new ConnPoolStoreWrapperManagerImpl(configLocation,
                        dataSourceManager.getDataSource());

        // log the database being used
        StoreWrapper wrapper = manager.getStoreWrapper();
        log.info("Database used: " + wrapper.getStore().getDatabaseType().getName());
        wrapper.close();

        DataManager repository = new SdbManagerImpl(manager);

        // clear existing registry
        log.info("Clearing existing registry details");
        repository.deleteAllInGraph(null);

        // load configuration files
        log.info("Loading registry details");
        Model model = ModelFactory.createDefaultModel();

        // load the registry and add it to the database

        for (String file : registryFiles) {
            log.info("Loading ... " + file);
            Model m = domain != null ? createTransformModelFromFile(file) : createModelFromFile(file);
            model.add(m);
        }
        repository.add(model);

        log.info("Loading local data into named graphs");

        for (String df : dataFiles) {

            String graph = "mca://";

            // limit the number of forward slashes at the start of the named graph
            graph += df.startsWith("/") ? df.substring(1) : df;

            log.info("Loading ... " + df + " into graph: " + graph);
            repository.deleteAllInGraph(graph);

            Model m = domain != null ? createTransformModelFromFile(df) : createModelFromFile(df);

            //m.write(System.out);

            if (m != null) {
                repository.add(graph, m);
                log.info("Added " + m.size() + " triples");
            }
        }

        log.info("Registry servlet finished loading data");


    }

    /**
     * Creates a model from the file without modification.
     *
     * @param file the data file.
     * @return a Jena Model representation of the file.
     */
    private Model createModelFromFile(String file) {
        Model m = ModelFactory.createDefaultModel();
        m.read(getClass().getResourceAsStream(file), null, langTypeAsString(file));
        return m;
    }

    /**
     * Creates a model from the file but the URIs might be modified to remove place holders.
     *
     * @param file the data file.
     * @return a Jena Model representation of the file, but the URIs might be modified.
     */
    private Model createTransformModelFromFile(String file) {

        // setup graph and sink to replace placeholder with domain
        Graph g = GraphFactory.createDefaultGraph();
        Sink<Triple> sink = new ModifyUriSink(g);
        NodeTransform transform = new ModifyUriNodeTransform(prefix, domain);

        // transform with the riot loader
        RiotLoader.readTriples(getClass().getResourceAsStream(file), langType(file), null,
                new SinkTripleNodeTransform(sink, transform));

        return ModelFactory.createModelForGraph(g);
    }

    /**
     * Determine the RDF format by file extension.
     *
     * @param fileName the RDF file.
     * @return the RDF type.
     */
    private Lang langType(String fileName) {

        if (fileName.endsWith("ttl") || fileName.endsWith("TTL")) {
            return Lang.TURTLE;
        } else if (fileName.endsWith("n3") || fileName.endsWith("N3")) {
            return Lang.NTRIPLES;
        } else {
            return Lang.RDFXML;
        }

    }

    /**
     * Determine the RDF format by file extension.
     * @param fileName the RDF file.
     * @return the RDF type.
     */
    private String langTypeAsString(String fileName) {

        if (fileName.endsWith("ttl") || fileName.endsWith("TTL")) {
            return "TTL";
        } else if (fileName.endsWith("n3") || fileName.endsWith("N3")) {
            return "N3";
        } else {
            return null;
        }
    }


    // hold the location of files for processing
    private final List<String> registryFiles = new ArrayList<String>();
    private final List<String> dataFiles = new ArrayList<String>();

    private String dataLocation = null;
    private String configLocation = null;
    private String prefix = null;
    private String domain = null;
    private final Logger log = Logger.getLogger(RegistryInitServlet.class);
}
