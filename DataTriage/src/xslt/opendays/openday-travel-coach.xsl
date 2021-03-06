<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0"
                xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:html="http://www.w3.org/1999/xhtml"
                xmlns:rdf="http://www.w3.org/1999/02/22-rdf-syntax-ns#"
                xmlns:rdfs="http://www.w3.org/2000/01/rdf-schema#"
                xmlns:mca="http://vocab.bris.ac.uk/mca/registry#"
				exclude-result-prefixes="html rdf rdfs mca">

				<!--
					Webpage: http://www.bristol.ac.uk/opendays/planning.html
					Command: xsltproc openday-exhibition-data.xsl exhibition.html
				-->

	<xsl:output encoding="utf-8" indent="yes"/>
	
	<xsl:param name="uri" select="'mca://data/graph/openday/travel/coach/'"/>
	
	<!-- match the root -->
	<xsl:template match="/">
		<rdf:RDF>
			<xsl:apply-templates select="/html:html/html:body/html:div/html:div[@id='uobcms-content']"/>
		</rdf:RDF>
	</xsl:template>
	
	<!-- the main content of the page -->
	<xsl:template match="/html:html/html:body/html:div/html:div[@id='uobcms-content']">
		<rdf:Description rdf:about="{$uri}">
			<mca:hasHtmlFragment>
				<xsl:variable name="header" select="."/>
				<xsl:text disable-output-escaping="yes">&lt;</xsl:text>
        		<xsl:value-of select="concat('!','[','CDATA','[')"/>
					<div id="openday-content">
						<p>Marlborough Street Bus Station is situated in the centre of Bristol close to the University precinct.</p>
						<xsl:apply-templates select="html:p"/>
					</div>
				<xsl:value-of select="concat(']',']')"/>
				<xsl:text disable-output-escaping="yes">&gt;</xsl:text>
			</mca:hasHtmlFragment>		
		</rdf:Description>
	</xsl:template>	
	
	<xsl:template match="html:p">
		<xsl:if test="position() = 4">
			<p><xsl:value-of select="." /></p>
	    </xsl:if>
	</xsl:template>

</xsl:stylesheet>