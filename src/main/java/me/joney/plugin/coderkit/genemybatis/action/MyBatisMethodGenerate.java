package me.joney.plugin.coderkit.genemybatis.action;


import com.intellij.CommonBundle;
import com.intellij.codeInsight.hint.HintManager;
import com.intellij.codeInsight.navigation.actions.GotoDeclarationAction;
import com.intellij.find.FindBundle;
import com.intellij.find.FindManager;
import com.intellij.find.actions.FindUsagesAction;
import com.intellij.find.actions.FindUsagesInFileAction;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.actionSystem.LangDataKeys;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiReference;
import com.intellij.psi.impl.source.PsiJavaFileImpl;
import com.intellij.psi.impl.source.resolve.reference.impl.providers.JavaClassReference;
import com.intellij.psi.impl.source.resolve.reference.impl.providers.JavaClassReferenceSet;
import com.intellij.psi.impl.source.xml.XmlFileImpl;
import com.intellij.psi.search.PsiElementProcessor;
import com.intellij.psi.xml.XmlAttribute;
import com.intellij.psi.xml.XmlTag;
import com.intellij.refactoring.psi.SearchUtils;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import me.joney.plugin.coderkit.genemybatis.ui.MyBatisMethodGenerateUi;
import org.jetbrains.annotations.NotNull;

/**
 * @author yang.qiang
 * @date 2018/10/25
 */
public class MyBatisMethodGenerate extends AnAction {


    static void chooseAmbiguousTargetAndPerform(@NotNull final Project project,
        final Editor editor,
        @NotNull PsiElementProcessor<PsiElement> processor) {
        if (editor == null) {
            Messages.showMessageDialog(project, FindBundle.message("find.no.usages.at.cursor.error"), CommonBundle.getErrorTitle(),
                Messages.getErrorIcon());
        } else {
            int offset = editor.getCaretModel().getOffset();
            boolean chosen = GotoDeclarationAction
                .chooseAmbiguousTarget(editor, offset, processor, FindBundle.message("find.usages.ambiguous.title"), null);
            if (!chosen) {
                ApplicationManager.getApplication().invokeLater(() -> {
                    if (editor.isDisposed() || !editor.getComponent().isShowing()) {
                        return;
                    }
                    HintManager.getInstance().showErrorHint(editor, FindBundle.message("find.no.usages.at.cursor.error"));
                }, project.getDisposed());
            }
        }
    }

    public static class ShowSettingsAndFindUsages extends FindUsagesAction {

        @Override
        protected void startFindUsages(@NotNull PsiElement element) {
            FindManager.getInstance(element.getProject()).findUsages(element, true);
        }
    }

    @Override
    public boolean startInTransaction() {
        return true;
    }

    @Override
    public void actionPerformed(AnActionEvent e) {
        Project project = e.getData(CommonDataKeys.PROJECT);
        PsiFile psiFile = e.getData(LangDataKeys.PSI_FILE);

        if (!(psiFile instanceof PsiJavaFileImpl)) {
            return;
        }
        PsiClass[] classes = ((PsiJavaFileImpl) psiFile).getClasses();
        if (classes.length <= 0) {
            return;
        }
        PsiClass psiElement = classes[0];
        if (psiElement == null) {
            return;
        }
        Set<XmlFileImpl> xmlFiles = getReferenceXml(psiElement);
        if (xmlFiles.isEmpty()) {
            return;
        }

        new MyBatisMethodGenerateUi(project,xmlFiles);

//        for (XmlFileImpl xmlFile : xmlFiles) {
//
//            // 获取所有resultMap tag
//            List<XmlTag> resultMapList = getResultMapList(xmlFile);
//
//            for (XmlTag xmlTag : resultMapList) {
//
//                XmlAttribute typeAttr = xmlTag.getAttribute("type");
//                if (typeAttr != null) {
//                    String typeValue = typeAttr.getValue();
//                }
//                XmlTag[] results = xmlTag.findSubTags("result");
//            }
//        }
    }

    private List<XmlTag> getResultMapList(XmlFileImpl xmlFile) {
        XmlTag[] resultMaps = xmlFile.getDocument().getRootTag().findSubTags("resultMap");
        if (resultMaps == null || resultMaps.length < 1) {
            return new ArrayList<>();
        }

        return Arrays.asList(resultMaps);
    }

    private Set<XmlFileImpl> getReferenceXml(PsiElement psiElement) {
        HashSet<XmlFileImpl> xmlFiles = new HashSet<>();
        try {
            Iterable<PsiReference> allReferences = SearchUtils.findAllReferences(psiElement);
            for (PsiReference reference : allReferences) {
                if (reference instanceof JavaClassReference) {
                    JavaClassReferenceSet javaClassReferenceSet = ((JavaClassReference) reference).getJavaClassReferenceSet();
                    PsiFile file = javaClassReferenceSet.getProvider().getContextFile(javaClassReferenceSet.getElement());
                    if (file instanceof XmlFileImpl) {
                        xmlFiles.add((XmlFileImpl) file);
                    }
                }

            }
        } catch (Exception e1) {
            e1.printStackTrace();
        }
        return xmlFiles;
    }

    protected void startFindUsages(@NotNull PsiElement element) {
        FindManager.getInstance(element.getProject()).findUsages(element);
    }

    @Override
    public void update(AnActionEvent event) {
        FindUsagesInFileAction.updateFindUsagesAction(event);
    }
}

