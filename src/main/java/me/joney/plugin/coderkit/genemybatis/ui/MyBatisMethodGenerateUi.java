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
import javax.swing.JRadioButton;
import javax.swing.JTextField;

/**
 * Created by yang.qiang on 2018/10/31.
 */
public class MyBatisMethodGenerateUi extends JDialog {



    private JPanel contentPanel;
    private JComboBox comboBox1;
    private JComboBox comboBox2;
    private JTextField textField1;
    private JComboBox<XmlFileImpl> mapperXmlSelectBox;
    private JComboBox<XmlTag> resultMapSelectBox;

    public MyBatisMethodGenerateUi(Set<XmlFileImpl> xmlFiles) {

        for (XmlFileImpl xmlFile : xmlFiles) {
            mapperXmlSelectBox.addItem(xmlFile);
        }
        mapperXmlSelectBox.addActionListener(event -> onSelectedXml());
        resultMapSelectBox.addActionListener(e -> onSelectedResultMap());

        mapperXmlSelectBox.setSelectedIndex(1);

        setTitle("Generate MyBatis Method");
        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        setContentPane(contentPanel);
        pack();
        setVisible(true);

        Dimension screenSize = java.awt.Toolkit.getDefaultToolkit().getScreenSize();
        Dimension frameSize = this.getSize();
        this.setLocation((screenSize.width - frameSize.width) / 2, (screenSize.height - frameSize.height) / 2);

    }

    private void onSelectedXml() {
        XmlFileImpl selectedXml = (XmlFileImpl) mapperXmlSelectBox.getSelectedItem();
        resultMapSelectBox.removeAllItems();

        List<XmlTag> resultMapList = getResultMapList(selectedXml);
        for (XmlTag xmlTag : resultMapList) {
            resultMapSelectBox.addItem(xmlTag);
        }
    }

    private void onSelectedResultMap() {
        XmlTag selectedResultMap = (XmlTag) resultMapSelectBox.getSelectedItem();
        System.out.println(selectedResultMap.getAttribute("type"));
    }

    private List<XmlTag> getResultMapList(XmlFileImpl xmlFile) {
        XmlTag[] resultMaps = xmlFile.getDocument().getRootTag().findSubTags("resultMap");
        if (resultMaps == null || resultMaps.length < 1) {
            return new ArrayList<>();
        }

        return Arrays.asList(resultMaps);
    }
}
