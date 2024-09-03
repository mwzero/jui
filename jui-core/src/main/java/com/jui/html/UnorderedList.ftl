<ul class="nav flex-column">
	<#list items as item>
    <li class="nav-item">
    	<#if item.link??>
        	<a class="nav-link" href="#" data-url="${(item.link)!"#"}">
        <#else>
        	<a class="nav-link" href="#" data-content="${(item.content)!"#"}">
        </#if>
            <i class="bi bi-${item.icon}-fill"></i> <span> ${item.label}</span>
        </a>
    </li>
    </#list>
</ul>