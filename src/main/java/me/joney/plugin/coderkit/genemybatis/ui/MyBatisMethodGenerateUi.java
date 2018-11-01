package me.joney.plugin.coderkit.genemybatis.ui;

import com.intellij.openapi.project.Project;
import com.intellij.psi.impl.source.xml.XmlFileImpl;
import com.intellij.psi.xml.XmlTag;
import java.awt.Dimension;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import me.joney.plugin.coderkit.genemybatis.bean.MapperResult;
import me.joney.plugin.coderkit.genemybatis.bean.MapperXml;
import me.joney.plugin.coderkit.genemybatis.bean.MapperXmlTag;

/**
 *
 * @author yang.qiang
 * @date 2018/10/31
 */
public class MyBatisMethodGenerateUi extends JDialog {

    private static final long serialVersionUID = -6688892856120994087L;
    private Project project;

    private JPanel contentPanel;
    private JComboBox<MapperXml> mapperXmlSelectBox;
    private JComboBox<MapperXmlTag> resultMapSelectBox;
    private JTextField methodNameField;
    private JButton cancelButton;
    private JButton oKButton;
    private JRadioButton multipleRadioButton;
    private JRadioButton singleRadioButton;
    private JCheckBox limitOneCheckBox;
    private JTextField returnTypeField;
    private JTextField textField2;
    private JButton chooserButton;
    private JTextArea textArea1;



    public MyBatisMethodGenerateUi(Project project, Set<XmlFileImpl> xmlFiles) {
        this.project = project;

        // 设置触发事件
        mapperXmlSelectBox.addActionListener(event -> onMapperXmlActivate());
        resultMapSelectBox.addActionListener(event -> onResultMapActivate());
        oKButton.addActionListener(event -> onOk());
        cancelButton.addActionListener(event -> onCancel());
        singleRadioButton.addActionListener(event -> onSingleReturn());
        multipleRadioButton.addActionListener(event -> onMultipleReturn());
        chooserButton.addActionListener(event -> onChooser());

        // 添加xml选择项
        for (XmlFileImpl xmlFile : xmlFiles) {
            mapperXmlSelectBox.addItem(new MapperXml(xmlFile));
            mapperXmlSelectBox.setEnabled(true);
        }

        if (!xmlFiles.isEmpty()) {
            mapperXmlSelectBox.setSelectedIndex(0);
        }

        setTitle("Generate MyBatis Method");
        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        setContentPane(contentPanel);
        pack();
        setVisible(true);

        Dimension screenSize = java.awt.Toolkit.getDefaultToolkit().getScreenSize();
        Dimension frameSize = this.getSize();
        this.setLocation((screenSize.width - frameSize.width) / 2, (screenSize.height - frameSize.height) / 2);

    }

    private void onChooser() {
        MapperXmlTag selectedItem = (MapperXmlTag) resultMapSelectBox.getSelectedItem();
        if (selectedItem == null) {
            return;
        }

        ArrayList<MapperResult> mapperResults = new ArrayList<>();
        XmlTag[] subTags = selectedItem.getXmlTag().getSubTags();
        for (XmlTag subTag : subTags) {
            if ("result".equals(subTag.getName())) {
                mapperResults.add(new MapperResult(subTag.getAttributeValue("result"), subTag.getAttributeValue("property")));
            } else if ("id".equals(subTag.getName())) {
                mapperResults.add(new MapperResult(subTag.getAttributeValue("id"), subTag.getAttributeValue("property")));
            }
        }

        WhereConditionChooser chooser = new WhereConditionChooser(project, mapperResults);
        chooser.show();

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

    private void onMapperXmlActivate() {
        MapperXml selectedXml = (MapperXml) mapperXmlSelectBox.getSelectedItem();
        if (selectedXml == null) {
            return;
        }

        resultMapSelectBox.removeAllItems();

        List<XmlTag> resultMapList = getResultMapList(selectedXml.getXmlFile());
        for (XmlTag xmlTag : resultMapList) {
            resultMapSelectBox.addItem(new MapperXmlTag(xmlTag));
            resultMapSelectBox.setEnabled(true);
        }
        if (!resultMapList.isEmpty()) {
            resultMapSelectBox.setSelectedIndex(0);
        }


    }

    private void onResultMapActivate() {
        MapperXmlTag selectedResultMap = (MapperXmlTag) resultMapSelectBox.getSelectedItem();
        if (selectedResultMap == null) {
            return;
        }

        returnTypeField.setText(selectedResultMap.getXmlTag().getAttributeValue("type"));
    }

    private List<XmlTag> getResultMapList(XmlFileImpl xmlFile) {
        XmlTag[] resultMaps = xmlFile.getDocument().getRootTag().findSubTags("resultMap");
        if (resultMaps == null || resultMaps.length < 1) {
            return new ArrayList<>();
        }

        return Arrays.asList(resultMaps);
    }
}
