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
package org.ilrt.mca.servlet;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import org.apache.log4j.Logger;
import org.ilrt.mca.rdf.ConnPoolStoreWrapperManagerImpl;
import org.ilrt.mca.rdf.DataManager;
import org.ilrt.mca.rdf.DataSourceManager;
import org.ilrt.mca.rdf.SdbManagerImpl;
import org.ilrt.mca.rdf.StoreWrapper;
import org.ilrt.mca.rdf.StoreWrapperManager;

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
 * @author Mike Jones (mike.a.jones@bristol.ac.uk)
 */
public class RegistryInitServlet extends HttpServlet {

    public RegistryInitServlet() {
    }

    @Override
    public void init(ServletConfig config) throws ServletException {


        super.init(config);

        log.info("RegistryInitServlet started.");

        // file that helps to locate data files
        dataLocation = config.getInitParameter("dataLocation");

        // database configuration
        configLocation = config.getInitParameter("configLocation");
        // find the data sources
        findDataSources();

        // load the data and save it to the RDF store
        loadData();

    }

    @Override
    public void destroy() {
        log.info("RegistryInitServlet shutdown.");
    }

    @Override
    public void doPost(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        response.sendError(HttpServletResponse.SC_FORBIDDEN);
    }

    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        response.sendError(HttpServletResponse.SC_FORBIDDEN);
    }

    private void findDataSources() throws ServletException {
        InputStream is = getClass().getResourceAsStream(dataLocation);

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
            model.read(getClass().getResourceAsStream(file), null, langType(file));
        }
        repository.add(model);

        log.info("Loading local data into named graphs");

        for (String file : dataFiles) {

            // limit the number of forward slashes at the start of the named graph
            if (file.startsWith("/")) {
                file = file.substring(1);
            }

            String graph = "mca://" + file;

            log.info("Loading ... " + file + " into graph: " + graph);
            repository.deleteAllInGraph(graph);
            Model m = ModelFactory.createDefaultModel();
            m.read(getClass().getResourceAsStream(file), null, langType(file));
            repository.add(graph, m);
            log.info("Added " + m.size() + " triples");
        }

        log.info("Registry servlet finished loading data");


    }

    private String langType(String fileName) {

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
    private final Logger log = Logger.getLogger(RegistryInitServlet.class);
}
