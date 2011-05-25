<?xml version="1.0" encoding="UTF-8"?>

<!--

XSLT to extract the libraries from a webpage and create a semi-colon separated list.

Webpage: http://www.bristol.gov.uk/item/venuelist/?VenueTypeId=1&XSL=venueaddress
Usage: xsltproc --novalid bcc_library.xsl library.html

-->

<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:html="http://www.w3.org/1999/xhtml" version="1.0">
	
	<!-- creating a list -->
	<xsl:output method="text" />

<!-- Used to create a newline -->
<xsl:variable name='newline'><xsl:text>
</xsl:text></xsl:variable>

	<!-- match the root of the document -->
	<xsl:template match="/">
		<xsl:apply-templates select="/html:html/html:body/html:div/html:div/html:table[@summary='This table displays address, phone and e-mail information for  Libraries.']"/>
	</xsl:template>

	<!-- match the table with the library details -->
	<xsl:template match="/html:html/html:body/html:div/html:div/html:table[@summary='This table displays address, phone and e-mail information for  Libraries.']">
		<xsl:apply-templates select="html:tr"/>
	</xsl:template>

	<!-- find each table row -->
	<xsl:template match="html:tr">
		<!-- find the headers -->
		<xsl:for-each select="html:th">
			<xsl:value-of select="normalize-space(.)"/>
			<xsl:choose>
				<xsl:when test="position() != last()"><xsl:text>;</xsl:text></xsl:when>
				<xsl:otherwise><xsl:value-of select="$newline" /></xsl:otherwise>
			</xsl:choose>
		</xsl:for-each>
		<!-- find the contents -->
		<xsl:for-each select="html:td">
			<xsl:value-of select="normalize-space(.)"/>
			<xsl:choose>
				<xsl:when test="position() != last()"><xsl:text>;</xsl:text></xsl:when>
				<xsl:otherwise><xsl:value-of select="$newline" /></xsl:otherwise>
			</xsl:choose>
		</xsl:for-each>
	</xsl:template>

</xsl:stylesheet>
