<#-- macros for extracting common rdf values-->
<#-- display a default label for a resource -->
<#macro Label resource>
    <#compress>
    <#if resource['rdfs:label']??>
        ${resource['rdfs:label']?first?html}
    <#elseif resource['foaf:name']??>
        ${resource['foaf:name']?first?html}
    <#elseif resource['dc:title']??>
        ${resource['dc:title']?first?html}
    <#elseif resource['rss:title']??>
        ${resource['rss:title']?first?html}
    <#else>
        Untitled
    </#if>
    </#compress>
</#macro>

<#macro NavLabel label>
    <#compress>
    <#if (label?length > 23)>
        ${label?substring(0,23)} ...
    <#else>
        ${label}
    </#if>
    </#compress>
</#macro>

<#macro Title label>
    <h1 id="title">${label!"Untitled Page"}</h1>
</#macro>

<#macro ParseXsdDate value>
<#assign length>${value?length}</#assign>
${value?substring(0, length?number - 3)}${value?substring(length?number - 2, length?number)}
</#macro>

<#macro EventDate value>
<#compress>
<#assign length>${value?length}</#assign>
<#assign temp>${value?substring(0, length?number - 3)}${value?substring(length?number - 2, length?number)}</#assign>
${temp?datetime("yyyy-MM-dd\'T\'HH:mm:ssZ")?string('d MMM yyyy')}&nbsp;<#if temp?datetime("yyyy-MM-dd\'T\'HH:mm:ssZ")?string('HH:mm') != "00:00">${temp?datetime("yyyy-MM-dd\'T\'HH:mm:ssZ")?string('HH:mm')}</#if>
</#compress>
</#macro>

<#-- Email address with mailto link -->
<#macro Email value><a href="mailto:${value}">${value}</a></#macro>

<#-- Phone number with tel link and label -->
<#macro Phone value>
<#compress>
<a href="${value}"><#if value['rdfs:label']??>${value['rdfs:label']?first}<#else>${value}</#if></a>
</#compress>
</#macro>

<#-- count items -->
<#macro CountItems resource>
<#compress>
<#if resource['rdfs:seeAlso']??>
<#assign count>${resource['rdfs:seeAlso']?first?size-1}</#assign>
<#if count?number <= 0>(0)<#else>(${count})</#if>
</#if>
</#compress>
</#macro>

<#macro Description resource><#if resource['dc:description']??>${resource['dc:description']?first}</#if></#macro>

<#macro Ellipses value number><#if value?length &gt;= number>${value?substring(0,20)}...<#else>${value}</#if></#macro>

<#macro BreadCrumbLabel resource>
<#compress>
    <#if resource['mca:shortLabel']??>
        ${resource['mca:shortLabel']?first}
        <#else>
        <@Label resource=resource/>
    </#if>
</#compress>
</#macro>

<#macro BreadCrumbWithParent resource>
    <#if resource['mca:hasParent']?first != '${domain}'>
        <li id="hometrail"><a class="" href="${contextPath}/"><span>Home</span></a></li>
        <li><a class="parent" href="${resource['mca:hasParent']?first}"><span><@BreadCrumbLabel resource=resource['mca:hasParent']?first/></span></a></li>
    <#else>
        <li id="hometrail"><a class="parent" href="${contextPath}/"><span>Home</span></a></li>
    </#if>
</#macro>

<#macro BreadCrumbWithGrandParent resource>
    <#-- we can ignore the homepage since we provide a link anyway -->
    <#if resource['mca:hasParent']?first['mca:hasParent']?first != '${domain}'>
        <li id="hometrail"><a class="" href="${contextPath}/"><span>Home</span></a></li>
        <li><a href="${resource['mca:hasParent']?first['mca:hasParent']?first}"><span>&#8230;</span></a></li>
        <li><a class="parent" href="${resource['mca:hasParent']?first}"><span><@BreadCrumbLabel resource=resource['mca:hasParent']?first/></span></a></li>
    <#else>
        <@BreadCrumbWithParent resource=resource/>
    </#if>
</#macro>

<#macro BreadCrumb resource>
    <#-- check we have a parent -->
    <#if resource['mca:hasParent']??>
        <#-- check to see if we have a parent for the parent ... #-->
        <#if resource['mca:hasParent']?first['mca:hasParent']??>
            <@BreadCrumbWithGrandParent resource=resource/>
        <#else>
            <@BreadCrumbWithParent resource=resource/>
        </#if>
    </#if>
</#macro>

<#macro Main resource>
<#compress>
<#assign style>main</#assign>
<#if resource['rdf:type']??>
    <#if resource['rdf:type']?first == 'http://vocab.bris.ac.uk/mca/registry#ActiveMapSource' || resource['rdf:type']?first == 'http://vocab.bris.ac.uk/mca/registry#KmlMapSource'>
        <#assign style>mapcontainer</#assign>
    </#if>
</#if>
<div id="${style}" role="main">
</#compress>
</#macro>


<#macro Shortcuts resource>
    <#if !resource['mca:hasParent']??>
        <div id="shortcuts"></div>
    </#if>
</#macro>

<#macro Shortcut resource>
    <#-- check we have a parent -->
    <#if resource['mca:hasParent']??>
        <#-- check to see if we have a parent for the parent ... #-->
        <#if resource['mca:hasParent']?first['mca:hasParent']??>
            <div id="shortcut"></div>
        </#if>
    </#if>
</#macro>

<#macro ShortCutKey resource>
<#compress>
<#assign key>${resource}</#assign>
<#if resource['rdf:type']??>
<#if resource['rdf:type']?first == 'http://vocab.bris.ac.uk/mca/registry#NewsItem'>
    <#assign key>${resource['mca:hasParent']?first}?item=${resource}</#assign>
<#elseif resource['rdf:type']?first == 'http://www.w3.org/2002/12/cal/ical#Vevent'>
    <#assign key>${resource['mca:hasParent']?first}?item=${resource['ical:uid']?first}</#assign>
<#elseif resource['rdf:type']?first == 'http://vocab.bris.ac.uk/mca/geo#vcrooms'>
    <#assign val>${resource?substring(domain?length, resource?length)}</#assign>
    <#assign key>${resource['mca:hasParent']?first}?id=${val}</#assign>
</#if>
${key?xml}
</#if>
</#compress>
</#macro>

<#macro ShortCutValue resource>
<#compress>
<#assign value><@Label resource=resource/></#assign>
<#--
<#if resource['rdf:type']?first == 'http://www.w3.org/2002/12/cal/ical#Vevent'>
    <#if resource['mca:hasEventItem']??>
        <#if resource['mca:hasEventItem']?first['rdfs:label']??>
            <#assign value><@Label resource=resource['mca:hasEventItem']?first/></#assign>
        </#if>
    </#if>
</#if>
-->
${value}
</#compress>
</#macro>