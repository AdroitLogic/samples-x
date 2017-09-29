/*
* Copyright (c) 2010-2017 AdroitLogic Private Ltd. (http://adroitlogic.org). All Rights Reserved.
*
* AdroitLogic PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
*/
package org.adroitlogic.x.sample.processors.custom;

import org.adroitlogic.x.annotation.config.Parameter;
import org.adroitlogic.x.annotation.config.Processor;
import org.adroitlogic.x.api.ExecutionResult;
import org.adroitlogic.x.api.IntegrationRuntimeException;
import org.adroitlogic.x.api.XMessageContext;
import org.adroitlogic.x.api.config.InputType;
import org.adroitlogic.x.api.config.ProcessorType;
import org.adroitlogic.x.base.processor.AbstractProcessingElement;
import org.adroitlogic.x.base.util.XBaseUtils;
import org.adroitlogic.x.sample.util.StatisticsEngine;

/**
 *  This processing element injects the news-read impressions into the internal Statistics Engine.
 */
@Processor(displayName = "Impression Injector", type = ProcessorType.CUSTOM,
        description = "This processing element injects the news-read impressions into the internal Statistics Engine")
public class ImpressionInjector extends AbstractProcessingElement {

    @Parameter(
            displayName = "News ID",
            propertyName = "newsId",
            order = 1,
            inputType = InputType.TEXT_BOX,
            description = "Unique news ID, which needs to be updated for the impression. This can be a plain text static " +
                    "string or any combination of @{message.headers.<name>}, @{message.properties.<name>}, " +
                    "@{variable.<name>}, @{current.timestamp.<timestamp_format>} and @{message.id}"
    )
    private String newsId;

    @Override
    public ExecutionResult process(XMessageContext messageContext) {
        try {
            String key = XBaseUtils.extractStringByReplacingPlaceHolders(messageContext, newsId);
            StatisticsEngine.INSTANCE.incrementReadCountByOne(key);

            return ExecutionResult.SUCCESS;
        } catch (Exception e) {
            logger.error(1, "Error while injecting the news read impressions into the internal Statistics Engine for " +
                    "message id : {}", messageContext.getMessage().getMessageId(), e);
            throw new IntegrationRuntimeException(e);
        }
    }

    public void setNewsId(String newsId) {
        this.newsId = newsId;
    }
}
