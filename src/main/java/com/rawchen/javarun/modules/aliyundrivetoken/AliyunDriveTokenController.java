package com.rawchen.javarun.modules.aliyundrivetoken;

import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import cn.hutool.http.HttpUtil;
import com.alibaba.fastjson.JSONObject;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.HashMap;
import java.util.Map;

/**
 * 获取阿里云盘refreshToken
 *
 * @author RawChen
 * @date 2022-12-20
 */
@Controller
public class AliyunDriveTokenController {

    private final String mobileLoginTokenApi = "https://passport.aliyundrive.com/newlogin/qrcode/generate.do?appName=aliyun_drive&isMobile=true";
    private final String querryApi = "https://passport.aliyundrive.com/newlogin/qrcode/query.do?appName=aliyun_drive&fromSite=52&_bx-v=2.0.31";

    @GetMapping("/alipan")
    public String alipan(Model model) {
        HttpRequest request = HttpUtil.createGet(mobileLoginTokenApi);
        HttpResponse response = request.execute();
        JSONObject jsonObject = JSONObject.parseObject(response.body());
        JSONObject content = (JSONObject) jsonObject.get("content");
        JSONObject data = (JSONObject) content.get("data");
        String codeContent = data.getString("codeContent");
        String t = data.getString("t");
        String ck = data.getString("ck");
        model.addAttribute("codeContent", codeContent);
        model.addAttribute("qrcode", "https://api.rawchen.com/api/qrcode?url=" + codeContent);
        model.addAttribute("t", t);
        model.addAttribute("ck", ck);
        return "aliyundrivetoken/index";
    }

    @ResponseBody
    @PostMapping("/api/aliyundrivetoken/query")
    public JSONObject query(String t, String ck) {
        Map<String, Object> param = new HashMap<>();
        param.put("t", t);
        param.put("ck", ck);
        String post = HttpUtil.post(querryApi, param);
        return JSONObject.parseObject(post);
    }

}
