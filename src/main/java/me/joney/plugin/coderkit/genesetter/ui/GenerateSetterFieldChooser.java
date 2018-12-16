package me.joney.plugin.coderkit.genesetter.ui;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.psi.PsiElement;
import com.intellij.ui.components.JBList;
import com.intellij.util.ui.JBUI;
import java.awt.Component;
import java.util.List;
import javax.swing.DefaultListCellRenderer;
import javax.swing.DefaultListModel;
import javax.swing.JComponent;
import javax.swing.JList;
import me.joney.plugin.coderkit.genesetter.BeanMember;

/**
 * @author yang.qiang
 * @date 2018/10/21
 */
public class GenerateSetterFieldChooser extends DialogWrapper {

    private final List<BeanMember> setterMemberList;
    private JList<BeanMember> myList;

    public GenerateSetterFieldChooser(List<BeanMember> setterMemberList, Project project) {
        super(project, true);
        this.setterMemberList = setterMemberList;
        init();
    }

    private static class MyListCellRenderer extends DefaultListCellRenderer {

        @Override
        public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
            super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
            BeanMember beanMember = (BeanMember) value;
            String fieldName = beanMember.getField().getName();
            String type = beanMember.getField().getType().getPresentableText();
            PsiElement[] descriptionElements = beanMember.getField().getDocComment().getDescriptionElements();
            String comment = "";
            if (descriptionElements.length < 2) {
                comment = "// " + descriptionElements[1].getText().trim();
            }
            setText(fieldName + " : " + type + comment);
            return this;
        }
    }

    @Override
    protected JComponent createCenterPanel() {
        final DefaultListModel<BeanMember> model = new DefaultListModel<>();
        for (BeanMember member : setterMemberList) {
            model.addElement(member);
        }
        myList = new JBList<>(model);
        myList.setCellRenderer(new GenerateSetterFieldChooser.MyListCellRenderer());
        myList.setPreferredSize(JBUI.size(300, 400));

        return myList;
    }

    public List<BeanMember> getSelectedElements() {
        return myList.getSelectedValuesList();
    }
}
