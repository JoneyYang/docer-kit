package me.joney.plugin.coderkit.apikit.executor;

import com.alibaba.fastjson.JSON;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.MessageType;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import me.joney.plugin.coderkit.apikit.bean.RestApiDoc;
import me.joney.plugin.coderkit.apikit.postman.KeyValuePair;
import me.joney.plugin.coderkit.apikit.postman.PostmanCollection;
import me.joney.plugin.coderkit.apikit.postman.PostmanCollection.PostmanInfo;
import me.joney.plugin.coderkit.apikit.postman.PostmanCollection.PostmanItem;
import me.joney.plugin.coderkit.apikit.postman.PostmanRequest;
import me.joney.plugin.coderkit.apikit.postman.PostmanRequest.RequestBody;
import me.joney.plugin.coderkit.apikit.postman.PostmanRequest.RequestParameter;
import me.joney.plugin.coderkit.apikit.postman.PostmanRequest.RequestUrl;
import me.joney.plugin.coderkit.apikit.store.PostmanStore;
import me.joney.plugin.coderkit.util.MessageUtil;

/**
 * Created by yang.qiang on 2018/12/3.
 */
public class PostmanGenerator {

    private Project project;
    private List<RestApiDoc> docs;
    private Module module;

    public PostmanGenerator(Project project, Module module, List<RestApiDoc> docs) {
        this.project = project;
        this.module = module;
        this.docs = docs;
    }

    public void execute() {
        PostmanStore postmanStore = PostmanStore.getInstance(project);
        PostmanCollection collection = new PostmanCollection();

        PostmanInfo info = new PostmanInfo();
        info.setPostmanId("dd36b8ed-603f-4e98-8565-c1030de5d9c3");
        info.setName("Export from Intellij");
        info.setSchema("https://schema.getpostman.com/json/collection/v2.1.0/collection.json");
        collection.setInfo(info);

        List<PostmanItem> items = docs.stream().map(doc -> {
            PostmanItem item = new PostmanItem();
            item.setName(doc.getName());

            PostmanRequest request = new PostmanRequest();
            request.setMethod(doc.getMethod());

            StringBuilder descBuilder = new StringBuilder();

            descBuilder.append(doc.getDescription()).append("\n\n");

            if (!doc.getRequestPathParam().isEmpty()) {
                descBuilder.append("\n\n路径参数:\n");
                doc.getRequestPathParam().forEach(param -> {
                    descBuilder.append(param.getName()).append(":").append(param.getType()).append("\t\t").append(param.getDescription());
                    descBuilder.append("\n");
                });
            }

            if (!doc.getRequestQueryParams().isEmpty()) {
                descBuilder.append("\n\n查询参数:\n");
                doc.getRequestQueryParams().forEach(param -> {
                    descBuilder.append(param.getName()).append(":").append(param.getType()).append("\t\t").append(param.getDescription());
                    descBuilder.append("\n");
                });
            }

            if (!doc.getRequestBodyParams().isEmpty()) {
                descBuilder.append("\n\nBody参数:\n");
                doc.getRequestBodyParams().forEach(param -> {
                    descBuilder.append(param.getName()).append(":").append(param.getType()).append("\t\t").append(param.getDescription());
                    descBuilder.append("\n");
                });
            }

            request.setDescription(descBuilder.toString());
            item.setRequest(request);

            List<RequestParameter> heads = doc.getRequestHeadParams().stream().map(head -> {
                RequestParameter postmanHead = new RequestParameter();
                postmanHead.setKey(head.getName());
                postmanHead.setValue("");
                postmanHead.setDescription(head.getDescription());
                return postmanHead;
            }).collect(Collectors.toList());

            List<KeyValuePair> headList = postmanStore.getHeadList();
            List<RequestParameter> storeHeads = headList.stream().filter(KeyValuePair::getSelected).map(head -> {
                RequestParameter postmanHead = new RequestParameter();
                postmanHead.setKey(head.getKey());
                postmanHead.setValue(head.getValue());
                return postmanHead;
            }).collect(Collectors.toList());
            heads.addAll(storeHeads);

            request.setHeader(heads);

            RequestBody body = new RequestBody();
            body.setMode("formdata");
            body.setRaw("");

            request.setBody(body);

            String docUrl = doc.getUrl();

            List<KeyValuePair> replaceList = postmanStore.getReplaceList();
            for (KeyValuePair replace : replaceList) {
                if (replace.getSelected()) {
                    docUrl = docUrl.replace(replace.getKey(), replace.getValue());
                }
            }

            RequestUrl url = new RequestUrl();
            url.setRaw(docUrl);
            String prefix = postmanStore.getPrefixMap().get(module.getName());
            url.setHost(new String[]{prefix == null ? "" : prefix});
            url.setPath(docUrl.split("[/\\\\]"));
            List<RequestParameter> queryList = doc.getRequestQueryParams().stream()
                .map(query -> new RequestParameter().setKey(query.getName()).setValue("").setDescription(query.getDescription()))
                .collect(Collectors.toList());
            url.setQuery(queryList);
            request.setUrl(url);

            return item;
        }).collect(Collectors.toList());

        ArrayList<PostmanItem> rootFolder = new ArrayList<>();
        rootFolder.add(new PostmanItem().setName("Requests").setItem(items));

        collection.setItem(rootFolder);

        try {
            OutputStreamWriter stream = new OutputStreamWriter(new FileOutputStream(postmanStore.getPath() + "/intellij-postman-export.json"),
                StandardCharsets.UTF_8);
            stream.write(JSON.toJSONString(collection));
            stream.close();

            System.out.println();

        } catch (Exception e) {
            e.printStackTrace();
        }

        MessageUtil.popup(project, "Generate Postman Export File Success.", MessageType.INFO);
    }

}
