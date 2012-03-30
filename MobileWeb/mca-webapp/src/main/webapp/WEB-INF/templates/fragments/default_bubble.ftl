<#assign content = false>
<#if resource['rdfs:label']??>
<p><strong>${resource['rdfs:label']?first}</p>
<#assign content = true>
</#if>
<#if resource['vcard:ADR']??>
<p><strong>${resource['vcard:ADR']?first}</p>
<#assign content = true>
</#if>

<#if content == false>
<p>Sorry, there are no details.</p>
</#if>

<p>${resource}</p>