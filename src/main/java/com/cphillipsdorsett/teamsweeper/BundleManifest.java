package com.cphillipsdorsett.teamsweeper;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class BundleManifest {

    @JsonProperty("common.js")
    public String commonJs;

    @JsonProperty("common.css")
    public String commonCss;

    @JsonProperty("vendor.js")
    public String vendorJs;

    @JsonProperty("home.js")
    public String homeJs;

    @JsonProperty("home.css")
    public String homeCss;

    @JsonProperty("singlePlayer.js")
    public String singlePlayerJs;

    @JsonProperty("singlePlayer.css")
    public String singlePlayerCss;

    public String getCommonJs() {
        return commonJs;
    }

    public String getCommonCss() {
        return commonCss;
    }

    public String getVendorJs() { return vendorJs; }

    public String getHomeJs() {
        return homeJs;
    }

    public String getHomeCss() { return homeCss; }

    public String getSinglePlayerJs() {
        return singlePlayerJs;
    }

    public String getSinglePlayerCss() { return singlePlayerCss; }
}
