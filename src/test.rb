require 'postcode_lookup.rb'

postcode = PostCodeLookup.new("BS305HZ").lookup

puts postcode.uri
puts postcode.latitude
puts postcode.longitude