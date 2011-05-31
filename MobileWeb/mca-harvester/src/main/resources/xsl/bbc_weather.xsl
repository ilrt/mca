<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0"
                xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:html="http://www.w3.org/1999/xhtml"
                xmlns:rdf="http://www.w3.org/1999/02/22-rdf-syntax-ns#"
                xmlns:rdfs="http://www.w3.org/2000/01/rdf-schema#"
                xmlns:mca="http://vocab.bris.ac.uk/mca/registry#">

    <xsl:output encoding="utf-8"/>

    <!-- URI for the weather data (this just be replaced by the harvester)-->
    <xsl:param name="uri" select="'mca://weather/bbc/example/'"/>

    <!-- match root and create the shell of the RDF document -->
    <xsl:template match="/">
        <rdf:RDF>
            <rdf:Description rdf:about="{$uri}">
                <mca:hasHtmlFragment>
                    <xsl:text disable-output-escaping="yes">&lt;</xsl:text>
                    <xsl:value-of select="concat('!','[','CDATA','[')"/>
                    <xsl:apply-templates select="rss/channel"/>
                    <xsl:value-of select="concat(']',']')"/>
                    <xsl:text disable-output-escaping="yes">&gt;</xsl:text>
                </mca:hasHtmlFragment>
            </rdf:Description>
        </rdf:RDF>
    </xsl:template>

    <!-- find the channel element -->
    <xsl:template match="rss/channel">
        <xsl:apply-templates select="item"/>
    </xsl:template>

    <!-- find the item(s) and place their content in html -->
    <xsl:template match="item">
        <p>
            <xsl:value-of select="./title"/>
        </p>
        <p>
            <xsl:value-of select="./description"/>
        </p>
        <p>Weather data from a <a href="{$uri}">BBC RSS feed</a>.
        </p>

    </xsl:template>

</xsl:stylesheet>
