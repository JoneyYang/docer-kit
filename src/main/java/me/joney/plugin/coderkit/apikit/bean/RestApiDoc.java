package me.joney.plugin.coderkit.apikit.bean;

import java.util.List;
import lombok.Data;

/**
 * Created by yang.qiang on 2018/08/30.
 */
@Data
public class RestApiDoc {

    /**
     * 接口名称
     */
    private String name;
    /**
     * 接口路径
     */
    private String url;
    /**
     * 接口Method  POST|GET ...
     */
    private String method;
    /**
     * 接口描述
     */
    private String description;

    /**
     * 请求参数:请求头
     */
    private List<RestParam> requestHeadParams;
    /**
     * 请求参数:Query参数
     */
    private List<RestParam> requestQueryParams;
    /**
     * 请求参数:路径参数
     */
    private List<RestParam> requestPathParam;
    /**
     * 请求参数:Body体参数
     */
    private List<RestParam> requestBodyParams;
    /**
     * 响应参数
     */
    private List<RestParam> responseParams;


}
