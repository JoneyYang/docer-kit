package me.joney.plugin.coderkit.apikit.ui;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import java.util.List;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JTextField;
import me.joney.plugin.coderkit.apikit.bean.RestApiDoc;
import org.jetbrains.annotations.Nullable;

/**
 * Created by yang.qiang on 2018/11/26.
 */
public class GeneratePostmanDialog extends DialogWrapper {

    private Project project;
    private JPanel contentPanel;
    private JTextField textField1;
    private JButton chooserButton;
    private List<RestApiDoc> docs;

    public GeneratePostmanDialog(@Nullable Project project, List<RestApiDoc> docs) {
        super(project, true);
        this.project = project;
        this.docs = docs;
        init();




    }




    @Nullable
    @Override
    protected JComponent createCenterPanel() {
        return contentPanel;
    }
}
