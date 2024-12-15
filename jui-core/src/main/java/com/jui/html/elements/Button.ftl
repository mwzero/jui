<button id="{{clientId}}"
  onclick="{{#onServerSide}}{{onClick}};sendEvent('{{clientId}}', 'click', {}){{/onServerSide}}{{^onServerSide}}{{onClick}}{{/onServerSide}};return false;" 
  class="btn btn-{{type}} ms-1">
  {{label}}
</button>
