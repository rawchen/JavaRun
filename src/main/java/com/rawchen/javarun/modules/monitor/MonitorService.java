package com.rawchen.javarun.modules.monitor;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

/**
 * @author shuangquan.chen
 * @date 2024-01-09 19:43
 */
@Service
public class MonitorService {

    @Scheduled(cron = "0 0 6 ? * *")
    public void websiteAvailable() {

    }
}
