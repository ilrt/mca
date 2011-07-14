<#compress>
<#include "includes/header.ftl"/>

<#include "includes/breadcrumbWithTitle.ftl"/>

<#if resource['mca:hasNewsItem']??>
<nav>
    <ul>
        <#list resource['mca:hasNewsItem']?reverse as item>
            <#assign label=item['rss:title']?first/>
            <#if item['mca:style']??><#assign style>${item['mca:style']?first}</#assign><#else><#assign style>standard</#assign></#if>
            <li class="${style}"><a href="./?item=${item?url("UTF8")}">${label}</span></a></li>
        </#list>
    </ul>
</nav>
<#else>
    <div id='content'>
        <#if resource['mca:noDataMessage']??>
            <p>${resource['mca:noDataMessage']?first}</p>
        <#else>
            <p>Sorry, there is no data.</p>
        </#if>
    </div>
</#if>

<#include "includes/footer.ftl"/>
</#compress>