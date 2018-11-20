package me.joney.plugin.coderkit.xiaoyaoji.xiaoyaoji;

import java.util.List;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

/**
 * Created by yang.qiang on 2018/09/22.
 */
@NoArgsConstructor
@Data
@Accessors(chain = true)
public class XiaoyaojiDoc {

    /**
     * 接口名
     */
    private String name;

    private String requestMethod;
    private String dataType;
    private String contentType;
    private String url;
    private String status;
    private String example;
    private String description;
    private List<ArgBean> requestArgs;
    private List<ArgBean> requestHeaders;
    private List<ArgBean> responseHeaders;
    private List<ArgBean> responseArgs;

    @Data
    public static class ArgBean {

        private String id;
        private String name;
        private String nameX;
        private String type;
        private String require;
        private String description;
        private List<ArgBean> children;

    }
}
