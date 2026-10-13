package com.tuusuario.wifitetplink;

public class NetworkModel {
    private String ssid;
    private String bssid;
    private String channel;
    private String vulnerability;

    public NetworkModel(String ssid, String bssid, String channel, String vulnerability) {
        this.ssid = ssid;
        this.bssid = bssid;
        this.channel = channel;
        this.vulnerability = vulnerability;
    }

    public String getSsid() { return ssid; }
    public String getBssid() { return bssid; }
    public String getChannel() { return channel; }
    public String getVulnerability() { return vulnerability; }
}
