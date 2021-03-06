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
package org.ilrt.mca.harvester.feeds;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.sun.syndication.feed.synd.SyndContent;
import com.sun.syndication.feed.synd.SyndContentImpl;
import com.sun.syndication.feed.synd.SyndEntry;
import com.sun.syndication.feed.synd.SyndFeed;
import com.sun.syndication.io.FeedException;
import com.sun.syndication.io.SyndFeedInput;
import com.sun.syndication.io.SyndFeedOutput;
import org.ilrt.mca.harvester.ResponseHandler;
import org.jdom.Element;
import org.joda.time.DateTime;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.Iterator;
import java.util.ArrayList;
import java.util.List;
import java.util.Date;

/**
 * @author Mike Jones (mike.a.jones@bristol.ac.uk)
 */
public class FeedResponseHandlerImpl implements ResponseHandler {

    @Override
    public Model getModel(String sourceUrl, InputStream is) {

        try {

            // get the feed and convert to rss 1.0
            SyndFeedInput synfeed = new SyndFeedInput();
            SyndFeed syndFeed = synfeed.build(new InputStreamReader(is, "UTF-8"));
            syndFeed.setFeedType("rss_1.0");
            syndFeed.setUri(sourceUrl);

            removeOldEntries(syndFeed, 180); // clear old content

            // The student union feeds GUID is not a valid URI - we check that the value of
            // getUri (which the ROME API populated with the RSS GUID) starts with "http:, if not
            // use the link as the URI.
            for (Object o : syndFeed.getEntries()) {
                SyndEntry entry = (SyndEntry) o;

                if (!entry.getUri().startsWith("http")) {
                    entry.setUri(entry.getLink());
                }

                // clear any unsupported content (via name spaces)
                List elements = clearForeignMarkup((List) entry.getForeignMarkup());
                entry.setForeignMarkup(elements);

                HtmlProcessor processor = new HtmlProcessor();

                // clean the description element
                String content = processor.process(syndFeed.getLink(), entry.getDescription().getValue());
                SyndContent e = entry.getDescription();
                e.setValue(content);

                // check additional content types (like rss:encoded which might have HTML)
                for (Object obj : entry.getContents()) {
                    if (obj instanceof SyndContentImpl) {
                        SyndContentImpl c = (SyndContentImpl) obj;
                        String clean = processor.process(syndFeed.getLink(), c.getValue());
                        c.setValue(clean);
                    }
                }

            }

            // remove foreign elements that cause icky RDF
            // TODO: create a data wrangling class for this type of stuff that can be institutional specific
            List elements = clearForeignMarkup((List) syndFeed.getForeignMarkup());
            syndFeed.setForeignMarkup(elements);


            // write the feed to a string
            StringWriter writer = new StringWriter();
            SyndFeedOutput output = new SyndFeedOutput();
            output.output(syndFeed, writer);
            String feed = writer.getBuffer().toString();

//            System.out.println(feed);

            // read into a model
            Model model = ModelFactory.createDefaultModel();
            model.read(new StringReader(feed), syndFeed.getLink());

            //model.write(System.out);

            return model;

        } catch (IOException e) {
            e.printStackTrace();
        } catch (FeedException e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    public boolean isSupportedMediaType(String mediaType) {

        return mediaType.startsWith("text/xml") || mediaType.startsWith("application/rss+xml")
                || mediaType.startsWith("application/xml")
                || mediaType.startsWith("application/atom+xml");

    }


    public List clearForeignMarkup(final List list) {

        Iterator i = list.iterator();

        while (i.hasNext()) {

            Element element = (Element) i.next();

            if (element.getNamespaceURI().equals("http://webns.net/mvcb/") ||
                    element.getNamespaceURI().equals("http://www.w3.org/2005/Atom") ||
                    element.getNamespaceURI().equals("http://rssnamespace.org/feedburner/ext/1.0") ||
                    element.getNamespaceURI().equals("http://www.itunes.com/dtds/podcast-1.0.dtd") ||
                    element.getNamespaceURI().equals("http://search.yahoo.com/mrss/")) {
                i.remove();
            }

        }
        return list;
    }

    public void removeOldEntries(SyndFeed syndFeed, int periodInDays) {

        List accepted = new ArrayList();

        for (Object o : syndFeed.getEntries()) {

            SyndEntry entry = (SyndEntry) o;

            // current date
            DateTime current = new DateTime();
            DateTime past = current.minusDays(periodInDays);

            Date pubDate = entry.getPublishedDate();
            DateTime pubDateTime = new DateTime(pubDate);

            if (pubDateTime.isAfter(past)) {
                accepted.add(entry);
            }
        }

        syndFeed.setEntries(accepted);
    }

}
