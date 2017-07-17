/*
* Copyright (c) 2010-2017 AdroitLogic Private Ltd. (https://www.adroitlogic.com/). All Rights Reserved.
*
* AdroitLogic PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
*/

package com.acme.esb;

import org.adroitlogic.x.annotation.config.OutPort;
import org.adroitlogic.x.annotation.config.Processor;
import org.adroitlogic.x.api.ExecutionResult;
import org.adroitlogic.x.api.XMessageContext;
import org.adroitlogic.x.api.config.ProcessorType;
import org.adroitlogic.x.api.processor.XProcessingElement;
import org.adroitlogic.x.base.format.ObjectFormat;
import org.adroitlogic.x.base.processor.AbstractProcessingElement;
import org.json.JSONArray;
import test.PurchaseType;

/**
 * Customer processing element to create objects using the database data and to set the created object as the message
 * payload
 *
 * @author Hasangi Thathsarani
 * @since 17.07
 */

@Processor(displayName = "Object Extractor", type = ProcessorType.CUSTOM)
public class ObjectExtractor extends AbstractProcessingElement {

    @OutPort(displayName = "Next", description = "Next processing element")
    private XProcessingElement nextElement;

    @Override
    public ExecutionResult process(XMessageContext messageContext) {
        String jsonStr = messageContext.getMessage().getPayload().readAsString(-1);
        JSONArray jsonArray = new JSONArray(jsonStr);
        for (int temp = 0; temp < jsonArray.length(); temp++) {
            PurchaseType purchaseType = new PurchaseType(jsonArray.getJSONObject(temp).getString("ShipTo"),
                    jsonArray.getJSONObject(temp).getString("BillTo"), jsonArray.getJSONObject(temp).getString("Item"),
                    jsonArray.getJSONObject(temp).getString("OrderDate"));
            messageContext.getMessage().setPayload(new ObjectFormat(purchaseType));
        }
        return nextElement.processMessage(messageContext);
    }

    public XProcessingElement getNextElement() {
        return nextElement;
    }

    public void setNextElement(XProcessingElement nextElement) {
        this.nextElement = nextElement;
    }
}
