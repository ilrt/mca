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

<script type="text/javascript" src="http://maps.google.com/maps/api/js?v=3.4&amp;sensor=true"></script>
<script type="text/javascript" src="${contextPath}/js/waypointMap.js"></script>
<script type="text/javascript">
    initializeMap("map", ${resource['geo:lat']?first?string.computer}, ${resource['geo:long']?first?string.computer}, "${contextPath}/geo/type?uri=${resource['mca:markers']?first?url('UTF-8')}", "${contextPath}/");
</script>

<div id="map"></div>
<div>&nbsp;</div>

<#include "includes/footer.ftl"/>