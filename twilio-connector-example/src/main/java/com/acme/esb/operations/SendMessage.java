/*
 * Copyright (c) 2010-2017 AdroitLogic Private Ltd. (https://www.adroitlogic.com). All Rights Reserved.
 *
 * AdroitLogic PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package com.acme.esb.operations;

import com.acme.esb.TwilioEgressConnector;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;
import org.adroitlogic.x.annotation.config.ConnectorOperation;
import org.adroitlogic.x.annotation.config.Parameter;
import org.adroitlogic.x.api.XMessageContext;
import org.adroitlogic.x.api.config.InputType;
import org.adroitlogic.x.base.connector.AbstractConnectorOperation;
import org.adroitlogic.x.logging.LogInfo;

/**
 * @author Hasangi Thathsarani
 * @since 18.01
 */
@LogInfo(loggerId = 4, nextLogCode = 3)
@ConnectorOperation(displayName = "Send Messages", belongsTo = TwilioEgressConnector.class,
        description = "User can use this processing element to send to a specified phone number")
public class SendMessage extends AbstractConnectorOperation {

    @Parameter(displayName = "From Phone Number", inputType = InputType.TEXT_BOX, placeHolder = "Insert your phone number",
            description = "Your phone number", propertyName = "fromNumber", order = 1)
    private String fromNumber;

    @Parameter(displayName = "To Phone number", inputType = InputType.TEXT_BOX, placeHolder = "Insert recipient's phone number ",
            description = "Phone number to send the message", propertyName = "toNumber", order = 2)
    private String toNumber;

    @Parameter(displayName = "Message Body", inputType = InputType.TEXT_BOX, placeHolder = "Insert the message body",
            description = "Message Body", propertyName = "userName", order = 3)
    private String messageBody;

    @Override
    public XMessageContext prepareMessage(XMessageContext messageContext) {
        Message message = Message
                .creator(new PhoneNumber(fromNumber), new PhoneNumber(toNumber),
                       messageBody ).create();
        logger.info(1,"Message Status : {} ", message.getStatus());
        messageContext.getMessage().addMessageProperty("Response", message.getStatus());
        return messageContext;
    }

    public String getFromNumber() {
        return fromNumber;
    }

    public void setFromNumber(String fromNumber) {
        this.fromNumber = fromNumber;
    }

    public String getToNumber() {
        return toNumber;
    }

    public void setToNumber(String toNumber) {
        this.toNumber = toNumber;
    }

    public String getMessageBody() {
        return messageBody;
    }

    public void setMessageBody(String messageBody) {
        this.messageBody = messageBody;
    }
}
