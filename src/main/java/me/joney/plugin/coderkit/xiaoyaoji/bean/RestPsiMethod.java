package me.joney.plugin.coderkit.xiaoyaoji.bean;

import static com.intellij.psi.util.PsiUtil.resolveClassInClassTypeOnly;

import com.intellij.lang.jvm.JvmAnnotation;
import com.intellij.lang.jvm.JvmParameter;
import com.intellij.lang.jvm.annotation.JvmAnnotationAttribute;
import com.intellij.lang.jvm.types.JvmType;
import com.intellij.psi.PsiAnnotation;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.PsiType;
import com.intellij.psi.impl.source.PsiClassReferenceType;
import com.intellij.psi.impl.source.PsiParameterImpl;
import com.intellij.psi.impl.source.tree.java.PsiNameValuePairImpl;
import com.intellij.psi.javadoc.PsiDocComment;
import com.intellij.psi.util.PsiTypesUtil;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import lombok.Getter;
import me.joney.plugin.coderkit.util.RestPsiUtil;
import org.apache.commons.lang.StringUtils;

/**
 * Created by yang.qiang on 2018/09/30.
 */
@Getter
public class RestPsiMethod {

    private PsiType returnType;
    private PsiAnnotation mappingAnnotation;
    private PsiDocComment docComment;
    private RestPsiClass controllerClass;
    private PsiType responseBodyType;
    private Map<String, String> commentTagMap;
    private PsiMethod contentPsiMethod;


    public RestPsiMethod(PsiMethod contentPsiMethod) {
        this.contentPsiMethod = contentPsiMethod;

        this.mappingAnnotation = RestPsiUtil.extractMappingAnnotation(contentPsiMethod);
        this.docComment = contentPsiMethod.getDocComment();
        this.returnType = RestPsiUtil.floorGenericType(contentPsiMethod.getReturnType());
        this.responseBodyType = RestPsiUtil.extractResponseBodyType(contentPsiMethod);
        this.commentTagMap = RestPsiUtil.extractDocParamTagMap(docComment);

        this.controllerClass = new RestPsiClass(contentPsiMethod.getContainingClass());
    }

    public String getMappingValue() {
        return RestPsiUtil.extractMappingValue(mappingAnnotation);
    }

    public String getCommentFirstLine() {
        PsiElement[] descriptionElements = docComment.getDescriptionElements();
        for (PsiElement descriptionElement : descriptionElements) {
            String text = descriptionElement.getText();
            if (StringUtils.isNotBlank(text)) {
                return text.trim();
            }
        }
        return "";
    }

    public String getCommentContent() {
        ArrayList<String> commentLine = new ArrayList<>();
        PsiElement[] descriptionElements = contentPsiMethod.getDocComment().getDescriptionElements();
        for (PsiElement descriptionElement : descriptionElements) {
            String text = descriptionElement.getText();
            if (StringUtils.isNotBlank(text)) {
                commentLine.add(text.trim());
            }
        }
        return String.join("\n", commentLine);
    }


    public String getMappingMethodValue() {
        return RestPsiUtil.getMappingMethod(mappingAnnotation);
    }

    public List<RestParam> getResponseParamList() {
        ArrayList<RestParam> paramsList = new ArrayList<>();

        PsiClass paramClass = PsiTypesUtil.getPsiClass(returnType);
        // 通过判断能否编辑, 判断是否为内部类
        boolean writable = paramClass.isWritable();
        if (writable) {
            paramsList.addAll(RestPsiUtil.extractFieldParam(paramClass));
        }
        return paramsList;
    }

    public List<RestParam> getPathVariableParamList() {
        ArrayList<RestParam> queryParams = new ArrayList<>();
        for (JvmParameter parameter : contentPsiMethod.getParameters()) {
            JvmAnnotation pathVariableAnnotation = parameter.getAnnotation("org.springframework.web.bind.annotation.PathVariable");
            if (pathVariableAnnotation != null) {
                RestParam param = new RestParam();

                // 代码参数名称
                String psiParamName = parameter.getName();

                // 获取参数名
                JvmAnnotationAttribute valueAttribute = pathVariableAnnotation.findAttribute("value");
                if (valueAttribute != null) {
                    String pathParamName = ((PsiNameValuePairImpl) valueAttribute).getLiteralValue();
                    param.setName(pathParamName);
                } else {
                    param.setName(psiParamName);
                }

                // 设置字段描述

                param.setDescription(StringUtils.trimToEmpty(commentTagMap.get(psiParamName)));
                param.setRequired(true);
                param.setType(((PsiParameterImpl) parameter).getTypeElement().getText());
                queryParams.add(param);
            }
        }

        return queryParams;
    }

    public List<RestParam> getHeadParamList() {

        ArrayList<RestParam> queryParams = new ArrayList<>();

        for (JvmParameter parameter : contentPsiMethod.getParameters()) {
            JvmAnnotation headParamAnnotation = parameter.getAnnotation("org.springframework.web.bind.annotation.RequestHeader");
            if (headParamAnnotation != null) {
                RestParam param = new RestParam();

                // 代码参数名称
                String psiParamName = parameter.getName();

                // 获取参数名
                JvmAnnotationAttribute valueAttribute = headParamAnnotation.findAttribute("value");
                if (valueAttribute != null) {
                    String pathParamName = ((PsiNameValuePairImpl) valueAttribute).getLiteralValue();
                    param.setName(pathParamName);
                } else {
                    param.setName(psiParamName);
                }

                // 设置字段描述
                param.setDescription(StringUtils.trimToEmpty(commentTagMap.get(psiParamName)));

                // 设置Required字段
                JvmAnnotationAttribute requiredAttribute = headParamAnnotation.findAttribute("required");
                if (requiredAttribute != null) {
                    String requiredAttributeValue = ((PsiNameValuePairImpl) requiredAttribute).getLiteralValue();
                    param.setRequired(!"false".equals(requiredAttributeValue));
                } else {
                    param.setRequired(true);
                }

                param.setType(((PsiParameterImpl) parameter).getTypeElement().getText());

                queryParams.add(param);
            }

        }

        return queryParams;
    }

    public List<RestParam> getQueryParamList() {
        ArrayList<RestParam> queryParams = new ArrayList<>();

        for (JvmParameter parameter : contentPsiMethod.getParameters()) {

            JvmAnnotation requestParamAnnotation = parameter.getAnnotation(RestPsiUtil.ANNOTATION_REQUEST_PARAM);
            JvmAnnotation requestBodyAnnotation = parameter.getAnnotation(RestPsiUtil.ANNOTATION_REQUEST_BODY);
            JvmAnnotation pathVariableAnnotation = parameter.getAnnotation(RestPsiUtil.ANNOTATION_PATH_VARIABLE);

            // RequestParam 字段
            if (requestParamAnnotation != null) {
                RestParam param = new RestParam();

                // 代码参数名称
                String psiParamName = parameter.getName();

                // 获取参数名
                JvmAnnotationAttribute valueAttribute = requestParamAnnotation.findAttribute("value");
                if (valueAttribute != null) {
                    String pathParamName = ((PsiNameValuePairImpl) valueAttribute).getLiteralValue();
                    param.setName(pathParamName);
                } else {
                    param.setName(psiParamName);
                }

                // 设置字段描述
                param.setDescription(StringUtils.trimToEmpty(commentTagMap.get(psiParamName)));

                // 设置Required字段
                JvmAnnotationAttribute requiredAttribute = requestParamAnnotation.findAttribute("required");
                if (requiredAttribute != null) {
                    String requiredAttributeValue = ((PsiNameValuePairImpl) requiredAttribute).getLiteralValue();
                    param.setRequired(!"false".equals(requiredAttributeValue));
                } else {
                    param.setRequired(true);
                }

                param.setType(((PsiParameterImpl) parameter).getTypeElement().getText());

                queryParams.add(param);

            } else if (pathVariableAnnotation != null) {
                // 路径参数
                // 路径参数直接跳过

            } else if (requestBodyAnnotation != null) {
                // RequestBody 参数直接跳过

            } else {

                JvmType parameterType = parameter.getType();
                PsiClass paramClass = ((PsiClassReferenceType) parameterType).resolve();
                // 通过判断能否编辑, 判断是否为内部类
                boolean writable = paramClass.isWritable();
                if (writable) {
                    // 可写代表为 自定义Param Bean
                    queryParams.addAll(RestPsiUtil.extractFieldParam(paramClass));
                } else {
                    // 没有定义RequestParam的路径参数
                    // 剔除常见的第三方包的类型

                    String className = ((PsiClassReferenceType) parameterType).getClassName();
                    if (className.startsWith("org.springframework")) {
                        continue;
                    }
                    if (className.startsWith("javax.servlet")) {
                        continue;
                    }

                    RestParam param = new RestParam();
                    // 代码参数名称
                    String psiParamName = parameter.getName();
                    param.setName(psiParamName);
                    // 设置字段描述
                    param.setDescription(StringUtils.trimToEmpty(commentTagMap.get(psiParamName)));
                    // 设置Required字段
                    param.setRequired(false);
                    param.setType(((PsiParameterImpl) parameter).getTypeElement().getText());

                    queryParams.add(param);
                }
            }
        }

        return queryParams;
    }

    public List<RestParam> getBodyParamList() {

        ArrayList<RestParam> bodyParams = new ArrayList<>();

        JvmParameter[] parameters = contentPsiMethod.getParameters();
        for (JvmParameter parameter : parameters) {
            JvmAnnotation annotation = parameter.getAnnotation("org.springframework.web.bind.annotation.RequestBody");
            if (annotation != null) {
                PsiClass aClass = resolveClassInClassTypeOnly(((PsiParameterImpl) parameter).getType());
                bodyParams.addAll(RestPsiUtil.extractFieldParam(aClass));
            }
        }

        return bodyParams;
    }
}
