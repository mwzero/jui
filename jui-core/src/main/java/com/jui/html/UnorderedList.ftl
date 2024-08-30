<h6 class="sidebar-heading d-flex justify-content-between align-items-center px-3 mt-4 mb-1 text-muted">
  <span>${label}</span>
  <a class="link-secondary" href="#" aria-label="Add a new report">
    <span data-feather="plus-circle"></span>
  </a>
</h6>
<ul class="nav flex-column">

	<#list items as item>
		<li class="nav-item">
		    <a class="nav-link active" aria-current="page" href="javascript:${(item.link)!"#"}">
		      <span data-feather="${item.icon}"></span>
		      ${item.label}
		    </a>
	  	</li>
	</#list>
  
</ul>