package me.joney.plugin.coderkit.genemybatis.ui;

import com.intellij.psi.impl.source.xml.XmlFileImpl;
import com.intellij.psi.xml.XmlTag;
import java.awt.Dimension;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JTextField;
import me.joney.plugin.coderkit.genemybatis.bean.MapperResultMap;
import me.joney.plugin.coderkit.genemybatis.bean.MapperXml;

/**
 *
 * @author yang.qiang
 * @date 2018/10/31
 */
public class MyBatisMethodGenerateUi extends JDialog {

    private static final long serialVersionUID = -6688892856120994087L;

    private JPanel contentPanel;
    private JComboBox<MapperXml> mapperXmlSelectBox;
    private JComboBox<MapperResultMap> resultMapSelectBox;
    private JTextField methodNameField;
    private JButton cancelButton;
    private JButton oKButton;



    public MyBatisMethodGenerateUi(Set<XmlFileImpl> xmlFiles) {
        // 设置触发事件
        mapperXmlSelectBox.addActionListener(event -> onMapperXmlActivate());
        resultMapSelectBox.addActionListener(event -> onResultMapActivate());
        oKButton.addActionListener(event -> onOk());
        cancelButton.addActionListener(event -> onCancel());

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
            resultMapSelectBox.addItem(new MapperResultMap(xmlTag));
            resultMapSelectBox.setEnabled(true);
        }
        if (!resultMapList.isEmpty()) {
            resultMapSelectBox.setSelectedIndex(0);
        }


    }

    private void onResultMapActivate() {
        MapperResultMap selectedResultMap = (MapperResultMap) resultMapSelectBox.getSelectedItem();
        if (selectedResultMap == null) {
            return;
        }
        System.out.println(selectedResultMap);
    }

    private List<XmlTag> getResultMapList(XmlFileImpl xmlFile) {
        XmlTag[] resultMaps = xmlFile.getDocument().getRootTag().findSubTags("resultMap");
        if (resultMaps == null || resultMaps.length < 1) {
            return new ArrayList<>();
        }

        return Arrays.asList(resultMaps);
    }
}
