package git.klodhem.backend.security;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import git.klodhem.backend.model.User;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.ZonedDateTime;
import java.util.Date;

@Component
public class JWTUtil {
    @Value("${jwt_secret}")
    private String secret;

    @Value("${jwt.expiration.minutes}")
    private long expirationMinutes;

    private final static String ISSUER = "klodhem";
    private JWTVerifier verifier;

    @PostConstruct
    public void init() {
        Algorithm algorithm = Algorithm.HMAC256(secret);
        verifier = JWT.require(algorithm)
                .withIssuer(ISSUER)
                .build();
    }

    public String generateToken(User user) {
        Date expirationDate = Date.from(ZonedDateTime.now().plusMinutes(expirationMinutes).toInstant());

        return JWT.create()
                .withClaim("username", user.getUsername())
                .withClaim("role", user.getRole().toString())
                .withIssuedAt(new Date())
                .withIssuer(ISSUER)
                .withExpiresAt(expirationDate)
                .sign(Algorithm.HMAC256(secret));
    }

    public String validateTokenAndRetrieveClaim(String token) throws JWTVerificationException {
        DecodedJWT jwt = verifier.verify(token);
        return jwt.getClaim("username").asString();
    }
}
