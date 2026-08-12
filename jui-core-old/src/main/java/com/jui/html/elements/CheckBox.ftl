{{#values}}
	<div class="form-check">
	  <input class="form-check-input" type="checkbox" name="{{clientId}}_{{#getIndex}}{{/getIndex}}" id="{{clientId}}_{{#getIndex}}{{/getIndex}}">
	  <label class="form-check-label" for="{{clientId}}_{{#getIndex}}{{/getIndex}}">
	    {{.}}
	  </label>
	</div>
{{/values}}
