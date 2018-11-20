package me.joney.plugin.coderkit.xiaoyaoji.ui;

import com.intellij.ide.util.ChooseElementsDialog;
import com.intellij.openapi.project.Project;
import java.awt.Component;
import java.util.List;
import javax.swing.Icon;
import me.joney.plugin.coderkit.xiaoyaoji.xiaoyaoji.XiaoyaojiDocStruct;
import org.jetbrains.annotations.Nullable;

/**
 * Created by yang.qiang on 2018/10/07.
 */
public class TreeChooserDialog extends ChooseElementsDialog<XiaoyaojiDocStruct> {


    public TreeChooserDialog(Project project, List<? extends XiaoyaojiDocStruct> items, String title, String description) {
        super(project, items, title, description);
    }

    public TreeChooserDialog(Project project, List<? extends XiaoyaojiDocStruct> items, String title, String description, boolean sort) {
        super(project, items, title, description, sort);
    }

    public TreeChooserDialog(Component parent, List<XiaoyaojiDocStruct> items, String title) {
        super(parent, items, title);
    }

    public TreeChooserDialog(Component parent, List<XiaoyaojiDocStruct> items, String title, @Nullable String description, boolean sort) {
        super(parent, items, title, description, sort);
    }

    @Override
    protected String getItemText(XiaoyaojiDocStruct item) {
        return item.getName();
    }

    @Nullable
    @Override
    protected Icon getItemIcon(XiaoyaojiDocStruct item) {
        return null;
    }
}
