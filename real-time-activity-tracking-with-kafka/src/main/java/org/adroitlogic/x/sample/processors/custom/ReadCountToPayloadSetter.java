/*
* Copyright 2010-2022
* AdroitLogic Private Ltd. (https://www.adroitlogic.com). All Rights Reserved.
*
* AdroitLogic PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
*/
package org.adroitlogic.x.sample.processors.custom;

import org.adroitlogic.x.annotation.config.Parameter;
import org.adroitlogic.x.annotation.config.Processor;
import org.adroitlogic.x.annotation.feature.FeatureRef;
import org.adroitlogic.x.api.ExecutionResult;
import org.adroitlogic.x.api.IntegrationRuntimeException;
import org.adroitlogic.x.api.XMessageContext;
import org.adroitlogic.x.api.config.InputType;
import org.adroitlogic.x.api.config.ProcessorType;
import org.adroitlogic.x.api.format.PayloadFactory;
import org.adroitlogic.x.base.processor.AbstractSequencedProcessingElement;
import org.adroitlogic.x.base.util.XBaseUtils;
import org.adroitlogic.x.sample.util.StatisticsEngine;

/**
 *  This processing element queries the Statistics Engine with a newsId, and message payload is modified to be the read
 *  count value retrieved.
 */
@Processor(displayName = "Read Count to Payload Setter", type = ProcessorType.CUSTOM,
        description = "This processing element queries the Statistics Engine with a newsId, and message payload is " +
                "modified to be the read count value retrieved")
public class ReadCountToPayloadSetter extends AbstractSequencedProcessingElement {

    @FeatureRef
    private PayloadFactory payloadFactory;

    @Parameter(
            displayName = "News ID",
            propertyName = "newsId",
            order = 1,
            inputType = InputType.TEXT_BOX,
            description = "Unique news ID, which the read count needs to be retrieved and set as the message payload. " +
                    "This can be a plain text static string or any combination of @{message.headers.<name>}, " +
                    "@{message.properties.<name>}, @{variable.<name>}, @{current.timestamp.<timestamp_format>} and " +
                    "@{message.id}"
    )
    private String newsId;

    @Override
    public ExecutionResult sequencedProcess(XMessageContext messageContext) {
        try {
            String key = XBaseUtils.extractStringByReplacingPlaceHolders(messageContext, newsId);
            Long readCount = StatisticsEngine.INSTANCE.getCount(key);

            messageContext.getMessage().setPayload(payloadFactory.createPayload(
                    "<message><count>" + readCount + "</count></message>"));

            return ExecutionResult.SUCCESS;
        } catch (Exception e) {
            logger.error(1, "Error while setting the configured string with the retrieved read count  as the current " +
                    "payload for message with message id : {}", messageContext.getMessage().getMessageId(), e);
            throw new IntegrationRuntimeException(e);
        }
    }

    public void setNewsId(String newsId) {
        this.newsId = newsId;
    }
}
