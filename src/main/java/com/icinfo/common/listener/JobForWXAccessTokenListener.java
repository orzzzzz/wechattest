package com.icinfo.common.listener;

import com.icinfo.common.util.AccessTokenUtil;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * 描述：TODO
 *
 * @author zhangmengwen
 * @date 2016/8/5
 */
public class JobForWXAccessTokenListener implements ApplicationListener<ContextRefreshedEvent> {

    public void onApplicationEvent(ContextRefreshedEvent event) {
        if (event.getApplicationContext().getParent() == null) {

            Runnable runnable = new Runnable() {
                public void run() {
                    /**
                     * 定时设置accessToken
                     */
                    AccessTokenUtil.initAndSetAccessToken();
                }
            };

            ScheduledExecutorService service = Executors.newSingleThreadScheduledExecutor();
            service.scheduleAtFixedRate(runnable, 1, 30, TimeUnit.SECONDS);
        }
    }
}