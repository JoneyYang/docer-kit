package me.joney.plugin.coderkit.feign.intention;

import com.intellij.codeInsight.intention.IntentionAction;
import com.intellij.ide.actions.GotoActionBase;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiIdentifier;
import com.intellij.psi.PsiJavaFile;
import com.intellij.psi.PsiMethod;
import com.intellij.refactoring.changeSignature.JavaChangeSignatureDialog;
import com.intellij.util.IncorrectOperationException;
import me.joney.plugin.coderkit.feign.ui.DemoDialog;
import me.joney.plugin.coderkit.feign.ui.DemoTable2Dialog;
import me.joney.plugin.coderkit.feign.ui.DemoTableDialog;
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
    public boolean startInWriteAction() {
        return false;
    }

    @Override
    public void invoke(@NotNull Project project, Editor editor, PsiFile file) throws IncorrectOperationException {
        if (!(file instanceof PsiJavaFile)) {
            return;
        }

        PsiElement element = file.findElementAt(editor.getCaretModel().getOffset());
        PsiElement parentElement = element.getParent();
        if (!(parentElement instanceof PsiMethod)) {
            return;
        }
        PsiMethod psiMethod = (PsiMethod) parentElement;

        PsiElement psiContext = GotoActionBase.getPsiContext(project);

        DemoTable2Dialog demoDialog = new DemoTable2Dialog(project, true,psiContext);
        demoDialog.show();

//        GotoClassModel2 model = new GotoClassModel2(project);
//        ChooseByNamePopup popup = ChooseByNamePopup.createPopup(project, model, element);
//        popup.invoke(new Callback() {
//            @Override
//            public void elementChosen(Object element) {
//                System.out.println("haha");
//            }
//        }, ModalityState.defaultModalityState(),false);
//
//
//        DumbService.getInstance(project).showDumbModeNotification("Message ");
//        ArrayList<ParameterInfoImpl> objects = new ArrayList<>();
//        JavaChangeSignatureDialog dialog = new JavaChangeSignatureDialog(project, psiMethod, true, psiContext);
//        dialog.show();
//        TestDialog testDialog = new TestDialog(project, psiMethod, true, psiContext);
//        testDialog.show();
//
//        Collection<Module> list = FindClassUtil.findModulesWithClass(project, "List");
//        dialog.show();

//        final JavaCodeFragmentFactory factory = JavaCodeFragmentFactory.getInstance(project);
//        PsiTypeCodeFragment fragment = factory.createTypeCodeFragment("List", psiMethod, true, JavaCodeFragmentFactory.ALLOW_VOID);
//        System.out.println(fragment);


//        final String returnTypeText = StringUtil.notNullize(myMethod.getReturnTypeText());
//        final JavaCodeFragmentFactory factory = JavaCodeFragmentFactory.getInstance(myProject);
//        return factory.createTypeCodeFragment(returnTypeText, myMethod.getMethod(), true, JavaCodeFragmentFactory.ALLOW_VOID);

//        final List<ParameterInfoImpl> parameterInfos = new ArrayList<>();
//        final PsiReferenceExpression refExpr = JavaTargetElementEvaluator.findReferenceExpression(editor);
//
//        GenerateFeignDialog dialog = new GenerateFeignDialog(project, true);
//        dialog.show();
//
//        System.out.println("END");

//        JavaCallerChooser chooser = new JavaCallerChooser(psiMethod, project, "xxx", null, null);
//        chooser.show();

    }

}
