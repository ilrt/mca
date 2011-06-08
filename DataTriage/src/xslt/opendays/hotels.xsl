<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0"
                xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:html="http://www.w3.org/1999/xhtml"
                xmlns:rdf="http://www.w3.org/1999/02/22-rdf-syntax-ns#"
                xmlns:rdfs="http://www.w3.org/2000/01/rdf-schema#"
                xmlns:mca="http://vocab.bris.ac.uk/mca/registry#">
	
	<xsl:output encoding="utf-8"/>
	
	<xsl:param name="uri" select="'mca://data/graph/openday/hotels.rdf'"/>
	
	<!-- match the root -->
	<xsl:template match="/">
		<rdf:RDF>
			<rdf:Description rdf:about="{$uri}">
				<mca:hasHtmlFragment>
					<xsl:text disable-output-escaping="yes">&lt;</xsl:text>
                	<xsl:value-of select="concat('!','[','CDATA','[')"/>
					<div id="openday-content">
					<xsl:apply-templates select="/html:html/html:body/html:div/html:div[@id='uobcms-content']"/>
					</div>
					<xsl:value-of select="concat(']',']')"/>
                	<xsl:text disable-output-escaping="yes">&gt;</xsl:text>
				</mca:hasHtmlFragment>
			</rdf:Description>
		</rdf:RDF>
	</xsl:template>
	
	<!-- the main content of the page -->
	<xsl:template match="/html:html/html:body/html:div/html:div[@id='uobcms-content']">
		<xsl:apply-templates/>
	</xsl:template>	
	
	<!-- drop the header, it will be defined in the configuration -->
	<xsl:template match="html:h1"></xsl:template>

	<!-- change the h2 to a h3 -->
	<xsl:template match="html:h2"><h3><xsl:value-of select="."/></h3></xsl:template>

	<!-- grab the href -->
	<xsl:template match="html:a">
        <xsl:variable name="link"><xsl:value-of select="@href"/></xsl:variable>
		<a href="{$link}"><xsl:value-of select="."/></a>
	</xsl:template>

	<!-- copy elements, but drop the attributes -->
	<xsl:template match="*">
	    <xsl:element name="{local-name(.)}">
	      <xsl:apply-templates />
	    </xsl:element>
	</xsl:template>
	
	<xsl:template match="@*">
        <xsl:copy/>
    </xsl:template>

</xsl:stylesheet>