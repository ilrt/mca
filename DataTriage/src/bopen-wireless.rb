# bopen-wireless.rb
#
# Script to take a comma separated list of addresses that provide a B-Open Wireless access
# point and turn to RDF. The postcode is used as a look-up to find a latitude and
# longitude.
#
# Author: Mike Jones (mike.a.jones@bristol.ac.uk)
#
# (c)2011 University of Bristol

require 'java'
require 'rubygems'
require 'digest/md5'

# the jena libraries
require 'mvn:com.ibm.icu:icu4j'
require 'mvn:com.hp.hpl.jena:iri'
require 'mvn:xml-apis:xml-apis'
require 'mvn:xerces:xercesImpl'
require 'mvn:org.slf4j:slf4j-api'
require 'mvn:log4j:log4j'
require 'mvn:org.slf4j:slf4j-log4j12'
require 'mvn:com.hp.hpl.jena:jena'

require 'postcode_lookup.rb'
require 'namespaces.rb'

include_class 'com.hp.hpl.jena.rdf.model.ModelFactory'
include_class 'com.hp.hpl.jena.vocabulary.RDFS'
include_class 'com.hp.hpl.jena.vocabulary.RDF'
include_class 'com.hp.hpl.jena.vocabulary.VCARD'
include_class 'com.hp.hpl.jena.datatypes.xsd.XSDDatatype'
include_class 'java.io.PrintStream'

class Location

    def initialize(uri_prefix, name, street, postcode, point)
        @uri_prefix = uri_prefix
        @name = name
        @street = street
        @postcode = postcode
        @point = point
    end

    # create a uri with prefix and hash of the address
    def uri
        uri_prefix + Digest::MD5.hexdigest(self.address)
    end

    # return address (name, street, postcode)
    def address
        self.name + ", " + self.street + ", " + self.postcode
    end

    attr_reader :uri_prefix, :name, :street, :postcode, :point
end

class ParseFile

    @@prefix = "mca://bcc/wireless/"
    @@address_list = []

    def parse
        process_file
        generate_rdf
    end

    # process the file
    def process_file

        counter = 1
        while line = gets
            if (counter > 1) #ignore the first line (header)
                process_line(line)
                sleep 2 # throttle the requests
            end
            counter += 1
        end
    end

    #process each line in the file
    def process_line(line)

        elements = line.split(',').each { |e| e.strip! }

        name = elements[4]
        street = elements[5]
        postcode = elements[0]

        point = PostCodeLookup.new(postcode.sub(/ /, '')).lookup

        location = Location.new(@@prefix, name, street, postcode, point)

        @@address_list << location
    end

    def generate_rdf

        m = ModelFactory.createDefaultModel();
        m.setNsPrefixes($prefixes)

        @@address_list.each do |item|
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
            r.addProperty(RDF.type, m.createResource($mcageo_ns + "bopen_wireless"))
            r.addProperty(m.createProperty($mcageo_ns + "hasTag"), "bopen_wireless")
        end

        m.write(PrintStream.new(System.out, true, "UTF-8"))
    end

end

ParseFile.new.parse