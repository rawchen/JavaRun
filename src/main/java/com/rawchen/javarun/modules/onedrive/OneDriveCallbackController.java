package com.rawchen.javarun.modules.onedrive;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.annotation.Resource;

/**
 * 此包为OneDrive的FODI列表程序相关类。
 * 生成token与cf所需代码。
 *
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
        model.addAttribute("cloudflare", getCloudflareContent(oneDriveToken.getRefreshToken()));
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

    public String getCloudflareContent(String refreshToken) {
        return "const EXPOSE_PATH = \"/fodi\"; // 可自定义的展示文件夹\n" +
                "const PASSWD_FILENAME = \".password\"; // 可自定义的密码文件名\n" +
                "\n" +
                "const ONEDRIVE_REFRESHTOKEN = \"" + refreshToken + "\";\n" +
                "const clientId = \"6d027ece-1899-4c3e-8239-6eae511f0964\";\n" +
                "const clientSecret = \"hSf7Q~CKP5pT8HHX_WGAhzCmxOU4JSYfJ3l2y\";\n" +
                "const loginHost = \"https://login.microsoftonline.com\";\n" +
                "const apiHost = \"https://graph.microsoft.com\";\n" +
                "\n" +
                "async function handleRequest(request) {\n" +
                "\tlet querySplited, requestPath;\n" +
                "\tlet queryString = decodeURIComponent(request.url.split(\"?\")[1]);\n" +
                "\tif (queryString) querySplited = queryString.split(\"=\");\n" +
                "\tif (querySplited && querySplited[0] === \"file\") {\n" +
                "\t\tconst file = querySplited[1];\n" +
                "\t\tconst fileName = file.split(\"/\").pop();\n" +
                "\t\tif (fileName === PASSWD_FILENAME)\n" +
                "\t\t\treturn Response.redirect(\n" +
                "\t\t\t\t\"https://www.baidu.com/s?wd=%E6%80%8E%E6%A0%B7%E7%9B%97%E5%8F%96%E5%AF%86%E7%A0%81\",\n" +
                "\t\t\t\t301\n" +
                "\t\t\t);\n" +
                "\t\trequestPath = file.replace(\"/\" + fileName, \"\");\n" +
                "\t\tconst url = await fetchFiles(requestPath, fileName);\n" +
                "\t\treturn Response.redirect(url, 302);\n" +
                "\t} else {\n" +
                "\t\tconst { headers } = request;\n" +
                "\t\tconst contentType = headers.get(\"content-type\");\n" +
                "\t\tlet body = {};\n" +
                "\t\tif (contentType && contentType.includes(\"form\")) {\n" +
                "\t\t\tconst formData = await request.formData();\n" +
                "\t\t\tfor (let entry of formData.entries()) {\n" +
                "\t\t\t\tbody[entry[0]] = entry[1];\n" +
                "\t\t\t}\n" +
                "\t\t}\n" +
                "\t\trequestPath = Object.getOwnPropertyNames(body).length ? body[\"?path\"] : \"\";\n" +
                "\t\tconst files = await fetchFiles(requestPath, null, body.passwd);\n" +
                "\t\treturn new Response(files, {\n" +
                "\t\t\theaders: {\n" +
                "\t\t\t\t\"content-type\": \"application/json; charset=utf-8\",\n" +
                "\t\t\t\t\"Access-Control-Allow-Origin\": \"*\",\n" +
                "\t\t\t},\n" +
                "\t\t});\n" +
                "\t}\n" +
                "}\n" +
                "\n" +
                "addEventListener(\"fetch\", (event) => {\n" +
                "\treturn event.respondWith(handleRequest(event.request));\n" +
                "});\n" +
                "\n" +
                "const OAUTH = {\n" +
                "\trefreshToken: ONEDRIVE_REFRESHTOKEN,\n" +
                "\tclientId: clientId,\n" +
                "\tclientSecret: clientSecret,\n" +
                "\toauthUrl: loginHost + \"/common/oauth2/v2.0/\",\n" +
                "\tapiUrl: apiHost + \"/v1.0/me/drive/root\",\n" +
                "\tscope: apiHost + \"/Files.ReadWrite.All offline_access\",\n" +
                "};\n" +
                "\n" +
                "async function gatherResponse(response) {\n" +
                "\tconst { headers } = response;\n" +
                "\tconst contentType = headers.get(\"content-type\");\n" +
                "\tif (contentType.includes(\"application/json\")) {\n" +
                "\t\treturn await response.json();\n" +
                "\t} else if (contentType.includes(\"application/text\")) {\n" +
                "\t\treturn await response.text();\n" +
                "\t} else if (contentType.includes(\"text/html\")) {\n" +
                "\t\treturn await response.text();\n" +
                "\t} else {\n" +
                "\t\treturn await response.text();\n" +
                "\t}\n" +
                "}\n" +
                "\n" +
                "async function getContent(url) {\n" +
                "\tconst response = await fetch(url);\n" +
                "\tconst result = await gatherResponse(response);\n" +
                "\treturn result;\n" +
                "}\n" +
                "\n" +
                "async function getContentWithHeaders(url, headers) {\n" +
                "\tconst response = await fetch(url, { headers: headers });\n" +
                "\tconst result = await gatherResponse(response);\n" +
                "\treturn result;\n" +
                "}\n" +
                "\n" +
                "async function fetchFormData(url, data) {\n" +
                "\tconst formdata = new FormData();\n" +
                "\tfor (const key in data) {\n" +
                "\t\tif (data.hasOwnProperty(key)) {\n" +
                "\t\t\tformdata.append(key, data[key]);\n" +
                "\t\t}\n" +
                "\t}\n" +
                "\tconst requestOptions = {\n" +
                "\t\tmethod: \"POST\",\n" +
                "\t\tbody: formdata,\n" +
                "\t};\n" +
                "\tconst response = await fetch(url, requestOptions);\n" +
                "\tconst result = await gatherResponse(response);\n" +
                "\treturn result;\n" +
                "}\n" +
                "\n" +
                "async function fetchAccessToken() {\n" +
                "\turl = OAUTH[\"oauthUrl\"] + \"token\";\n" +
                "\tdata = {\n" +
                "\t\tclient_id: OAUTH[\"clientId\"],\n" +
                "\t\tclient_secret: OAUTH[\"clientSecret\"],\n" +
                "\t\tgrant_type: \"refresh_token\",\n" +
                "\t\trequested_token_use: \"on_behalf_of\",\n" +
                "\t\trefresh_token: OAUTH[\"refreshToken\"],\n" +
                "\t};\n" +
                "\tconst result = await fetchFormData(url, data);\n" +
                "\treturn result.access_token;\n" +
                "}\n" +
                "\n" +
                "async function fetchFiles(path, fileName, passwd) {\n" +
                "\tif (path === \"/\") path = \"\";\n" +
                "\tif (path || EXPOSE_PATH) path = \":\" + EXPOSE_PATH + path;\n" +
                "\n" +
                "\tconst accessToken = await fetchAccessToken();\n" +
                "\tconst uri =\n" +
                "\t\tOAUTH.apiUrl +\n" +
                "\t\tencodeURI(path) +\n" +
                "\t\t\"?expand=children(select=name,size,parentReference,lastModifiedDateTime,@microsoft.graph.downloadUrl)\";\n" +
                "\tconst body = await getContentWithHeaders(uri, {\n" +
                "\t\tAuthorization: \"Bearer \" + accessToken,\n" +
                "\t});\n" +
                "\tif (fileName) {\n" +
                "\t\tlet thisFile = null;\n" +
                "\t\tbody.children.forEach((file) => {\n" +
                "\t\t\tif (file.name === decodeURIComponent(fileName)) {\n" +
                "\t\t\t\tthisFile = file[\"@microsoft.graph.downloadUrl\"];\n" +
                "\t\t\t\treturn;\n" +
                "\t\t\t}\n" +
                "\t\t});\n" +
                "\t\treturn thisFile;\n" +
                "\t} else {\n" +
                "\t\tlet files = [];\n" +
                "\t\tlet encrypted = false;\n" +
                "\t\tfor (let i = 0; i < body.children.length; i++) {\n" +
                "\t\t\tconst file = body.children[i];\n" +
                "\t\t\tif (file.name === PASSWD_FILENAME) {\n" +
                "\t\t\t\tconst PASSWD = await getContent(file[\"@microsoft.graph.downloadUrl\"]);\n" +
                "\t\t\t\tif (PASSWD !== passwd) {\n" +
                "\t\t\t\t\tencrypted = true;\n" +
                "\t\t\t\t\tbreak;\n" +
                "\t\t\t\t} else {\n" +
                "\t\t\t\t\tcontinue;\n" +
                "\t\t\t\t}\n" +
                "\t\t\t}\n" +
                "\t\t\tfiles.push({\n" +
                "\t\t\t\tname: file.name,\n" +
                "\t\t\t\tsize: file.size,\n" +
                "\t\t\t\ttime: file.lastModifiedDateTime,\n" +
                "\t\t\t\turl: file[\"@microsoft.graph.downloadUrl\"],\n" +
                "\t\t\t});\n" +
                "\t\t}\n" +
                "\t\tlet parent = body.children.length\n" +
                "\t\t\t? body.children[0].parentReference.path\n" +
                "\t\t\t: body.parentReference.path;\n" +
                "\t\tparent = parent.split(\":\").pop().replace(EXPOSE_PATH, \"\") || \"/\";\n" +
                "\t\tparent = decodeURIComponent(parent);\n" +
                "\t\tif (encrypted) {\n" +
                "\t\t\treturn JSON.stringify({ parent: parent, files: [], encrypted: true });\n" +
                "\t\t} else {\n" +
                "\t\t\treturn JSON.stringify({ parent: parent, files: files });\n" +
                "\t\t}\n" +
                "\t}\n" +
                "}\n";
    }
}
