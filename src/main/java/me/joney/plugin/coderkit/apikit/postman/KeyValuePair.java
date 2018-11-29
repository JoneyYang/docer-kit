package me.joney.plugin.coderkit.apikit.postman;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * Created by yang.qiang on 2018/11/29.
 */

@Data
@Accessors(chain = true)
public class KeyValuePair {

    private Boolean selected = false;
    private String key;
    private String value;

}
