package org.ilrt.mca.dao;

import com.hp.hpl.jena.rdf.model.Resource;

/**
 * @author Mike Jones (mike.a.jones@bristol.ac.uk)
 */
public interface DataDao {

    Resource findData(String id);
}
