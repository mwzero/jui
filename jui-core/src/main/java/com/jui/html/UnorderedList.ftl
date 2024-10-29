<ul class="nav flex-column">
  {{#items}}
    <li class="nav-item">
      {{#link}}
        <a class="nav-link" href="#" data-url="{{link}}">
      {{/link}}
      {{^link}}
        <a class="nav-link" href="#" data-content="{{content}}">
      {{/link}}
          <i class="bi bi-{{icon}}-fill"></i> <span>{{label}}</span>
        </a>
    </li>
  {{/items}}
</ul>
