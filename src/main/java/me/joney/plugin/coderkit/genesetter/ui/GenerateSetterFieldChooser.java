package me.joney.plugin.coderkit.genesetter.ui;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.psi.PsiField;
import com.intellij.psi.PsiSubstitutor;
import com.intellij.psi.util.PsiFormatUtil;
import com.intellij.psi.util.PsiFormatUtilBase;
import com.intellij.ui.DoubleClickListener;
import com.intellij.ui.components.JBList;
import com.intellij.util.ui.JBUI;
import java.awt.Component;
import java.awt.event.MouseEvent;
import java.util.List;
import javax.swing.DefaultListCellRenderer;
import javax.swing.DefaultListModel;
import javax.swing.Icon;
import javax.swing.JComponent;
import javax.swing.JList;
import me.joney.plugin.coderkit.genesetter.SetterMember;

/**
 * @author yang.qiang
 * @date 2018/10/21
 */
public class GenerateSetterFieldChooser extends DialogWrapper {

    private final List<SetterMember> setterMemberList;
    private JList<SetterMember> myList;

    public GenerateSetterFieldChooser(List<SetterMember> setterMemberList, Project project) {
        super(project, true);
        this.setterMemberList = setterMemberList;
        init();
    }

    private static class MyListCellRenderer extends DefaultListCellRenderer {

        private static final long serialVersionUID = -504818487572788780L;

        @Override
        public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
            super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
            Icon icon = null;
            if (value instanceof PsiField) {
                PsiField field = (PsiField) value;
                icon = field.getIcon(0);
                final String text = PsiFormatUtil
                    .formatVariable(field, PsiFormatUtilBase.SHOW_NAME | PsiFormatUtilBase.SHOW_TYPE, PsiSubstitutor.EMPTY);
                setText(text);
            }
            super.setIcon(icon);
            return this;
        }
    }

    @Override
    protected JComponent createCenterPanel() {
        final DefaultListModel<SetterMember> model = new DefaultListModel<>();
        for (SetterMember member : setterMemberList) {
            model.addElement(member);
        }
        myList = new JBList<>(model);
        myList.setCellRenderer(new GenerateSetterFieldChooser.MyListCellRenderer());
        new DoubleClickListener() {
            @Override
            protected boolean onDoubleClick(MouseEvent e) {
                if (!myList.getSelectedValuesList().isEmpty()) {
                    doOKAction();
                    return true;

                }
                return false;
            }
        }.installOn(myList);

        myList.setPreferredSize(JBUI.size(300, 400));

        return myList;
    }

    public List<SetterMember> getSelectedElements() {
        return myList.getSelectedValuesList();
    }
}
