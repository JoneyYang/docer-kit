package me.joney.plugin.coderkit.genesetter.intention;

import com.intellij.codeInsight.intention.IntentionAction;
import com.intellij.lang.jvm.JvmParameter;
import com.intellij.lang.jvm.types.JvmType;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiDocumentManager;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiField;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiIdentifier;
import com.intellij.psi.PsiJavaFile;
import com.intellij.psi.PsiLocalVariable;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.PsiParameter;
import com.intellij.psi.PsiType;
import com.intellij.psi.impl.source.PsiClassReferenceType;
import com.intellij.psi.impl.source.PsiMethodImpl;
import com.intellij.psi.util.PsiTypesUtil;
import com.intellij.util.IncorrectOperationException;
import java.util.ArrayList;
import java.util.List;
import me.joney.plugin.coderkit.genesetter.SetterMember;
import me.joney.plugin.coderkit.genesetter.ui.GenerateSetterFieldChooser;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.Nls.Capitalization;
import org.jetbrains.annotations.NotNull;

/**
 * @author yang.qiang
 * @date 2018/10/18
 */
public class GenerateSettersIntentionAction implements IntentionAction {

    @Nls(capitalization = Capitalization.Sentence)
    @NotNull
    @Override
    public String getFamilyName() {
        return "Generate setters";
    }

    @Nls(capitalization = Capitalization.Sentence)
    @NotNull
    @Override
    public String getText() {
        return "Generate setters";
    }

    @Override
    public void invoke(@NotNull Project project, Editor editor, PsiFile file) throws IncorrectOperationException {
        PsiElement element = file.findElementAt(editor.getCaretModel().getOffset());
        PsiElement parent = element.getParent();

        PsiType type;
        int textOffset;
        if (parent instanceof PsiLocalVariable) {
            PsiLocalVariable psiLocalVariable = (PsiLocalVariable) parent;
            type = psiLocalVariable.getType();
            TextRange textRange = psiLocalVariable.getTextRange();
            textOffset = textRange.getEndOffset();
        } else if (parent instanceof PsiParameter) {
            PsiParameter psiParameter = (PsiParameter) parent;
            type = psiParameter.getType();

            PsiElement parent1 = psiParameter.getParent().getParent();
            textOffset = ((PsiMethodImpl) parent1).getBody().getTextOffset();

        }else {
            return;
        }

        PsiClass psiClass = PsiTypesUtil.getPsiClass(type);
        PsiDocumentManager psiDocumentManager = PsiDocumentManager.getInstance(project);
        Document document = psiDocumentManager.getDocument(element.getContainingFile());

        ArrayList<SetterMember> setterMembers = new ArrayList<>();

        //// 获取有setter 字段
        for (PsiField field : psiClass.getFields()) {
            String fieldName = field.getName();
            char firstChar = Character.toUpperCase(fieldName.charAt(0));

            StringBuilder sb = new StringBuilder();
            sb.append("set");
            sb.append(firstChar);
            sb.append(fieldName.substring(1));

            PsiMethod[] methods = psiClass.findMethodsByName(sb.toString(), true);
            for (PsiMethod method : methods) {
                JvmParameter[] parameters = method.getParameters();
                if (parameters.length == 1) {
                    JvmType paramType = parameters[0].getType();
                    PsiType fieldType = field.getType();
                    if (fieldType.getInternalCanonicalText().equals(((PsiClassReferenceType) paramType).getInternalCanonicalText())) {
                        setterMembers.add(new SetterMember(field, method));
                    }
                }
            }
        }

        GenerateSetterFieldChooser chooser = new GenerateSetterFieldChooser(setterMembers, project);
        chooser.show();

        List<SetterMember> selectedElements = chooser.getSelectedElements();
        if (selectedElements.isEmpty()) {
            return;
        }

        String fileText = file.getText();
        int line = editor.getCaretModel().getLogicalPosition().line;
        String lineText = fileText.split("\n")[line];
        String linePrefix = lineText.replaceFirst("^(\\s+).*$","$1");

        // 生成代码
        StringBuilder sb = new StringBuilder();
        sb.append("\n");
        for (SetterMember selectedElement : selectedElements) {
            sb.append(linePrefix);
            sb.append(element.getText());
            sb.append(".").append(selectedElement.getMethod().getName());
            sb.append("(").append("null").append(");\n");
        }
        sb.append("\n");

        document.insertString(textOffset + 1, sb.toString());

        System.out.println(setterMembers);


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

        PsiElement parent = element.getParent();
        if (!(parent instanceof PsiLocalVariable)
            && !(parent instanceof PsiParameter)) {
            return false;
        }

        return true;
    }

    @Override
    public boolean startInWriteAction() {
        return false;
    }
}
