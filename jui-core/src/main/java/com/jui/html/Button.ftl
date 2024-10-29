{{#onServerSide}}
  {{#assign "js" value="{{onClick}};sendClick('{{key}}')"}}
{{/onServerSide}}
{{^onServerSide}}
  {{#assign "js" value="{{onClick}}"}}
{{/onServerSide}}

<button onclick="{{js}};return false;" class="btn btn-{{type}} ms-1">{{label}}</button>
