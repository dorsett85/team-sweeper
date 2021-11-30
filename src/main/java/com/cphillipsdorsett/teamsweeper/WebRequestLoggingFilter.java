package com.cphillipsdorsett.teamsweeper;

import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.AbstractRequestLoggingFilter;

import javax.servlet.http.HttpServletRequest;

import org.apache.logging.log4j.LogManager;

@Component
public class WebRequestLoggingFilter extends AbstractRequestLoggingFilter {
    Logger logger = LogManager.getLogger(WebRequestLoggingFilter.class);

    public WebRequestLoggingFilter() {
        setIncludeQueryString(true);
        setIncludeClientInfo(true);
        setIncludePayload(true);
        setMaxPayloadLength(10000);
        setBeforeMessagePrefix("");
        setAfterMessagePrefix("");
    }

    @Override
    protected void beforeRequest(HttpServletRequest request, String message) {
        logger.info(message);
    }

    @Override
    protected void afterRequest(HttpServletRequest request, String message) {
    }
}
