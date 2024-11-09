<div class="mb-3">
	<div id="{{clientId}}" style="height: 300px; width: 600px">
	</div>
</div>

<script>
	var {{clientId}} = L.map('{{clientId}}').setView([{{lat}}, {{lng}}], {{zoom}});
	L.tileLayer('https://tile.openstreetmap.org/{z}/{x}/{y}.png', {
		maxZoom: 19,
		attribution: '&copy; <a href="http://www.openstreetmap.org/copyright">OpenStreetMap</a>'
	}).addTo({{clientId}});
	
	{{clientId}}.on('moveend', function() {
	    var center = {{clientId}}.getCenter();
	    var zoom = {{clientId}}.getZoom();
	    handleEvent({"name": "{{clientId}}", "value": {"lat": center.lat, "lng": center.lng, "zoom": zoom}});
	});
	
	function postData{{clientId}}() {
	
		var center = {{clientId}}.getCenter();
	    var zoom = {{clientId}}.getZoom();
	    
		return {
			"lat": center.lat, 
			"lng": center.lng, 
			"zoom": zoom
		};
	}
</script>
