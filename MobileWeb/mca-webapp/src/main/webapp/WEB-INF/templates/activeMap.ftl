<#include "includes/header.ftl"/>

<#include "includes/breadcrumbWithTitle.ftl"/>

<#-- url used for getting information via a proxy -->
<#if resource['mca:urlStem']??>
    <#assign urlStem>${contextPath}/${resource['mca:urlStem']?first}</#assign>
<#else>
    <#assign urlStem>undefined</#assign>
</#if>  

<#-- custom icon to display on the map -->
<#if resource['mca:icon']??>
    <#assign mcaIcon>${contextPath}/${resource['mca:icon']?first}</#assign>
<#else>
    <#assign mcaIcon>${contextPath}/images/blackblank.png</#assign>
</#if>

<#if resource['mca:mapZoom']??>
    <#assign mapZoom>${resource['mca:mapZoom']?first}</#assign>
<#else>
    <#assign mapZoom>17</#assign>
</#if>

<script type="text/javascript" src="http://maps.google.com/maps/api/js?v=3.4&sensor=true"></script>
<script type="text/javascript" src="${contextPath}/js/activeMap.js"></script>
<script type="text/javascript">
initializeMap("map", ${resource['geo:lat']?first?string.computer}, ${resource['geo:long']?first?string.computer}, 10, 200, 1000, 3000, ${mapZoom}, "${urlStem}", "${mcaIcon}", "${contextPath}/${resource['mca:markers']?first}", "${contextPath}/");
</script>

<div id="searching">Searching for location ...</div>
<div id="map"></div>
<div>&nbsp;</div>

</div>

<script src="//ajax.googleapis.com/ajax/libs/jquery/1.5.1/jquery.js"></script>
<script>window.jQuery || document.write("<script src='${contextPath}/js/libs/jquery-1.5.1.min.js'>\x3C/script>")</script>
<script src="${contextPath}/js/mylibs/helper.js"></script>
<script>
MBP.scaleFix();
yepnope({
  test : Modernizr.mq('(min-width)'),
  nope : ['js/libs/respond.min.js']
});
 </script>
<#if googleAnalyticsKey??>
<script type="text/javascript">
  var _gaq = _gaq || [];
  _gaq.push(['_setAccount', '${googleAnalyticsKey}']);
  _gaq.push(['_trackPageview']);

  (function() {
    var ga = document.createElement('script'); ga.type = 'text/javascript'; ga.async = true;
    ga.src = ('https:' == document.location.protocol ? 'https://ssl' : 'http://www') + '.google-analytics.com/ga.js';
    var s = document.getElementsByTagName('script')[0]; s.parentNode.insertBefore(ga, s);
  })();
</script>
</#if>
</body>
</html>
