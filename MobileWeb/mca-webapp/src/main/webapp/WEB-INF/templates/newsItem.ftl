<#include "includes/header.ftl"/>

<#include "includes/breadcrumbWithoutTitle.ftl"/>

    <div id="content">
        <#if resource['rss:title']??><h1>${resource['rss:title']?first}</h1></#if>
        <#if resource['dc:date']??>
            <h2 class="date">${resource['dc:date']?first?datetime("yyyy-MM-dd\'T\'HH:mm:ss'Z'")?string('dd MMMM yyyy')}</h2>
        </#if>
        <#if resource['content:encoded']??>
            ${resource['content:encoded']?first}
        <#else>
            <#if resource['rss:description']??>${resource['rss:description']?first}</#if>
        </#if>
        <#if resource['rss:link']??>
            <p><a class="more" href="${resource['rss:link']?first}">Read more...</a>
            <span class="contentWarning">(content not optimized for mobile devices)</span></p>
         </#if>
        <#if resource['mca:hasSource']??>
            <p class="note">Source: <a href="${resource['mca:hasSource']?first}">${resource['mca:hasSource']?first}</a></p>
        </#if>
    </div>
<#include "includes/footer.ftl"/>
