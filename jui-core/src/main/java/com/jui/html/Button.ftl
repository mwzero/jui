<button 
  onclick="{{#onServerSide}}{{onClick}};sendClick('{{key}}'){{/onServerSide}}{{^onServerSide}}{{onClick}}{{/onServerSide}};return false;" 
  class="btn btn-{{type}} ms-1">
  {{label}}
</button>
