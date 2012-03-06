<#include "macro.ftl">
<nav>
    <ul>
    <#list resource['mca:hasItem'] as item>
        <#assign label><@Label resource=item/></#assign>
        <#if item['mca:style']??><#assign style>${item['mca:style']?first}</#assign><#else><#assign style>standard</#assign></#if>
        <li class="${style}"><a href="${item}">${label} <#if item['dc:description']??><span>(${item['dc:description']?first})</span></#if></a></li>
    </#list>
    </ul>
</nav>