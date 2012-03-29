<#if resource['rdfs:label']??>
<p><strong>${resource['rdfs:label']?first}<br/>
</#if>
<#if resource['VCARD:ADR']??>
    <p><strong>${resource['VCARD:ADR']?first}<br/>
</#if>