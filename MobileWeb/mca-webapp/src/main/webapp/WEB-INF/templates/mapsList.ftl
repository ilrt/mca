<#include "includes/header.ftl"/>

<#include "includes/breadcrumbWithTitle.ftl"/>

<#assign label><@Label resource/></#assign>
<@Title label="${label}" />

<#if resource['mca:hasItem']??>
<div class="nav">
        <ul class="nav-list">
    <#list resource['mca:hasItem'] as item>
        <#assign label><@Label resource=item/></#assign>
        <li><a href="./?item=${item?url("UTF8")}"><#if item['mca:style']??><span class="${item['mca:style']?first}"></span></#if>${label}<#-- <@CountItems resource=item/>--><span class="arrow"></span></a></li>
    </#list>
    </ul>
<#else>
<p>Sorry, no news items.</p>
</#if>

<#include "includes/footer.ftl"/>