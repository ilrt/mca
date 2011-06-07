<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0"
                xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:html="http://www.w3.org/1999/xhtml"
                xmlns:rdf="http://www.w3.org/1999/02/22-rdf-syntax-ns#"
                xmlns:rdfs="http://www.w3.org/2000/01/rdf-schema#"
                xmlns:mca="http://vocab.bris.ac.uk/mca/registry#"
				exclude-result-prefixes="html rdf rdfs mca">
	
	<xsl:output encoding="utf-8"/>
	
	<xsl:param name="uri" select="'mca://data/graph/openday/planning-union.rdf'"/>
	
	<!-- match the root -->
	<xsl:template match="/">
		<xsl:apply-templates select="/html:html/html:body/html:div/html:div[@id='uobcms-content']"/>
	</xsl:template>
	
	<!-- the main content of the page -->
	<xsl:template match="/html:html/html:body/html:div/html:div[@id='uobcms-content']">
		<xsl:apply-templates select="html:div/html:h2[@id='union']"/>
	</xsl:template>	

	<!-- Registration tent -->
	<xsl:template match="html:div/html:h2[@id='union']">
		<rdf:Description rdf:about="{$uri}">
			<mca:hasHtmlFragment>
				<xsl:text disable-output-escaping="yes">&lt;</xsl:text>
        		<xsl:value-of select="concat('!','[','CDATA','[')"/>
					<div id="openday-content">
						<p>The Students' Union is an important part of university life. Run by students 
						for students, the Union provides an opportunity to try new things and get involved
						in the many activities and sports on offer. As well as numerous clubs, societies and
						charity events, the Union runs the Student Council, Athletic Union, Student Community
						Action, Student Media and Student Development activities. Sabbatical officers will be 
						available at the exhibition throughout the day to give you information about student
						societies and how you can get involved.</p>
					</div>
				<xsl:value-of select="concat(']',']')"/>
		    	<xsl:text disable-output-escaping="yes">&gt;</xsl:text>
			</mca:hasHtmlFragment>
		</rdf:Description>
	</xsl:template>

</xsl:stylesheet>