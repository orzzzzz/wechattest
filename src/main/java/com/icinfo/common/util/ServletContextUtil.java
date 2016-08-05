package com.icinfo.common.util;

import org.springframework.web.context.ContextLoader;
import org.springframework.web.context.WebApplicationContext;

import javax.servlet.ServletContext;

/**
 * 描述：TODO
 *
 * @author zhangmengwen
 * @date 2016/8/5
 */
public final class ServletContextUtil {
    private static ServletContext servletContext = null;

    private ServletContextUtil(){}

    public synchronized static ServletContext get() {

        if(null == servletContext) {
            WebApplicationContext webApplicationContext = ContextLoader.getCurrentWebApplicationContext();
            servletContext = webApplicationContext.getServletContext();
        }
        return servletContext;
    }
}
