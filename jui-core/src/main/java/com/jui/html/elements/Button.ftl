<button id="{{clientId}}"
  onclick="{{?onServerSide}}{{clientClick}};sendEvent('{{clientId}}', 'click', {}){{/onServerSide}}{{^onServerSide}}{{clientClick}}{{/onServerSide}};return false;" 
  class="btn btn-{{type}} ms-1">
  {{label}}
</button>
