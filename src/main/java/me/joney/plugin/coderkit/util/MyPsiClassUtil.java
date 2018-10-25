package me.joney.plugin.coderkit.util;

import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.util.PsiMethodUtil;
import java.util.ArrayList;
import java.util.List;

/**
 * @author yang.qiang
 * @date 2018/10/20
 */
public class MyPsiClassUtil {



    public static List<PsiMethod> getSetters(PsiClass psiClass) {
        ArrayList<PsiMethod> setters = new ArrayList<>();
        for (PsiMethod psiMethod : psiClass.getAllMethods()) {
            if (MyPsiMethodUtil.isSetter(psiMethod)) {
                setters.add(psiMethod);
            }
        }
        return setters;
    }
}
