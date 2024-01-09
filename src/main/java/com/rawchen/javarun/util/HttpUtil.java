package com.rawchen.javarun.util;

import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import lombok.extern.slf4j.Slf4j;

/**
 * @author RawChen
 * @date 2024-01-10
 */
@Slf4j
public class HttpUtil {

	public static boolean isWebsiteAvailable(String url) {

		String resultStr = "";
		for (int i = 0; i < 3; i++) {
			try {
				HttpResponse execute = HttpRequest.get(url).execute();
				resultStr = execute.body();
				execute.close();
//				System.out.println(resultStr);
				if (StrUtil.isNotEmpty(resultStr)) {
					if (resultStr.contains("502 Bad Gateway") || resultStr.contains("504 Gateway Time-out")) {
						log.error("请求网站失败，重试 {} 次, body: {}", i + 1, resultStr);
						try {
							Thread.sleep(2000);
						} catch (InterruptedException ecp) {
							log.error("sleep异常", ecp);
						}
					}
				}
			} catch (Exception e) {
				log.error("请求网站异常，重试 {} 次, message: {}, body: {}", i + 1, e.getMessage(), resultStr);
				try {
					Thread.sleep(2000);
				} catch (InterruptedException ecp) {
					log.error("sleep异常", ecp);
				}
			}
			if (StrUtil.isNotEmpty(resultStr) && (!resultStr.contains("502 Bad Gateway")) || resultStr.contains("504 Gateway Time-out")) {
				break;
			}
		}
		// 重试完检测
		if ("".equals(resultStr) || resultStr.contains("502 Bad Gateway") || resultStr.contains("504 Gateway Time-out")) {
			log.error("重试3次请求网站都失败");
			return false;
		} else {
			return true;
		}
	}
}
