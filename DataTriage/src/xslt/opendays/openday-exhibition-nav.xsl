<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0"
                xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:html="http://www.w3.org/1999/xhtml"
                xmlns:rdf="http://www.w3.org/1999/02/22-rdf-syntax-ns#"
                xmlns:rdfs="http://www.w3.org/2000/01/rdf-schema#"
                xmlns:mca="http://vocab.bris.ac.uk/mca/registry#"
				xmlns:dc="http://purl.org/dc/elements/1.1/">
	
	<xsl:output encoding="utf-8" indent="yes"/>
	<xsl:param name="uri" select="'mca://registry/openday/exhibition/'"/>
	<xsl:param name="graph" select="'mca://data/graph/openday/openday-exhibition.rdf'"/>
	<xsl:param name="data_base" select="'mca://data/graph/openday/exhibition/'"/>
	
	<!-- match the root -->
	<xsl:template match="/">
		<rdf:RDF>
			<xsl:apply-templates select="/html:html/html:body/html:div/html:div[@id='uobcms-content']"/>
		</rdf:RDF>
	</xsl:template>
	
	<!-- the main content of the page -->
	<xsl:template match="/html:html/html:body/html:div/html:div[@id='uobcms-content']">
		<xsl:call-template name="parent"/>
		<xsl:call-template name="children"/>
	</xsl:template>	


	<xsl:template name="parent">
		<rdf:Description rdf:about="mca://registry/openday/exhibition/">
			<rdf:type rdf:resource="http://vocab.bris.ac.uk/mca/registry#Group"/>
			<rdfs:label>Exhibition</rdfs:label>
		    <dc:description>Open Day exhibition</dc:description>
			<xsl:for-each select="html:h2">
				<mca:hasItem rdf:resource="{$uri}{@id}/"/>
			</xsl:for-each>
		    <mca:order rdf:datatype="http://www.w3.org/2001/XMLSchema#integer">1</mca:order>
		    <mca:template rdf:resource="template://navigation.ftl"/>
		    <mca:style rdf:datatype="http://www.w3.org/2001/XMLSchema#string">events</mca:style>
		</rdf:Description>
	</xsl:template>	

	<xsl:template name="children">
		<xsl:for-each select="html:h2">
			<rdf:Description rdf:about="{$uri}{@id}/">
				<rdf:type rdf:resource="http://vocab.bris.ac.uk/mca/registry#HtmlFragment"/>
				<rdfs:label><xsl:value-of select="."/></rdfs:label>
				<dc:description>Harvested data: <xsl:value-of select="."/></dc:description>
				<mca:hasSource rdf:resource="{$data_base}{@id}/"/>
			    <rdfs:seeAlso rdf:resource="{$graph}"/>
			    <mca:template rdf:resource="template://htmlFragment.ftl"/>
			    <mca:order rdf:datatype="http://www.w3.org/2001/XMLSchema#integer"><xsl:value-of select="position()" /></mca:order>
			</rdf:Description>
		</xsl:for-each>
	</xsl:template>
	
	
</xsl:stylesheet>