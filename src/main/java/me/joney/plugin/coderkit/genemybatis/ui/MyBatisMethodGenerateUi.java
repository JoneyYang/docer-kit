package me.joney.plugin.coderkit.genemybatis.ui;

import com.intellij.codeInsight.generation.ui.SimpleFieldChooser;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.colors.EditorColors;
import com.intellij.openapi.editor.colors.EditorColorsManager;
import com.intellij.openapi.editor.colors.EditorFontType;
import com.intellij.openapi.editor.event.DocumentEvent;
import com.intellij.openapi.editor.event.DocumentListener;
import com.intellij.openapi.fileTypes.StdFileTypes;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.ComboBox;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.openapi.ui.VerticalFlowLayout;
import com.intellij.psi.JavaCodeFragment;
import com.intellij.psi.JavaCodeFragmentFactory;
import com.intellij.psi.PsiCodeFragment;
import com.intellij.psi.PsiDocumentManager;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiField;
import com.intellij.psi.PsiType;
import com.intellij.psi.impl.source.PsiTypeCodeFragmentImpl;
import com.intellij.psi.impl.source.xml.XmlFileImpl;
import com.intellij.psi.util.PsiTypesUtil;
import com.intellij.psi.xml.XmlTag;
import com.intellij.ui.AnActionButton;
import com.intellij.ui.EditorTextField;
import com.intellij.ui.SeparatorFactory;
import com.intellij.ui.TitledSeparator;
import com.intellij.ui.ToolbarDecorator;
import com.intellij.ui.table.TableView;
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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTable;
import javax.swing.JTextField;
import lombok.Data;
import lombok.experimental.Accessors;
import me.joney.plugin.coderkit.feign.ui.DemoTable2Dialog;
import me.joney.plugin.coderkit.genemybatis.bean.MapperXml;
import me.joney.plugin.coderkit.genemybatis.bean.MapperXmlTag;
import org.apache.commons.lang.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 *
 * @author yang.qiang
 * @date 2018/10/31
 */
public class MyBatisMethodGenerateUi extends DialogWrapper {

    private Set<XmlFileImpl> xmlFiles;
    private Project project;
    private PsiElement psiContext;

    private JPanel contentPanel;

    private ListTableModel<ConditionItem> conditionsTableModel;
    private TableView<ConditionItem> conditionsTableView;
    private ListTableModel<OrderItem> ordersTableModel;
    private TableView<OrderItem> ordersTableView;

    private JavaCodeFragment returnTypeCodeFragment;

    private EditorTextField returnTypeTextField;
    private JCheckBox resultMapCheckBox;

    private JComboBox<MapperXml> mapperXmlComboBox;
    private JComboBox<MapperXmlTag> resultMapComboBox;
    private JPanel returnPanel;
    private JPanel conditionsPanel;
    private JTextField methodNameField;
    private JRadioButton multipleRadioButton;
    private JRadioButton singleRadioButton;
    private JCheckBox limitOneCheckBox;
    private JPanel resultMapCheckBoxPanel;
    private JTextField descriptionTextField;
    private JPanel xmlPreviewPanel;
    private JPanel mapperPreviewPanel;
    private JPanel ordersPanel;
    private JLabel returnTypeLabel;
    private JLabel mapperXmlLabel;
    private JLabel methodNameLabel;
    private JLabel descriptionLabel;



    public MyBatisMethodGenerateUi(Project project, Set<XmlFileImpl> xmlFiles, PsiElement psiContext) {
        super(project);
        this.project = project;
        this.xmlFiles = xmlFiles;
        this.psiContext = psiContext;

        initComponents();
        initListener(xmlFiles);
    }

    @NotNull
    private static String convertNameToField(String name) {
        return name.replaceAll("([A-Z])", "_$1").toLowerCase();
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

    private void onSingleReturn() {
        multipleRadioButton.setSelected(false);
        limitOneCheckBox.setEnabled(true);

    }

    private void onMultipleReturn() {
        singleRadioButton.setSelected(false);
        limitOneCheckBox.setEnabled(false);
    }

    private void onCancel() {
    }

    private void onOk() {
    }

    @Data
    @Accessors(chain = true)
    private class OrderItem {

        private String paramFieldName;
        private String orderType = "DESC";
    }

    private void onMapperXmlActivate() {
        MapperXml selectedXml = (MapperXml) mapperXmlComboBox.getSelectedItem();
        if (selectedXml == null) {
            return;
        }

        resultMapComboBox.removeAllItems();

        List<XmlTag> resultMapList = getResultMapList(selectedXml.getXmlFile());
        for (XmlTag xmlTag : resultMapList) {
            resultMapComboBox.addItem(new MapperXmlTag(xmlTag));
            resultMapComboBox.setEnabled(true);
        }
        if (!resultMapList.isEmpty()) {
            resultMapComboBox.setSelectedIndex(0);
        }


    }

    private List<XmlTag> getResultMapList(XmlFileImpl xmlFile) {
        XmlTag[] resultMaps = xmlFile.getDocument().getRootTag().findSubTags("resultMap");
        if (resultMaps == null || resultMaps.length < 1) {
            return new ArrayList<>();
        }

        return Arrays.asList(resultMaps);
    }

    private void onResultMapActivate() {
        MapperXmlTag selectedResultMap = (MapperXmlTag) resultMapComboBox.getSelectedItem();
        if (selectedResultMap == null) {
            return;
        }
        if (resultMapCheckBox.isSelected()) {
            String text = selectedResultMap.getXmlTag().getAttributeValue("type").replaceAll("^.*\\.(\\w+)$", "$1");
            returnTypeTextField.setText(text);
            try {
                PsiType type = ((PsiTypeCodeFragmentImpl) returnTypeCodeFragment).getType();
                System.out.println(type);
            } catch (Exception ignored) {
            }
        }
    }

    @Nullable
    @Override
    protected JComponent createCenterPanel() {
        return contentPanel;
    }

    private void initListener(Set<XmlFileImpl> xmlFiles) {
        // 设置触发事件
        mapperXmlComboBox.addActionListener(event -> onMapperXmlActivate());
        resultMapComboBox.addActionListener(event -> onResultMapActivate());
        singleRadioButton.addActionListener(event -> onSingleReturn());
        multipleRadioButton.addActionListener(event -> onMultipleReturn());
        resultMapCheckBox.addActionListener(event -> {
            resultMapComboBox.setEnabled(resultMapCheckBox.isSelected());
            returnTypeTextField.setEnabled(!resultMapCheckBox.isSelected());

            if (resultMapCheckBox.isSelected()) {
                onResultMapActivate();

            }
        });

        // 添加xml选择项
        for (XmlFileImpl xmlFile : xmlFiles) {
            mapperXmlComboBox.addItem(new MapperXml(xmlFile));
            mapperXmlComboBox.setEnabled(true);
        }
        if (!xmlFiles.isEmpty()) {
            mapperXmlComboBox.setSelectedIndex(0);
        }
    }

    private void initComponents() {
        init();

        {
            conditionsTableView = createConditionsTableView();
            JBListTable listTable = createConditionsListTable();
            JPanel tablePanel = createConditionsTablePanel(listTable);

            JPanel panel = new JPanel(new BorderLayout());
            TitledSeparator separator = SeparatorFactory.createSeparator("_Where Conditions", conditionsTableView);
            DialogUtil.registerMnemonic(separator.getLabel(), tablePanel, '_');
            panel.add(separator, BorderLayout.NORTH);
            panel.add(tablePanel, BorderLayout.CENTER);
            conditionsPanel.add(panel);
        }

        {
            ordersTableView = createOrdersTableView();
            JBListTable ordersListTable = createOrdersListTable();
            JPanel tablePanel = createOrdersTablePanel(ordersListTable);

            JPanel panel = new JPanel(new BorderLayout());
            TitledSeparator separator = SeparatorFactory.createSeparator("_Order Conditions", ordersTableView);
            DialogUtil.registerMnemonic(separator.getLabel(), tablePanel, '_');
            panel.add(separator, BorderLayout.NORTH);
            panel.add(tablePanel, BorderLayout.CENTER);
            ordersPanel.add(panel);
        }

        ///  设置ReturnType
        JavaCodeFragmentFactory f = JavaCodeFragmentFactory.getInstance(project);
        // TODO 优化Type初始化
        returnTypeCodeFragment = f.createTypeCodeFragment("", psiContext, true, JavaCodeFragmentFactory.ALLOW_ELLIPSIS);
        final Document document = PsiDocumentManager.getInstance(project).getDocument(returnTypeCodeFragment);
        returnTypeTextField = new EditorTextField(document, project, StdFileTypes.JAVA);
        returnTypeTextField.setEnabled(false);
        returnTypeTextField.setPreferredWidth(150);
        returnPanel.add(returnTypeTextField);

        resultMapCheckBox = new JCheckBox("_Result Map");
        resultMapCheckBox.setSelected(true);
        resultMapCheckBoxPanel.add(resultMapCheckBox);

        {

            JPanel panel = new JPanel(new BorderLayout());
            panel.add(SeparatorFactory.createSeparator("SQL Preview", null), BorderLayout.NORTH);
            EditorTextField editorTextField = new EditorTextField("select * from user\n where age = 18", project, StdFileTypes.XML);
            editorTextField.setFont(EditorColorsManager.getInstance().getGlobalScheme().getFont(EditorFontType.PLAIN));
            editorTextField.setBackground(EditorColorsManager.getInstance().getGlobalScheme().getColor(EditorColors.CARET_ROW_COLOR));
            editorTextField.setEnabled(false);
            editorTextField.setPreferredSize(new Dimension(-1, 80));
            editorTextField.setMinimumSize(new Dimension(-1, 80));
            panel.add(editorTextField, BorderLayout.CENTER);
            xmlPreviewPanel.add(panel);
        }

        {

            JPanel panel = new JPanel(new BorderLayout());
            panel.add(SeparatorFactory.createSeparator("Mapper Method Preview", null), BorderLayout.NORTH);
            EditorTextField editorTextField = new EditorTextField("public static void main(){\n   System.out.println();}", project, StdFileTypes.XML);
            editorTextField.setFont(EditorColorsManager.getInstance().getGlobalScheme().getFont(EditorFontType.PLAIN));
            editorTextField.setBackground(EditorColorsManager.getInstance().getGlobalScheme().getColor(EditorColors.CARET_ROW_COLOR));
            editorTextField.setEnabled(false);
            editorTextField.setPreferredSize(new Dimension(-1, 80));
            editorTextField.setMinimumSize(new Dimension(-1, 80));
            panel.add(editorTextField, BorderLayout.CENTER);
            mapperPreviewPanel.add(panel);
        }

        // 设置快捷点绑定
        DialogUtil.registerMnemonic(resultMapCheckBox, '_');
        DialogUtil.registerMnemonic(multipleRadioButton, '_');
        DialogUtil.registerMnemonic(singleRadioButton, '_');
        DialogUtil.registerMnemonic(limitOneCheckBox, '_');
        DialogUtil.registerMnemonic(returnTypeLabel, returnTypeTextField, '_');
        DialogUtil.registerMnemonic(mapperXmlLabel, mapperXmlComboBox, '_');
        DialogUtil.registerMnemonic(methodNameLabel, methodNameField, '_');
        DialogUtil.registerMnemonic(descriptionLabel, descriptionTextField, '_');

    }

    private TableView<ConditionItem> createConditionsTableView() {
        conditionsTableModel = new ListTableModel<>();
        return new TableView<>(conditionsTableModel);
    }

    private TableView<OrderItem> createOrdersTableView() {
        ordersTableModel = new ListTableModel<>();
        return new TableView<>(ordersTableModel);
    }

    private JBListTable createOrdersListTable() {
        return new JBListTable(ordersTableView, MyBatisMethodGenerateUi.this.getDisposable()) {
            JBTableRowRenderer renderer = new EditorTextFieldJBTableRowRenderer(project, StdFileTypes.JAVA,
                MyBatisMethodGenerateUi.this.getDisposable()) {
                @Override
                protected String getText(JTable table, int row) {
                    OrderItem item = ordersTableView.getRow(row);
                    if (item.getParamFieldName() == null || item.getOrderType() == null) {
                        return "// uninitialized";
                    }

                    return item.getParamFieldName() + " " + item.getOrderType();
                }
            };


            protected OrderItem getRowItem(int row) {
                return ordersTableView.getItems().get(row);
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

            protected JBTableRowEditor getRowEditor(OrderItem item) {
                return new JBTableRowEditor() {

                    private static final long serialVersionUID = 4525396249989190101L;
                    private EditorTextField fieldNameTextField;
                    private ComboBox<String> orderTypeComboBox;

                    @Override
                    public void prepareEditor(JTable table, int row) {
                        /// 创建组件

                        fieldNameTextField = new EditorTextField();
                        fieldNameTextField.setPlaceholder("parameter name");
                        fieldNameTextField.setText(item.getParamFieldName());

                        orderTypeComboBox = new ComboBox<>(new String[]{"DESC", "ASC"});
                        orderTypeComboBox.setSelectedItem(item.getOrderType());

                        // 设置事件
                        fieldNameTextField.addDocumentListener(new DocumentListener() {
                            @Override
                            public void documentChanged(DocumentEvent event) {
                                item.setParamFieldName(event.getDocument().getText());
                            }
                        });
                        orderTypeComboBox.addActionListener(e -> item.setOrderType(orderTypeComboBox.getSelectedItem().toString()));
                        /// 组件布局
                        setLayout(new GridLayout(1, 0));
                        {
                            // First Line
                            FlowLayout layout = new FlowLayout(FlowLayout.LEFT);
                            JPanel linePanel = new JPanel(layout);
                            fieldNameTextField.setPreferredWidth(120);
                            linePanel.add(DemoTable2Dialog.createLabeledPanel("Param _Name", fieldNameTextField));
                            linePanel.add(DemoTable2Dialog.createLabeledPanel("Order Type", orderTypeComboBox));
                            add(linePanel);

                        }

                    }

                    @Override
                    public JBTableRow getValue() {
                        return column -> "getValue";
                    }

                    @Override
                    public JComponent getPreferredFocusedComponent() {
                        return fieldNameTextField;
                    }

                    @Override
                    public JComponent[] getFocusableComponents() {
                        return new JComponent[]{fieldNameTextField, orderTypeComboBox};

                    }
                };
            }
        };
    }

    private JPanel createConditionsTablePanel(JBListTable listTable) {
        AnActionButton addPropertyButton = new AnActionButton("Add Property") {
            @Override
            public void actionPerformed(AnActionEvent e) {

                PsiType type;
                try {
                    type = ((PsiTypeCodeFragmentImpl) returnTypeCodeFragment).getType();
                } catch (Exception e1) {
                    return;
                }
                if (type == null) {
                    return;
                }

                PsiField[] fields = PsiTypesUtil.getPsiClass(type).getFields();

                SimpleFieldChooser chooser = new SimpleFieldChooser(fields, project);
                chooser.show();

                for (Object selectedElement : chooser.getSelectedElements()) {
                    PsiField field = (PsiField) selectedElement;
                    ConditionItem item = new ConditionItem();
                    item.setIsParam(true);
                    item.setDescription(field.getDocComment().getDescriptionElements()[1].getText().trim());
                    item.setCheckNull(false);
                    item.setParamName(field.getName());
                    item.setParamType(field.getType().getPresentableText());

                    String fieldName = convertNameToField(field.getName());
                    item.setParamFieldName(fieldName);

                    String value = convertParamNameToFieldValue(field.getName());
                    item.setValue(value);
                    conditionsTableModel.addRow(item);
                }

            }
        };

        JPanel panel = ToolbarDecorator.createDecorator(listTable.getTable())
            .addExtraAction(addPropertyButton)
            .setAddAction(anActionButton -> {
                ConditionItem newItem = new ConditionItem();
                conditionsTableModel.addRow(newItem);
            })
            .createPanel();
        panel.setPreferredSize(new Dimension(500, 200));
        return panel;
    }

    @NotNull
    private String convertParamNameToFieldValue(String name) {
        return "#{" + name + "}";
    }

    private JPanel createOrdersTablePanel(JBListTable listTable) {
        AnActionButton addPropertyButton = new AnActionButton("Add Property") {
            @Override
            public void actionPerformed(AnActionEvent e) {

                PsiType type;
                try {
                    type = ((PsiTypeCodeFragmentImpl) returnTypeCodeFragment).getType();
                } catch (Exception e1) {
                    return;
                }
                if (type == null) {
                    return;
                }

                PsiField[] fields = PsiTypesUtil.getPsiClass(type).getFields();

                SimpleFieldChooser chooser = new SimpleFieldChooser(fields, project);
                chooser.show();

                for (Object selectedElement : chooser.getSelectedElements()) {
                    PsiField field = (PsiField) selectedElement;
                    OrderItem orderItem = new OrderItem();
                    orderItem.setParamFieldName(convertNameToField(field.getName()));
                    ordersTableModel.addRow(orderItem);
                }

            }
        };

        JPanel panel = ToolbarDecorator.createDecorator(listTable.getTable())
            .addExtraAction(addPropertyButton)
            .setAddAction(anActionButton -> {
                OrderItem newItem = new OrderItem();
                ordersTableModel.addRow(newItem);
            })
            .createPanel();
        panel.setPreferredSize(new Dimension(500, 60));
        return panel;
    }

    private JBListTable createConditionsListTable() {
        return new JBListTable(conditionsTableView, MyBatisMethodGenerateUi.this.getDisposable()) {
            JBTableRowRenderer renderer = new EditorTextFieldJBTableRowRenderer(project, StdFileTypes.JAVA,
                MyBatisMethodGenerateUi.this.getDisposable()) {
                @Override
                protected String getText(JTable table, int row) {
                    ConditionItem item = conditionsTableView.getRow(row);
                    if (item.getParamFieldName() == null || item.getOperator() == null || item.getValue() == null) {
                        return "// uninitialized";
                    }

                    return item.getParamFieldName() + " " + item.getOperator() + " " + item.getValue() + " // " + StringUtils
                        .trimToNull(item.getDescription());
                }
            };


            protected ConditionItem getRowItem(int row) {
                return conditionsTableView.getItems().get(row);
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
                                        String fieldName = convertNameToField(text);
                                        fieldNameText.setText(fieldName);
                                    }
                                    if (!valueTextModifyFlag) {
                                        valueText.setText(convertParamNameToFieldValue(text));
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


}
