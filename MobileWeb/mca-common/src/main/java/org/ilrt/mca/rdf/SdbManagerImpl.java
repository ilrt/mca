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
package org.ilrt.mca.rdf;

import com.hp.hpl.jena.query.Dataset;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QuerySolutionMap;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.ResourceFactory;
import com.hp.hpl.jena.sdb.SDBFactory;

/**
 * @author Mike Jones (mike.a.jones@bristol.ac.uk)
 */
public class SdbManagerImpl implements DataManager {

    public SdbManagerImpl(final StoreWrapperManager manager) {
        this.manager = manager;
    }

    @Override
    public Model find(String sparql) {
        return find(null, sparql);
    }

    @Override
    public Model find(String bindingId, String id, String sparql) {

        QuerySolutionMap bindings = new QuerySolutionMap();
        bindings.add(bindingId, ResourceFactory.createResource(id));
        return find(bindings, sparql);
    }

    @Override
    public Model find(QuerySolutionMap bindings, String sparql) {

        StoreWrapper storeWrapper = null;
        Dataset dataset = null;
        Model results = null;
        QueryExecution qe = null;

        try {

            // get a store
            storeWrapper = manager.getStoreWrapper();

            // dataset to query
            dataset = SDBFactory.connectDataset(storeWrapper.getStore());

            if (bindings != null) {
                qe = QueryExecutionFactory.create(sparql, dataset, bindings);
            } else {
                qe = QueryExecutionFactory.create(sparql, dataset);
            }

            results = qe.execConstruct();

        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            // clean up and return
            if (qe != null) {
                qe.close();
            }
            if (dataset != null) {
                dataset.close();
            }
            if (storeWrapper != null) {
                storeWrapper.close();
            }

        }

        return results;
    }

    @Override
    public void add(Model model) {

        StoreWrapper storeWrapper = null;
        Model sdbModel = null;

        try {
            storeWrapper = manager.getStoreWrapper();
            sdbModel = SDBFactory.connectDefaultModel(storeWrapper.getStore());
            sdbModel.add(model);

        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            cleanUp(sdbModel, storeWrapper);
        }
    }

    @Override
    public void add(String graphUri, Model model) {

        StoreWrapper storeWrapper = null;
        Model sdbModel = null;

        try {
            storeWrapper = manager.getStoreWrapper();
            sdbModel = SDBFactory.connectNamedModel(storeWrapper.getStore(), graphUri);
            sdbModel.add(model);
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            cleanUp(sdbModel, storeWrapper);
        }
    }

    @Override
    public void delete(String graphUri, Model model) {

        StoreWrapper storeWrapper = null;
        Model sdbModel = null;

        try {
            storeWrapper = manager.getStoreWrapper();
            sdbModel = SDBFactory.connectNamedModel(storeWrapper.getStore(), graphUri);
            sdbModel.remove(model);
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            cleanUp(sdbModel, storeWrapper);
        }
    }

    @Override
    public void delete(Model model) {

        StoreWrapper storeWrapper = null;
        Model sdbModel = null;

        try {
            storeWrapper = manager.getStoreWrapper();
            sdbModel = SDBFactory.connectDefaultModel(storeWrapper.getStore());
            sdbModel.remove(model);
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            cleanUp(sdbModel, storeWrapper);
        }
    }

    @Override
    public void updatePropertyInGraph(String graphUri, String uri, Property property,
                                      RDFNode value) {

        StoreWrapper storeWrapper = null;
        Model model = null;

        try {
            storeWrapper = manager.getStoreWrapper();
            model = SDBFactory.connectNamedModel(storeWrapper.getStore(), graphUri);
            Resource resource = model.getResource(uri);

            if (resource.hasProperty(property)) {
                resource.getProperty(property).changeObject(value);
            } else {
                resource.addProperty(property, value);
            }

        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            cleanUp(model, storeWrapper);
        }
    }

    @Override
    public void deleteAllInGraph(String graphUri) {

        StoreWrapper storeWrapper = null;
        Model model = null;

        try {

            storeWrapper = manager.getStoreWrapper();

            if (graphUri != null) {
                model = SDBFactory.connectNamedModel(storeWrapper.getStore(), graphUri);
            } else {
                model = SDBFactory.connectDefaultModel(storeWrapper.getStore());
            }

            model.removeAll();

        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            cleanUp(model, storeWrapper);
        }
    }

    private void cleanUp(Model m, StoreWrapper wrapper) {

        if (m != null) {
            m.close();
        }

        if (wrapper != null) {
            wrapper.close();
        }
    }

    private final StoreWrapperManager manager;
}
