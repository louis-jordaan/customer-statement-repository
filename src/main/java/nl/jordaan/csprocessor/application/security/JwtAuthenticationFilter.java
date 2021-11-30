package nl.jordaan.csprocessor.application.security;

import com.google.common.collect.Lists;
import nl.jordaan.csprocessor.objectmodel.constant.Constants;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private static final Logger LOGGER = LoggerFactory.getLogger(JwtAuthenticationFilter.class);

    @Autowired
    private JWTUtil jwtUtil;

    @Override
    protected void doFilterInternal(final HttpServletRequest request, final HttpServletResponse response, final FilterChain chain) throws ServletException, IOException {
        // Get the authentication header. Tokens are supposed to be passed in the authentication header.
        String requestTokenHeader = request.getHeader(Constants.JWT_TOKEN_HEADER);

        // JWT token is in the form "Bearer token". Remove Bearer word and get only the Token.
        if (StringUtils.startsWith(requestTokenHeader, Constants.JWT_TOKEN_PREFIX)) {
            // Extract the token
            String jwtToken = requestTokenHeader.replace(Constants.JWT_TOKEN_PREFIX, "");
            try {
                // Validate the token.
                if (jwtUtil.validateToken(jwtToken)) {
                    String username = jwtUtil.getUsernameFromToken(jwtToken);
                    if (StringUtils.isNotBlank(username) && SecurityContextHolder.getContext().getAuthentication() == null) {
                        // If token is valid configure Spring Security to manually set authentication
                        List<String> authorities = Lists.newArrayList(Constants.DEFAULT_JWT_AUTHORITY);

                        UsernamePasswordAuthenticationToken authenticationToken =
                                new UsernamePasswordAuthenticationToken(username, null, authorities.stream().map(SimpleGrantedAuthority::new).collect(Collectors.toList()));

                        authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                        // After setting the Authentication in the context, we specify that the current user is authenticated.
                        SecurityContextHolder.getContext().setAuthentication(authenticationToken);
                    }
                } else {
                    LOGGER.debug("JWT token is not valid.");
                    SecurityContextHolder.clearContext();
                }
            } catch (Exception e) {
                LOGGER.debug("An exception occurred while processing JWT token.", e);
                SecurityContextHolder.clearContext();
            }
        }

        chain.doFilter(request, response);
    }
}
