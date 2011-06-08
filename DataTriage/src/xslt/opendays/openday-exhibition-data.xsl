<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0"
                xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:html="http://www.w3.org/1999/xhtml"
                xmlns:rdf="http://www.w3.org/1999/02/22-rdf-syntax-ns#"
                xmlns:rdfs="http://www.w3.org/2000/01/rdf-schema#"
                xmlns:mca="http://vocab.bris.ac.uk/mca/registry#"
				exclude-result-prefixes="html rdf rdfs mca">
	
	<xsl:output encoding="utf-8" indent="yes"/>
	
	<xsl:param name="uri_base" select="'mca://data/graph/openday/exhibition/'"/>
	
	<!-- match the root -->
	<xsl:template match="/">
		<rdf:RDF>
			<xsl:apply-templates select="/html:html/html:body/html:div/html:div[@id='uobcms-content']"/>
		</rdf:RDF>
	</xsl:template>
	
	<!-- the main content of the page -->
	<xsl:template match="/html:html/html:body/html:div/html:div[@id='uobcms-content']">
		<xsl:apply-templates select="html:h2"/>
	</xsl:template>	
	
	<!-- content under each h2 will represent an RDF resource  -->
	<xsl:template match="html:h2">

		<xsl:param name="uri_id" select="./@id"/>
		
		<rdf:Description rdf:about="{$uri_base}{$uri_id}/">
			<mca:hasHtmlFragment>
				<xsl:variable name="header" select="."/>
				<xsl:text disable-output-escaping="yes">&lt;</xsl:text>
        		<xsl:value-of select="concat('!','[','CDATA','[')"/>
					<div id="openday-content">
						<xsl:apply-templates mode="found" select="following-sibling::html:p[preceding-sibling::html:h2[1] = $header]"/>
					</div>
				<xsl:value-of select="concat(']',']')"/>
		    	<xsl:text disable-output-escaping="yes">&gt;</xsl:text>
			</mca:hasHtmlFragment>		
		</rdf:Description>
				
	</xsl:template>
	
	<xsl:template match="html:p[@class='screen']" mode="found"></xsl:template>
	
	<xsl:template match="html:p" mode="found">
		<p><xsl:apply-templates /></p>
	</xsl:template>
	
	<xsl:template match="html:a">
		<xsl:choose>
			<xsl:when test="starts-with('/', @href)">
		    	<a href="{@href}"><xsl:value-of select="."/></a>
		  	</xsl:when>
		  <xsl:otherwise>
		   	<a href="http://www.bristol.ac.uk{@href}"><xsl:value-of select="."/></a> (<em>Link not optimised for mobile devices</em>)
		  </xsl:otherwise>
		</xsl:choose>
	</xsl:template>

</xsl:stylesheet>