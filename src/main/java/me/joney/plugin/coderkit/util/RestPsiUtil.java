package me.joney.plugin.coderkit.util;

import static com.intellij.psi.util.PsiUtil.extractIterableTypeParameter;

import com.intellij.lang.jvm.JvmAnnotation;
import com.intellij.lang.jvm.JvmParameter;
import com.intellij.psi.PsiAnnotation;
import com.intellij.psi.PsiAnnotationParameterList;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiClassType;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiField;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.PsiModifierList;
import com.intellij.psi.PsiNameValuePair;
import com.intellij.psi.PsiType;
import com.intellij.psi.impl.source.PsiParameterImpl;
import com.intellij.psi.javadoc.PsiDocComment;
import com.intellij.psi.javadoc.PsiDocTag;
import com.intellij.psi.util.PsiTypesUtil;
import com.intellij.psi.util.PsiUtil;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import me.joney.plugin.coderkit.xiaoyaoji.bean.RestParam;
import org.apache.commons.lang.StringUtils;
import org.jetbrains.annotations.NotNull;

/**
 * Created by yang.qiang on 2018/09/29.
 */
public class RestPsiUtil {

    public static final String ANNOTATION_PUT_MAPPING = "org.springframework.web.bind.annotation.PutMapping";
    public static final String ANNOTATION_POST_MAPPING = "org.springframework.web.bind.annotation.PostMapping";
    public static final String ANNOTATION_DELETE_MAPPING = "org.springframework.web.bind.annotation.DeleteMapping";
    public static final String ANNOTATION_GET_MAPPING = "org.springframework.web.bind.annotation.GetMapping";
    public static final String ANNOTATION_REQUEST_MAPPING = "org.springframework.web.bind.annotation.RequestMapping";
    public static final String ANNOTATION_PATH_VARIABLE = "org.springframework.web.bind.annotation.PathVariable";
    public static final String ANNOTATION_REST_CONTROLLER = "org.springframework.web.bind.annotation.RestController";
    public static final String ANNOTATION_CONTROLLER = "org.springframework.stereotype.Controller";
    public static final String ANNOTATION_REQUEST_BODY = "org.springframework.web.bind.annotation.RequestBody";
    public static final String ANNOTATION_REQUEST_PARAM = "org.springframework.web.bind.annotation.RequestParam";


    public static final HashSet<String> mappingAnnotations = new HashSet<String>() {{
        add(ANNOTATION_PUT_MAPPING);
        add(ANNOTATION_POST_MAPPING);
        add(ANNOTATION_DELETE_MAPPING);
        add(ANNOTATION_GET_MAPPING);
        add(ANNOTATION_REQUEST_MAPPING);
    }};

    public static final HashSet<String> controllerAnnotations = new HashSet<String>() {{
        add(ANNOTATION_REST_CONTROLLER);
        add(ANNOTATION_CONTROLLER);
    }};


    public static boolean isRestApiMethod(PsiMethod psiMethod) {
        if (psiMethod == null) {
            return false;
        }

        PsiClass psiClass = psiMethod.getContainingClass();
        if (!isRestControllerClass(psiClass)) {
            return false;
        }

        PsiAnnotation[] annotations = psiMethod.getAnnotations();
        for (PsiAnnotation annotation : annotations) {
            if (mappingAnnotations.contains(annotation.getQualifiedName())) {
                return true;
            }
        }

        return false;
    }

    public static boolean isRestControllerClass(PsiClass psiClass) {
        if (psiClass == null) {
            return false;
        }

        PsiAnnotation[] annotations = psiClass.getAnnotations();
        for (PsiAnnotation annotation : annotations) {
            if (controllerAnnotations.contains(annotation.getQualifiedName())) {
                return true;
            }
        }

        return false;
    }

    /**
     * get rest api name.
     *
     * @param docComment docComment
     * @return method comment first comment line
     */
    public static String extractApiName(PsiDocComment docComment) {
        PsiElement[] descriptionElements = docComment.getDescriptionElements();
        for (PsiElement descriptionElement : descriptionElements) {
            String text = descriptionElement.getText();
            if (StringUtils.isNotBlank(text)) {
                return text.trim();
            }
        }
        return "";
    }

    /**
     * extract api description from method
     *
     * @param psiMethod PsiMethod
     * @return api description
     */
    public static String extractApiDescription(PsiMethod psiMethod) {
        ArrayList<String> commentLine = new ArrayList<>();
        PsiElement[] descriptionElements = psiMethod.getDocComment().getDescriptionElements();
        for (PsiElement descriptionElement : descriptionElements) {
            String text = descriptionElement.getText();
            if (StringUtils.isNotBlank(text)) {
                commentLine.add(text.trim());
            }
        }
        return String.join("\n", commentLine);
    }

    /**
     * extract api method from method
     *
     * @param psiMethod psiMethod
     * @return api method (POST|PUT|GET...)
     */
    public static String extractApiMethod(PsiMethod psiMethod) {
        PsiAnnotation mappingAnnotation = extractMappingAnnotation(psiMethod);
        return getMappingMethod(mappingAnnotation);
    }

    /**
     * get Mapping Annotation method value
     *
     * @param annotation Mapping Annotation
     * @return the mapping value of method attribute
     */
    public static String getMappingMethod(PsiAnnotation annotation) {
        if (annotation == null) {
            throw new IllegalArgumentException("Mapping annotation can not null!");
        }

        String annotationQualifiedName = annotation.getQualifiedName();

        if (ANNOTATION_REQUEST_MAPPING.equals(annotationQualifiedName)) {
            PsiAnnotationParameterList parameterList = annotation.getParameterList();
            PsiNameValuePair[] attributes = parameterList.getAttributes();
            for (PsiNameValuePair attribute : attributes) {
                if ("method".equals(attribute.getName())) {
                    return attribute.getLiteralValue();
                }
            }
        }
        if (ANNOTATION_POST_MAPPING.equals(annotationQualifiedName)) {
            return "POST";
        }

        if (ANNOTATION_PUT_MAPPING.equals(annotationQualifiedName)) {
            return "PUT";
        }
        if (ANNOTATION_DELETE_MAPPING.equals(annotationQualifiedName)) {
            return "DELETE";
        }
        if (ANNOTATION_GET_MAPPING.equals(annotationQualifiedName)) {
            return "GET";
        }
        return "";
    }


    /**
     * Extract Spring Mapping Annotation(PostMapping, GetMapping....) in PsiMethod
     *
     * @param restMethod PsiMethod
     * @return Spring Mapping Annotation
     */
    public static PsiAnnotation extractMappingAnnotation(PsiMethod restMethod) {
        PsiAnnotation[] annotations = restMethod.getAnnotations();
        for (PsiAnnotation annotation : annotations) {
            if (mappingAnnotations.contains(annotation.getQualifiedName())) {
                return annotation;
            }
        }
        return null;
    }

    /**
     * Extract Spring Mapping Annotation(PostMapping, GetMapping....) in PsiMethod
     *
     * @param psiClass PsiMethod
     * @return Spring Mapping Annotation
     */
    public static PsiAnnotation extractMappingAnnotation(PsiClass psiClass) {
        PsiAnnotation[] annotations = psiClass.getAnnotations();
        for (PsiAnnotation annotation : annotations) {
            if (mappingAnnotations.contains(annotation.getQualifiedName())) {
                return annotation;
            }
        }
        return null;
    }


    /**
     * extract api url from method
     *
     * @param psiMethod PsiMethod
     * @return api url
     */
    public static String extractApiUrl(PsiMethod psiMethod) {
        if (psiMethod == null) {
            return "";
        }
        PsiClass psiClass = psiMethod.getContainingClass();
        if (psiClass == null) {
            return "";
        }

        PsiAnnotation controllerMappingAnnotation = extractMappingAnnotation(psiClass);
        String controllerUrl = extractMappingValue(controllerMappingAnnotation);

        PsiAnnotation apiMappingAnnotation = extractMappingAnnotation(psiMethod);
        String methodUrl = extractMappingValue(apiMappingAnnotation);

        return controllerUrl + "/" + methodUrl;
    }

    /**
     * get Mapping Annotation Value
     *
     * @param annotation Mapping Annotation
     * @return the mapping value of value attribute
     */
    public static String extractMappingValue(PsiAnnotation annotation) {
        if (annotation == null) {
            return "";
        }

        PsiAnnotationParameterList parameterList = annotation.getParameterList();
        PsiNameValuePair[] attributes = parameterList.getAttributes();
        for (PsiNameValuePair attribute : attributes) {
            if ("value".equals(attribute.getAttributeName())) {
                String value = attribute.getLiteralValue();
                if (value.startsWith("/")) {
                    value = value.substring(1);
                }
                if (value.endsWith("/")) {
                    value = value.substring(0, value.length() - 1);
                }
                return value;
            }
        }

        return "";
    }

    public static List<RestParam> extractResponseParam(PsiMethod psiMethod) {
        PsiType returnType = psiMethod.getReturnType();
        PsiType type = floorGenericType(returnType);
        System.out.println(type);
        return null;
    }

    public static PsiType floorGenericType(PsiType type) {

        List<PsiType> psiTypes = new ArrayList<>();
        if (type instanceof PsiClassType) {
            PsiClassType pct = (PsiClassType) type;
            psiTypes = new ArrayList<>(pct.resolveGenerics().getSubstitutor().getSubstitutionMap().values());
        }
        if (psiTypes.isEmpty()) {
            return type;
        } else {
            return floorGenericType(psiTypes.get(0));
        }
    }

    public static PsiType extractResponseBodyType(PsiMethod psiMethod) {
        for (JvmParameter parameter : psiMethod.getParameters()) {
            JvmAnnotation annotation = parameter.getAnnotation(ANNOTATION_REQUEST_BODY);
            if (annotation != null) {
                return ((PsiParameterImpl) parameter).getType();
            }
        }
        return null;
    }


    @NotNull
    public static HashMap<String, String> extractDocParamTagMap(PsiDocComment methodDocComment) {
        PsiDocTag[] paramTags = methodDocComment.findTagsByName("param");
        HashMap<String, String> tagMap = new HashMap<>();
        for (PsiDocTag paramTag : paramTags) {
            PsiElement[] dataElements = paramTag.getDataElements();
            tagMap.put(dataElements[0].getText(), dataElements[1].getText());
        }
        return tagMap;
    }


    public static List<RestParam> extractFieldParam(PsiClass paramClass, Set<String> parentTyps) {

        ArrayList<RestParam> restParams = new ArrayList<>();
        ArrayList<PsiField> modelField = extractModelField(paramClass);
        for (PsiField field : modelField) {
            RestParam restParam = new RestParam();
            restParam.setName(field.getName());
            restParam.setDescription(StringUtils.trimToEmpty(extractFieldCommentContent(field)));

            PsiClass fieldTypePsiClass = PsiTypesUtil.getPsiClass(field.getType());
            if (fieldTypePsiClass != null) {
                restParam.setType(fieldTypePsiClass.getName());
            } else {
                restParam.setType(field.getTypeElement().getText());
            }
            restParam.setSubType(getSubTypeName(field));
            restParam.setRequired(containValidationAnnotation(field));

            PsiType trueType = getTrueType(field);

            if (trueType != null && !isBasicType(trueType.getPresentableText())) {
                String qualifiedName = trueType.getInternalCanonicalText();
                if (!parentTyps.contains(qualifiedName)) {

                    HashSet<String> parentTypeBranch = new HashSet<>(parentTyps);
                    parentTypeBranch.add(qualifiedName);

                    PsiClass psiClass = PsiTypesUtil.getPsiClass(trueType);
                    restParam.setSubParams(extractFieldParam(psiClass, parentTypeBranch));
                }
            }

            restParams.add(restParam);

        }
        return restParams;

    }

    private static PsiType getTrueType(PsiField field) {
        PsiType iterableType = PsiUtil.extractIterableTypeParameter(field.getType(), false);
        if (iterableType != null) {
            return iterableType;
        }

        return field.getType();
    }


    public static boolean isBasicType(String name) {
        HashSet<String> ListTypeName = new HashSet<String>() {{
            add("byte");
            add("Byte");
            add("short");
            add("Short");
            add("int");
            add("Integer");
            add("long");
            add("Long");
            add("float");
            add("Float");
            add("double");
            add("Double");
            add("boolean");
            add("Boolean");
            add("char");
            add("Char");
            add("BigDecimal");
            add("Date");
            add("String");
        }};
        return ListTypeName.contains(name);
    }


    private static Boolean containValidationAnnotation(PsiField field) {
        PsiAnnotation[] annotations = field.getAnnotations();
        for (PsiAnnotation annotation : annotations) {
            if (annotation.getQualifiedName().contains("javax.validation.constraints")) {
                return true;
            }
            if (annotation.getQualifiedName().contains("org.hibernate.validator.constraints")) {
                return true;
            }

        }
        return false;
    }

    public static String getSubTypeName(PsiField field) {
        PsiType iterableType = extractIterableTypeParameter(field.getType(), false);
        if (iterableType == null) {
            return null;
        }
        return iterableType.getPresentableText();
    }


    public static String extractFieldCommentContent(PsiField field) {
        ArrayList<String> commentLines = new ArrayList<>();
        if (field != null && field.getDocComment() != null) {
            for (PsiElement descriptionElement : field.getDocComment().getDescriptionElements()) {
                String text = descriptionElement.getText();
                if (StringUtils.isNotBlank(text)) {
                    commentLines.add(text);
                }
            }

        }
        return String.join("\n", commentLines);
    }

    public static ArrayList<PsiField> extractModelField(PsiClass aClass) {
        HashSet<String> excludeFieldNames = new HashSet<String>() {{
            add("serialVersionUID");
        }};

        ArrayList<PsiField> fields = new ArrayList<>();

        if (aClass == null) {
            return fields;
        }

        PsiField[] classFields = aClass.getFields();
        for (PsiField classField : classFields) {
            String name = classField.getName();
            if (excludeFieldNames.contains(name)) {
                continue;
            }

            PsiModifierList modifierList = classField.getModifierList();
            if (modifierList.hasModifierProperty("final")) {
                continue;
            }
            if (modifierList.hasModifierProperty("static")) {
                continue;
            }

            fields.add(classField);
        }

        return fields;
    }

    public static boolean isListType(String type) {
        HashSet<String> ListTypeName = new HashSet<String>() {{
            add("List");
            add("ArrayList");
            add("Set");
        }};
        return ListTypeName.contains(type);
    }

    public static List<RestParam> extractFieldParam(PsiClass paramClass) {
        return extractFieldParam(paramClass, new HashSet<>());
    }
}
