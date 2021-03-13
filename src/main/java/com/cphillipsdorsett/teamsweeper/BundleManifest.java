package com.cphillipsdorsett.teamsweeper;

import com.fasterxml.jackson.annotation.JsonProperty;

public class BundleManifest {
    @JsonProperty("vendor.js")
    public String vendorJs;

    @JsonProperty("main.js")
    public String mainJs;

    @JsonProperty("main.css")
    public String mainCss;

    public String getVendorJs() {
        return vendorJs;
    }

    public String getMainJs() {
        return mainJs;
    }

    public String getMainCss() {
        return mainCss;
    }
}
