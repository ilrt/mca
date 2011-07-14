<#compress>
<#include "includes/header.ftl"/>

<#include "includes/breadcrumbWithTitle.ftl"/>

<#if resource['mca:hasEventItem']??>
<nav>
    <ul>
        <#list resource['mca:hasEventItem'] as item>
            <#assign label=item['ical:summary']?first/>
            <#if item['mca:style']??><#assign style>${item['mca:style']?first}</#assign><#else><#assign style>standard</#assign></#if>
            <li class="${style}"><a href="./?item=${item['ical:uid']?first?url("UTF8")}">${label} <span><@EventDate item['ical:dtstart']?first /></span></a></li>
         </#list>
    </ul>
</nav>

<#--
<div class="calendarlinks">
    <#if resource['mca:htmlLink']??>
    <div class="calendarlink"><a href="${resource['mca:htmlLink']?first}"><img src="${contextPath}/images/htmlcalicon.png" alt="View on the Web"/><br/>View on web</a></div>
    </#if>
    <#if resource['mca:icalLink']??>
    <div class="calendarlink"><a href="${resource['mca:icalLink']?first}"><img src="${contextPath}/images/icalicon.png" alt="iCal File"/><br/>.ics file</a></div>
    </#if>
</div>
-->

<#else>
<div id="content">
<p>Sorry, there are no upcoming events.</p>
</div>
</#if>

<#include "includes/footer.ftl"/>
</#compress>