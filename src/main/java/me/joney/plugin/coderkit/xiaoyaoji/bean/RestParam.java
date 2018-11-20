package me.joney.plugin.coderkit.xiaoyaoji.bean;

import java.util.List;
import lombok.Data;

/**
 * Created by yang.qiang on 2018/08/30.
 */
@Data
public class RestParam {

    /**
     * 参数名称
     */
    private String name;
    /**
     * 参数类型
     */
    private String Type;

    /**
     * 二级类型
     */
    private String subType;

    /**
     * 参数描述
     */
    private String description;
    /**
     * 子参数
     */
    private List<RestParam> subParams;

    /**
     * 是否必填
     */
    private Boolean required = false;

}
