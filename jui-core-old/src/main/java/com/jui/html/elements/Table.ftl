{{?caption}}
<p>{{caption}}</p>
{{/caption}}

<table class="table {{#styles}}{{this}} {{/styles}}">
  <thead>
    <tr>
      {{#df.ds.headers}}
        <th scope="col">{{this}}</th>
      {{/df.ds.headers}}
    </tr>
  </thead>
  <tbody>
    {{#df.ds.data}}
      <tr>
        {{#this}}
          <td>{{this}}</td>
        {{/this}}
      </tr>
    {{/df.ds.data}}
  </tbody>
</table>
