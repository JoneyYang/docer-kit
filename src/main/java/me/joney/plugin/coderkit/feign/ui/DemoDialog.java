package me.joney.plugin.coderkit.feign.ui;

import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CustomShortcutSet;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.fileTypes.StdFileTypes;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.psi.JavaCodeFragmentFactory;
import com.intellij.psi.PsiCodeFragment;
import com.intellij.psi.PsiDocumentManager;
import com.intellij.psi.PsiMethod;
import com.intellij.refactoring.changeSignature.JavaParameterTableModel.JavaNameColumn;
import com.intellij.refactoring.changeSignature.JavaParameterTableModel.JavaTypeColumn;
import com.intellij.ui.AnActionButton;
import com.intellij.ui.EditorTextField;
import com.intellij.ui.ToolbarDecorator;
import com.intellij.ui.table.JBTable;
import com.intellij.ui.table.TableView;
import com.intellij.util.ui.ColumnInfo;
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
import javax.swing.event.TableModelListener;
import javax.swing.table.TableModel;
import lombok.Data;
import lombok.experimental.Accessors;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Created by yang.qiang on 2018/12/11.
 */
public class DemoDialog extends DialogWrapper {

    //    private final Descriptor myMethod;
    private Project project;
    private JPanel content;
    private PsiMethod psiMethod;
    private PsiCodeFragment typeCodeFragment;
    private EditorTextField typeField;
    private JBTable table;
    private JBListTable myParametersList;
    private TableView<ParameterTableModelItem> myParametersTable;
//    private ListTableModel<ParameterTableModelItem> tableModel;

    public DemoDialog(@Nullable Project project, boolean canBeParent, PsiMethod psiMethod) {
        super(project, canBeParent);
        this.project = project;
        this.psiMethod = psiMethod;

        initComponent();
    }

    private class ParametersJBListTable extends JBListTable {

        JBTableRowRenderer myRowRenderer = new EditorTextFieldJBTableRowRenderer(project, StdFileTypes.JAVA, DemoDialog.this.getDisposable()) {
            @Override
            protected String getText(JTable table, int row) {
                // TODO 联动实现time的展示
                return "/////////////";
            }
        };

        public ParametersJBListTable() {
            super(myParametersTable, DemoDialog.this.getDisposable());
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
                public void prepareEditor(JTable table, int row) {
                    setLayout(new BorderLayout());
                    myTypeEditor = new EditorTextField("TypeEditor");
                    add(createLabeledPanel("Type:", myTypeEditor), BorderLayout.WEST);
                    myNameEditor = new EditorTextField("NameEditor");
                    add(createLabeledPanel("Name:", myNameEditor), BorderLayout.CENTER);
                    myDefaultValueEditor = new EditorTextField("DefaultValueEditor");
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
                    return myNameEditor.getFocusTarget();
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
            return myParametersTable.getItems().get(row);
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
            // TODO 需要做判断
            return false;
        }

    }

    @Data
    private class ParameterTableModel implements TableModel {

        private List<ParameterTableModelItem> modelList;


        public ParameterTableModel() {
            modelList = new ArrayList<>();
        }

        public ParameterTableModel(List<ParameterTableModelItem> modelList) {
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
                    return String.class;
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
            ParameterTableModelItem pair = modelList.get(rowIndex);

            switch (columnIndex) {
                case 0:
                    return "";
                case 1:
                    return "";
                case 2:
                    return "";
                default:
                    return null;
            }
        }

        @Override
        public void setValueAt(Object value, int rowIndex, int columnIndex) {
            ParameterTableModelItem pair = modelList.get(rowIndex);

            switch (columnIndex) {
                case 0:
//                    pair.setSelected((boolean) value);
                    break;
                case 1:
//                    pair.setKey((String) value);
                    break;
                case 2:
//                    pair.setValue((String) value);
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

    @Data
    @Accessors(chain = true)
    private class ParameterTableModelItem {

        private String name;
        private String type;
        private String defaultValue;

    }

    private void initComponent() {
        content = new JPanel();
        typeCodeFragment = createTypeCodeFragment();
        myParametersTable = createTableView();

        typeField = createTypeField();
        content.add(typeField);

        myParametersList = new ParametersJBListTable();

        content.add(createTablePanel());

        init();
    }

    private TableView<ParameterTableModelItem> createTableView() {
        List<ParameterTableModelItem> objects = new ArrayList<>();
        objects.add(new ParameterTableModelItem().setName("hahah"));
        objects.add(new ParameterTableModelItem().setName("hahah1"));
        objects.add(new ParameterTableModelItem().setName("hahah2"));

        ListTableModel<ParameterTableModelItem> tableView = new ListTableModel<>(
//            new JavaTypeColumn(project),
//            new JavaNameColumn(project),
        );
        tableView.setItems(objects);

        return new TableView<ParameterTableModelItem>(tableView) {
        };
    }

    private JPanel createTablePanel() {

        AnActionButton button = new AnActionButton("Button Name") {
            @Override
            public void actionPerformed(AnActionEvent e) {
                System.out.println("event active");
            }
        };
        button.setShortcut(CustomShortcutSet.fromString("alt INSERT"));

        JPanel panel = ToolbarDecorator.createDecorator(myParametersList.getTable())
//            .addExtraAction(button)
            .createPanel();
        panel.setSize(300, 400);
        return panel;


    }

    // 创建CodeFragment
    private PsiCodeFragment createTypeCodeFragment() {
        final String returnTypeText = StringUtil.notNullize("String");
        final JavaCodeFragmentFactory factory = JavaCodeFragmentFactory.getInstance(project);
        return factory.createTypeCodeFragment(returnTypeText, psiMethod, true, JavaCodeFragmentFactory.ALLOW_VOID);

    }

    // 创建TextField
    private EditorTextField createTypeField() {
        final Document document = PsiDocumentManager.getInstance(project).getDocument(typeCodeFragment);
        EditorTextField field = new EditorTextField(document, project, StdFileTypes.JAVA);
        field.setText("String");
        field.setPreferredWidth(200);

        return field;
    }


    @Nullable
    @Override
    protected JComponent createCenterPanel() {
        return content;
    }
}
