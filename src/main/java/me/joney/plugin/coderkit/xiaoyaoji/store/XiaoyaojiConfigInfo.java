package me.joney.plugin.coderkit.xiaoyaoji.store;

import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import com.intellij.openapi.project.Project;
import com.intellij.util.xmlb.XmlSerializerUtil;
import java.util.HashMap;
import java.util.Map;
import lombok.Data;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Created by yang.qiang on 2018/09/25.
 */
@Data
@State(name = "XiaoyaojiConfigInfo", storages = @Storage("doc-kit-xiaoyaoji-config-info.xml"))
public class XiaoyaojiConfigInfo implements PersistentStateComponent<XiaoyaojiConfigInfo> {

    private String parentId;
    private String projectId;
    private String urlPrefix;
    private String host;
    private String username;
    private String password;
    private Map<String, String> selectParentMap;
    private Map<String,String> urlPrefixMap;

    private XiaoyaojiConfigInfo() {
        selectParentMap = new HashMap<>();
        urlPrefixMap = new HashMap<>();
    }

    public static synchronized XiaoyaojiConfigInfo getInstance(Project project) {
        XiaoyaojiConfigInfo configInfo = ServiceManager.getService(project, XiaoyaojiConfigInfo.class);
        if (configInfo == null) {
            return new XiaoyaojiConfigInfo();
        }
        return configInfo;
    }


    @Nullable
    @Override
    public XiaoyaojiConfigInfo getState() {
        return this;
    }

    @Override
    public void loadState(@NotNull XiaoyaojiConfigInfo configInfo) {
        // 覆盖初始配置
        XmlSerializerUtil.copyBean(configInfo, this);
    }
}
