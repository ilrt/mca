<#include "includes/header.ftl"/>

<#include "includes/breadcrumbWithoutTitle.ftl"/>

<div id="content">

    <h2>${resource['rdfs:label']?first}</h2>

    <div class="contactDetails">
        <ul>
            <#if resource['foaf:phone']??>
            <li class="phoneContact"><a href="${resource['foaf:phone']?first}">${resource['foaf:phone']?first['rdfs:label']?first}</a></li>
            </#if>
            <#if resource['foaf:mbox']??>
            <li class="emailContact"><a href="mailto:${resource['foaf:mbox']?first}">${resource['foaf:mbox']?first}</a></li>
            </#if>
            <#if resource['vcard:ADR']??>
            <li>${resource['vcard:ADR']?first}</a></li>
            </#if>
            <#if resource['foaf:homepage']??>
            <li><a href="${resource['foaf:homepage']?first}">Visit website</a></li>
            </#if>
        </ul>
    </div>
</div>
<#include "includes/footer.ftl"/>