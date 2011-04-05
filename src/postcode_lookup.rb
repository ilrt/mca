require 'net/http'
require 'CGI'
require 'java'
require 'rubygems'

# the jena libraries
require 'mvn:com.ibm.icu:icu4j'
require 'mvn:com.hp.hpl.jena:iri'
require 'mvn:xml-apis:xml-apis'
require 'mvn:xerces:xercesImpl'
require 'mvn:org.slf4j:slf4j-api'
require 'mvn:log4j:log4j'
require 'mvn:org.slf4j:slf4j-log4j12'
require 'mvn:com.hp.hpl.jena:jena'

include_class 'com.hp.hpl.jena.rdf.model.ModelFactory'
include_class 'java.lang.System'
include_class 'java.io.StringReader'


class PostCodeLookup

    @@postcode_uri= 'http://data.ordnancesurvey.co.uk/id/postcodeunit/'
    @@sparql_domain = 'api.talis.com'
    @@sparl_port = 80
    @@sparql_query = "/stores/ordnance-survey/services/sparql?query="
    @@wgs84_ns = 'http://www.w3.org/2003/01/geo/wgs84_pos'

    def initialize(postcode)
        @postcode = postcode;
    end

    def encode_postcode_query
        CGI::escape("DESCRIBE <" + uri + ">")
    end

    def uri
        "#@@postcode_uri#{@postcode}"
    end

    def lookup()

        query = encode_postcode_query

        headers = {
                "Accept" => "application/rdf+xml"
        }

        response = Net::HTTP.new(@@sparql_domain, @@sparl_port)
        response = response.get("#@@sparql_query#{query}", headers)

        if response.message == "OK"
            m = ModelFactory.createDefaultModel()
            m.read(StringReader.new(response.body), nil)

            r = m.getResource(self.uri)
            lat = r.getProperty(m.createProperty("#@@wgs84_ns#lat")).getLiteral().getLexicalForm()
            long = r.getProperty(m.createProperty("#@@wgs84_ns#long")).getLiteral().getLexicalForm()

            PostCodePoint.new(self.uri, lat, long)
        end

    end

end

class PostCodePoint

    def initialize(uri, latitude, longitude)
        @uri = uri
        @latitude = latitude
        @longitude = longitude
    end

    attr_reader :uri, :latitude, :longitude

end