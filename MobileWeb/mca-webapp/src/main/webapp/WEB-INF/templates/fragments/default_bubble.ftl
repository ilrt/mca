<#assign content = false>
<#if resource['rdfs:label']??>
<p><strong>${resource['rdfs:label']?first}</p>
<#assign content = true>
</#if>
<#if resource['vcard:ADR']??>
<p><strong>${resource['vcard:ADR']?first}</p>
<#assign content = true>
</#if>
<#if resource['foaf:phone']??>
<p><strong>Phone:</strong> <a href='${resource['foaf:phone']?first}'>${resource['foaf:phone']?first['rdfs:label']?first}</a></p>
</#if>
<#if resource['foaf:mbox']??>
<p><strong>Email:</strong> <a href='${resource['foaf:mbox']?first}'>${resource['foaf:mbox']?first['rdfs:label']?first}</a></p>
</#if>
<#if content == false>
<p>Sorry, there are no details.</p>
</#if>

<#--<p>${resource}</p>-->