package me.joney.plugin.coderkit.apikit.postman;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

/**
 * Created by yang.qiang on 2018/11/26.
 */
@NoArgsConstructor
@Data
public class PostmanCollection {

    private PostmanInfo info;
    private List<PostmanItem> item;
    private PostmanRequest request;

    @NoArgsConstructor
    @Data
    public static class PostmanInfo {

        @JsonProperty("_postman_id")
        private String postmanId;
        private String name;
        private String schema;
    }

    @NoArgsConstructor
    @Data
    @Accessors(chain = true)
    public static class PostmanItem {

        private List<PostmanItem> item;
        private String name;
        private PostmanRequest request;

    }

}
