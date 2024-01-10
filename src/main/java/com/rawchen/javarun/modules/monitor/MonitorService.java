package com.rawchen.javarun.modules.monitor;

import com.rawchen.javarun.util.DataUtil;
import com.rawchen.javarun.util.HttpUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author shuangquan.chen
 * @date 2024-01-09 19:43
 */
@Slf4j
@Service
public class MonitorService {

	@Autowired
	private JavaMailSenderImpl sender;

	@Value("${spring.mail.properties.from}")
	private String from;

	@Scheduled(cron = "0 0 6 ? * *")
	public void websiteAvailable() {
		String text = "";
		List<String> websites = DataUtil.getIdsByFileName("website");
		int errorWebsiteNumber = 0;
		for (String website : websites) {
			boolean isWebsiteAvailable = HttpUtil.isWebsiteAvailable(website);
			if (!isWebsiteAvailable) {
				errorWebsiteNumber++;
			}
			text += "网址：" + website + " 状态：" + isWebsiteAvailable + "\n";
			log.info("网址：{}，状态：{}",website, isWebsiteAvailable);
		}

		if (errorWebsiteNumber > 0) {
			sendMail(text);
			log.info("已发送网站访问异常提醒邮件");
		}
	}

	public void sendMail(String text) {
		SimpleMailMessage simpleMailMessage = new SimpleMailMessage();
		simpleMailMessage.setFrom(from);
		simpleMailMessage.setTo(from);
		simpleMailMessage.setSubject("网站访问异常提醒");
		simpleMailMessage.setText(text);
		sender.send(simpleMailMessage);
	}
}
