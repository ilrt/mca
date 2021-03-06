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

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import org.ilrt.mca.harvester.ResponseHandler;

import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import java.io.InputStream;
import java.io.StringReader;
import java.io.StringWriter;

/**
 * @author Mike Jones (mike.a.jones@bristol.ac.uk)
 */
public abstract class AbstractXmlResponseHandlerImpl implements ResponseHandler {

    @Override
    public abstract Model getModel(String sourceUri, InputStream is);

    @Override
    public abstract boolean isSupportedMediaType(String mediaType);

    protected Model getModelFromXml(Source xmlSource, Source xslSource, String sourceUri) {

        Model model;

        try {

            TransformerFactory transformerFactory = TransformerFactory.newInstance();

            // output for the transformer
            StringWriter writer = new StringWriter();
            javax.xml.transform.Result result =
                    new javax.xml.transform.stream.StreamResult(writer);

            // transform ...
            Transformer transformer = transformerFactory.newTransformer(xslSource);
            transformer.setParameter("uri", sourceUri);
            transformer.transform(xmlSource, result);

            // create the model
            model = ModelFactory.createDefaultModel();
            model.read(new StringReader(writer.getBuffer().toString()), sourceUri);

        } catch (TransformerConfigurationException e) {
            throw new RuntimeException(e);
        } catch (TransformerException e) {
            throw new RuntimeException(e);
        }

        return model;
    }

}

