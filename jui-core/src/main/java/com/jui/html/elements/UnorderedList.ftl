<ul class="nav flex-column">
  {{#items}}
    <li class="nav-item">
      {{?this.link}}
        <a class="nav-link" href="#" data-url="{{this.link}}">
      {{/this.link}}
      {{?this.content}}
        <a class="nav-link" href="#" data-content="{{this.content}}">
      {{/this.content}}
      {{?this.containerId}}
        <a class="nav-link" href="#" data-jui="{{this.containerId}}">
      {{/this.containerId}}
          <i class="bi bi-{{this.icon}}-fill"></i> <span>{{this.label}}</span>
        </a>
    </li>
  {{/items}}
</ul>
