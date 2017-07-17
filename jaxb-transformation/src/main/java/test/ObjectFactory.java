/*
* Copyright (c) 2010-2017 AdroitLogic Private Ltd. (https://www.adroitlogic.org). All Rights Reserved.
*
* AdroitLogic PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
*/

package test;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the test package. 
 * <p>An ObjectFactory allows you to programatically 
 * construct new instances of the Java representation 
 * for XML content. The Java representation of XML 
 * content can consist of schema derived interfaces 
 * and classes representing the binding of schema 
 * type definitions, element declarations and model 
 * groups.  Factory methods for each of these are 
 * provided in this class.
 * 
 */
@XmlRegistry
public class ObjectFactory {

    private final static QName _PurchaseOrder_QNAME = new QName("", "purchaseType");
    private final static QName _Comment_QNAME = new QName("", "comment");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: test
     * 
     */
    public ObjectFactory() {
    }


    public PurchaseType createPurchaseOrderType() {
        return new PurchaseType();
    }


    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link PurchaseType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "", name = "purchaseOrder")
    public JAXBElement<PurchaseType> createPurchaseOrder(PurchaseType value) {
        return new JAXBElement<PurchaseType>(_PurchaseOrder_QNAME, PurchaseType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "", name = "comment")
    public JAXBElement<String> createComment(String value) {
        return new JAXBElement<String>(_Comment_QNAME, String.class, null, value);
    }

}
