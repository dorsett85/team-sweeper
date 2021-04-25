<#include "macros/commonPage.ftlh"/>

<@commonPage>
    <@commonHead title="Team Sweeper - Minesweeper with others" cssHrefs=[bundleManifest.commonCss!, bundleManifest.homeCss!]>
        <link rel="prefetch" href="/game/single-player" as="document">
        <link rel="prefetch" href="${bundleManifest.singlePlayerJs}" as="script">
        <link rel="prefetch" href="${bundleManifest.singlePlayerCss!}" as="style">
    </@commonHead>
    <@commonBody jsSources=[bundleManifest.commonJs, bundleManifest.vendorJs, bundleManifest.homeJs]>
        <nav class="select-game-nav text-center">
            <a href="/game/single-player">Single Player</a>
            <a href="/game/multi-player">Multi Player</a>
        </nav>
    </@commonBody>
</@commonPage>

