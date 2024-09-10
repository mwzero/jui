<#if caption??>
<p>${caption}</p>
</#if>
<table class="table <#list styles as style>${style} </#list>">
  <thead>
    <tr>
    	<#assign cols=st.getHtmlCols()>
    	<#if cols??>
    	<#list cols as col>
      		<th scope="col">${col}</th>
      	</#list>
      	</#if>
      	
    </tr>
  </thead>
  <tbody>
  	<#assign rows=st.getHtmlRows()>
  	<#list rows as row>
    <tr>
      <#list row as col>
      <td>${col}</td>
      </#list>
    </tr>
	</#list>
  </tbody>
</table>