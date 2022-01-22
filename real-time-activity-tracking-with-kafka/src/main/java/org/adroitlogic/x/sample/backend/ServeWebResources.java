/*
* Copyright 2010-2022
* AdroitLogic Private Ltd. (https://www.adroitlogic.com). All Rights Reserved.
*
* AdroitLogic PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
*/
package org.adroitlogic.x.sample.backend;
import org.adroitlogic.x.annotation.config.Parameter;
import org.adroitlogic.x.annotation.feature.FeatureRef;
import org.adroitlogic.x.api.ExecutionResult;
import org.adroitlogic.x.api.IntegrationRuntimeException;
import org.adroitlogic.x.api.XMessageContext;
import org.adroitlogic.x.annotation.config.Processor;
import org.adroitlogic.x.api.config.InputType;
import org.adroitlogic.x.api.config.ProcessorType;
import org.adroitlogic.x.api.format.PayloadFactory;
import org.adroitlogic.x.base.processor.AbstractProcessingElement;
import org.adroitlogic.x.base.processor.AbstractSequencedProcessingElement;
import org.adroitlogic.x.base.util.XBaseUtils;

import java.io.File;
import java.net.URI;

@Processor(displayName = "ServeWebResources", type = ProcessorType.CUSTOM,
        description = "This processing element serves the required web resources")
public class ServeWebResources extends AbstractSequencedProcessingElement {

    private static enum ContentTypes {
        CSS("text/css"),
        JAVASCRIPT("application/javascript"),
        HTML("text/html"),
        IMAGE("image/*");

        private String value;

        ContentTypes(String value) {
            this.value = value;
        }

        public String value() {
            return value;
        }
    };

    @FeatureRef
    private PayloadFactory payloadFactory;

    @Parameter(
            displayName = "resourceURL",
            inputType = InputType.TEXT_BOX,
            description = "Resource URL which should be served for a web server. Resource URL can be set dynamically as " +
                    "well, using any combination of @{message.headers.<name>}, @{message.properties.<name>}, " +
                    "@{variable.<name>}, @{current.timestamp.<timestamp_format>} and @{message.id}",
            order = 1,
            propertyName = "resourceURL"
    )
    private String resourceURL;

    @Override
    public ExecutionResult sequencedProcess(XMessageContext messageContext) {

        try {
            String url = XBaseUtils.extractStringByReplacingPlaceHolders(messageContext, resourceURL);
            url = ("/".equals(url)) ? "web/index.html" : "web" + url;

            URI resource = getResource(XBaseUtils.extractStringByReplacingPlaceHolders(messageContext, url));

            if (resource != null) {
                messageContext.getMessage().setPayload(payloadFactory.createPayload(new File(resource)));

                String extension = url.substring(url.lastIndexOf('.'));

                switch (extension) {
                    case "css":
                        messageContext.getMessage().setContentType(ContentTypes.CSS.value());
                        break;
                    case "js":
                        messageContext.getMessage().setContentType(ContentTypes.JAVASCRIPT.value());
                        break;
                    case "html":
                        messageContext.getMessage().setContentType(ContentTypes.HTML.value());
                        break;
                    case "jpg":
                    case "png":
                        messageContext.getMessage().setContentType(ContentTypes.IMAGE.value());
                        break;
                }

                return ExecutionResult.SUCCESS;

            } else {
                logger.error(1, "Invalid resource URL : {}", resourceURL);
                throw new IllegalArgumentException("Invalid resource URL : " + resourceURL);
            }

        } catch (Exception e) {
            logger.error(2, "Error while setting the configured file as the current payload for message with message id : {}",
                    messageContext.getMessage().getMessageId(), e);
            throw new IntegrationRuntimeException(e);
        }
    }

    public void setResourceURL(String resourceURL) {
        this.resourceURL = resourceURL;
    }
}
