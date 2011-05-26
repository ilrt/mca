require 'java'
require 'rubygems'
require 'digest/md5'

require 'postcode_lookup.rb'
require 'namespaces.rb'
require 'generate_rdf.rb'

class LibraryLocation

    def initialize(uri_prefix, name, address, point)
        @uri_prefix = uri_prefix
        @name = name
        @address = address
        @point = point
    end

    # create a uri with prefix and hash of the address
    def uri
        uri_prefix + Digest::MD5.hexdigest(self.address)
    end

    attr_reader :uri_prefix, :name, :street, :address, :point
end

class ParseFile

    @@prefix = "mca://bcc/libraries/"
    @@address_list = []

    def parse
        process_file
        GenerateRDF.new.generate_location_rdf(@@address_list, "bcc_library")
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

        # get the details from each line
        elements = line.split(';').each { |e| e.strip! }
        name = elements[0]
        address = elements[1]
        phone = elements[2]
        fax = elements[3]
        email = elements[4]
      
        # get the postcode from the address
        temp = address.split(',').each { |e| e.strip! }
        postcode = temp.last
        
        # remove the period
        if (postcode.end_with?("."))
          postcode = postcode[0..-2]
        end
        
        #lookup the latitude and longitude from the postcode
        point = PostCodeLookup.new(postcode.sub(/ /, '')).lookup
        location = LibraryLocation.new($bcc_library, name, address, point)

        @@address_list << location
    end

end

ParseFile.new.parse