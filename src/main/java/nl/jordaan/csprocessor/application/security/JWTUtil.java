package nl.jordaan.csprocessor.application.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;

@ConditionalOnWebApplication
@Component
public class JWTUtil {

    private static final Logger LOGGER = LoggerFactory.getLogger(JWTUtil.class);

    @Value("${auth.jwt.issuer}")
    private String issuer;

    @Value("${auth.jwt.secret}")
    private String secret;

    @Value("${auth.jwt.audience}")
    private String audience;

    @Value("${auth.jwt.ttl-seconds}")
    private long timeToLiveSeconds;

    private Key secretKey;

    @PostConstruct
    private void initSecretKey() {
        secretKey = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }

    public String generateToken(UserDetails user) {
        return Jwts.builder()
                .setId(UUID.randomUUID().toString())
                .setSubject(user.getUsername())
                .setIssuer(issuer)
                .setAudience(audience)
                .setIssuedAt(Date.from(Instant.now()))
                .setExpiration(Date.from(Instant.now().plus(Duration.ofSeconds(timeToLiveSeconds))))
                .signWith(secretKey)
                .compact();
    }

    public Claims getClaims(String token) {

        Jws<Claims> headerClaimsJwt =
                Jwts.parserBuilder()
                        .setSigningKey(secretKey)
                        .build()
                        .parseClaimsJws(token);

        return headerClaimsJwt.getBody();
    }

    public String getUsernameFromToken(String token) {
        return getClaimFromToken(token, Claims::getSubject);
    }

    public <T> T getClaimFromToken(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = getClaims(token);
        return claimsResolver.apply(claims);
    }

    public boolean validateToken(String token) {
        Claims claims = getClaims(token);
        // Validate claims
        return checkIssuer(claims)
                && checkIssuedAt(claims)
                && checkExpiry(claims);
    }

    private boolean checkIssuer(Claims claims) {
        if (StringUtils.isNotBlank(issuer) && !StringUtils.equals(claims.getIssuer(), issuer)) {
            LOGGER.debug(String.format("Token issuer is invalid. Expected = %s | Actual = %s", issuer, claims.getIssuer()));
            return false;
        }
        return true;
    }

    private boolean checkIssuedAt(Claims claims) {
        long iat = claims.getIssuedAt() == null ? 0 : claims.getIssuedAt().getTime();

        if (iat > System.currentTimeMillis()) {
            LOGGER.debug("Token issuedAt time must be in the past.");
            return false;
        }
        if (timeToLiveSeconds > 0 && (iat + (TimeUnit.of(ChronoUnit.SECONDS).toMillis(timeToLiveSeconds)) < System.currentTimeMillis())) {
            LOGGER.debug("Token has expired.");
            return false;
        }
        return true;
    }

    private boolean checkExpiry(Claims claims) {

        if (claims.getExpiration() == null) {
            LOGGER.debug("Token must have an expiry time.");
            return false;
        } else if (claims.getExpiration().before(new Date())) {
            LOGGER.debug("The token has expired.");
            return false;
        }

        return true;
    }

}
