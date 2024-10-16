<!doctype html>
<html lang="en">
  <head>
    <meta charset="utf-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <meta name="description" content="">
    <meta name="author" content="Maurizio Farina">
    <title>JUI</title>

    <link href="/css/bootstrap.min.css" rel="stylesheet">

    <style>
      .bd-placeholder-img {
        font-size: 1.125rem;
        text-anchor: middle;
        -webkit-user-select: none;
        -moz-user-select: none;
        user-select: none;
      }

      @media (min-width: 768px) {
        .bd-placeholder-img-lg {
          font-size: 3.5rem;
        }
      }
      
	    .div_main_not_active{
	        display: none;
	    }
	
	
	    .div_main_active {
	        display: block;
	    }
    </style>

    
    <!-- Custom styles for this template -->
    <link href="/css/dashboard.css" rel="stylesheet">
    
    <!--leaflet -->
	<link rel="stylesheet" href="https://unpkg.com/leaflet@1.9.4/dist/leaflet.css" integrity="sha256-p4NxAoJBhIIN+hmNHrzRCf9tD/miZyoHS5obTRR9BMY=" crossorigin=""/>
	<script src="https://unpkg.com/leaflet@1.9.4/dist/leaflet.js" integrity="sha256-20nQCchB9co0qIjJZRGuk2/Z9VM+kNiyxNV1lvTlZBo=" crossorigin=""></script>
	
  </head>
  <body>
	<#if header_context??>
		<header class="navbar navbar-dark sticky-top bg-dark flex-md-nowrap p-0 shadow">
		  <a class="navbar-brand col-md-3 col-lg-2 me-0 px-3" href="#">Company name</a>
		  <button class="navbar-toggler position-absolute d-md-none collapsed" type="button" data-bs-toggle="collapse" data-bs-target="#sidebarMenu" aria-controls="sidebarMenu" aria-expanded="false" aria-label="Toggle navigation">
		    <span class="navbar-toggler-icon"></span>
		  </button>
		  <input class="form-control form-control-dark w-100" type="text" placeholder="Search" aria-label="Search">
		  <div class="navbar-nav">
		    <div class="nav-item text-nowrap">
		      <a class="nav-link px-3" href="#">Sign out</a>
		    </div>
		  </div>
		</header>
	</#if>

<div class="container-fluid">
  <div class="row">
  	<#if sidebar_context??>
  	
	    <nav id="sidebarMenu" class="col-md-3 col-lg-2 d-md-block bg-light sidebar collapse">
	      <div class="position-sticky pt-3">
	      	
			<#list sidebar_context?keys as prop>
				<#assign component=sidebar_context[prop]>
				${component.render()}
			</#list>
					
	      </div>
	    </nav>
	</#if>

	<#if sidebar_context??>	
    	<main class="col-md-9 ms-sm-auto col-lg-10 px-md-4">
	<#else>
    	<main class="d-flex align-items-center justify-content-center">
	</#if>
    
    <#list main_contexts as div_context>
    
    	<#if div_context.getContext().getLinkedMapContext()??>
	    <#assign main_context=div_context.getContext().getLinkedMapContext()>
    	<div id="${div_context['cliendId']}" class="div_main_not_active">
	    
	      	<form id="jui-form" method="post" action="/send_post">
				<fieldset>
					<#list main_context?keys as prop>
						<#assign component=main_context[prop]>
						${component.render()}
					</#list>
				</fieldset>
			</form>
			
			<script>
			
			
				
			    
				document.getElementById("jui-form").addEventListener("submit", function(event) {
					event.preventDefault();
					submitForm();
				});
				
				//elementMapping = ${elementMapping};
				
				
				function submitForm() {
	
			    	const data = {};
			    
				    <#list elementPostData?keys as key>
				    	//data["${key}"]= eval(${elementPostData[key]}); 
				    	data["${key}"]= ${elementPostData[key]}
					</#list>;
					
			        fetch('/send_post', {
				        method: 'POST',
				        headers: {
				            'Content-Type': 'application/json',
				        },
				        body: JSON.stringify(data),
				    })
				    .then(response => response.json())
				    .then(data => {
				        console.log('Success:', data);
				    })
				    .catch((error) => {
				        console.error('Error:', error);
				    });
				}
	
	    </script>
	
	      </div>
	    </div>
	    </#if>

	</#list>;
		    </main>
  </div>
</div>


    <script src="/js/bootstrap.bundle.min.js"></script>

      <script src="https://cdn.jsdelivr.net/npm/feather-icons@4.28.0/dist/feather.min.js" integrity="sha384-uO3SXW5IuS1ZpFPKugNNWqTZRRglnUJK6UAZ/gxOX80nxEkN9NcGZTftn6RzhGWE" crossorigin="anonymous"></script>
      <script src="https://cdn.jsdelivr.net/npm/chart.js@2.9.4/dist/Chart.min.js" integrity="sha384-zNy6FEbO50N+Cg5wap8IKA4M/ZnLJgzc6w2NqACZaK0u0FXfOWRRJOnQtpZun8ha" crossorigin="anonymous"></script>
      <script src="/js/core.js"></script>
  </body>
</html>
