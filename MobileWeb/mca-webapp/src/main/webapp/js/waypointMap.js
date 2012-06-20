/**

 Display directions via way points.

 Expected parameter values:

 mapElementId            the ID of the HTML element to hold the map
 pUrl                    a URL that will return JSON of the points
 contextPath             the deployed context path

 Author: Mike Jones (mike.a.jones@bristol.ac.uk)

 **/
var map;
var points = new Array(); // holds stop markers
var pointsUrl;
var context;

var initializeMap = function(mapElementId, originLat, originLon, pUrl, contextPath) {

    pointsUrl = pUrl;
    context = contextPath;

    window.onload = function() {

        var latlng = new google.maps.LatLng(originLat, originLon);
        var myOptions = {
            zoom: 16,
            center: latlng,
            disableDefaultUI: true,
            mapTypeId: google.maps.MapTypeId.ROADMAP
        };
        map = new google.maps.Map(document.getElementById(mapElementId), myOptions);
        map.setCenter(latlng);

        // do the markers
        getPoints(pointsUrl, map);

        // test - restrict zoom out
        google.maps.event.addListener(map, 'zoom_changed', function() {
            if (map.getZoom() < 16) {
                map.setZoom(16);
            }
        });

    }
};

var overlayPoints = function(map, points) {

    var path = new google.maps.Polyline({
        path: points,
        strokeColor: "#000000",
        strokeOpacity: 1.0,
        strokeWeight: 2
    });

    path.setMap(map);

    var start =  marker = new google.maps.Marker({
        position: points[0]
    });

    start.setMap(map);

    var end =  marker = new google.maps.Marker({
        position: points[points.length - 1]
    });

    end.setMap(map);

};

//ajax request for marker data
var getPoints = function(url, map) {

    var xmlhttp;

    if (window.XMLHttpRequest) {

        // code for IE7+, Firefox, Chrome, Opera, Safari
        xmlhttp = new XMLHttpRequest();

    } else {

        // code for IE6, IE5
        xmlhttp = new ActiveXObject("Microsoft.XMLHTTP");
    }

    xmlhttp.onreadystatechange = function() {



        if (xmlhttp.readyState == 4) {

            // populate the markers array
            createPoints(xmlhttp.responseText, map);

        }

    }

    // make the request
    xmlhttp.open("GET", url, true);
    xmlhttp.setRequestHeader("accept", "application/json");
    xmlhttp.send(null);

}

function compare(a,b) {

    var _a = parseFloat(a.order);
    var _b = parseFloat(b.order);

    if (_a < _b)
        return -1;
    if (_a >  _b)
        return 1;
    return 0;
}

// intialise the markers
var createPoints = function(markerJson, map) {

    // slurp the incoming json
    var m = eval('(' + markerJson + ')');

    var pointData = m.markers;

    pointData.sort(compare);

    for (var i = 0; i < pointData.length; i++) {
        var point = new google.maps.LatLng(pointData[i].lat, pointData[i].lng);
        points[i] = point;
    }
    overlayPoints(map, points);
};


