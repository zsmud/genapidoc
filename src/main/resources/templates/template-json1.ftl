{
project:{
	id:1,
	<#if docVersions?? && (docVersions?size > 0)>
	  <#list docVersions as doc>
	   <#if !doc_has_next>
        version:"${doc.versionNum}",
        log:"${doc.changelog}",
        </#if>
       </#list>
	<#else>
	    version:"cash-api",
    	log:"版本说明",
	</#if>
	moduleList:[
		{
		name:"cash-api",
		introduction:"说明",
		pageList:[
			{
			name:"页名(业务名称)",
			introduction:"说明",
			actionList:[
			<#list apiDescribes as api>
				{
				name:"${api.apiName}",description:"${api.describe!}",requestType:"2",requestUrl:"${api.openUrl!}",requestPublic:1,responsePublic:2,remarks:"${api.changelog!}",chargeMan:"${api.author!}",
				requestParameterList:[
				<#list api.reqFieldMetadatas as req>
				    {identifier:"${req.fieldName!}"，name:"${req.fieldCNName!}",dataType:"${req.fieldTypeShortName!}",validator:"${req.length!}",mustNeed:"${req.notNull?string('是', '否')}",remark:"${req.memo!}",
				    <#if (req.collection)>
				        parameterList:[类型为复杂对象时]
				    <#else>
				        parameterList:[]
				    </#if>
				    }
				</#list>
				],responseParameterList:[
				<#list api.resFieldMetadatas as res>
				    {identifier:"${res.fieldName!}"，name:"${res.fieldCNName!}",dataType:"${res.fieldTypeShortName!}",validator:"${res.length!}",mustNeed:"${res.notNull?string('是', '否')}",remark:"${res.memo!}",
				    <#if (res.collection)>
                        parameterList:[类型为复杂对象时]
                    <#else>
                        parameterList:[]
                    </#if>
                    }
				</#list>
				]
				}<#if api_has_next>,</#if>
			</#list>
			]
			}
		]
		}
	]
}
}