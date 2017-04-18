/*
 * Copyright (c) 2010-2017 AdroitLogic Private Ltd. (http://adroitlogic.org). All Rights Reserved.
 *
 * AdroitLogic PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package com.esb.samples;

import org.adroitlogic.x.annotation.config.Parameter;
import org.adroitlogic.x.annotation.config.Processor;
import org.adroitlogic.x.api.ExecutionResult;
import org.adroitlogic.x.api.IntegrationRuntimeException;
import org.adroitlogic.x.api.XMessage;
import org.adroitlogic.x.api.XMessageContext;
import org.adroitlogic.x.api.config.ProcessorType;
import org.adroitlogic.x.base.format.StringFormat;
import org.adroitlogic.x.base.processor.AbstractSequencedProcessingElement;
import org.adroitlogic.x.logging.LogInfo;
import org.springframework.context.ApplicationContext;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;

/**
 * At initialization, this processing element loads a set of credentials from a comma-separated username-password file
 * into an in-memory cache (map), and utilizes it at runtime to authenticate users
 */
@Processor(displayName = "Basic Authenticator", type = ProcessorType.CUSTOM)
@LogInfo(loggerId = 1, nextLogCode = 2)
public class BasicAuthenticator extends AbstractSequencedProcessingElement {

    private static final String AUTH_HEADER = "Authorization";

    private final Map<String, String> credentialCache = new HashMap<>();

    @Parameter(displayName = "Credential File", propertyName = "credentialFile",
            description = "Location of a credential file with comma-separated username-password pairs")
    private String credentialFile;

    @Override
    protected ExecutionResult sequencedProcess(XMessageContext xMessageContext) {
        XMessage msg = xMessageContext.getMessage();
        Optional<String> authHeader = msg.getFirstStringTransportHeader(AUTH_HEADER);
        if (!authHeader.isPresent()) {
            throw deauthorize(msg, "Please provide an " + AUTH_HEADER + " header for basic auth");
        }

        try {
            String authString = new String(Base64.getDecoder().decode(authHeader.get().substring(6)));
            String[] credentials = authString.split(":", 2);
            if (credentials.length != 2) {
                throw deauthorize(msg, AUTH_HEADER + " header is not in the correct format");
            }
            if (!credentials[1].equals(credentialCache.get(credentials[0]))) {
                throw deauthorize(msg, "Invalid username/password");
            }
            msg.removeTransportHeader(AUTH_HEADER);
            return ExecutionResult.SUCCESS;

        } catch (AuthenticationException e) {
            throw e;
        } catch (Exception e) {
            throw deauthorize(msg, "Failed to process the " + AUTH_HEADER + " header: " + e.getMessage());
        }
    }

    private AuthenticationException deauthorize(XMessage msg, String reason) {
        msg.setResponseCode(401);
        msg.setPayload(new StringFormat(reason));
        return new AuthenticationException("Authentication failed for message " + msg.getMessageId());
    }

    @Override
    protected void initElement(ApplicationContext context) {
        try (InputStream is = new FileInputStream(getResource(credentialFile).getPath())) {
            Scanner scanner = new Scanner(is);
            while (scanner.hasNextLine()) {
                String[] values = scanner.nextLine().split(",");
                credentialCache.put(values[0], values[1]);
            }
        } catch (IOException e) {
            throw new IntegrationRuntimeException("Failed to initialize credential cache", e);
        }
    }

    public void setCredentialFile(String credentialFile) {
        this.credentialFile = credentialFile;
    }
}
