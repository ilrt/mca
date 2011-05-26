
# the jena libraries
require 'mvn:com.ibm.icu:icu4j'
require 'mvn:com.hp.hpl.jena:iri'
require 'mvn:xml-apis:xml-apis'
require 'mvn:xerces:xercesImpl'
require 'mvn:org.slf4j:slf4j-api'
require 'mvn:log4j:log4j'
require 'mvn:org.slf4j:slf4j-log4j12'
require 'mvn:com.hp.hpl.jena:jena'

require 'namespaces.rb'

include_class 'com.hp.hpl.jena.rdf.model.ModelFactory'
include_class 'com.hp.hpl.jena.vocabulary.RDFS'
include_class 'com.hp.hpl.jena.vocabulary.RDF'
include_class 'com.hp.hpl.jena.vocabulary.VCARD'
include_class 'com.hp.hpl.jena.datatypes.xsd.XSDDatatype'
include_class 'java.io.PrintStream'

class GenerateRDF

  def generate_location_rdf(address_list, type)

    m = ModelFactory.createDefaultModel();
    m.setNsPrefixes($prefixes)
    
    address_list.each do |item|
      r = m.createResource(item.uri)
      m.add(m.createStatement(r, RDFS.label, item.name))
      if (item.point != nil)
        r.addProperty(m.createProperty($wgs84_ns + "lat"),
                      m.createTypedLiteral(item.point.latitude, XSDDatatype::XSDdouble));
        r.addProperty(m.createProperty($wgs84_ns + "long"),
                      m.createTypedLiteral(item.point.longitude, XSDDatatype::XSDdouble));
      end
      r.addProperty(RDF.type, m.createProperty($wgs84_ns + "Point"))
      r.addProperty(VCARD::ADR, item.address)
      r.addProperty(RDF.type, m.createResource($mcageo_ns + "amenity"))
      r.addProperty(RDF.type, m.createResource($mcageo_ns + type))
      r.addProperty(m.createProperty($mcageo_ns + "hasTag"), type)
    end

    m.write(PrintStream.new(System.out, true, "UTF-8"))
  end

end