<#if onServerSide??>
<#assign js="${onClick};sendClick('${key}')">
<#else>
<#assign js="${onClick}">
</#if>
<button onclick="${js};return false;" class="btn btn-${type} ms-1">${label}</button>