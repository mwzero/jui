{{#values}}
	<div class="form-check">
	  <input class="form-check-input" type="checkbox" name="{{key}}_{{#getIndex}}{{/getIndex}}" id="{{key}}_{{#getIndex}}{{/getIndex}}">
	  <label class="form-check-label" for="{{key}}_{{#getIndex}}{{/getIndex}}">
	    {{.}}
	  </label>
	</div>
	{{#incrementIndex}}{{/incrementIndex}}
{{/values}}
