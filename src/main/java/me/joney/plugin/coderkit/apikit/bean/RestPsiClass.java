package me.joney.plugin.coderkit.apikit.bean;

import com.intellij.psi.PsiAnnotation;
import com.intellij.psi.PsiClass;
import lombok.Data;
import me.joney.plugin.coderkit.util.RestPsiUtil;

/**
 * Created by yang.qiang on 2018/09/30.
 */
@Data
public class RestPsiClass {

    private PsiAnnotation mappingAnnotation;
    private PsiClass psiClass;

    public RestPsiClass(PsiClass psiClass) {
        this.psiClass = psiClass;
        this.mappingAnnotation = RestPsiUtil.extractMappingAnnotation(psiClass);
    }

    public String getMappingValue() {
        return RestPsiUtil.extractMappingValue(mappingAnnotation);
    }

}
