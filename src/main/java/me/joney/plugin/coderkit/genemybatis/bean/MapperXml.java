package me.joney.plugin.coderkit.genemybatis.bean;

import com.intellij.psi.impl.source.xml.XmlFileImpl;

/**
 * @author yang.qiang
 * @date 2018/11/01
 */
public class MapperXml {

    private XmlFileImpl xmlFile;

    public MapperXml(XmlFileImpl xmlFile) {
        this.xmlFile = xmlFile;
    }

    @Override
    public String toString() {
        return xmlFile.getName();
    }

    public XmlFileImpl getXmlFile() {
        return xmlFile;
    }

    public void setXmlFile(XmlFileImpl xmlFile) {
        this.xmlFile = xmlFile;
    }
}
