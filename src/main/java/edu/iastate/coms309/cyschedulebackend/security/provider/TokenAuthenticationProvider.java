package edu.iastate.coms309.cyschedulebackend.security.provider;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import edu.iastate.coms309.cyschedulebackend.Service.UserTokenService;
import edu.iastate.coms309.cyschedulebackend.exception.auth.request.RequestAlreadyExpireException;
import edu.iastate.coms309.cyschedulebackend.exception.auth.request.RequestIncorrectException;
import edu.iastate.coms309.cyschedulebackend.exception.auth.request.RequestVerifyFaildException;
import edu.iastate.coms309.cyschedulebackend.persistence.model.permission.UserToken;
import edu.iastate.coms309.cyschedulebackend.security.model.AuthenticationToken;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Component;

import java.sql.Time;

@Component
public class TokenAuthenticationProvider implements AuthenticationProvider {

    @Autowired
    UserTokenService userTokenService;

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        AuthenticationToken credential = (AuthenticationToken) authentication;
        DecodedJWT decodedJWT = (DecodedJWT) credential.getCredentials();
        UserToken token = userTokenService.getTokenObject((String) credential.getPrincipal());

        if (token.getExpireTime().after(new Time(System.currentTimeMillis())))
            throw new RequestAlreadyExpireException();

        //Verify Token details
        if(!credential.getRequestUrl().equals(decodedJWT.getClaim("requestUrl").asString()))
            throw new RequestIncorrectException();

        //Verify Token changed or not
        Algorithm algorithm = Algorithm.HMAC256(token.getSecret());
        try {
            JWTVerifier verifier = JWT.require(algorithm)
                    .withIssuer("CySchedule")
                    .acceptLeeway(1)   //1 sec for nbf and iat
                    .acceptExpiresAt(5)   //5 secs for exp
                    .build();
            DecodedJWT jwt = verifier.verify(decodedJWT);
        } catch (JWTVerificationException exception) {
            throw new RequestVerifyFaildException();
        }

        logger.debug("A new Request for user [" + token.getOwner().getUserID() + "] is success authenticated");
        return new UsernamePasswordAuthenticationToken(token.getOwner().getUserID(),null,token.getPermissions());
    }

    @Override
    public boolean supports(Class<?> aClass) {
        return aClass.equals(AuthenticationToken .class);
    }
}
