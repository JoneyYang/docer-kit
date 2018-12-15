package me.joney.plugin.coderkit.feign.ui;

import com.intellij.openapi.editor.event.DocumentEvent;
import com.intellij.openapi.editor.event.DocumentListener;
import com.intellij.openapi.fileTypes.StdFileTypes;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.ui.EditorTextField;
import com.intellij.ui.ToolbarDecorator;
import com.intellij.ui.table.TableView;
import com.intellij.util.ui.ListTableModel;
import com.intellij.util.ui.table.EditorTextFieldJBTableRowRenderer;
import com.intellij.util.ui.table.JBListTable;
import com.intellij.util.ui.table.JBTableRow;
import com.intellij.util.ui.table.JBTableRowEditor;
import com.intellij.util.ui.table.JBTableRowRenderer;
import java.awt.BorderLayout;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JTable;
import lombok.Data;
import lombok.ToString;
import lombok.experimental.Accessors;
import org.jetbrains.annotations.Nullable;

/**
 * Created by yang.qiang on 2018/12/14.
 */
public class DemoTableDialog extends DialogWrapper {

    private Project project;
    private JPanel content;
    private JBListTable listTable;
    private TableView<ParameterTableModelItem> tableView;
    private ListTableModel<ParameterTableModelItem> tableModel;

    public DemoTableDialog(@Nullable Project project, boolean canBeParent) {
        super(project, canBeParent);
        this.project = project;

        initComponent();

    }

    @Override
    protected void doOKAction() {
        List<ParameterTableModelItem> items = tableModel.getItems();
        for (ParameterTableModelItem item : items) {
        System.out.println(item);

        }
        super.doOKAction();
    }

    private class ParametersJBListTable extends JBListTable {

        JBTableRowRenderer myRowRenderer = new EditorTextFieldJBTableRowRenderer(project, StdFileTypes.JAVA, DemoTableDialog.this.getDisposable()) {
            @Override
            protected String getText(JTable table, int row) {
                ParameterTableModelItem item = getRowItem(row);
                return item.getName() + ":" + item.getType() + "  " + item.getDefaultValue();
            }
        };

        public ParametersJBListTable() {
            super(tableView, DemoTableDialog.this.getDisposable());
        }

        @Override
        protected JBTableRowRenderer getRowRenderer(int row) {
            return getRowRenderer();

        }

        private JBTableRowRenderer getRowRenderer() {
            return myRowRenderer;
        }

        protected JBTableRowEditor getRowEditor(ParameterTableModelItem item) {
            return new JBTableRowEditor() {
                private EditorTextField myTypeEditor;
                private EditorTextField myNameEditor;
                private EditorTextField myDefaultValueEditor;


                @Override
                public void fireDocumentChanged(DocumentEvent e, int column) {
                    ParameterTableModelItem xxxx = getRowItem(column);
                    System.out.println("fireDocumentChanged "+xxxx.getName());
                    super.fireDocumentChanged(e, column);
                }

                @Override
                public void prepareEditor(JTable table, int row) {
                    setLayout(new BorderLayout());
                    myTypeEditor = new EditorTextField(item.getType());
                    myTypeEditor.addDocumentListener(new DocumentListener() {
                        @Override
                        public void documentChanged(DocumentEvent event) {
                            item.setType(event.getDocument().getText());
                        }
                    });
                    add(createLabeledPanel("Type:", myTypeEditor), BorderLayout.WEST);
                    myNameEditor = new EditorTextField(item.getName());
                    myNameEditor.addDocumentListener(new DocumentListener() {
                        @Override
                        public void documentChanged(DocumentEvent event) {
                            item.setName(event.getDocument().getText());
                        }
                    });
                    add(createLabeledPanel("Name:", myNameEditor), BorderLayout.CENTER);
                    myDefaultValueEditor = new EditorTextField(item.getDefaultValue());
                    myDefaultValueEditor.addDocumentListener(new DocumentListener() {
                        @Override
                        public void documentChanged(DocumentEvent event) {
                            item.setDefaultValue(event.getDocument().getText());
                        }
                    });
                    add(createLabeledPanel("Default value:", myDefaultValueEditor), BorderLayout.EAST);
                }

                @Override
                public JBTableRow getValue() {
                    return column -> {
                        switch (column) {
                            case 0:
                                myTypeEditor.getText();
                            case 1:
                                myNameEditor.getText();
                            case 2:
                                myDefaultValueEditor.getText();
                            default:
                                return null;
                        }
                    };
                }



                @Override
                public JComponent getPreferredFocusedComponent() {
                    return myTypeEditor.getFocusTarget();
                }

                @Override
                public JComponent[] getFocusableComponents() {
                    final List<JComponent> focusable = new ArrayList<>();
                    focusable.add(myTypeEditor.getFocusTarget());
                    focusable.add(myNameEditor.getFocusTarget());
                    focusable.add(myDefaultValueEditor.getFocusTarget());
                    return focusable.toArray(new JComponent[0]);
                }
            };
        }

        protected ParameterTableModelItem getRowItem(int row) {
            return tableView.getItems().get(row);
        }


        @Override
        protected JBTableRowEditor getRowEditor(int row) {

            JBTableRowEditor editor = getRowEditor(getRowItem(row));
            // 可以添加DocumentListener
            // See ChangeSignatureDialogBase.java:696
            return editor;
        }

        @Override
        protected boolean isRowEmpty(int row) {
            ParameterTableModelItem rowItem = getRowItem(row);
            return false;
        }

    }

    @Override
    public void doCancelAction() {
        System.out.println("doCancelAction");
        super.doCancelAction();
    }

    @Data
    @Accessors(chain = true)
    @ToString
    private class ParameterTableModelItem {

        private String name;
        private String type;
        private String defaultValue;

    }

    private void initComponent() {
        content = new JPanel();
        // 创建TableView
        tableView = createTableView();
        // 创建
        listTable = createJBListTable();
        // 创建Table容器
        JPanel tablePanel = createTablePanel();
        // 设置布局
        content.add(tablePanel);
        init();
    }

    private TableView<ParameterTableModelItem> createTableView() {
        tableModel = new ListTableModel<ParameterTableModelItem>() {
            @Override
            public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
                System.out.println("setValueAt(Object aValue, int rowIndex, int columnIndex)");
                super.setValueAt(aValue, rowIndex, columnIndex);
            }

            @Override
            public void setValueAt(Object aValue, int rowIndex, int columnIndex, boolean notifyListeners) {
                System.out.println("setValueAt");
                super.setValueAt(aValue, rowIndex, columnIndex, notifyListeners);
            }

            private static final long serialVersionUID = -4316993996840420618L;
        };

        List<ParameterTableModelItem> items = new ArrayList<>();
        tableModel.setItems(items);
        return new TableView<>(tableModel);

    }


    private JBListTable createJBListTable() {
        ParametersJBListTable listTable = new ParametersJBListTable();
        return listTable;
    }

    private JPanel createTablePanel() {
        JPanel panel = ToolbarDecorator.createDecorator(listTable.getTable())
            .setAddAction(anActionButton -> {
//                List<ParameterTableModelItem> items = tableModel.getItems();
//
//                newList.add(newItem);
//                tableModel.setItems(newList);
//                ArrayList<ParameterTableModelItem> newList = new ArrayList<>(items);
                ParameterTableModelItem newItem = new ParameterTableModelItem().setName("").setType("").setDefaultValue("");
                tableModel.addRow(newItem);
            })
            .createPanel();
        panel.setSize(300, 400);
        return panel;
    }

    @Nullable
    @Override
    protected JComponent createCenterPanel() {
        return content;
    }

}
