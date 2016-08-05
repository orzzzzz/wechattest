package com.icinfo.system.controller;

import com.icinfo.common.constant.Constants;
import com.icinfo.common.model.AccessToken;
import com.icinfo.common.model.JsApiTicket;
import com.icinfo.common.util.AccessTokenUtil;
import com.icinfo.common.util.ServletContextUtil;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 描述：TODO
 *
 * @author zhangmengwen
 * @date 2016/8/5
 */
@RestController
@RequestMapping(value = "/core")
public class coreController {
    @RequestMapping(value = "/init")
    public String init(){
        //AccessToken accessToken =  (AccessToken) ServletContextUtil.get().getAttribute(Constants.ACCESS_TOKEN);
        //System.out.println(accessToken.toString());
        AccessTokenUtil.initAndSetAccessToken();
        return "success";
    }

    @RequestMapping(value = "/gettoken")
    public String getToken(){
        AccessToken accessToken =  (AccessToken) ServletContextUtil.get().getAttribute(Constants.ACCESS_TOKEN);
        System.out.println(accessToken.toString());

        JsApiTicket jsApiTicket =  (JsApiTicket) ServletContextUtil.get().getAttribute(Constants.JSAPI_TICKET);
        System.out.println(jsApiTicket.toString());

        return accessToken.getAccessToken();
    }
}
