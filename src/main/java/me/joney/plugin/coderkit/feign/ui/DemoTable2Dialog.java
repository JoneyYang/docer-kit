package me.joney.plugin.coderkit.feign.ui;

import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.event.DocumentEvent;
import com.intellij.openapi.editor.event.DocumentListener;
import com.intellij.openapi.fileTypes.StdFileTypes;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.ComboBox;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.openapi.ui.VerticalFlowLayout;
import com.intellij.openapi.ui.VerticalFlowLayout.VerticalFlowAlignment;
import com.intellij.psi.JavaCodeFragmentFactory;
import com.intellij.psi.PsiCodeFragment;
import com.intellij.psi.PsiDocumentManager;
import com.intellij.psi.PsiElement;
import com.intellij.ui.EditorTextField;
import com.intellij.ui.ToolbarDecorator;
import com.intellij.ui.components.JBLabel;
import com.intellij.ui.table.TableView;
import com.intellij.util.IJSwingUtilities;
import com.intellij.util.ui.DialogUtil;
import com.intellij.util.ui.ListTableModel;
import com.intellij.util.ui.UIUtil;
import com.intellij.util.ui.UIUtil.ComponentStyle;
import com.intellij.util.ui.table.EditorTextFieldJBTableRowRenderer;
import com.intellij.util.ui.table.JBListTable;
import com.intellij.util.ui.table.JBTableRow;
import com.intellij.util.ui.table.JBTableRowEditor;
import com.intellij.util.ui.table.JBTableRowRenderer;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JTable;
import lombok.Data;
import lombok.experimental.Accessors;
import org.apache.commons.lang.StringUtils;
import org.jetbrains.annotations.Nullable;

/**
 * Created by yang.qiang on 2018/12/15.
 */
public class DemoTable2Dialog extends DialogWrapper {

    private PsiElement psiContext;
    private Project project;

    private JPanel contentPanel = new JPanel();



    private TableView<ConditionItem> tableView;
    private ListTableModel<ConditionItem> tableModel;


    public DemoTable2Dialog(@Nullable Project project, boolean canBeParent, PsiElement psiContext) {
        super(project, canBeParent);
        this.project = project;
        this.psiContext = psiContext;
        initComponents();
    }

    public static JPanel createLabeledPanel(String labelText, JComponent component) {
        return createLabeledPanel(labelText, component, VerticalFlowLayout.TOP);
    }

    public static JPanel createLabeledPanel(String labelText, JComponent component, @VerticalFlowAlignment int alignment) {
        final JPanel panel = new JPanel(new VerticalFlowLayout(alignment, 4, 2, true, false));
        final JBLabel label = new JBLabel(labelText, UIUtil.ComponentStyle.SMALL);
        DialogUtil.registerMnemonic(label, component, '_');
        IJSwingUtilities.adjustComponentsOnMac(label, component);
        panel.add(label);
        panel.add(component);
        return panel;
    }

    @Data
    @Accessors(chain = true)
    private class ConditionItem {

        private Boolean isParam = true;
        private String paramName;
        private String paramType;
        private String paramFieldName;
        private String operator = "=";
        private String value;
        private Boolean checkNull = false;
        private String description;

    }

    /**
     * 初始化组件
     */
    private void initComponents() {
        tableView = createConditionsTableView();
        JBListTable listTable = createConditionsListTable();
        JPanel tablePanel = createConditionsTablePanel(listTable);
        contentPanel.add(tablePanel);
        init();
    }

    private JPanel createConditionsTablePanel(JBListTable listTable) {
        JPanel panel = ToolbarDecorator.createDecorator(listTable.getTable())
            .setAddAction(anActionButton -> {
                ConditionItem newItem = new ConditionItem();
                tableModel.addRow(newItem);
            })
            .createPanel();
        panel.setPreferredSize(new Dimension(500, 200));
        return panel;
    }

    private JBListTable createConditionsListTable() {
        return new JBListTable(tableView, DemoTable2Dialog.this.getDisposable()) {
            JBTableRowRenderer renderer = new EditorTextFieldJBTableRowRenderer(project, StdFileTypes.JAVA, DemoTable2Dialog.this.getDisposable()) {
                @Override
                protected String getText(JTable table, int row) {
                    ConditionItem item = tableView.getRow(row);
                    if (item.getParamFieldName() == null || item.getOperator() == null || item.getValue() == null) {
                        return "// uninitialized";
                    }

                    return item.getParamFieldName() + " " + item.getOperator() + " " + item.getValue() + " // " + StringUtils.trimToNull(item.getDescription());
                }
            };


            protected ConditionItem getRowItem(int row) {
                return tableView.getItems().get(row);
            }


            @Override
            protected JBTableRowRenderer getRowRenderer(int row) {
                return getRowRenderer();
            }

            private JBTableRowRenderer getRowRenderer() {
                return renderer;
            }

            @Override
            protected JBTableRowEditor getRowEditor(int row) {
                // 可以添加DocumentListener
                // See ChangeSignatureDialogBase.java:696
                return getRowEditor(getRowItem(row));
            }

            protected JBTableRowEditor getRowEditor(ConditionItem item) {
                return new JBTableRowEditor() {

                    private static final long serialVersionUID = 4525396249989190101L;
                    private JCheckBox paramCheckBox;
                    private EditorTextField paramNameText;
                    private EditorTextField typeText;
                    private EditorTextField fieldNameText;
                    private ComboBox<String> operatorCombo;
                    private EditorTextField valueText;
                    private JCheckBox checkNullBox;
                    private EditorTextField descText;
                    private PsiCodeFragment typeFragment;
                    private boolean fieldNameTextModifyFlag = false;
                    private boolean valueTextModifyFlag = false;

                    @Override
                    public void prepareEditor(JTable table, int row) {
                        /// 创建组件
                        paramCheckBox = new JCheckBox("_Param");
                        paramCheckBox.setSelected(item.getIsParam());
                        UIUtil.applyStyle(ComponentStyle.SMALL, paramCheckBox);
                        DialogUtil.registerMnemonic(paramCheckBox, '_');

                        paramNameText = new EditorTextField();
                        paramNameText.setPlaceholder("parameter name");
                        paramNameText.setText(item.getParamName());
                        paramNameText.setEnabled(item.getIsParam());

                        JavaCodeFragmentFactory f = JavaCodeFragmentFactory.getInstance(project);
                        typeFragment = f.createTypeCodeFragment(item.getParamType() == null ? "" : item.getParamType(), psiContext, true,
                            JavaCodeFragmentFactory.ALLOW_ELLIPSIS);
                        final Document document = PsiDocumentManager.getInstance(project).getDocument(typeFragment);
                        typeText = new EditorTextField(document, project, StdFileTypes.JAVA);
                        typeText.setText(item.getParamType());
                        typeText.setEnabled(item.isParam);

                        fieldNameText = new EditorTextField();
                        fieldNameText.setText(item.getParamFieldName());
//                        fieldNameText.setEnabled(item.isParam);

                        operatorCombo = new ComboBox<>(new String[]{"=", "!=", ">", ">=", "<", "<=", "in", "not in"});
                        operatorCombo.setSelectedItem(item.getOperator());

                        valueText = new EditorTextField();
                        valueText.setText(item.getValue());

                        checkNullBox = new JCheckBox("_Check Null");
                        checkNullBox.setSelected(item.getCheckNull());
                        checkNullBox.setEnabled(item.isParam);
                        DialogUtil.registerMnemonic(checkNullBox, '_');

                        descText = new EditorTextField();
                        descText.setText(item.getDescription());
                        descText.setEnabled(item.isParam);

                        /// 组件绑定事件
                        paramCheckBox.addActionListener(e -> {
                            item.setIsParam(paramCheckBox.isSelected());

                            // 判官是否启用
                            paramNameText.setEnabled(paramCheckBox.isSelected());
                            typeText.setEnabled(paramCheckBox.isSelected());
                            checkNullBox.setEnabled(paramCheckBox.isSelected());
                            descText.setEnabled(paramCheckBox.isSelected());

                            if (paramCheckBox.isSelected()) {
                                paramNameText.requestFocus();
                            } else {
                                fieldNameText.requestFocus();
                            }
                        });
                        operatorCombo.addActionListener(e -> {
                            item.setOperator(operatorCombo.getSelectedItem().toString());
                        });
                        checkNullBox.addChangeListener(e -> item.setCheckNull(checkNullBox.isSelected()));
                        paramNameText.addDocumentListener(new DocumentListener() {
                            @Override
                            public void documentChanged(DocumentEvent event) {
                                String text = event.getDocument().getText();
                                item.setParamName(text);

                                if (StringUtils.isNotBlank(text)) {
                                    if (!fieldNameTextModifyFlag) {
                                        text = StringUtils.uncapitalize(text.trim());
                                        String fieldName = text.replaceAll("([A-Z])", "_$1").toLowerCase();
                                        fieldNameText.setText(fieldName);
                                    }
                                    if (!valueTextModifyFlag) {
                                        valueText.setText("#{" + text + "}");
                                    }
                                }
                            }
                        });
                        typeText.addDocumentListener(new DocumentListener() {
                            @Override
                            public void documentChanged(DocumentEvent event) {
                                item.setParamType(event.getDocument().getText());
                            }
                        });
                        fieldNameText.addDocumentListener(new DocumentListener() {
                            @Override
                            public void documentChanged(DocumentEvent event) {
                                if (fieldNameText.isFocusOwner()) {
                                    // 设置标记
                                    fieldNameTextModifyFlag = true;
                                }
                                item.setParamFieldName(event.getDocument().getText());
                            }
                        });
                        valueText.addDocumentListener(new DocumentListener() {
                            @Override
                            public void documentChanged(DocumentEvent event) {
                                if (valueText.isFocusOwner()) {
                                    valueTextModifyFlag = true;
                                }
                                item.setValue(event.getDocument().getText());
                            }
                        });
                        descText.addDocumentListener(new DocumentListener() {
                            @Override
                            public void documentChanged(DocumentEvent event) {
                                item.setDescription(event.getDocument().getText());
                            }
                        });

                        /// 组件布局
                        setLayout(new GridLayout(3, 0));
                        {
                            // First Line
                            FlowLayout layout = new FlowLayout(FlowLayout.LEFT);
                            JPanel linePanel = new JPanel(layout);
                            paramNameText.setPreferredWidth(120);
                            typeText.setPreferredWidth(150);
                            linePanel.add(paramCheckBox);
                            linePanel.add(DemoTable2Dialog.createLabeledPanel("Param _Name", paramNameText));
                            linePanel.add(DemoTable2Dialog.createLabeledPanel("Param _Type", typeText));
                            linePanel.add(checkNullBox);
                            add(linePanel);

                        }

                        {
                            // Third Line
                            JPanel linePanel = new JPanel(new BorderLayout());
                            linePanel.add(DemoTable2Dialog.createLabeledPanel("_Description", descText, VerticalFlowLayout.BOTTOM));
                            add(linePanel);
                        }

                        {
                            // Second Line
                            FlowLayout layout = new FlowLayout(FlowLayout.LEFT);
                            JPanel linePanel = new JPanel(layout);

                            fieldNameText.setPreferredWidth(125);
                            operatorCombo.setMinimumAndPreferredWidth(10);
                            valueText.setPreferredWidth(150);
                            linePanel.add(DemoTable2Dialog.createLabeledPanel("_Field Name", fieldNameText));
                            linePanel.add(DemoTable2Dialog.createLabeledPanel("_Operator", operatorCombo));
                            linePanel.add(DemoTable2Dialog.createLabeledPanel("Field _Value", valueText));
                            add(linePanel);

                        }
                    }

                    @Override
                    public JBTableRow getValue() {
                        return column -> "getValue";
                    }

                    @Override
                    public JComponent getPreferredFocusedComponent() {
                        if (item.getIsParam()) {
                            return paramNameText;
                        }
                        return fieldNameText;
                    }

                    @Override
                    public JComponent[] getFocusableComponents() {
                        if (item.getIsParam()) {
                            return new JComponent[]{paramCheckBox, paramNameText, typeText, checkNullBox,
                                descText, fieldNameText, operatorCombo, valueText,};
                        } else {
                            return new JComponent[]{paramCheckBox, fieldNameText, operatorCombo, valueText,};
                        }

                    }
                };
            }
        };
    }

    private TableView<ConditionItem> createConditionsTableView() {
        tableModel = new ListTableModel<>();
        return new TableView<>(tableModel);
    }

    @Nullable
    @Override
    protected JComponent createCenterPanel() {
        return contentPanel;
    }
}
