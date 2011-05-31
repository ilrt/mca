package org.ilrt.mca.harvester.xml;

import com.hp.hpl.jena.rdf.model.Model;
import org.ccil.cowan.tagsoup.Parser;
import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;

import javax.xml.transform.Source;
import javax.xml.transform.sax.SAXSource;
import javax.xml.transform.stream.StreamSource;
import java.io.InputStream;

/**
 * @author Mike Jones (mike.a.jones@bristol.ac.uk)
 */
public class XhtmlResponseHandlerImpl  extends XmlResponseHandlerImpl  {

    public XhtmlResponseHandlerImpl(String xslFilePath) {
        super(xslFilePath);
    }

    @Override
    public Model getModel(String sourceUri, InputStream is) {

        XMLReader xmlReader = new Parser(); // tag soup (clean up the HTML)
        Source xmlSource = new SAXSource(xmlReader, new InputSource(is));
        Source xslSource = new StreamSource(getClass().getResourceAsStream(xslFilePath));

        return getModelFromXml(xmlSource, xslSource, sourceUri);
    }

}
