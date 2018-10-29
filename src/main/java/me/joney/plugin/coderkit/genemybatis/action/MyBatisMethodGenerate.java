package me.joney.plugin.coderkit.genemybatis.action;

import com.intellij.codeInsight.CodeInsightActionHandler;
import com.intellij.codeInsight.generation.actions.BaseGenerateAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiCompiledElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiJavaFile;
import org.jetbrains.annotations.NotNull;

/**
 * @author yang.qiang
 * @date 2018/10/25
 */
public class MyBatisMethodGenerate extends BaseGenerateAction {


    public MyBatisMethodGenerate() {
        super(null);
    }

    public MyBatisMethodGenerate(CodeInsightActionHandler handler) {
        super(handler);
    }

    @Override
    public void actionPerformed(AnActionEvent e) {
    }

    @Override
    protected boolean isValidForFile(@NotNull Project project, @NotNull Editor editor, @NotNull PsiFile file) {
        if (!(file instanceof PsiJavaFile)) {
            return false;
        }
        if (file instanceof PsiCompiledElement) {
            return false;
        }

        PsiClass targetClass = getTargetClass(editor, file);
        return targetClass.isInterface();
    }
}
