package me.joney.plugin.coderkit.xiaoyaoji.ui;

import com.intellij.ide.util.TreeChooser;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.psi.PsiDirectory;
import javax.swing.JComponent;
import me.joney.plugin.coderkit.xiaoyaoji.xiaoyaoji.XiaoyaojiDocStruct;
import org.jetbrains.annotations.Nullable;

/**
 * Created by yang.qiang on 2018/10/07.
 */
public class DocStructChooser extends DialogWrapper implements TreeChooser<XiaoyaojiDocStruct> {

    protected DocStructChooser(@Nullable Project project, boolean canBeParent) {
        super(project, canBeParent);
    }

    @Override
    public XiaoyaojiDocStruct getSelected() {
        return null;
    }

    @Override
    public void select(XiaoyaojiDocStruct aClass) {

    }

    @Override
    public void selectDirectory(PsiDirectory directory) {

    }

    @Override
    public void showDialog() {

    }

    @Override
    public void showPopup() {

    }

    @Nullable
    @Override
    protected JComponent createCenterPanel() {
        return null;
    }
}
