package com.icinfo;

import com.icinfo.common.util.AccessTokenUtil;

/**
 * 描述：TODO
 *
 * @author zhangmengwen
 * @date 2016/8/5
 */
public class main {
    public static void main(String[] args) {
        AccessTokenUtil.initAndSetAccessToken();
        //AccessToken accessToken =  (AccessToken) ServletContextUtil.get().getAttribute(Constants.ACCESS_TOKEN);
        //System.out.println(accessToken.toString());
    }
}
