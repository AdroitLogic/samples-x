<xsl:stylesheet version="2.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform"   xmlns:xs="http://www.w3.org/2001/XMLSchema" xmlns="http://adrt.com/studio/testing">

    <xsl:output indent="yes" omit-xml-declaration="no"/>
    <xsl:strip-space elements="*"/>

    <xsl:template match="Currency" xpath-default-namespace="http://adrt.com/studio/testing">
        <ExchangeRates>
            <OriginatedFrom>Global Currency Ltd</OriginatedFrom>
            <ReferenceNo><xsl:value-of select="CurrencyInfo[1]/REFERENCE_NO"/></ReferenceNo>
            <CreationTimestamp><xsl:value-of select="adjust-dateTime-to-timezone(current-dateTime(), xs:dayTimeDuration('PT0H'))"/></CreationTimestamp>
            <Rates>
                <xsl:apply-templates/>
            </Rates>
        </ExchangeRates>
    </xsl:template>

    <xsl:template match="CurrencyInfo" xpath-default-namespace="http://adrt.com/studio/testing">
        <Rate>
            <Currency><xsl:value-of select="CRNCY_CODE"/></Currency>
            <Rate><xsl:value-of select="EXCH_RATE"/></Rate>
            <RelativeTo>USD</RelativeTo>
        </Rate>
    </xsl:template>

</xsl:stylesheet>
