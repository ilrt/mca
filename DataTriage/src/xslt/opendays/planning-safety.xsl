<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0"
                xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:html="http://www.w3.org/1999/xhtml"
                xmlns:rdf="http://www.w3.org/1999/02/22-rdf-syntax-ns#"
                xmlns:rdfs="http://www.w3.org/2000/01/rdf-schema#"
                xmlns:mca="http://vocab.bris.ac.uk/mca/registry#"
				exclude-result-prefixes="html rdf rdfs mca">
	
	<xsl:output encoding="utf-8"/>
	
	<xsl:param name="uri" select="'mca://data/graph/openday/planning-safety.rdf'"/>
	
	<!-- match the root -->
	<xsl:template match="/">
		<xsl:apply-templates select="/html:html/html:body/html:div/html:div[@id='uobcms-content']"/>
	</xsl:template>
	
	<!-- the main content of the page -->
	<xsl:template match="/html:html/html:body/html:div/html:div[@id='uobcms-content']">
		<xsl:apply-templates select="html:div/html:h2[@id='safety']"/>
	</xsl:template>	

	<!-- Registration tent -->
	<xsl:template match="html:div/html:h2[@id='safety']">
		<rdf:Description rdf:about="{$uri}">
			<mca:hasHtmlFragment>
				<xsl:text disable-output-escaping="yes">&lt;</xsl:text>
        		<xsl:value-of select="concat('!','[','CDATA','[')"/>
					<div id="openday-content">
						<p><xsl:value-of select="following-sibling::html:p[1]"/></p>
						<p><xsl:value-of select="following-sibling::html:p[2]"/></p>
						<p><xsl:value-of select="following-sibling::html:p[3]"/></p>
					</div>
				<xsl:value-of select="concat(']',']')"/>
		    	<xsl:text disable-output-escaping="yes">&gt;</xsl:text>
			</mca:hasHtmlFragment>
		</rdf:Description>
	</xsl:template>

</xsl:stylesheet>