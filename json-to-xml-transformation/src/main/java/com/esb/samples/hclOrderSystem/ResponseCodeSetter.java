/*
* Copyright (c) 2010-2017 AdroitLogic Private Ltd. (http://adroitlogic.org). All Rights Reserved.
*
* AdroitLogic PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
*/
package com.esb.samples.hclOrderSystem;

import org.adroitlogic.x.annotation.config.Parameter;
import org.adroitlogic.x.api.ExecutionResult;
import org.adroitlogic.x.api.XMessage;
import org.adroitlogic.x.api.XMessageContext;
import org.adroitlogic.x.annotation.config.Processor;
import org.adroitlogic.x.api.config.InputType;
import org.adroitlogic.x.api.config.ProcessorType;
import org.adroitlogic.x.base.format.EmptyFormat;
import org.adroitlogic.x.base.processor.AbstractProcessingElement;
import org.adroitlogic.x.base.processor.AbstractSequencedProcessingElement;

/**
 * This processing element will set the specified response code to the message and also provides the option to set an
 * empty payload.
 * <p>
 * This element is intended for the use of mock backend service used in this sample.
 */
@Processor(displayName = "Response Code Setter", type = ProcessorType.CUSTOM)
public class ResponseCodeSetter extends AbstractSequencedProcessingElement {

    @Parameter(displayName = "Response Code", inputType = InputType.TEXT_BOX, placeHolder = "200")
    private int responseCode;

    @Parameter(displayName = "Set Empty Payload", inputType = InputType.CHECK_BOX, defaultValue = "false")
    private boolean setEmptyPayload = false;

    @Override
    protected ExecutionResult sequencedProcess(XMessageContext xMessageContext) {
        XMessage message = xMessageContext.getMessage();
        if (message != null) {
            message.setResponseCode(responseCode);
            if (setEmptyPayload) {
                message.setPayload(new EmptyFormat());
            }
        }
        return ExecutionResult.SUCCESS;
    }

    public void setResponseCode(int responseCode) {
        this.responseCode = responseCode;
    }

    public void setSetEmptyPayload(boolean setEmptyPayload) {
        this.setEmptyPayload = setEmptyPayload;
    }
}
