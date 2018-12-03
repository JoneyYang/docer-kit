package me.joney.plugin.coderkit.util;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.MessageType;
import com.intellij.openapi.ui.popup.Balloon;
import com.intellij.openapi.ui.popup.JBPopupFactory;
import com.intellij.openapi.wm.StatusBar;
import com.intellij.openapi.wm.WindowManager;
import com.intellij.ui.awt.RelativePoint;

/**
 * Created by yang.qiang on 2018/12/3.
 */
public class MessageUtil {

    /**
     * 提示POP
     *
     * @param project project
     * @param content content
     * @param messageType messageType
     */
    public static void popup(Project project, String content, MessageType messageType) {
        StatusBar statusBar = WindowManager.getInstance().getStatusBar(project);
        JBPopupFactory.getInstance()
            .createHtmlTextBalloonBuilder(content, messageType, null)
            .setFadeoutTime(3000)
            .createBalloon()
            .show(RelativePoint.getCenterOf(statusBar.getComponent()), Balloon.Position.atRight);
    }


}
