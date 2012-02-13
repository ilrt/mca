<?xml version="1.0" encoding="UTF-8"?>
<!--
XSLT to extract the naptan data from an XML file and create RDF
-->
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:html="http://www.w3.org/1999/xhtml" version="1.0">
<xsl:output method="text" indent="no"/>

<!-- match the root of the document -->
<xsl:template match="/">
@prefix rdfs:       &lt;http://www.w3.org/2000/01/rdf-schema#&gt; .
@prefix rdf:        &lt;http://www.w3.org/1999/02/22-rdf-syntax-ns#&gt; .
@prefix xsd:        &lt;http://www.w3.org/2001/XMLSchema#&gt; .
@prefix geo:        &lt;http://www.w3.org/2003/01/geo/wgs84_pos#&gt; .
@prefix gn:         &lt;http://www.geonames.org/ontology#&gt; .
<xsl:apply-templates/>
</xsl:template>

<!-- match each of the bus stops -->
<xsl:template match="/NaPTAN/Area/Stops/Stop">
<!-- create the URI -->
&lt;mca://data/naptan/bustops/busstop/<xsl:value-of select="ATCOCode"/>&gt; rdf:type geo:Point ;
rdf:type &lt;http://www.geonames.org/ontology#S.BUSTP&gt; ;
rdfs:label "<xsl:value-of select="./CommonName"/>" ;
geo:lat "<xsl:value-of select="Lat"/>"^^xsd:double ;
geo:long "<xsl:value-of select="Lon"/>"^^xsd:double ;
.
</xsl:template>
<!-- Do nothing -->
<xsl:template match="/NaPTAN/Area/AreaID">
</xsl:template>
<xsl:template match="/NaPTAN/Area/AreaName">
</xsl:template>
<xsl:template match="/NaPTAN/Area/StopGroups">
</xsl:template>
</xsl:stylesheet>