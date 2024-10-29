{{#caption}}
<p>{{caption}}</p>
{{/caption}}

<table class="table {{#styles}}{{.}} {{/styles}}">
  <thead>
    <tr>
      {{#df.ds.headers}}
        <th scope="col">{{.}}</th>
      {{/df.ds.headers}}
    </tr>
  </thead>
  <tbody>
    {{#df.ds.data}}
      <tr>
        {{#.}}
          <td>{{.}}</td>
        {{/.}}
      </tr>
    {{/df.ds.data}}
  </tbody>
</table>
