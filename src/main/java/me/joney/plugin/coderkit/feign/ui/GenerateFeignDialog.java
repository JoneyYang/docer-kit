package me.joney.plugin.coderkit.feign.ui;

import com.intellij.openapi.Disposable;
import com.intellij.openapi.fileTypes.StdFileTypes;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.psi.PsiElement;
import com.intellij.ui.AnActionButton;
import com.intellij.ui.ToolbarDecorator;
import com.intellij.ui.components.JBList;
import com.intellij.util.ui.table.EditorTextFieldJBTableRowRenderer;
import com.intellij.util.ui.table.JBListTable;
import com.intellij.util.ui.table.JBTableRowEditor;
import com.intellij.util.ui.table.JBTableRowRenderer;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.event.TableModelListener;
import javax.swing.table.TableModel;
import lombok.Data;
import me.joney.plugin.coderkit.apikit.postman.KeyValuePair;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import sun.security.pkcs11.Secmod.Module;

/**
 * Created by yang.qiang on 2018/12/6.
 */
public class GenerateFeignDialog extends DialogWrapper {

    private Project project;
    private Module module;
    private JPanel contentPanel;

    public GenerateFeignDialog(@Nullable Project project, boolean canBeParent) {
        super(project, canBeParent);
        init();

//        ArrayList<KeyValuePair> list = new ArrayList<>();
//        list.add(new KeyValuePair().setValue("222").setKey("xx").setSelected(false));
//        list.add(new KeyValuePair().setValue("222").setKey("xx").setSelected(false));
//        list.add(new KeyValuePair().setValue("222").setKey("xx").setSelected(false));
//        list.add(new KeyValuePair().setValue("222").setKey("xx").setSelected(false));
//        list.add(new KeyValuePair().setValue("222").setKey("xx").setSelected(false));
//        ParameterTableModel tableModel = new ParameterTableModel(list);
//
//        FeignParameterListTable listtable = new FeignParameterListTable(new JTable(tableModel), this.getDisposable());
//        JBTable table1 = listtable.getTable();
//
//        JPanel panel = new JPanel();
//        panel.add(table1);
//        panel.add(new JBTable(new ParameterTableModel(list)));
//        contentPanel = panel;

        contentPanel = ToolbarDecorator.createDecorator(new JBList<>("1", "2", "3")).createPanel();
    }



    private JPanel createParametersPanel(){

        JBListTable myParametersList = null;
        AnActionButton myPropagateParamChangesButton = null;

        JPanel panel = ToolbarDecorator.createDecorator(myParametersList.getTable()).addExtraAction(myPropagateParamChangesButton).createPanel();

//        FeignParameterListTable feignParameterListTable = new FeignParameterListTable(null,null);
//        TableView<FeignParameterListTable> myParametersTable = new TableView<FeignParameterListTable>(null){
//
//            @Override
//            public void removeEditor() {
//                super.removeEditor();
//            }
//        };

//        myParametersTable.setCellSelectionEnabled(true);
//        myParametersTable.getSelectionModel().setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
//        myParametersTable.getSelectionModel().setSelectionInterval(0, 0);
//        myParametersTable.setSurrendersFocusOnKeystroke(true);
//
//        myParametersList = createParametersListTable();
//        final JPanel buttonsPanel = ToolbarDecorator.createDecorator(myParametersList.getTable())
//            .addExtraAction(myPropagateParamChangesButton)
//            .createPanel();
//        myParametersList.getTable().getModel().addTableModelListener(mySignatureUpdater);
//        return buttonsPanel;
        return null;
    }



    private class FeignParameterListTable extends JBListTable {

        public FeignParameterListTable(@NotNull JTable t, @NotNull Disposable parent) {
            super(t, parent);
        }


        @Override
        protected JBTableRowRenderer getRowRenderer(int row) {
            return new EditorTextFieldJBTableRowRenderer(project, StdFileTypes.JAVA, null) {
                @Override
                protected String getText(JTable table, int row1) {
                    return "Haaaaaaaa" + row1;
                }
            };
        }

        @Override
        protected JBTableRowEditor getRowEditor(int row) {
            return null;
//            return new JBTableRowEditor() {
//                private EditorTextField myNameField;
//                private EditorTextField myTypeField;
//                private EditorTextField myDescriptionField;
//                private EditorTextField myAnnotationFeild;
//                private EditorTextField myAnnotationValueField;
//                private EditorTextField myAnnotationRequiredField;
//                private EditorTextField myAnnotationDefaultField;
//
//                @Override
//                public void prepareEditor(JTable table, int row) {
//
//                }
//
//                @Override
//                public JBTableRow getValue() {
//                    return null;
//                }
//
//                @Override
//                public JComponent getPreferredFocusedComponent() {
//                }
//
//                @Override
//                public JComponent[] getFocusableComponents() {
//                    final List<JComponent> focusable = new ArrayList<>();
////                    focusable.add(myTypeEditor.getFocusTarget());
////                    focusable.add(myNameEditor.getFocusTarget());
////                    if (myDefaultValueEditor != null) {
////                        focusable.add(myDefaultValueEditor.getFocusTarget());
////                    }
////                    if (myAnyVar != null) {
////                        focusable.add(myAnyVar);
////                    }
//                    return focusable.toArray(new JComponent[focusable.size()]);
//                }
//            };

        }
    }

    @Data
    private class ParameterTableModel implements TableModel {

        private List<KeyValuePair> modelList;

        public ParameterTableModel() {
            modelList = new ArrayList<>();
        }

        public ParameterTableModel(List<KeyValuePair> modelList) {
            this.modelList = modelList;
        }

        @Override
        public int getRowCount() {
            return modelList.size();
        }

        @Override
        public int getColumnCount() {
            return 3;
        }

        @Override
        public String getColumnName(int columnIndex) {
            switch (columnIndex) {
                case 0:
                    return "Enable";
                case 1:
                    return "Key";
                case 2:
                    return "Value";
                default:
                    return "";
            }
        }

        @Override
        public Class<?> getColumnClass(int columnIndex) {
            switch (columnIndex) {
                case 0:
                    return Boolean.class;
                case 1:
                    return String.class;
                case 2:
                    return String.class;
                default:
                    return Object.class;
            }
        }

        @Override
        public boolean isCellEditable(int rowIndex, int columnIndex) {
            return true;
        }

        @Override
        public Object getValueAt(int rowIndex, int columnIndex) {
            KeyValuePair pair = modelList.get(rowIndex);

            switch (columnIndex) {
                case 0:
                    return pair.getSelected();
                case 1:
                    return pair.getKey();
                case 2:
                    return pair.getValue();
                default:
                    return null;
            }
        }

        @Override
        public void setValueAt(Object value, int rowIndex, int columnIndex) {
            KeyValuePair pair = modelList.get(rowIndex);

            switch (columnIndex) {
                case 0:
                    pair.setSelected((boolean) value);
                    break;
                case 1:
                    pair.setKey((String) value);
                    break;
                case 2:
                    pair.setValue((String) value);
                    break;
                default:
                    break;
            }


        }

        @Override
        public void addTableModelListener(TableModelListener l) {

        }

        @Override
        public void removeTableModelListener(TableModelListener l) {

        }
    }

    @Nullable
    @Override
    protected JComponent createCenterPanel() {
        return contentPanel;
    }
}
