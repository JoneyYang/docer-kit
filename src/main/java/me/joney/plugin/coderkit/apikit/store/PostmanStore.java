package me.joney.plugin.coderkit.apikit.store;

import com.alibaba.fastjson.JSON;
import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import com.intellij.openapi.project.Project;
import com.intellij.util.xmlb.XmlSerializerUtil;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.Data;
import me.joney.plugin.coderkit.apikit.postman.KeyValuePair;
import org.apache.commons.lang.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Created by yang.qiang on 2018/11/29.
 */

@State(name = "PostmanStore", storages = @Storage("postman-store.xml"))
@Data
public class PostmanStore implements PersistentStateComponent<PostmanStore> {


    private String headListJson;
    private String replaceListJson;

    private String path;
    private Map<String, String> prefixMap;

    private String json;


    public List<KeyValuePair> getReplaceList(){

        if (StringUtils.isBlank(this.replaceListJson)) {
            return new ArrayList<>();
        }
        return JSON.parseArray(this.replaceListJson, KeyValuePair.class);
    }

    public List<KeyValuePair> getHeadList(){

        if (StringUtils.isBlank(this.headListJson)) {
            return new ArrayList<>();
        }
        return JSON.parseArray(this.headListJson, KeyValuePair.class);
    }


    public PostmanStore() {
        prefixMap = new HashMap<>();
//        headList = new ArrayList<>();
//        replaceList = new ArrayList<>();
    }

    public static synchronized PostmanStore getInstance(Project project) {
        PostmanStore configInfo = ServiceManager.getService(project, PostmanStore.class);
        if (configInfo == null) {
            return new PostmanStore();
        }
        return configInfo;
    }

    @Nullable
    @Override
    public PostmanStore getState() {
        return this;
    }

    @Override
    public void loadState(@NotNull PostmanStore state) {
        // 覆盖初始配置
        XmlSerializerUtil.copyBean(state, this);
    }

}
