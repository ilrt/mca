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
package org.ilrt.mca.dao;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.Resource;
import org.apache.log4j.Logger;
import org.ilrt.mca.rdf.QueryManager;

/**
 * @author Mike Jones (mike.a.jones@bristol.ac.uk)
 */
public class DataDaoImpl extends AbstractDao implements DataDao {

    public DataDaoImpl(final QueryManager queryManager) throws Exception {
        this.queryManager = queryManager;
        findDataSparql = loadSparql("/sparql/findData.rql");
    }

    @Override
    public Resource findData(String id) {

        System.out.println("Looking for " + id);

        Model model = queryManager.find("s", id, findDataSparql);
        return model.isEmpty() ? null : model.getResource(id);
    }

    private String findDataSparql = null;
    private QueryManager queryManager;

    Logger log = Logger.getLogger(DataDaoImpl.class);
}
