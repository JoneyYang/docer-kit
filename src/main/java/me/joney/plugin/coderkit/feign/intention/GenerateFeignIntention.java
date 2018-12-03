package me.joney.plugin.coderkit.feign.intention;

import com.intellij.codeInsight.intention.IntentionAction;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleUtil;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiIdentifier;
import com.intellij.psi.PsiJavaFile;
import com.intellij.psi.PsiMethod;
import com.intellij.util.IncorrectOperationException;
import me.joney.plugin.coderkit.apikit.bean.RestApiDoc;
import me.joney.plugin.coderkit.util.RestDocFactory;
import me.joney.plugin.coderkit.util.RestPsiUtil;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.Nls.Capitalization;
import org.jetbrains.annotations.NotNull;

/**
 * Created by yang.qiang on 2018/11/30.
 */
public class GenerateFeignIntention implements IntentionAction {

    @Nls(capitalization = Capitalization.Sentence)
    @NotNull
    @Override
    public String getText() {
        return "Generate feign interface";
    }

    @Nls(capitalization = Capitalization.Sentence)
    @NotNull
    @Override
    public String getFamilyName() {
        return "Generate feign interface";
    }

    @Override
    public boolean isAvailable(@NotNull Project project, Editor editor, PsiFile file) {

        if (!(file instanceof PsiJavaFile)) {
            return false;
        }

        PsiElement element = file.findElementAt(editor.getCaretModel().getOffset());
        if (!(element instanceof PsiIdentifier)) {
            return false;
        }

        PsiElement parentElement = element.getParent();
        if ((parentElement instanceof PsiMethod)) {
            PsiMethod psiMethod = (PsiMethod) parentElement;
            return RestPsiUtil.isRestApiMethod(psiMethod);
        } else {
            return false;
        }
    }

    @Override
    public void invoke(@NotNull Project project, Editor editor, PsiFile file) throws IncorrectOperationException {
        PsiElement element = file.findElementAt(editor.getCaretModel().getOffset());
        PsiElement parentElement = element.getParent();
        PsiMethod psiMethod = (PsiMethod) parentElement;

        Module module = ModuleUtil.findModuleForFile(file);
        String controllerMappingValue = RestPsiUtil.extractMappingValue(RestPsiUtil.extractMappingAnnotation(psiMethod.getContainingClass()));
        String methodMappingValue = RestPsiUtil.extractMappingValue(RestPsiUtil.extractMappingAnnotation(psiMethod));
        String url = controllerMappingValue + "/" + methodMappingValue;

        RestApiDoc restApiDoc = RestDocFactory.exportDoc(psiMethod);

    }

    @Override
    public boolean startInWriteAction() {
        return false;
    }
}
