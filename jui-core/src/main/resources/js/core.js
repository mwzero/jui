(function () {
  'use strict'

  feather.replace({ 'aria-hidden': 'true' })

  
})()

var elementMapping = [];
    
document.addEventListener("DOMContentLoaded", function listener() {
    const formElements = document.querySelectorAll("form input, form select, form textarea");

    function handleChange(event) {
    
        const sourceElement = event.target;
        
        handleEvent(sourceElement);
	}

    formElements.forEach(function(element) {
        element.addEventListener("change", handleChange);
        element.addEventListener("input", handleChange); 
    });
    
   
});

/*
* used to manage events fired by web components.
* for example in MapChart: handleEvent({"name": "${clientId}", "value": {"lat": center.lat, "lng": center.lng, "zoom": zoom}});
*/
function handleEvent(sourceElement) {

	elementMapping.forEach((element) => {
		if ( element.source === sourceElement.name ) {
			element.commands.forEach((command) => {
				console.log(`Elemento modificato: ${sourceElement.name}, Nuovo valore: ${sourceElement.value}, Aggiornato: ${command}`);
				
				let dynamicFunction = new Function('value' , command);
				dynamicFunction(sourceElement.value);
			});
		}	
	});

}

function switchNavBar(divId) {
			        let div1 = document.getElementById(divId);
			        let activeElement = document.getElementsByClassName("div_main_active");
			        if ( activeElement.length > 0 )
			        	activeElement[0].classList.remove("div_main_active");
			        div1.classList.add("div_main_active");
			        
			        c6.invalidateSize()
			    }