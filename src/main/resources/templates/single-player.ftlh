<#include "macros/commonPage.ftlh"/>

<@commonPage>
    <@commonHead title="Team Sweeper - single player" cssHrefs=[bundleManifest.commonCss!, bundleManifest.singlePlayerCss!] />
    <@commonBody jsSources=[bundleManifest.commonJs, bundleManifest.vendorJs, bundleManifest.singlePlayerJs]>
        <div id="root"></div>
    </@commonBody>
</@commonPage>
