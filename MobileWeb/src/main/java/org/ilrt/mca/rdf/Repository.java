package org.ilrt.mca.rdf;

import com.hp.hpl.jena.query.QuerySolutionMap;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.RDFNode;

/**
 * @author Mike Jones (mike.a.jones@bristol.ac.uk)
 */
public interface Repository {

    Model find(String sparql);

    Model find(String bindingId, String id, String sparql);

    Model find(QuerySolutionMap bindings, String sparql);

    void add(Model model);

    void add(String graphUri, Model model);

    void delete(Model model);

    void delete(String graphUri, Model model);

    void updatePropertyInGraph(String graphUri, String uri,
                               Property property, RDFNode object);
}