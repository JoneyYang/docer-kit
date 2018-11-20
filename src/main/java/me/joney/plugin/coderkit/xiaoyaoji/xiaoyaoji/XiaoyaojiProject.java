package me.joney.plugin.coderkit.xiaoyaoji.xiaoyaoji;

import lombok.Data;

/**
 * Created by yang.qiang on 2018/10/06.
 */
@Data
public class XiaoyaojiProject {

    private String name;
    private String id;
    private String editable;
    private String description;
    private String details;

    @Override
    public String toString() {
        return name;
    }
}
