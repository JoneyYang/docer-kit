package me.joney.plugin.coderkit.apikit.intention;

import com.intellij.codeInsight.intention.IntentionAction;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleUtil;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiIdentifier;
import com.intellij.psi.PsiJavaFile;
import com.intellij.psi.PsiMethod;
import com.intellij.util.IncorrectOperationException;
import java.util.ArrayList;
import java.util.List;
import me.joney.plugin.coderkit.apikit.bean.RestApiDoc;
import me.joney.plugin.coderkit.apikit.ui.GenerateXiaoyaojiDocDialog;
import me.joney.plugin.coderkit.util.RestDocFactory;
import me.joney.plugin.coderkit.util.RestPsiUtil;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.Nls.Capitalization;
import org.jetbrains.annotations.NotNull;

/**
 * Created by yang.qiang on 2018/09/29.
 */
public class GenerateXiaoyaojiDocIntentionAction implements IntentionAction {

    @Nls(capitalization = Capitalization.Sentence)
    @NotNull
    @Override
    public String getText() {
        return "Generate xiaoyaoji doc";
    }

    @Nls(capitalization = Capitalization.Sentence)
    @NotNull
    @Override
    public String getFamilyName() {
        return "Generate xiaoyaoji doc";
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
        } else if (parentElement instanceof PsiClass) {
            PsiClass psiClass = (PsiClass) parentElement;
            return RestPsiUtil.isRestControllerClass(psiClass);
        } else {
            return false;
        }


    }

    @Override
    public void invoke(@NotNull Project project, Editor editor, PsiFile file) throws IncorrectOperationException {
        PsiElement element = file.findElementAt(editor.getCaretModel().getOffset());
        PsiElement parentElement = element.getParent();

        List<RestApiDoc> docs = new ArrayList<>();
        if ((parentElement instanceof PsiMethod)) {
            PsiMethod psiMethod = (PsiMethod) parentElement;
            RestApiDoc doc = RestDocFactory.exportDoc(psiMethod);
            docs.add(doc);
        } else if (parentElement instanceof PsiClass) {
            PsiClass psiClass = (PsiClass) parentElement;
            List<RestApiDoc> controllerDocs = RestDocFactory.exportDoc(psiClass);
            docs.addAll(controllerDocs);
        }

        if (!docs.isEmpty()) {
            Module module = ModuleUtil.findModuleForFile(file);
            GenerateXiaoyaojiDocDialog dialog = new GenerateXiaoyaojiDocDialog(project,module,docs);
            dialog.show();
        } else {
            // TODO 提示消息
        }
    }




    @Override
    public boolean startInWriteAction() {
        return false;
    }
}
