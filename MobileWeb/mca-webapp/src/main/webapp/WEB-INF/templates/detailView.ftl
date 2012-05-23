<#include "includes/header.ftl"/>
<#include "includes/breadcrumbWithTitle.ftl"/>

<div id="content">

    <p><@Label resource=resource/></p>

<#if resource['geo:lat']??>
    <#assign lat>${resource['geo:lat']?first}</#assign>
    <#assign long>${resource['geo:long']?first}</#assign>
    <p><img src="http://maps.googleapis.com/maps/api/staticmap?center=${lat},${long}&markers=${lat},${long}&zoom=16&size=300x300&sensor=true"/></p>
</#if>

<#if resource['vcard:ADR']??>
    <p class="contact-address">${resource['vcard:ADR']?first}</p>
</#if>
</div>
<#include "includes/footer.ftl"/>