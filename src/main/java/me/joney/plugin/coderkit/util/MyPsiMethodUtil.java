package me.joney.plugin.coderkit.util;

import com.intellij.psi.PsiKeyword;
import com.intellij.psi.PsiMethod;

/**
 * @author yang.qiang
 * @date 2018/10/20
 */
public class MyPsiMethodUtil {


    public static boolean isSetter(PsiMethod psiMethod) {
        if (psiMethod == null) {
            return false;
        }
        if (!psiMethod.hasModifierProperty(PsiKeyword.PUBLIC)) {
            return false;
        }
        if (psiMethod.hasModifierProperty(PsiKeyword.STATIC)) {
            return false;
        }
        return psiMethod.getName().startsWith("set");
    }

}
