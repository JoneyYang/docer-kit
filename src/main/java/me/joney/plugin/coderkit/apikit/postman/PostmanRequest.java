package me.joney.plugin.coderkit.apikit.postman;

import java.util.List;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

/**
 * Created by yang.qiang on 2018/11/26.
 */
@NoArgsConstructor
@Data
public class PostmanRequest {

    private String method;
    private RequestBody body;
    private RequestUrl url;
    private String description;
    private List<RequestParameter> header;


    @Data
    public static class RequestBody {

        private String mode;
        private String raw;
        private List<RequestParameter> formdata;
    }


    @Data
    public static class RequestUrl {

        private String raw;
        private String port;
        private String[] host;
        private String[] path;
        private List<RequestParameter> query;
    }

    @Accessors(chain = true)
    @Data
    public static class RequestParameter {

        private String key;
        private String value;
        private String description;
        private String type;
    }
}
