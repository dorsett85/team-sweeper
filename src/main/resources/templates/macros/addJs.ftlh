<#macro addJs sources>
    <#list sources as src>
        <script src="${src}"></script>
    </#list>
</#macro>