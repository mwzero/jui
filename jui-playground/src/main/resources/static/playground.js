let currentFileName = ""; // Variabile per mantenere il nome del file selezionato
let optionsLoaded = false;

require.config({ paths: { 'vs': 'https://cdnjs.cloudflare.com/ajax/libs/monaco-editor/0.23.0/min/vs' } });
require(['vs/editor/editor.main'], function () {
 	window.editor = monaco.editor.create(document.getElementById('code-editor'), {
	    value: '',
	    language: 'java',
	    automaticLayout: true,
	    theme: "vs-dark",
	    lineNumbers: "on",
		minimap: { enabled: false }
 	});
});
	    
function loadExamples() {
  if (optionsLoaded) return;
   
  fetch('/api/examples')
    .then(response => response.json())
    .then(data => {
      const exampleList = document.getElementById('example-selector');
      exampleList.innerHTML = ""; // Pulisci l'elenco degli esempi prima di aggiornare
      
      const optionDefault = document.createElement('option');
      optionDefault.textContent = "choose a file";
      optionDefault.value = "";
      exampleList.appendChild(optionDefault);
        
      data.forEach(example => {
        const option = document.createElement('option');
        option.textContent = example.name;
        option.value = example.name;
        exampleList.appendChild(option);
      });
    });
    
    optionsLoaded = true;
}
	    document.getElementById('example-selector').addEventListener('click', loadExamples);
	    
function loadExampleCode(fileName) {
  fetch(`/api/examples/${fileName}`)
    .then(response => response.text())
    .then(code => {
      editor.setValue(code);
      currentFileName = fileName; 
      runButton.disabled = false; 
    });
}
	    
function setDraftCode() {
	    	var code = `package src.examples;
import static com.jui.JuiApp.jui;
public class JUIExample {

	public static void main(String... args) throws FileNotFoundException, IOException {
		
		jui.input.header("Input Example", "blue");
    	
		//write here JUI code
		jui.input.header("Input Example", "blue");
		
    	jui.start();
    }
}
`
	
editor.setValue(code);
currentFileName = "JUIExample.java";
	         
}
    
window.onload = setDraftCode;

	const captureButton = document.getElementById('capture-btn');
    const runButton = document.getElementById('run-btn');
    const saveButton = document.getElementById('save-btn');
    const outputDiv = document.getElementById('console');
    const previewContainer = document.getElementById('preview-container');
    const urlInput = document.getElementById('url-input');
    const openBrowserButton = document.getElementById('open-browser-btn');
    const exampleSelector = document.getElementById('example-selector');
    const consoleDiv = document.getElementById('console');
    const sidebar = document.getElementById('sidebar');
    const mainContent = document.getElementById('main-content');

    saveButton.addEventListener('click', function () {
        
        const code = editor.getValue();

	  	let fileName;
		if (currentFileName) fileName = currentFileName;
	    else fileName = "CustomCode.java";
		
		fileName = prompt("Inserisci il nome del file da salvare", fileName);  // Altrimenti chiedi all'utente il nome
	
		if (fileName) {
    		fetch('/api/save', {
      			method: 'POST',
      			headers: { 'Content-Type': 'application/json' },
      			body: JSON.stringify({ code, fileName })
    		})
    		.then(response => response.json())
    		.then(data => {
      			outputDiv.innerHTML+= data.message  + "\n";
      			loadExamples();
      			currentFileName = fileName; 
      			runButton.disabled = false; 
    		});
  		}
	});
	
	runButton.addEventListener('click', function () {
      if (!currentFileName) {
        outputDiv.innerHTML  = "Salva prima il file per poterlo eseguire.";
        return;
      }

      outputDiv.innerHTML   = "Il codice sta venendo compilato ed eseguito, attendere prego...";

      // Effettua la richiesta per compilare ed eseguire il codice
      fetch('/api/compile', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ fileName: currentFileName })
      })
      .then(response => {
        if (!response.ok) {
          return response.text().then(errorMessage => {
            throw new Error(errorMessage);
          });
        }
        return response.text();
      })
      .then(output => {
        outputDiv.innerHTML  = output;
        
		// Specifica l'URL della pagina da caricare nell'iframe
	    previewContainer.src = 'http://127.0.0.1:8080/index.html';
	    urlInput.value = 'http://127.0.0.1:8080/index.html';
	    
	  })
      
      .catch(error => {
        // Mostra l'errore nella console di output
        outputDiv.innerHTML  = "Errore durante la compilazione o esecuzione: " + error.message;
      });
    });
    
    // Funzione per caricare un esempio predefinito nel primo editor
    exampleSelector.addEventListener('change', function () {
        const selectedExample = exampleSelector.value;
        if (selectedExample) {
	        loadExampleCode(selectedExample);
        }
    });

    // Funzione per aprire il sito in una nuova finestra del browser
    openBrowserButton.addEventListener('click', function () {
        const url = urlInput.value;
        if (url) {
            window.open(url, '_blank');
        } else {
            alert('Please enter a valid URL');
        }
    });

    // Funzione per mostrare/nascondere la console
    function toggleConsole() {
        if (consoleDiv.style.display === 'block') {
            consoleDiv.style.display = 'none';
            toggleConsoleLink.textContent = 'Hide Console';
        } else {
            consoleDiv.style.display = 'block';
            toggleConsoleLink.textContent = 'Show Console';
        }
    }

    function toggleSidebar() {
    
    	var sidebar = document.getElementById('sidebar');
	    var overlay = document.getElementById('overlay');
	    
	    // Attiva/disattiva la sidebar
	    sidebar.classList.toggle('active');
	    
	    // Mostra/nascondi l'overlay
	    if (sidebar.classList.contains('active')) {
	        overlay.classList.add('active');
	    } else {
	        overlay.classList.remove('active');
	    }
	    
    }
    
		// Bottone per catturare lo snapshot
        captureButton.addEventListener('click', function() {
        
            const captureArea = document.createElement('div');
            captureArea.id = 'capture-area';
            captureArea.style.display = 'block'; // Mostra il contenitore per la cattura
            
            const cloneEditor1 = document.getElementById('code-editor').cloneNode(true);
            const cloneEditor2 = document.getElementById('preview-container').cloneNode(true);

            // Aggiungi entrambi i div clonati al contenitore temporaneo
            captureArea.appendChild(cloneEditor1);
            captureArea.appendChild(cloneEditor2);

            // Aggiungi il contenitore al body (non sarà visibile perché è nascosto)
            document.body.appendChild(captureArea);

            // Usa html2canvas per catturare il contenuto del contenitore con i due editor
            html2canvas(document.getElementById('main-content')).then(function(canvas) {
                // Converte il canvas in un'immagine PNG
                const imgData = canvas.toDataURL('image/png');

                // Crea un link per scaricare l'immagine
                const link = document.createElement('a');
                link.href = imgData;
                link.download = 'editor_snapshot.png';
                link.click();

                // Rimuovi il contenitore temporaneo dopo la cattura
                document.body.removeChild(captureArea);
            });
            
            /*
            const iframe = document.getElementById('preview-container');
			const iframeDoc = iframe.contentWindow.document; // Accedi al documento dell'iframe
			
			html2canvas(iframeDoc.body).then(function(canvas) {
			    const imgData = canvas.toDataURL('image/png');
			    const link = document.createElement('a');
			    link.href = imgData;
			    link.download = 'iframe_snapshot.png';
			    link.click();
			});
			*/
        });
