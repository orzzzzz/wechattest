package com.icinfo.common.model;

/**
 * 描述：TODO
 *
 * @author zhangmengwen
 * @date 2016/8/3
 */
public class JsApiTicket {
    private String jsapiTicket;

    private int expiresIn;

    public String getJsapiTicket() {
        return jsapiTicket;
    }

    public void setJsapiTicket(String jsapiTicket) {
        this.jsapiTicket = jsapiTicket;
    }

    public int getExpiresIn() {
        return expiresIn;
    }

    public void setExpiresIn(int expiresIn) {
        this.expiresIn = expiresIn;
    }
}
