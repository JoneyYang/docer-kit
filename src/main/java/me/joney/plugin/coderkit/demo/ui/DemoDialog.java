package me.joney.plugin.coderkit.demo.ui;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.ui.components.JBBox;
import com.intellij.ui.components.JBList;
import com.intellij.ui.components.JBTextField;
import java.awt.FlowLayout;
import java.awt.Panel;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JList;
import org.jetbrains.annotations.Nullable;

/**
 * @author yang.qiang
 * @date 2018/11/5
 */
public class DemoDialog extends DialogWrapper {

    private JList<String> myList;

    public DemoDialog(@Nullable Project project) {
        super(project);
        init();
    }


    @Nullable
    @Override
    protected JComponent createCenterPanel() {
        JBList<String> stringJBList = new JBList<>();

//        stringJBList.setModel();

        JBBox jbBox = new JBBox(3);

        for (int i = 0; i < 5; i++) {

            Panel panel = new Panel(new FlowLayout(FlowLayout.LEFT));

            char c = (char) (95 + i);
            JLabel label = new JLabel("column " + c);
            panel.add(label);

            JBTextField jbTextField = new JBTextField();
            jbTextField.setToolTipText("....");
//            jbTextField.setEnabled(false);
            panel.add(jbTextField);

            jbBox.add(panel);
        }

        return jbBox;
    }

}
