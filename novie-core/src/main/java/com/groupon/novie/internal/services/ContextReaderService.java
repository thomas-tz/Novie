package com.groupon.novie.internal.services;

import com.google.common.base.Strings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.context.ServletContextAware;

import javax.servlet.ServletContext;
import java.util.Enumeration;

/**
 * This service allows access to different configuration
 *
 * @author thomas
 */
@Service
public class ContextReaderService implements ServletContextAware {

    private static final Logger LOG = LoggerFactory.getLogger(ContextReaderService.class);


    private static final String RESULTSIZELIMIT_PARAM_NAME = "novie.resultSizeLimit";

    private ServletContext servletContext;


    @Override
    public void setServletContext(ServletContext servletContext) {
        this.servletContext = servletContext;

        if (LOG.isTraceEnabled()) {
            LOG.trace("Servlet context Init parameters:");
            Enumeration<String> initParamNames = servletContext.getInitParameterNames();
            while (initParamNames.hasMoreElements()) {
                String paramName = initParamNames.nextElement();
                LOG.trace("Param \"{}\": [{}]", paramName, servletContext.getInitParameter(paramName));
            }
        }
    }


    /**
     * Returns the result size limit if it's defined 0 otherwise.
     */
    public long getResultSizeLimit() {
        String paramValue = servletContext.getInitParameter(RESULTSIZELIMIT_PARAM_NAME);
        if (Strings.isNullOrEmpty(paramValue)) {
            return 0;
        }
        try {
            return Long.parseLong(paramValue);
        } catch (Exception e) {
            LOG.warn("{} with value \"{}\" is not an long: {}.", RESULTSIZELIMIT_PARAM_NAME, paramValue, e);
            return 0;
        }
    }
}
