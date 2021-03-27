<#include "macros/header.ftlh"/>
<#include "macros/footer.ftlh"/>

<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Team Sweeper - Minesweeper with others</title>
    <link rel="icon" type="image/png" href="/bundles/mine-16.png" sizes="16x16">
    <link rel="icon" type="image/png" href="/bundles/mine-32.png" sizes="32x32">
    <link rel="icon" type="image/png" href="/bundles/mine-96.png" sizes="96x96">
    <#--
    css bundles are only available for production builds, otherwise the style
    tags are inserted by the javascript bundles. If the styling is out of sync
    when using the webpack dev server, run `yarn build:dev` first.
    -->
    <#if bundleManifest.commonCss?has_content>
        <link href="${bundleManifest.commonCss}" rel="stylesheet">
    </#if>
    <#if bundleManifest.homeCss?has_content>
        <link href="${bundleManifest.homeCss}" rel="stylesheet">
    </#if>
</head>
<body>

<@header />
<nav class="select-game-nav text-center">
    <a href="/single-player">Single Player</a>
    <a href="/multi-player">Multi Player</a>
</nav>
<@footer />

<script src="${bundleManifest.commonJs}"></script>
<script src="${bundleManifest.vendorJs}"></script>
<script src="${bundleManifest.homeJs}"></script>
</body>
</html>
