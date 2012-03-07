<#if resource['mcageo:hasSpaces']??>
<strong>Spaces:</strong> ${resource['mcageo:hasSpaces']?first}<br/>
</#if>
<#if resource['mcageo:hasOperatingHours']??>
<strong>Operating Hours:</strong> ${resource['mcageo:hasOperatingHours']?first}<br/>
</#if>
<#if resource['mcageo:hasDisabledBays']??>
<strong>Disabled Bays:</strong> ${resource['mcageo:hasDisabledBays']?first}<br/>
</#if>
<#if resource['vcard:ADR']??>
<strong>Address:</strong> ${resource['vcard:ADR']?first}<br/>
</#if>