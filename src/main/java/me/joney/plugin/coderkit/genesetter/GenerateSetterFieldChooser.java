package me.joney.plugin.coderkit.genesetter;

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

/**
 * @author yang.qiang
 * @date 2018/10/21
 */
public class GenerateSetterFieldChooser extends DialogWrapper {

//    private final PsiField[] myFields;
    private JList<SetterMember> myList;
    private final List<SetterMember> setterMemberList;

    public GenerateSetterFieldChooser(List<SetterMember> setterMemberList, Project project) {
        super(project, true);
        this.setterMemberList = setterMemberList;
        init();
    }

    @Override
    protected JComponent createCenterPanel() {
        final DefaultListModel model = new DefaultListModel ();
        for (SetterMember member : setterMemberList) {
            model.addElement(member);
        }
        myList = new JBList(model);
        myList.setCellRenderer(new GenerateSetterFieldChooser.MyListCellRenderer());
        new DoubleClickListener() {
            @Override
            protected boolean onDoubleClick(MouseEvent e) {
                if (myList.getSelectedValues().length > 0) {
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

    private static class MyListCellRenderer extends DefaultListCellRenderer {
        @Override
        public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
            super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
            Icon icon = null;
            if (value instanceof PsiField) {
                PsiField field = (PsiField)value;
                icon = field.getIcon(0);
                final String text = PsiFormatUtil.formatVariable(field, PsiFormatUtilBase.SHOW_NAME | PsiFormatUtilBase.SHOW_TYPE, PsiSubstitutor.EMPTY);
                setText(text);
            }
            super.setIcon(icon);
            return this;
        }
    }
}
