package com.rawchen.javarun.onedrive;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.annotation.Resource;

/**
 * @author RawChen
 */
@Controller
@RequestMapping(value = "/onedrive")
public class OneDriveCallbackController {

    @Resource
    private OneDriveService oneDriveServiceImpl;

    @GetMapping("/callback")
    public String oneDriveCallback(String code, Model model) {
        OneDriveToken oneDriveToken = oneDriveServiceImpl.getToken(code);
        model.addAttribute("accessToken", oneDriveToken.getAccessToken());
        model.addAttribute("refreshToken", oneDriveToken.getRefreshToken());
        return "callback";
    }

    @GetMapping("/authorize")
    public String authorize() {
        return "redirect:https://login.microsoftonline.com/common/oauth2/v2.0/authorize?client_id=" +
                oneDriveServiceImpl.getClientId() +
                "&response_type=code&redirect_uri=" +
                oneDriveServiceImpl.getRedirectUri() +
                "&scope=" +
                oneDriveServiceImpl.getScope();
    }
}
