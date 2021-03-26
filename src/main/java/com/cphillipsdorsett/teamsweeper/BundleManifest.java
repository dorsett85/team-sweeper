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

    @JsonProperty("mine-16.png")
    public String mine16Png;

    @JsonProperty("mine-32.png")
    public String mine32Png;

    @JsonProperty("mine-96.png")
    public String mine96Png;

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

    public String getMine16Png() {
        return mine16Png;
    }

    public String getMine32Png() { return mine32Png; }

    public String getMine96Png() { return mine96Png; }
}
