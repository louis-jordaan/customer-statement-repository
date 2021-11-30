package nl.jordaan.csprocessor.application.security;

import nl.jordaan.csprocessor.objectmodel.ReferenceNumberGenerator;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.ContentCachingRequestWrapper;
import org.springframework.web.util.ContentCachingResponseWrapper;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;

@Component
public class InteractionLoggingFilter extends OncePerRequestFilter {

    private static final Logger LOGGER = LoggerFactory.getLogger(InteractionLoggingFilter.class);

    @Value("${auditing.enable-web-interactions-logging}")
    private boolean enableInteractionLogging;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws ServletException, IOException {

        if (enableInteractionLogging) {
            ContentCachingRequestWrapper requestWrapper = new ContentCachingRequestWrapper(request);
            ContentCachingResponseWrapper responseWrapper = new ContentCachingResponseWrapper(response);

            String key = ReferenceNumberGenerator.generate();

            try {
                chain.doFilter(requestWrapper, responseWrapper);
            } finally {
                // body can only be read after the request handling was done
                logRequest(requestWrapper, key);
                logResponse(responseWrapper, key);
                responseWrapper.copyBodyToResponse();
            }
        } else {
            // pass through filter chain to actual request handling
            chain.doFilter(request, response);
        }
    }

    private void logRequest(ContentCachingRequestWrapper request, String logKey) throws IOException {
        StringBuilder sb = new StringBuilder(System.lineSeparator());
        sb.append(String.format("<************** %s : REQUEST (start) **************>", logKey)).append(System.lineSeparator())
                .append(String.format("URI    : %s", request.getRequestURI())).append(System.lineSeparator())
                .append(String.format("Method : %s", request.getMethod())).append(System.lineSeparator())
                .append(String.format("Headers: %s", Arrays.toString(Collections.list(request.getHeaderNames()).stream().filter(h -> !StringUtils.equalsIgnoreCase(h, "authorization")).map(h -> String.format("%s: %s", h, request.getHeader(h))).toArray()))).append(System.lineSeparator());
        byte[] content = request.getContentAsByteArray();
        if (content.length > 0) {
            String requestBody = new String(content, request.getCharacterEncoding());
            sb.append(String.format("Request body: %s", isSensitiveRequestUri(request.getRequestURI()) ? "--> Stripped from logging <--" : requestBody)).append(System.lineSeparator());
        }
        sb.append(String.format("<************** %s : REQUEST (end) **************>", logKey));
        LOGGER.info(sb.toString());
    }

    private void logResponse(ContentCachingResponseWrapper response, String logKey) throws IOException {
        StringBuilder sb = new StringBuilder(System.lineSeparator());
        sb.append(String.format("<************** %s : RESPONSE (start) **************>", logKey)).append(System.lineSeparator())
                .append(String.format("Status code       : %s", response.getStatus())).append(System.lineSeparator())
                .append(String.format("Status description: %s", HttpStatus.valueOf(response.getStatus()))).append(System.lineSeparator())
                .append(String.format("Headers           : %s", Arrays.toString(response.getHeaderNames().stream().filter(h -> !StringUtils.equalsIgnoreCase(h, "authorization")).map(h -> String.format("%s: %s", h, response.getHeader(h))).toArray()))).append(System.lineSeparator());
        byte[] content = response.getContentAsByteArray();
        if (content.length > 0) {
            sb.append(String.format("Response body: %s", StringUtils.equals(response.getContentType(), MediaType.APPLICATION_OCTET_STREAM.toString()) ? "--> Stripped from logging <--" : new String(content, response.getCharacterEncoding()))).append(System.lineSeparator());
        }
        sb.append(String.format("<************** %s : RESPONSE (end) **************>", logKey));
        LOGGER.info(sb.toString());
    }

    private boolean isSensitiveRequestUri(String uri) {
        return StringUtils.endsWithIgnoreCase(uri,"authentication/login") || uri.matches(".*(validation-jobs/)\\d(/results)+");
    }
}
