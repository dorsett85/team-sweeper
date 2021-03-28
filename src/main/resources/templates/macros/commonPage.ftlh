<#include "./header.ftlh"/>
<#include "./footer.ftlh"/>
<#include "./addCss.ftlh" />
<#include "./addJs.ftlh" />

<#macro commonHead title cssHrefs>
    <head>
        <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1">
        <title>${title}</title>
        <link rel="icon" type="image/png" href="/bundles/mine-16.png" sizes="16x16">
        <link rel="icon" type="image/png" href="/bundles/mine-32.png" sizes="32x32">
        <link rel="icon" type="image/png" href="/bundles/mine-96.png" sizes="96x96">

        <@addCss hrefs=cssHrefs />
        <#nested />
    </head>
</#macro>

<#macro commonBody jsSources>
    <body>
    <@header />
    <#nested />
    <@footer />
    <@addJs sources=jsSources />
    </body>
</#macro>

<#macro commonPage>
    <!DOCTYPE html>
    <html lang="en">
    <#nested />
    </html>
</#macro>