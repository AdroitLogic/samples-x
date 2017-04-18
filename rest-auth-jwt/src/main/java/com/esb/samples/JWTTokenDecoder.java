/*
 * Copyright (c) 2010-2017 AdroitLogic Private Ltd. (http://adroitlogic.org). All Rights Reserved.
 *
 * AdroitLogic PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package com.esb.samples;

import org.adroitlogic.x.annotation.config.OutPort;
import org.adroitlogic.x.annotation.config.Parameter;
import org.adroitlogic.x.api.ExecutionResult;
import org.adroitlogic.x.api.XMessage;
import org.adroitlogic.x.api.XMessageContext;
import org.adroitlogic.x.annotation.config.Processor;
import org.adroitlogic.x.api.config.InputType;
import org.adroitlogic.x.api.config.ProcessorType;
import org.adroitlogic.x.api.processor.XProcessingElement;
import org.adroitlogic.x.base.processor.AbstractProcessingElement;

import javax.xml.bind.DatatypeConverter;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.Claims;

import java.util.Map;

import static com.esb.samples.AuthHeaderUtil.ERROR_KEY;

/**
 * This processing element decodes the JWT token received as a bearer token in the Authorization transport header of
 * the request, validates it against the shared user cache, and allows the message flow to proceed upon success
 */
@Processor(displayName = "JWT Token Decoder", type = ProcessorType.CUSTOM)
public class JWTTokenDecoder extends AbstractProcessingElement {

    @OutPort(displayName = "Next element")
    private XProcessingElement nextElement;

    @Parameter(displayName = "Secret Key", description = "Specify the Secret key to be used", propertyName = "secretKey")
    private String secretKey;

    @Parameter(displayName = "User Cache", inputType = InputType.RESOURCE,
            description = "Shared Map of username-password credentials")
    private Map<String, String> userCache;

    @Override
    public ExecutionResult process(XMessageContext messageContext) {
        try {
            final String token = AuthHeaderUtil.getAuthHeaderValue(messageContext, "Bearer ");

            Claims claims = Jwts.parser()
                    .setSigningKey(DatatypeConverter.parseBase64Binary(secretKey))
                    .parseClaimsJws(token).getBody();

            // check if user is known
            if (!userCache.containsKey(claims.getId())) {
                throw new RuntimeException("Token source mismatch");
            }

            return nextElement.processMessage(messageContext);

        } catch (RuntimeException e) {
            // on authentication failure
            final XMessage msg = messageContext.getMessage();
            msg.setResponseCode(401);
            msg.addMessageProperty(ERROR_KEY, e.getMessage());
            return getErrorHandler().processMessage(messageContext);
        }
    }

    public void setSecretKey(String secretKey) {
        this.secretKey = secretKey;
    }

    public XProcessingElement getNextElement() {
        return nextElement;
    }

    public void setNextElement(XProcessingElement nextElement) {
        this.nextElement = nextElement;
    }

    public void setUserCache(Map<String, String> userCache) {
        this.userCache = userCache;
    }
}
