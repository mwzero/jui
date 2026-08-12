{{?caption}}
<p>{{caption}}</p>
{{/caption}}

<table class="table {{#styles}}{{this}} {{/styles}}">
  <thead>
    <tr>
      {{#df.headers}}
        <th scope="col">{{this}}</th>
      {{/df.headers}}
    </tr>
  </thead>
  <tbody>
    {{#df.data}}
      <tr>
        {{#this}}
          <td>{{this}}</td>
        {{/this}}
      </tr>
    {{/df.data}}
  </tbody>
</table>