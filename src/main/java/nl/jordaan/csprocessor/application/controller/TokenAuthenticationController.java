package nl.jordaan.csprocessor.application.controller;

import nl.jordaan.csprocessor.application.security.JWTUtil;
import nl.jordaan.csprocessor.objectmodel.ReferenceNumberGenerator;
import nl.jordaan.csprocessor.objectmodel.exception.StatementProcessingApiException;
import nl.jordaan.csprocessor.objectmodel.constant.Constants;
import nl.jordaan.csprocessor.objectmodel.dto.LoginRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@ConditionalOnWebApplication
@RestController
@RequestMapping("/rest-api/v1/authentication/")
public class TokenAuthenticationController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JWTUtil jwtUtil;

    @PostMapping(path = "/login", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> getToken(@Valid @RequestBody LoginRequest loginRequest) {
        try {
            UsernamePasswordAuthenticationToken credentials =
                    new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword());

            Authentication authentication = authenticationManager.authenticate(credentials);

            String jwtToken = jwtUtil.generateToken((UserDetails) authentication.getPrincipal());

            return ResponseEntity.ok().header(HttpHeaders.AUTHORIZATION, Constants.JWT_TOKEN_PREFIX + jwtToken).build();
        } catch (final Exception ex) {
            throw new StatementProcessingApiException(ReferenceNumberGenerator.generate(), "JWT login request failed: " + ex, ex);
        }
    }
}
