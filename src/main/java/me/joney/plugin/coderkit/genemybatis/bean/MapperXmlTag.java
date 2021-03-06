package me.joney.plugin.coderkit.genemybatis.bean;

import com.intellij.psi.xml.XmlTag;

/**
 * @author yang.qiang
 * @date 2018/11/01
 */
public class MapperXmlTag {

    private XmlTag xmlTag;

    public MapperXmlTag(XmlTag xmlTag) {
        this.xmlTag = xmlTag;
    }

    @Override
    public String toString() {
        return xmlTag.getName();
    }

    public XmlTag getXmlTag() {
        return xmlTag;
    }

    public void setXmlTag(XmlTag xmlTag) {
        this.xmlTag = xmlTag;
    }
}
