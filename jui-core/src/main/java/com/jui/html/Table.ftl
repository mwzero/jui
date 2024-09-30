<#if caption??>
<p>${caption}</p>
</#if>
<table class="table <#list styles as style>${style} </#list>">
  <thead>
    <tr>
    	<#assign cols=df.getDf().getColumns()>
    	<#if cols??>
    	<#list cols as col>
      		<th scope="col">${col.getName()}</th>
      	</#list>
      	</#if>
      	
    </tr>
  </thead>
  <tbody>
  	<#list juiComponent as row>
    <tr>
      <#list row as col>
      <td>${col}</td>
      </#list>
    </tr>
	</#list>
  </tbody>
</table>