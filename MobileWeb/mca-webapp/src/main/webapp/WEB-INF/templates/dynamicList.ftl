<#compress>
<#include "includes/header.ftl"/>
<#include "includes/breadcrumbWithTitle.ftl"/>
<#include "includes/macro.ftl"/>
<nav>
    <ul>
        <#list resource['mca:hasItem'] as item>
            <#assign label><@Label resource=item/></#assign>
            <#if item['mca:style']??><#assign style>${item['mca:style']?first}</#assign><#else><#assign style>standard</#assign></#if>
            <li class="${style}"><a href="?id=${item?substring(domain?length, item?length)}">${label} <#if item['vcard:ADR']??><span>(${item['vcard:ADR']?first})</span></#if></a></li>
            <#--
            <li class="${style}"><a href="${item}">${label} <#if item['dc:description']??><span>(${item['dc:description']?first})</span></#if></a></li>
            -->
        </#list>
    </ul>
</nav>
<#include "includes/footer.ftl"/>
</#compress>