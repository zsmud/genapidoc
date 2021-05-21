<#list apiDescribes as api>
<#if api.apiName!?contains('查询')>
    是
<#elseif api.apiName!?contains('MAP-KEY')>
    map-key
 <#else>
    none
  </#if>
</#list>