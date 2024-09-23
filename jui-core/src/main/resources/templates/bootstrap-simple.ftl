<!DOCTYPE html>
<html>
<head>
	<link rel="stylesheet" href="css/bootstrap.min.css">
	<script src="js/bootstrap.min.js"></script>
	
	<!-- apexcharts -->
	<script src="https://cdn.jsdelivr.net/npm/apexcharts"></script>
	
	<!--leaflet -->
	<link rel="stylesheet" href="https://unpkg.com/leaflet@1.9.4/dist/leaflet.css" integrity="sha256-p4NxAoJBhIIN+hmNHrzRCf9tD/miZyoHS5obTRR9BMY=" crossorigin=""/>
	<script src="https://unpkg.com/leaflet@1.9.4/dist/leaflet.js" integrity="sha256-20nQCchB9co0qIjJZRGuk2/Z9VM+kNiyxNV1lvTlZBo=" crossorigin=""></script>
	
	<script src="js/core.js"></script>
	
	
</head>
<body>


   	
	<div class="container" >
		<form id="jui-form" method="post" action="/send_post">
			<fieldset>
				<#list main_contexts as div_context>
				<#assign main_context=div_context.getContext().getLinkedMapContext()>
				<#list main_context?keys as prop>
					<#assign component=main_context[prop]>
					${component.render()}
				</#list>
				</#list>
			</fieldset>
		</form>
		
		<script>
			document.getElementById("jui-form").addEventListener("submit", function(event) {
				event.preventDefault();
				submitForm();
			});
			
			elementMapping = ${elementMapping};
			
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

</body>
<html>
