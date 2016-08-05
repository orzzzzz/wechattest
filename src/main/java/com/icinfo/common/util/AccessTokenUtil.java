package com.icinfo.common.util;

import com.icinfo.common.constant.Constants;
import com.icinfo.common.model.AccessToken;
import com.icinfo.common.model.JsApiTicket;
import com.icinfo.dataservice.MyX509TrustManager;
import net.sf.json.JSONException;
import net.sf.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.servlet.ServletContext;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.ConnectException;
import java.net.URL;

/**
 * 描述：TODO
 *
 * @author zhangmengwen
 * @date 2016/8/5
 */
public class AccessTokenUtil {
    private static Logger log = LoggerFactory.getLogger(AccessTokenUtil.class);

    // 获取access_token的接口地址（GET） 限200（次/天）
    public final static String access_token_url = "https://api.weixin.qq.com/cgi-bin/token?grant_type=client_credential&appid=APPID&secret=APPSECRET";
    //采用http GET方式请求获得jsapi_ticket
    private static final String JSAPI_TICKET = "https://api.weixin.qq.com/cgi-bin/ticket/getticket?access_token=ACCESS_TOKEN&type=jsapi";

    /**
     * @Author:yangwl
     * @date 2016年5月11日 下午5:25:03
     * @Description: //设置access_token
     */
    public static void initAndSetAccessToken() {
        log.info("execute initAndSetAccessToken Start : {}", System.currentTimeMillis());
        //Properties prop = new Properties();
        //try {
        //    InputStream in = AccessTokenUtil.class.getResourceAsStream("/properties/web.properties");
        //    prop.load(in);
        //} catch (IOException e) {
        //    log.info("execute initAndSetAccessToken {}", e.getMessage());
        //}
        String appid = Constants.APPID;
        String appsecret = Constants.APPSECRET;
        if(!appid.isEmpty() && !appsecret.isEmpty()) {
            AccessToken accessToken = getAccessToken(appid,appsecret);
            if(null != accessToken) {
                /**
                 * cache access_token
                 */
                ServletContext sc = ServletContextUtil.get();
                sc.removeAttribute(Constants.ACCESS_TOKEN);
                sc.setAttribute(Constants.ACCESS_TOKEN, accessToken);

                /**
                 * cache jsapi_ticket
                 */
                JsApiTicket jsApiTicket = getJsApiTicket(accessToken.getAccessToken());
                if(null != jsApiTicket) {
                    sc.removeAttribute(Constants.JSAPI_TICKET);
                    sc.setAttribute(Constants.JSAPI_TICKET, jsApiTicket);
                }
                //这里可以向数据库中写access_token
            }
        } else {
            log.info("execute initAndSetAccessToken appid,appsecret 为空.{}");
        }
        log.info("execute initAndSetAccessToken End   : {}", System.currentTimeMillis());
    }
    /**
     * 获取access_token
     *
     * @param appid 凭证
     * @param appsecret 密钥
     * @return
     */
    public static AccessToken getAccessToken(String appid, String appsecret) {
        AccessToken accessToken = null;

        String requestUrl = access_token_url.replace("APPID", appid).replace("APPSECRET", appsecret);
        JSONObject jsonObject = httpRequest(requestUrl, "GET", null);
        // 如果请求成功
        if (null != jsonObject) {
            try {
                accessToken = new AccessToken();
                accessToken.setAccessToken(jsonObject.getString("access_token"));
                accessToken.setExpiresIn(jsonObject.getInt("expires_in"));
            } catch (JSONException e) {
                accessToken = null;
                // 获取token失败
                log.error("获取token失败 errcode:{} errmsg:{}", jsonObject.getInt("errcode"), jsonObject.getString("errmsg"));
            }
        }
        return accessToken;
    }

    public static JsApiTicket getJsApiTicket(String accessToken) {
        JsApiTicket jsApiTicket = null;

        String requestUrl = JSAPI_TICKET.replace("ACCESS_TOKEN", accessToken);
        JSONObject jsonObject = httpRequest(requestUrl, "GET", null);
        // 如果请求成功
        if (null != jsonObject) {
            try {
                jsApiTicket = new JsApiTicket();
                jsApiTicket.setJsapiTicket(jsonObject.getString("ticket"));
                jsApiTicket.setExpiresIn(jsonObject.getInt("expires_in"));
            } catch (JSONException e) {
                accessToken = null;
                // 获取jsApiTicket失败
                log.error("获取jsApiTicket失败 errcode:{} errmsg:{}", jsonObject.getInt("errcode"), jsonObject.getString("errmsg"));
            }
        }
        return jsApiTicket;
    }

    /**
     * 发起https请求并获取结果
     *
     * @param requestUrl 请求地址
     * @param requestMethod 请求方式（GET、POST）
     * @param outputStr 提交的数据
     * @return JSONObject(通过JSONObject.get(key)的方式获取json对象的属性值)
     */
    public static JSONObject httpRequest(String requestUrl, String requestMethod, String outputStr) {
        JSONObject jsonObject = null;
        StringBuffer buffer = new StringBuffer();
        try {
            // 创建SSLContext对象，并使用我们指定的信任管理器初始化
            TrustManager[] tm = { new MyX509TrustManager() };
            SSLContext sslContext = SSLContext.getInstance("SSL", "SunJSSE");
            sslContext.init(null, tm, new java.security.SecureRandom());
            // 从上述SSLContext对象中得到SSLSocketFactory对象
            SSLSocketFactory ssf = sslContext.getSocketFactory();

            URL url = new URL(requestUrl);
            HttpsURLConnection httpUrlConn = (HttpsURLConnection) url.openConnection();
            httpUrlConn.setSSLSocketFactory(ssf);

            httpUrlConn.setDoOutput(true);
            httpUrlConn.setDoInput(true);
            httpUrlConn.setUseCaches(false);
            // 设置请求方式（GET/POST）
            httpUrlConn.setRequestMethod(requestMethod);

            if ("GET".equalsIgnoreCase(requestMethod))
                httpUrlConn.connect();

            // 当有数据需要提交时
            if (null != outputStr) {
                OutputStream outputStream = httpUrlConn.getOutputStream();
                // 注意编码格式，防止中文乱码
                outputStream.write(outputStr.getBytes("UTF-8"));
                outputStream.close();
            }

            // 将返回的输入流转换成字符串
            InputStream inputStream = httpUrlConn.getInputStream();
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream, "utf-8");
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

            String str = null;
            while ((str = bufferedReader.readLine()) != null) {
                buffer.append(str);
            }
            bufferedReader.close();
            inputStreamReader.close();
            // 释放资源
            inputStream.close();
            inputStream = null;
            httpUrlConn.disconnect();
            jsonObject = JSONObject.fromObject(buffer.toString());
        } catch (ConnectException ce) {
            log.error("Weixin server connection timed out.");
        } catch (Exception e) {
            log.error("https request error:{}", e);
        }
        return jsonObject;
    }
}
