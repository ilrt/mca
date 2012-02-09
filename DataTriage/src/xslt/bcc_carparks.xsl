<?xml version="1.0" encoding="UTF-8"?>
<!--
XSLT to extract the carpark data from an XML file and create RDF

Data: http://travelbristol.org/sites/all/ajax/carparks/index.xml
Usage: xsltproc bcc_carparks.xsl carparkdata.xml
-->
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:html="http://www.w3.org/1999/xhtml" version="1.0">
<xsl:output method="text" indent="no"/>

<!-- match the root of the document -->
<xsl:template match="/">
@prefix rdfs:       &lt;http://www.w3.org/2000/01/rdf-schema#&gt; .
@prefix rdf:        &lt;http://www.w3.org/1999/02/22-rdf-syntax-ns#&gt; .
@prefix xsd:        &lt;http://www.w3.org/2001/XMLSchema#&gt; .
@prefix mca:        &lt;http://vocab.bris.ac.uk/mca/registry#&gt; .
@prefix dc:         &lt;http://purl.org/dc/elements/1.1/&gt; .
@prefix dcterms:    &lt;http://purl.org/dc/terms/&gt; .	
@prefix geo:        &lt;http://www.w3.org/2003/01/geo/wgs84_pos#&gt; .
@prefix foaf:       &lt;http://xmlns.com/foaf/0.1/&gt; .
@prefix vcard:      &lt;http://www.w3.org/2001/vcard-rdf/3.0#&gt; .
@prefix mcageo:     &lt;http://vocab.bris.ac.uk/mca/geo#&gt; .
@prefix gn:         &lt;http://www.geonames.org/ontology#&gt; .
<xsl:apply-templates/>
</xsl:template>

<!-- match each of the car parks -->
<xsl:template match="/CarParkComponent/CarParkList/CarPark">
<!-- create the URI - the data has Id and id :-( -->
&lt;mca://data/bcc/parkingdata/carpark/<xsl:value-of select="Id"/><xsl:value-of select="id"/>&gt; rdf:type geo:Point ;
rdf:type &lt;http://www.geonames.org/ontology#S.PKLT&gt; ;
rdf:type mcageo:Amenity ;
rdfs:label "<xsl:value-of select="./Name"/>" ;
mcageo:hasTag "bcc_carpark"^^xsd:string ;
geo:lat "<xsl:value-of select="Latitude"/>"^^xsd:double ;
geo:long "<xsl:value-of select="Longitude"/>"^^xsd:double ;
mcageo:hasOperator &lt;mca://bcc/parkingdata/type/<xsl:value-of select="Operator"/>&gt; ;
mcageo:hasSpaces "<xsl:value-of select="Spaces"/>"^^xsd:integer ;
<xsl:if test="DisabledBays != ''">mcageo:hasDisabledBays "<xsl:value-of select="DisabledBays"/>"^^xsd:string ;</xsl:if>
vcard:ADR "<xsl:value-of select="Address1"/><xsl:if test="Address2 != ''">, <xsl:value-of select="Address2"/></xsl:if><xsl:if test="Address3 != ''">, <xsl:value-of select="Address3"/></xsl:if><xsl:if test="Postcode != ''">, <xsl:value-of select="Postcode"/></xsl:if>" ;
<xsl:if test="OpHours != ''">mcageo:hasOperatingHours "<xsl:value-of select="OpHours"/>"^^xsd:string ;</xsl:if> 
.
</xsl:template>

<xsl:template match="/CarParkComponent/CarParkOperators/CarParkOperator">
&lt;mca://data/bcc/parkingdata/operator/<xsl:value-of select="code"/>&gt; rdf:type mcageo:CarParkOperator;
rdfs:label "<xsl:value-of select="name"/>" ;
.
</xsl:template>

<xsl:template match="/CarParkComponent/CarParkTypes/CarParkType">
&lt;mca://data/bcc/parkingdata/type/<xsl:value-of select="typeid"/>&gt; rdf:type mcageo:carpark_type;
rdfs:label "<xsl:value-of select="description"/>" ;
.
</xsl:template>

<!-- do nothing -->
<xsl:template match="/CarParkComponent/CarParkAreas">
</xsl:template>
</xsl:stylesheet>