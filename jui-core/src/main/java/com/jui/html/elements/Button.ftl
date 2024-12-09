<button id="{{clientId}}"
  onclick="{{#onServerSide}}{{onClick}};sendClick('{{clientId}}'){{/onServerSide}}{{^onServerSide}}{{onClick}}{{/onServerSide}};return false;" 
  class="btn btn-{{type}} ms-1">
  {{label}}
</button>
