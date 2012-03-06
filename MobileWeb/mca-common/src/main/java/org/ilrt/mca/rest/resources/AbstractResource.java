package org.ilrt.mca.rest.resources;

import com.sun.jersey.spi.container.servlet.WebConfig;
import org.ilrt.mca.rdf.ConnPoolStoreWrapperManagerImpl;
import org.ilrt.mca.rdf.DataSourceManager;
import org.ilrt.mca.rdf.StoreWrapperManager;

import javax.ws.rs.core.Context;

public abstract class AbstractResource {

    public AbstractResource() {
        DataSourceManager dataSourceManager = new DataSourceManager();
        manager = new ConnPoolStoreWrapperManagerImpl("/sdb.ttl",
                dataSourceManager.getDataSource());
    }

    /**
     * Obtains the domain name part of a URI that the application is deployed too, e.g.
     * http://m.bristol.ac.uk. This is specified in the web.xml in a context parameter.
     * If null, return the internal "mca://: scheme.
     *
     * @return return the domain name specified in the web.xml or "mca://".
     */
    protected String getDomain(String path) {

        if (domain == null) {
            domain = config.getServletContext().getInitParameter("domain");

            if (domain != null) {
                if (!domain.endsWith("/")) {
                    domain += "/";
                }
                domain += path;
            } else {
                domain = "mca://";
            }
        }

        return domain;
    }
    
    private String domain;
    
    protected StoreWrapperManager manager;

    @Context
    private
    WebConfig config;
}
