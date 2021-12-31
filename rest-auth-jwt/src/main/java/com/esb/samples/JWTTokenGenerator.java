/*
 * Copyright (c) 2010-2017 AdroitLogic Private Ltd. (http://adroitlogic.org). All Rights Reserved.
 *
 * AdroitLogic PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package com.esb.samples;

import io.jsonwebtoken.JwtBuilder;
import org.adroitlogic.x.annotation.config.OutPort;
import org.adroitlogic.x.annotation.config.Parameter;
import org.adroitlogic.x.api.ExecutionResult;
import org.adroitlogic.x.api.XMessage;
import org.adroitlogic.x.api.XMessageContext;
import org.adroitlogic.x.annotation.config.Processor;
import org.adroitlogic.x.api.config.ProcessorType;
import org.adroitlogic.x.api.processor.XProcessingElement;
import org.adroitlogic.x.base.format.StringFormat;
import org.adroitlogic.x.base.processor.AbstractProcessingElement;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.apache.commons.codec.binary.Base64;

import javax.crypto.spec.SecretKeySpec;
import java.security.Key;
import java.util.Date;
import java.util.Optional;

import static com.esb.samples.AuthHeaderUtil.ERROR_KEY;
import static org.adroitlogic.x.annotation.config.Parameter.Validators.POSITIVE_INT_EXCLUDING_ZERO;

/**
 * This processing element generates a JWT token using the username passed as a message context parameter and sets
 * it as the message payload
 */
@Processor(displayName = "JWT Token Generator", type = ProcessorType.CUSTOM, iconFileName = "default",
        description = "This processing element generates a JWT token using the username passed as a message context " +
        "parameter and sets it as the message payload", documentationURL = "https://developer.adroitlogic.com")
public class JWTTokenGenerator extends AbstractProcessingElement {

    @OutPort(displayName = "Next Element")
    private XProcessingElement nextElement;

    @Parameter(displayName = "Secret Key", description = "Specify the Secret key to be used", propertyName = "secretKey")
    private String secretKey;

    @Parameter(displayName = "Valid Period", description = "Specify the valid time period for the token in milliseconds",
            validator = POSITIVE_INT_EXCLUDING_ZERO, propertyName = "validTimePeriod")
    private long validTimePeriod;

    @Override
    public ExecutionResult process(XMessageContext messageContext) {
        try {
            final Optional<String> userName = messageContext.getMessage().getStringMessageProperty(AuthHeaderUtil.USERNAME_KEY);
            if (!userName.isPresent()) {
                throw new RuntimeException(AuthHeaderUtil.USERNAME_KEY + " unavailable");
            }

            //The JWT signature algorithm we will be using to sign the token
            SignatureAlgorithm signatureAlgorithm = SignatureAlgorithm.HS256;

            long nowMillis = System.currentTimeMillis();
            Date now = new Date(nowMillis);

            // sign our JWT with our  secret
            byte[] apiKeySecretBytes = Base64.decodeBase64(secretKey);
            Key signingKey = new SecretKeySpec(apiKeySecretBytes, signatureAlgorithm.getJcaName());


            //Let's set the JWT Claims
            JwtBuilder builder = Jwts.builder().setId(userName.get())
                    .setIssuedAt(now)
                    .setSubject(userName.get())
                    .setIssuer("AdroitLogic")
                    .signWith(signatureAlgorithm, signingKey);

            //if it has been specified, let's add the expiration
            if (validTimePeriod >= 0) {
                long expMillis = nowMillis + validTimePeriod;
                Date exp = new Date(expMillis);
                builder.setExpiration(exp);
            }

            //Builds the JWT and serializes it to a compact, URL-safe string and set it as new payload
            messageContext.getMessage().setPayload(new StringFormat(builder.compact()));

            return nextElement.processMessage(messageContext);

        } catch (RuntimeException e) {
            // server error
            final XMessage msg = messageContext.getMessage();
            msg.setResponseCode(500);
            msg.addMessageProperty(ERROR_KEY, e.getMessage());
            return getErrorHandler().processMessage(messageContext);
        }
    }

    public XProcessingElement getNextElement() {
        return nextElement;
    }

    public void setNextElement(XProcessingElement nextElement) {
        this.nextElement = nextElement;
    }

    public void setSecretKey(String secretKey) {
        this.secretKey = secretKey;
    }

    public void setValidTimePeriod(long validTimePeriod) {
        this.validTimePeriod = validTimePeriod;
    }
}
