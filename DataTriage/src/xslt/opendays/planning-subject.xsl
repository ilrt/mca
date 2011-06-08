<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0"
                xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:html="http://www.w3.org/1999/xhtml"
                xmlns:rdf="http://www.w3.org/1999/02/22-rdf-syntax-ns#"
                xmlns:rdfs="http://www.w3.org/2000/01/rdf-schema#"
                xmlns:mca="http://vocab.bris.ac.uk/mca/registry#"
				exclude-result-prefixes="html rdf rdfs mca">
	
	<xsl:output encoding="utf-8"/>
	
	<xsl:param name="uri" select="'mca://data/graph/openday/planning-subject.rdf'"/>
	
	<!-- match the root -->
	<xsl:template match="/">
		<xsl:apply-templates select="/html:html/html:body/html:div/html:div[@id='uobcms-content']"/>
	</xsl:template>
	
	<!-- the main content of the page -->
	<xsl:template match="/html:html/html:body/html:div/html:div[@id='uobcms-content']">
		<xsl:apply-templates select="html:div/html:h2[@id='subject']"/>
	</xsl:template>	
	
	<!-- Registration tent -->
	<xsl:template match="html:div/html:h2[@id='subject']">
		<rdf:Description rdf:about="{$uri}">
			<mca:hasHtmlFragment>
				<xsl:text disable-output-escaping="yes">&lt;</xsl:text>
        		<xsl:value-of select="concat('!','[','CDATA','[')"/>
					<div id="openday-content">
						<p>Throughout the day, academic departments will be hosting a series of
						sessions that you will need to book in advance. Details of the sessions 
						can be found on our subject sessions page. Your pre-booked subject sessions
						will be listed on your booking letter.</p>
						<p>Staff will be on hand to answer in-depth questions at the subject displays.</p>
					</div>
				<xsl:value-of select="concat(']',']')"/>
		    	<xsl:text disable-output-escaping="yes">&gt;</xsl:text>
			</mca:hasHtmlFragment>
		</rdf:Description>
	</xsl:template>

</xsl:stylesheet>