/*
 * Copyright 2010-2022
 * AdroitLogic Private Ltd. (https://www.adroitlogic.com). All Rights Reserved.
 *
 * AdroitLogic PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package com.esb.samples;

import org.adroitlogic.x.annotation.config.OutPort;
import org.adroitlogic.x.annotation.config.Parameter;
import org.adroitlogic.x.api.ExecutionResult;
import org.adroitlogic.x.api.IntegrationRuntimeException;
import org.adroitlogic.x.api.XMessage;
import org.adroitlogic.x.api.XMessageContext;
import org.adroitlogic.x.annotation.config.Processor;
import org.adroitlogic.x.api.config.InputType;
import org.adroitlogic.x.api.config.ProcessorType;
import org.adroitlogic.x.api.processor.XProcessingElement;
import org.adroitlogic.x.base.processor.AbstractProcessingElement;
import org.springframework.context.ApplicationContext;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Map;
import java.util.Scanner;

/**
 * At initialization, this processing element loads a set of credentials from a comma-separated username-password file
 * into a project-wide shared map (cache), and utilizes it at runtime to authenticate users
 */
@Processor(displayName = "User Authenticator", type = ProcessorType.CUSTOM, iconFileName = "default",
        description = "This processing element loads a set of credentials from a comma-separated username-password file " +
        "into a project-wide shared map (cache) at server startup, and utilizes it at runtime to authenticate users.",
        documentationURL = "https://developer.adroitlogic.com")
public class UserAuthenticator extends AbstractProcessingElement {

    @OutPort(displayName = "Next Element")
    private XProcessingElement nextElement;

    @Parameter(displayName = "Credential File", propertyName = "credentialFile",
            description = "Location of a credential file with comma-separated username-password pairs")
    private String credentialFile;

    @Parameter(displayName = "User Cache", inputType = InputType.RESOURCE,
            description = "Shared Map of username-password credentials")
    private Map<String, String> userCache;

    @Override
    public ExecutionResult process(XMessageContext messageContext) {
        final XMessage msg = messageContext.getMessage();
        try {
            final String authHeader = AuthHeaderUtil.getAuthHeaderValue(messageContext, "Basic ");

            final String[] userCredentials = authHeader.split(":", 2);
            if (userCredentials.length != 2) {
                throw new RuntimeException(AuthHeaderUtil.AUTH_HEADER + " header is not properly formatted");
            }

            if (!userCredentials[1].equals(userCache.get(userCredentials[0]))) {
                throw new RuntimeException("Invalid username/password");
            }
            msg.removeTransportHeader(AuthHeaderUtil.AUTH_HEADER);

            // adding the username a property which can be used to generate JWT token
            msg.addMessageProperty(AuthHeaderUtil.USERNAME_KEY, userCredentials[0]);

            return nextElement.processMessage(messageContext);

        } catch (RuntimeException e) {
            // on authentication failure
            msg.setResponseCode(401);
            msg.addMessageProperty(AuthHeaderUtil.ERROR_KEY, e.getMessage());
            return getErrorHandler().processMessage(messageContext);
        }
    }

    @Override
    protected void initElement(ApplicationContext context) {
        try {
            InputStream is = new FileInputStream(getResource(credentialFile).getPath());
            Scanner scanner = new Scanner(is);
            while (scanner.hasNextLine()) {
                String[] values = scanner.nextLine().split(",");
                userCache.put(values[0], values[1]);
            }
        } catch (Exception e) {
            throw new IntegrationRuntimeException("Failed to initialize credential cache", e);
        }
    }

    public XProcessingElement getNextElement() {
        return nextElement;
    }

    public void setNextElement(XProcessingElement nextElement) {
        this.nextElement = nextElement;
    }

    public void setCredentialFile(String credentialFile) {
        this.credentialFile = credentialFile;
    }

    public void setUserCache(Map<String, String> userCache) {
        this.userCache = userCache;
    }
}
