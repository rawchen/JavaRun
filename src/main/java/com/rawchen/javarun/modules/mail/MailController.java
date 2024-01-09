package com.rawchen.javarun.modules.mail;

import cn.hutool.core.util.StrUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

/**
 * @author shuangquan.chen
 * @date 2023-11-02 15:59
 */

@Controller
public class MailController {
    @Autowired
    private JavaMailSenderImpl sender;

    @Value("${spring.mail.properties.from}")
    private String from;
//    @Value("${spring.mail.properties.from}")
//    private String to;

    @ResponseBody
    @GetMapping("/mail/send01")
    public String sendMail(String to) {
        SimpleMailMessage simpleMailMessage = new SimpleMailMessage();
        simpleMailMessage.setFrom(from);
        simpleMailMessage.setTo(to);
        simpleMailMessage.setSubject("spring boot 发送邮件");
        simpleMailMessage.setText("我的第一封邮件！！！");
        sender.send(simpleMailMessage);
        return "success";
    }

    @ResponseBody
    @GetMapping("/mail/send02")
    public String sendMail02(String to) throws MessagingException {

        String content = "<img src=\"https://api.rawchen.com/api/qrcode/?url=234224\" style=\"pointer-events: none;\">";

        MimeMessage message = sender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true);
        helper.setFrom(from);
        helper.setTo(to);
        helper.setSubject("标题");
        //第二个参数是否是html，true
        helper.setText(content, true);

        sender.send(message);
        return "success";
    }

    @ResponseBody
    @GetMapping("/mail/send03")
    public String sendMail03(String to, String name, String company, String time, String office, String receptionist, String code) throws MessagingException {

        if (name == null) {
            name = "";
        }
        if (company == null) {
            company = "";
        }
        if (time == null) {
            time = "";
        }
        if (office == null) {
            office = "";
        }
        if (receptionist == null) {
            receptionist = "";
        }
        if (code == null) {
            code = "";
        }

        String text = "<div>Hi " + name + ", welcome to Lundong!<p><p>You name: " + name + "<p>You company: " + company + "<p>Time: " + time + "<p>Office: " + office + "<p>Receptionist: " + receptionist + "</div>";
        String content = "<p><img src=\"https://api.rawchen.com/api/qrcode/?url=" + code + "\" style=\"pointer-events: none;\">";

        MimeMessage message = sender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true);
        helper.setFrom(from);
        helper.setTo(to);
        helper.setSubject("标题");
        //第二个参数是否是html，true
        helper.setText(text + content, true);

        sender.send(message);
        return "success";
    }

    @ResponseBody
    @GetMapping("/mail/send")
    public String sendMail04(String to, String text, String title) {

        if (StrUtil.isEmpty(to)) {
            return "接收人不允许为空";
        }
        if (StrUtil.isEmpty(text)) {
            text = "空文本";
        }
        if (StrUtil.isEmpty(title)) {
            title = "未命名标题";
        }

        SimpleMailMessage simpleMailMessage = new SimpleMailMessage();
        simpleMailMessage.setFrom(from);
        simpleMailMessage.setTo(to);
        simpleMailMessage.setSubject(title);
        simpleMailMessage.setText(text);
        sender.send(simpleMailMessage);
        return "success";
    }
}
