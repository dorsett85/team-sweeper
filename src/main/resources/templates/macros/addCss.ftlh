<#macro addCss hrefs>
    <#--
    css bundles are only available for production builds, otherwise the style
    tags are inserted by the javascript bundles. If the styling is out of sync
    when using the webpack dev server, run `yarn build:dev` first.
    -->
    <#list hrefs as href>
        <#if href?has_content>
            <link href="${href}" rel="stylesheet">
        </#if>
    </#list>
</#macro>