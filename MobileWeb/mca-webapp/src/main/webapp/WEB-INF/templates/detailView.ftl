<#include "includes/header.ftl"/>
<#include "includes/breadcrumbWithTitle.ftl"/>

<#if resource['geo:lat']??>
    <p>There is a lat, so we want to display a map somewhere</p>
</#if>

<div id="content">
<p><@Label resource=resource/></p>
<#if resource['vcard:ADR']??>
    <p class="contact-address">${resource['vcard:ADR']?first}</p>
</#if>
</div>
<#include "includes/footer.ftl"/>