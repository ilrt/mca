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

require 'postcode_lookup.rb'
require 'generate_rdf.rb'

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
        GenerateRDF.new.generate_location_rdf(@@address_list, "bopen_wireless")
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
end

ParseFile.new.parse