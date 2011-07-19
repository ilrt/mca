<#compress>
<#include "includes/header.ftl"/>
<#include "includes/breadcrumbWithTitle.ftl"/>
<#assign message>Sorry, not data is available</#assign>
<#if resource['mca:hasSource']??>
    <#if resource['mca:hasSource']?first['mca:hasHtmlFragment']??>
        <#assign message>${resource['mca:hasSource']?first['mca:hasHtmlFragment']?first}</#assign>
    </#if>
<#elseif resource['rdfs:seeAlso']??>
    <#if resource['rdfs:seeAlso']?first['mca:hasHtmlFragment']??>
        <#assign message>${resource['rdfs:seeAlso']?first['mca:hasHtmlFragment']?first}</#assign>
    </#if>
</#if>
<div id="content">
${message}
</div>
<#include "includes/footer.ftl"/>
</#compress>