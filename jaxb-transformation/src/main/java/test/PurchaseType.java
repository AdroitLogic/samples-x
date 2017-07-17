/*
* Copyright (c) 2010-2017 AdroitLogic Private Ltd. (https://www.adroitlogic.com/). All Rights Reserved.
*
* AdroitLogic PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
*/

package test;

import javax.xml.bind.annotation.XmlRootElement;

/**
 * Java class to create objects
 *
 * @author Hasangi Thathsarani
 * @since 17.07
 */

@XmlRootElement(namespace = "", name = "comment")
public class PurchaseType {

    protected String shipTo;
    protected String billTo;
    protected String items;
    protected String orderDate;

    public PurchaseType() {
    }

    public PurchaseType(String shipTo, String billTo, String items, String orderDate) {
        this.shipTo = shipTo;
        this.billTo = billTo;
        this.items = items;
        this.orderDate = orderDate;
    }

    public String getShipTo() {
        return shipTo;
    }

    public void setShipTo(String shipTo) {
        this.shipTo = shipTo;
    }

    public String getBillTo() {
        return billTo;
    }

    public void setBillTo(String billTo) {
        this.billTo = billTo;
    }

    public String getItems() {
        return items;
    }

    public void setItems(String items) {
        this.items = items;
    }

    public String getOrderDate() {
        return orderDate;
    }

    public void setOrderDate(String orderDate) {
        this.orderDate = orderDate;
    }
}
