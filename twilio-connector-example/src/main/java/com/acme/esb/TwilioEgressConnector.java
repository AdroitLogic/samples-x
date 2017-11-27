/*
 * Copyright (c) 2010-2017 AdroitLogic Private Ltd. (https://www.adroitlogic.com). All Rights Reserved.
 *
 * AdroitLogic PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package com.acme.esb;

import com.twilio.Twilio;
import org.adroitlogic.x.annotation.config.EgressConnector;
import org.adroitlogic.x.annotation.config.Parameter;
import org.adroitlogic.x.api.XMessage;
import org.adroitlogic.x.api.XMessageContext;
import org.adroitlogic.x.api.config.InputType;
import org.adroitlogic.x.base.connector.call.CallOutEgressConnectorElement;
import org.adroitlogic.x.logging.LogInfo;
import org.springframework.context.ApplicationContext;

/**
 * @author Hasangi Thathsarani
 * @since 18.01
 */
@LogInfo(loggerId = 1, nextLogCode = 2)
@EgressConnector(displayName = "Twilio Egress Connector",
        description = "Egress Connector for Twilio")
public class TwilioEgressConnector extends CallOutEgressConnectorElement{

    @Parameter(displayName = "User Name", inputType = InputType.TEXT_BOX, placeHolder = "Twilio Account User Name",
            description = "Twilio Account User Name", propertyName = "userName")
    private String userName;

    @Parameter(displayName = "Password", inputType = InputType.TEXT_BOX, placeHolder = "Twilio Account Password",
            description = "Twilio Account Password", propertyName = "password")
    private String password;

    @Override
    protected void initBiEgressConnector(ApplicationContext context) {
        Twilio.init(userName, password);
        logger.info(1, "Initializing Twilio API");
    }

    @Override
    protected XMessage sendReceiveMessage(XMessageContext xMessageContext) {
        return xMessageContext.getMessage();
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}