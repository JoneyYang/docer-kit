package me.joney.plugin.coderkit.apikit.bean;

import java.util.List;
import java.util.Map;
import lombok.Data;
import me.joney.plugin.coderkit.apikit.postman.KeyValuePair;

/**
 * Created by yang.qiang on 2018/11/29.
 */
@Data
public class PostmanConfig {

    private String path;
    private List<KeyValuePair> headList;
    private List<KeyValuePair> replaceList;
    private Map<String, String> prefixMap;

}
