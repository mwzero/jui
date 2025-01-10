<ul class="nav flex-column">
  {{#items}}
    <li class="nav-item">
      {{?this.link}}
        <a class="nav-link" href="#" data-url="{{this.link}}">
      {{/this.link}}
      {{^link}}
        <a class="nav-link" href="#" data-content="{{this.content}}">
      {{/link}}
          <i class="bi bi-{{icon}}-fill"></i> <span>{{this.label}}</span>
        </a>
    </li>
  {{/items}}
</ul>
