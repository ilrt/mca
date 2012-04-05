package org.ilrt.mca.rest.providers;

import com.sun.jersey.api.view.Viewable;
import com.sun.jersey.spi.resource.Singleton;
import com.sun.jersey.spi.template.ViewProcessor;
import freemarker.cache.WebappTemplateLoader;
import freemarker.template.Configuration;
import freemarker.template.Template;
import org.apache.log4j.Logger;

import javax.servlet.ServletContext;
import javax.ws.rs.core.Context;
import javax.ws.rs.ext.Provider;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintStream;
import java.net.MalformedURLException;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * Matches a Jersey viewable to a Freemarker template. This is based on Chris Winters'
 * FreemarkerTemplateProvider. The Provider template has been deprecated, so this
 * provides a refactor version for the new ViewProcessor and includes some additional
 * bit and bobs I use in MCA. For the original implementation see
 * https://github.com/cwinters/jersey-freemarker
 *
 */
@Provider
@Singleton
public class FreeMarkerTemplateProcessor implements ViewProcessor<Template> {

    public FreeMarkerTemplateProcessor() {
    }

    // ---------- methods that implement the ViewProcessor interface

    /**
     * Resolves a Jersey viewable path (e.g. '/home') to a freemarker template.
     *
     * @param path the path specified by the Jersey viewable.
     * @return the freemarker template for the path.
     */
    @Override
    public Template resolve(String path) {

        if (log.isDebugEnabled())
            log.debug("Resolving template path: " + path);

        final String filePath = templatePath(path); // resolve viewable path to location in webapp
        final boolean isTemplateFound = isTemplateFound(filePath); // does it exist

        try {
            if (!isTemplateFound)
                log.warn("Template nor found for path" + path);

            return isTemplateFound ? freemarkerConfig.getTemplate(filePath) : null;

        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
    }

    /**
     * Write the template to the output stream.
     *
     * @param template     the Freemarker template.
     * @param viewable     the Jersey viewable.
     * @param outputStream the output stream.
     * @throws IOException when it all goes tragically wrong.
     */
    @Override
    @SuppressWarnings("unchecked")
    public void writeTo(Template template, Viewable viewable, OutputStream outputStream)
            throws IOException {

        outputStream.flush();

        Object model = viewable.getModel();
        final Map<String, Object> vars = new HashMap<String, Object>();

        if (model instanceof Map) {
            vars.putAll((Map<String, Object>) model);
        } else {
            vars.put("resource", model);
        }

        // useful for referencing static assets like images and style sheets
        vars.put("contextPath", servletContext.getContextPath());
        vars.put("domain", domain);

        // add the google analytics key
        if (googleAnalyticsKey != null && googleAnalyticsKey.length() > 0) {
            vars.put("googleAnalyticsKey", googleAnalyticsKey);
        }

        final OutputStreamWriter writer = new OutputStreamWriter(outputStream, "UTF-8");

        try {
            template.process(vars, writer);
        } catch (Throwable t) {
            onProcessException(t, template, outputStream);
        }
    }


    @Context
    public void setServletContext(ServletContext context) {

        this.servletContext = context;

        freemarkerConfig = new Configuration();

        // find the path for the templates
        rootPath = context.getInitParameter(TEMPLATE_KEY);

        if (rootPath == null || rootPath.trim().length() == 0) {
            log.debug("There is no init parameter + " + TEMPLATE_KEY + ", defaulting to "
                    + DEFAULT_PATH);
            rootPath = DEFAULT_PATH;
        }
        rootPath = rootPath.replaceAll("/$", "");

        freemarkerConfig.setTemplateLoader(new WebappTemplateLoader(context, rootPath));

        final InputStream fmProps = context.getResourceAsStream(FREEMARKER_PROPERTIES_PATH);

        if (fmProps != null) {
            try {
                freemarkerConfig.setSettings(fmProps);
                log.info("Loaded freemarker properties from " + FREEMARKER_PROPERTIES_PATH);
            } catch (Exception ex) {
                ex.printStackTrace();
                log.warn("Failed to load freemarker properties from " + FREEMARKER_PROPERTIES_PATH
                        + "; " + ex.getMessage());
            }
        }

        // find the google analytics key
        googleAnalyticsKey = context.getInitParameter("googleAnalyticsKey");
        domain = context.getInitParameter("domain");

        if (domain!= null && !domain.endsWith("/")) {
            domain += "/";
        }


    }

    protected String getDefaultExtension() {
        return ".ftl";
    }


    /**
     * @param path the location of the freemarker template
     * @return true or false that the template exits.
     */
    private boolean isTemplateFound(String path) {
        final String fullPath = rootPath + path;
        try {
            return servletContext.getResource(fullPath) != null;
        } catch (MalformedURLException ex) {
            log.warn("Caught a MalformedURLException when trying to access freemarker resource"
                    + path + ": " + ex.getMessage());
            return false;
        }
    }

    /**
     * Resolves a path specified in a Jersey viewable such as '/home' to the location of
     * a freemarker template - '/WEB-INF/templates/home.ftl. Paths with or without the
     * extension are excepted, e.g. '/home' or /'home.ftl'.
     *
     * @param path the path specified by the Jersey "Viewable"
     * @return translated path to actual freemarker template location.
     */
    private String templatePath(String path) {
        return path.endsWith(getDefaultExtension()) ? path : path + getDefaultExtension();
    }


    /**
     * Handle the error when processing the freemarker template.
     *
     * @param t        the throwable.
     * @param template the freemarker template.
     * @param out      the output stream.
     * @throws IOException errors when writing about the errors!
     */
    protected void onProcessException(final Throwable t, final Template template,
                                      final OutputStream out)
            throws IOException {
        log.error("Error processing freemarker template @ " + template.getName()
                + ": " + t.getMessage(), t);
        out.write("<pre>".getBytes());
        t.printStackTrace(new PrintStream(out));
        out.write("</pre>".getBytes());
    }


    private ServletContext servletContext;
    private static Configuration freemarkerConfig;
    private String rootPath;

    private String googleAnalyticsKey;
    private String domain;

    private final String TEMPLATE_KEY = "freemarker.template.path";
    private final String DEFAULT_PATH = "/WEB-INF/templates";
    private final String FREEMARKER_PROPERTIES_PATH = "/WEB-INF/classes/freemarker.properties";


    Logger log = Logger.getLogger(FreeMarkerTemplateProcessor.class);
}
