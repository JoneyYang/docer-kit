package me.joney.plugin.coderkit.xiaoyaoji.intention;

import com.intellij.codeInsight.intention.IntentionAction;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiIdentifier;
import com.intellij.psi.PsiJavaFile;
import com.intellij.psi.PsiMethod;
import com.intellij.util.IncorrectOperationException;
import java.awt.Toolkit;
import java.awt.datatransfer.StringSelection;
import me.joney.plugin.coderkit.util.RestDocFactory;
import me.joney.plugin.coderkit.util.RestPsiUtil;
import me.joney.plugin.coderkit.xiaoyaoji.bean.RestApiDoc;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.Nls.Capitalization;
import org.jetbrains.annotations.NotNull;

/**
 * Created by yang.qiang on 2018/10/08.
 */
public class CopyUrlIntentionAction  implements IntentionAction {

    @Nls(capitalization = Capitalization.Sentence)
    @NotNull
    @Override
    public String getText() {
        return "Copy rest api url";
    }

    @Nls(capitalization = Capitalization.Sentence)
    @NotNull
    @Override
    public String getFamilyName() {
        return "Copy rest api url";
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

        if (!(parentElement instanceof PsiMethod)) {
            return false;
        } else {
            PsiMethod psiMethod = (PsiMethod) parentElement;
            return RestPsiUtil.isRestApiMethod(psiMethod);
        }


    }

    @Override
    public void invoke(@NotNull Project project, Editor editor, PsiFile file) throws IncorrectOperationException {
        PsiElement element = file.findElementAt(editor.getCaretModel().getOffset());
        PsiElement parentElement = element.getParent();
        PsiMethod psiMethod = (PsiMethod) parentElement;

        psiMethod.getContainingClass().accept(new PsiElementVisitor() {
            @Override
            public void visitElement(PsiElement element) {
                super.visitElement(element);
            }
        });


        RestApiDoc doc = RestDocFactory.exportDoc(psiMethod);

        Toolkit.getDefaultToolkit().getSystemClipboard().setContents(new StringSelection(doc.getUrl()),null);

    }

    @Override
    public boolean startInWriteAction() {
        return true;
    }
}
