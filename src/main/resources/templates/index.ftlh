<#include "macros/header.ftlh"/>

<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Team Sweeper - Minesweeper with others</title>
    <link rel="icon" type="image/png" href="${bundleManifest.mine16Png}" sizes="16x16">
    <link rel="icon" type="image/png" href="${bundleManifest.mine32Png}" sizes="32x32">
    <link rel="icon" type="image/png" href="${bundleManifest.mine96Png}" sizes="96x96">
    <#--
    css bundles are only available for production builds, otherwise the style
    tags are inserted by the javascript bundles. If the styling is out of sync
    when using the webpack dev server, run `yarn build:dev` first.
    -->
    <#if bundleManifest.mainCss?has_content>
        <link href="${bundleManifest.mainCss}" rel="stylesheet">
    </#if>
    <#if bundleManifest.homeCss?has_content>
        <link href="${bundleManifest.homeCss}" rel="stylesheet">
    </#if>
</head>
<body>

<@appHeading />
<div id="root"></div>

<script src="${bundleManifest.mainJs}"></script>
<script src="${bundleManifest.vendorJs}"></script>
<script src="${bundleManifest.homeJs}"></script>
</body>
</html>
