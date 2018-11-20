package me.joney.plugin.coderkit.xiaoyaoji.xiaoyaoji;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import me.joney.plugin.coderkit.util.RestPsiUtil;
import me.joney.plugin.coderkit.xiaoyaoji.bean.RestApiDoc;
import me.joney.plugin.coderkit.xiaoyaoji.bean.RestParam;
import me.joney.plugin.coderkit.xiaoyaoji.xiaoyaoji.XiaoyaojiDoc.ArgBean;

/**
 * Created by yang.qiang on 2018/09/22.
 */
public class XiaoyaojiConvert {

    static Set<String> stringTypes = new HashSet<String>() {{
        add("String");
        add("string");
        add("BigDecimal");
        add("Date");
    }};

    static Set<String> numberTypes = new HashSet<String>() {{
        add("Integer");
        add("Long");
        add("double");
        add("Double");
        add("float");
        add("Float");
        add("int");
        add("long");
        add("byte");
        add("Byte");
        add("short");
        add("Short");
    }};

    static Set<String> booleanTypes = new HashSet<String>() {{
        add("Boolean");
        add("boolean");
    }};

    static Set<String> listTypes = new HashSet<String>() {{
        add("List");
        add("Set");
    }};


    public static XiaoyaojiDoc convertDoc(RestApiDoc doc) {
        XiaoyaojiDoc xiaoyaojiDoc = new XiaoyaojiDoc();
        xiaoyaojiDoc.setName(doc.getName());
        xiaoyaojiDoc.setRequestMethod(doc.getMethod());
        xiaoyaojiDoc.setUrl(doc.getUrl());
        xiaoyaojiDoc.setStatus("有效");
        xiaoyaojiDoc.setDescription(doc.getDescription());

        if ("GET".equals(doc.getMethod())) {
            xiaoyaojiDoc.setDataType("X-WWW-FORM-URLENCODED");
            xiaoyaojiDoc.setContentType("JSON");
        } else {
            xiaoyaojiDoc.setDataType("JSON");
            xiaoyaojiDoc.setContentType("JSON");
        }

        List<ArgBean> headParams = convertSubParameter(doc.getRequestHeadParams());
        List<ArgBean> queryParams = convertSubParameter(doc.getRequestQueryParams());
        List<ArgBean> pathParams = convertSubParameter(doc.getRequestPathParam());
        List<ArgBean> bodyParams = convertSubParameter(doc.getRequestBodyParams());
        List<ArgBean> responseParams = convertSubParameter(doc.getResponseParams());

        pathParams.forEach(e -> e.setDescription("[Path  Param] " + e.getDescription()));
        queryParams.forEach(p -> p.setDescription("[Query Param] " + p.getDescription()));
        bodyParams.forEach(p -> p.setDescription(p.getDescription()));

        xiaoyaojiDoc.setRequestArgs(Stream.of(pathParams, queryParams, bodyParams).flatMap(Collection::stream).collect(Collectors.toList()));
        xiaoyaojiDoc.setRequestHeaders(headParams);
        xiaoyaojiDoc.setResponseArgs(responseParams);

        return xiaoyaojiDoc;
    }

    private static List<ArgBean> convertSubParameter(List<RestParam> requestQueryParams) {
        ArrayList<ArgBean> args = new ArrayList<>();
        if (requestQueryParams == null || requestQueryParams.isEmpty()) {
            return args;
        }

        for (RestParam requestQueryParam : requestQueryParams) {
            ArgBean arg = new ArgBean();
            arg.setId(UUID.randomUUID().toString().replaceAll("-", ""));
            arg.setName(requestQueryParam.getName());
            arg.setDescription(requestQueryParam.getDescription());
            arg.setRequire(requestQueryParam.getRequired().toString());
            arg.setType(convertArgType(requestQueryParam));
            arg.setChildren(convertSubParameter(requestQueryParam.getSubParams()));
            args.add(arg);
        }

        return args;
    }

    public static String convertArgType(RestParam restParam) {

        String type = restParam.getType();
        String subType = restParam.getSubType();

        if (RestPsiUtil.isListType(restParam.getType())) {
            if (subType != null) {
                if (stringTypes.contains(subType)) {
                    return "array[string]";
                }
                if (booleanTypes.contains(subType)) {
                    return "array[boolean]";
                }
                if (numberTypes.contains(subType)) {
                    return "array[number]";
                }
                if (listTypes.contains(subType)) {
                    return "array[array]";
                }
            }
            return "array[object]";
        } else {

            if (stringTypes.contains(type)) {
                return "string";
            }
            if (booleanTypes.contains(type)) {
                return "boolean";
            }
            if (numberTypes.contains(type)) {
                return "number";
            }
        }
        return "object";
    }


}
