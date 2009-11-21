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
package org.ilrt.mca.harvester.xml;

import com.hp.hpl.jena.datatypes.xsd.XSDDatatype;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.ResIterator;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.vocabulary.DC;
import com.hp.hpl.jena.vocabulary.RDF;
import org.apache.log4j.Logger;
import org.ilrt.mca.Common;
import org.ilrt.mca.dao.AbstractDao;
import org.ilrt.mca.harvester.Harvester;
import org.ilrt.mca.harvester.Source;
import org.ilrt.mca.rdf.Repository;
import org.ilrt.mca.vocab.MCA_REGISTRY;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author Mike Jones (mike.a.jones@bristol.ac.uk)
 */
public abstract class AbstractXmlSourceHarvesterImpl extends AbstractDao implements Harvester {

    @Override
    public abstract void harvest();

    protected void saveOrUpdate(Source source, Date lastVisited, Model model) {

        if (model != null) {

            // delete the old data
            repository.deleteAllInGraph(source.getUrl());

            // add the harvested data
            repository.add(source.getUrl(), model);

            // update the last visited date
            RDFNode date = ModelFactory.createDefaultModel()
                    .createTypedLiteral(Common.parseXsdDate(lastVisited), XSDDatatype.XSDdateTime);
            repository.updatePropertyInGraph(Common.AUDIT_GRAPH_URI, source.getUrl(),
                    DC.date, date);
        } else {
            log.info("Unable to cache " + source.getUrl());
        }
    }

    protected List<XmlSource> findSources(String findSources) {

        List<XmlSource> sources = new ArrayList<XmlSource>();

        Model m = repository.find(findSources);

        if (!m.isEmpty()) {

            ResIterator iterator = m.listSubjectsWithProperty(RDF.type);

            while (iterator.hasNext()) {
                sources.add(getDetails(iterator.nextResource()));
            }
        }

        return sources;
    }

    protected XmlSource getDetails(Resource resource) {

        Date lastVisited = null;
        String xsl = null;

        String uri = resource.getURI();

        if (resource.hasProperty(MCA_REGISTRY.lastVisitedDate)) {
            try {
                lastVisited = Common.parseXsdDate(resource.getProperty(MCA_REGISTRY.lastVisitedDate)
                        .getLiteral().getLexicalForm());
            } catch (ParseException e) {
                throw new RuntimeException(e.getMessage());
            }
        }

        if (resource.hasProperty(MCA_REGISTRY.hasXslSource)) {
            xsl = resource.getProperty(MCA_REGISTRY.hasXslSource).getResource().getURI();
        }

        return new XmlSource(uri, xsl, lastVisited);
    }

    protected Repository repository = null;
    Logger log = Logger.getLogger(AbstractXmlSourceHarvesterImpl.class);
}