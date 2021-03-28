<#include "macros/commonPage.ftlh"/>

<@commonPage>
    <@commonHead title="Team Sweeper - Minesweeper with others" cssHrefs=[bundleManifest.commonCss!, bundleManifest.homeCss!]>
        <link rel="prefetch" href="/single-player">
    </@commonHead>
    <@commonBody jsSources=[bundleManifest.commonJs, bundleManifest.vendorJs, bundleManifest.homeJs]>
        <nav class="select-game-nav text-center">
            <a href="/single-player">Single Player</a>
            <a href="/multi-player">Multi Player</a>
        </nav>
    </@commonBody>
</@commonPage>

