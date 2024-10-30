<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title></title>
    
    <!-- Bootstrap CSS -->
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
    
    <!-- Bootstrap Icons -->
    <link href="https://cdn.jsdelivr.net/npm/bootstrap-icons/font/bootstrap-icons.css" rel="stylesheet">
    
    
    <!--leaflet -->
	<link rel="stylesheet" href="https://unpkg.com/leaflet@1.9.4/dist/leaflet.css" integrity="sha256-p4NxAoJBhIIN+hmNHrzRCf9tD/miZyoHS5obTRR9BMY=" crossorigin=""/>
	<script src="https://unpkg.com/leaflet@1.9.4/dist/leaflet.js" integrity="sha256-20nQCchB9co0qIjJZRGuk2/Z9VM+kNiyxNV1lvTlZBo=" crossorigin=""></script>
	
	
    
    <style>
        /* Main layout */
        body {
            display: flex;
        }

        /* Sidebar styles */
        #sidebar {
            width: 250px;
            height: 100vh;
            background: #343a40;
            color: #fff;
            transition: width 0.3s ease-in-out;
            padding-top: 60px;
            position: relative;
            z-index: 1000;
            display: flex;
            flex-direction: column;
            justify-content: space-between;
            transform: translateX(0); /* Sidebar visible */
        }

        #sidebar.collapsed {
            width: 60px; /* Maintain a minimal width for the toggle button */
        }

        #sidebar .nav-link {
            color: #fff;
            display: flex;
            align-items: center;
        }

        #sidebar .nav-link span {
            margin-left: 10px;
        }

        /* Hide the text when the sidebar is collapsed */
        #sidebar.collapsed .nav-link span {
            display: none;
        }

        /* Increase the size of icons */
        #sidebar .nav-link i {
            font-size: 1.5rem; /* Increase icon size */
        }

        /* Toggle button for the sidebar */
        #sidebarToggle {
            position: absolute;
            top: 10px;
            left: 0;
            background: #343a40;
            color: #fff;
            border: none;
            z-index: 1001;
            width: 60px;
            height: 50px;
            display: flex;
            align-items: center;
            justify-content: center;
        }

        /* Main content styles */
        #content {
            flex-grow: 1;
            padding: 20px;
            margin-left: 250px;
            transition: margin-left 0.3s ease-in-out;
        }

        #content.collapsed {
            margin-left: 60px; /* Align content with the collapsed sidebar */
        }

        /* Light mode with vibrant colors */
        .light-mode {
            background-color: #fef3c7;
            color: #374151;
        }

        .light-mode #sidebar {
            background: #f59e0b;
            color: #1f2937;
        }

        .light-mode #sidebar .nav-link {
            color: #1f2937;
        }

        .light-mode #sidebarToggle {
            background: #f59e0b;
            color: #1f2937;
        }

        .light-mode .dropdown-menu {
            background-color: #fef3c7;
            color: #374151;
        }

        .light-mode .dropdown-item:hover {
            background-color: #f59e0b;
            color: #fff;
        }

        .light-mode .modal-content {
            background-color: #fef3c7;
            color: #374151;
        }

        .light-mode .btn-primary {
            background-color: #f59e0b;
            border-color: #f59e0b;
        }

        .light-mode .btn-secondary {
            background-color: #9ca3af;
            border-color: #9ca3af;
        }

        /* Always keep the sidebar collapsed on small screens */
        @media (max-width: 768px) {
            #sidebar {
                width: 60px; /* Keep the sidebar collapsed */
            }

            #content {
                margin-left: 60px; /* Align content with the collapsed sidebar */
            }
        }
        
        
        .div_main_not_active{
	        display: none;
	    }
	
	
	    .div_main_active {
	        display: block;
	    }
	    
    </style>
</head>
<body>

    <!-- Sidebar -->
    <#if sidebar_context??>
    <nav id="sidebar" class="collapsed">
        <button type="button" id="sidebarToggle" class="btn">
            <span class="bi bi-list"></span>
        </button>
        
        <#list sidebar_context?keys as prop>
			<#assign component=sidebar_context[prop]>
			${component.render()}
		</#list>
		
		<!--	 
        <div class="dropdown p-3">
            <a class="nav-link dropdown-toggle text-white" href="#" id="dropdownMenuLink" data-bs-toggle="dropdown" aria-expanded="false">
                <i class="bi bi-gear-fill"></i> <span>Settings</span>
            </a>
            <ul class="dropdown-menu" aria-labelledby="dropdownMenuLink">
                <li><a class="dropdown-item" href="#">Profile</a></li>
                <li><a class="dropdown-item" href="#">Account Settings</a></li>
                <li><a class="dropdown-item" href="#">Logout</a></li>
            </ul>
        </div>
        -->
    </nav>
    </#if>

    <!-- Top-right context menu -->
    <div id="topRightMenu" class="dropdown" style="position: fixed; top: 10px; right: 10px; z-index: 1000;">
        <button class="btn btn-secondary dropdown-toggle" type="button" id="topRightMenuButton" data-bs-toggle="dropdown" aria-expanded="false">
            <span id="themeLabel">Light Mode</span>
        </button>
        <ul class="dropdown-menu dropdown-menu-end" aria-labelledby="topRightMenuButton">
            <li><a class="dropdown-item" href="#" id="toggleTheme">Toggle Mode</a></li>
            <li><a class="dropdown-item" href="#" data-bs-toggle="modal" data-bs-target="#deployModal">Deploy</a></li>
        </ul>
    </div>

    <!-- Modal for "Deploy" option -->
    <div class="modal fade" id="deployModal" tabindex="-1" aria-labelledby="deployModalLabel" aria-hidden="true">
        <div class="modal-dialog modal-dialog-centered">
            <div class="modal-content">
                <div class="modal-header">
                    <h5 class="modal-title" id="deployModalLabel">Deploy</h5>
                    <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
                </div>
                <div class="modal-body">
                    <p>This is the modal window for the "Deploy" option. Here you can add information about the deployment process or other relevant actions.</p>
                </div>
                <div class="modal-footer">
                    <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Close</button>
                    <button type="button" class="btn btn-primary">Confirm Deploy</button>
                </div>
            </div>
        </div>
    </div>

    <div id="content" class="collapsed">
        <div id="page-content">
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
				</div>
				</#if>
			</#list>    
        </div>
    </div>
    
	<!-- Bootstrap JS and dependencies -->
    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>

    <!-- Custom script -->
    <script>
        document.addEventListener('DOMContentLoaded', function() {
            const sidebar = document.getElementById('sidebar');
            const content = document.getElementById('content');
            const sidebarToggle = document.getElementById('sidebarToggle');
            const pageContent = document.getElementById('page-content');
            const toggleThemeButton = document.getElementById('toggleTheme');
            const themeLabel = document.getElementById('themeLabel');

            // Update theme label based on the current mode
            function updateThemeLabel() {
                if (document.body.classList.contains('light-mode')) {
                    themeLabel.innerText = 'Dark Mode';
                } else {
                    themeLabel.innerText = 'Light Mode';
                }
            }

            // Event listener to toggle the sidebar
            sidebarToggle.addEventListener('click', function() {
                sidebar.classList.toggle('collapsed');
                content.classList.toggle('collapsed');
            });

            // Event listener for sidebar links to dynamically load content
            document.querySelectorAll('#sidebar .nav-link').forEach(link => {
                link.addEventListener('click', function(e) {
                    e.preventDefault();
                    
                    const url = this.getAttribute('data-url');
                    const divContent = this.getAttribute('data-content');
                    if ( url === null ) {
                    
                    	let div1 = document.getElementById(divContent);
    					let activeElement = document.getElementsByClassName("div_main_active");
    					if ( activeElement.length > 0 )
    						activeElement[0].classList.remove("div_main_active");
    					div1.classList.add("div_main_active");
    
    					c6.invalidateSize();
    					
                    } else {
	                    fetch(url)
	                        .then(response => response.text())
	                        .then(html => {
	                            pageContent.innerHTML = html;
	                        })
	                        .catch(error => {
	                            console.error('Error loading content:', error);
	                            pageContent.innerHTML = '<p>There was an error loading the content.</p>';
	                        });
					}
                });
			});
                
    
           

            // Event listener to toggle light/dark mode
            toggleThemeButton.addEventListener('click', function() {
                document.body.classList.toggle('light-mode');
                updateThemeLabel();
            });

            // Initialize theme label on first load
            updateThemeLabel();
            
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
				
        });
    </script>
    
	<script src="/js/core.js"></script>
	
</body>
</html>
