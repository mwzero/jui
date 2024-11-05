<div class="dropdown p-3 fixed-bottom">
    <a class="nav-link dropdown-toggle text-white" href="#" id="dropdownMenuLink" data-bs-toggle="dropdown" aria-expanded="false">
        <i class="bi bi-gear-fill"></i> <span>{{label}}</span>
    </a>
    <ul class="dropdown-menu" aria-labelledby="dropdownMenuLink">
        {{#items}}
            <li><a class="dropdown-item" href="#">{{label}}</a></li>
        {{/items}}
    </ul>
</div>
