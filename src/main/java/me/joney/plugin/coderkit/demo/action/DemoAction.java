package me.joney.plugin.coderkit.demo.action;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.LangDataKeys;
import com.intellij.openapi.project.Project;
import me.joney.plugin.coderkit.demo.ui.DemoDialog;

/**
 * @author yang.qiang
 * @date 2018/11/5
 */
public class DemoAction extends AnAction {

    @Override
    public void actionPerformed(AnActionEvent e) {
        Project project = e.getData(LangDataKeys.PROJECT);
        DemoDialog dialog = new DemoDialog(project);
        dialog.show();
        // TODO: insert action logic here
    }
}
