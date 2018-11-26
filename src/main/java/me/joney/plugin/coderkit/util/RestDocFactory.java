package me.joney.plugin.coderkit.util;

import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiMethod;
import java.util.ArrayList;
import java.util.List;
import me.joney.plugin.coderkit.apikit.bean.RestApiDoc;
import me.joney.plugin.coderkit.apikit.bean.RestPsiClass;
import me.joney.plugin.coderkit.apikit.bean.RestPsiMethod;
import org.apache.xmlbeans.impl.xb.xsdownload.DownloadedSchemaEntry;
import org.apache.xmlgraphics.ps.PSImageUtils;

/**
 * Created by yang.qiang on 2018/09/29.
 */
public class RestDocFactory {


    /**
     * export doc from method
     *
     * @param psiMethod PsiMethod
     * @return RestApiDoc
     */
    public static RestApiDoc exportDoc(PsiMethod psiMethod) {
        if (!RestPsiUtil.isRestApiMethod(psiMethod)) {
            return null;
        }

        RestPsiMethod restPsiMethod = new RestPsiMethod(psiMethod);

        RestApiDoc doc = new RestApiDoc();
        doc.setName(restPsiMethod.getCommentFirstLine());
        doc.setDescription(restPsiMethod.getCommentContent());
        doc.setMethod(restPsiMethod.getMappingMethodValue());

        RestPsiClass restPsiClass = restPsiMethod.getControllerClass();
        doc.setUrl(restPsiClass.getMappingValue() + "/" + restPsiMethod.getMappingValue());

        doc.setRequestHeadParams(restPsiMethod.getHeadParamList());
        doc.setRequestQueryParams(restPsiMethod.getQueryParamList());
        doc.setRequestBodyParams(restPsiMethod.getBodyParamList());
        doc.setRequestPathParam(restPsiMethod.getPathVariableParamList());

        doc.setResponseParams(restPsiMethod.getResponseParamList());

        return doc;
    }

    public static List<RestApiDoc> exportDoc(PsiClass psiClass) {
        ArrayList<RestApiDoc> docs = new ArrayList<>();
        if (psiClass == null) {
            return docs;
        }
        for (PsiMethod method : psiClass.getMethods()) {
            if (RestPsiUtil.isRestApiMethod(method)) {
                docs.add(exportDoc(method));
            }
        }
        return docs;
    }
}
