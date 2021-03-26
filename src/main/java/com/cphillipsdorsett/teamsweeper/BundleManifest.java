package com.cphillipsdorsett.teamsweeper;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class BundleManifest {

    @JsonProperty("main.js")
    public String mainJs;

    @JsonProperty("main.css")
    public String mainCss;

    @JsonProperty("vendor.js")
    public String vendorJs;

    @JsonProperty("home.js")
    public String homeJs;

    @JsonProperty("home.css")
    public String homeCss;

    public String getMainJs() {
        return mainJs;
    }

    public String getMainCss() {
        return mainCss;
    }

    public String getVendorJs() { return vendorJs; }

    public String getHomeJs() {
        return homeJs;
    }

    public String getHomeCss() {
        return homeCss;
    }
}
