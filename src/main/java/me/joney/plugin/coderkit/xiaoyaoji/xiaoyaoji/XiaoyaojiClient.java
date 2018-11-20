package me.joney.plugin.coderkit.xiaoyaoji.xiaoyaoji;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.JSONPath;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import me.joney.plugin.coderkit.util.HttpUtil;
import org.apache.commons.lang.StringUtils;
import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.util.EntityUtils;

/**
 * Created by yang.qiang on 2018/09/22.
 */
public class XiaoyaojiClient {

    private String token;

    /**
     * 创建逍遥级文档
     *
     * @param doc       doc
     * @param host
     * @param projectId
     * @param parentId
     * @param urlPrefix
     */
    public String createDoc(XiaoyaojiDoc doc, String host, String projectId, String parentId, String urlPrefix) throws IOException {

        HashMap<String, String> param = new HashMap<>();
        param.put("name", doc.getName());
        param.put("type", "sys.http");
        param.put("projectId", projectId);
        param.put("parentId", parentId);

        if (StringUtils.isNotBlank(urlPrefix)) {
            if (doc.getUrl().startsWith("/") || urlPrefix.endsWith("/")) {
                doc.setUrl(urlPrefix + doc.getUrl());
            } else {
                doc.setUrl(urlPrefix + "/" + doc.getUrl());
            }
        }
        param.put("content", JSON.toJSONString(doc, false));

        HashMap<String, String> headers = new HashMap<>();
        headers.put("Cookie", token);
        HttpResponse response = HttpUtil.doPost(host + "/" + XiaoyaojiConstant.URL_PATH_CREATE_DOC, param, headers);
        String resultString = EntityUtils.toString(response.getEntity());
        return JSON.parseObject(resultString).get("data").toString();
    }

    /**
     * 登录小幺鸡
     */
    public boolean login(String username, String password, String host) throws IOException {
        try {
            HashMap<String, String> param = new HashMap<>();
            param.put("email", username);
            param.put("password", password);
            HttpResponse response = HttpUtil.doPost(host + "/" + XiaoyaojiConstant.URL_PATH_LOGIN, param, null);
            String responseContent = EntityUtils.toString(response.getEntity());
            if (StringUtils.isBlank(responseContent)) {
                return false;
            }

            JSONObject jsonObject = JSON.parseObject(responseContent);
            Object code = jsonObject.get("code");
            if (!"0".equals(code.toString())) {
                return false;
            }

            Header[] headers = response.getHeaders("Set-Cookie");
            this.token = headers[0].getValue();
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public List<XiaoyaojiProject> projectList(String host) throws Exception {
        HashMap<String, String> headers = new HashMap<>();
        headers.put("Cookie", token);
        headers.put("X-Requested-With", "XMLHttpRequest");
        HttpResponse response = HttpUtil.doGet(host + "/" + XiaoyaojiConstant.URL_PROJECT_LIST, null, headers);
        String resultContent = EntityUtils.toString(response.getEntity());
        return JSON.parseArray(JSONPath.read(resultContent, "$.data.projects").toString(), XiaoyaojiProject.class);
    }

    public List<XiaoyaojiDocStruct> docList(String host, String projectId) throws Exception {

        HashMap<String, String> headers = new HashMap<>();
        headers.put("Cookie", token);
        HttpResponse response = HttpUtil.doGet(host + "/" + XiaoyaojiConstant.URL_DOC_LIST + "/" + projectId, null, headers);

        String resultContent = EntityUtils.toString(response.getEntity());
        return JSON.parseArray(JSONPath.read(resultContent, "$.data").toString(), XiaoyaojiDocStruct.class);
    }
}
