package me.joney.plugin.coderkit.apikit.xiaoyaoji;

import java.util.ArrayList;
import java.util.List;
import javax.swing.event.TreeModelListener;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;
import lombok.Data;

/**
 * Created by yang.qiang on 2018/10/07.
 */
@Data
public class XiaoyaojiDocStruct implements TreeModel  {

    private String content;
    private String createTime;
    private String id;
    private String lastUpdateTime;
    private String name;
    private String parentId;
    private String projectId;
    private int sort;
    private String type;
    private List<XiaoyaojiDocStruct> children;
    private int level;

    public static List<XiaoyaojiDocStruct> getAllFolder(List<XiaoyaojiDocStruct> structList) {
        ArrayList<XiaoyaojiDocStruct> allList = new ArrayList<>();
        for (XiaoyaojiDocStruct xiaoyaojiDocStruct : structList) {
            if (xiaoyaojiDocStruct.isFolder()) {
                allList.add(xiaoyaojiDocStruct);
                if (xiaoyaojiDocStruct.getChildren() != null && !xiaoyaojiDocStruct.getChildren().isEmpty()) {
                    allList.addAll(getAllFolder(xiaoyaojiDocStruct.getChildren()));
                }
            }
        }
        return allList;
    }

    public boolean isFolder() {
        return ("sys.doc.md".equals(this.getType()) || "sys.folder".equals(this.getType()));
    }

    @Override
    public String toString() {
        return name;
    }

    @Override
    public Object getRoot() {
        return this;
    }

    @Override
    public Object getChild(Object parent, int index) {
        return this.children;
    }

    @Override
    public int getChildCount(Object parent) {
        return children.size();
    }

    @Override
    public boolean isLeaf(Object node) {
        return children ==null || children.size() == 0;
    }

    @Override
    public void valueForPathChanged(TreePath path, Object newValue) {

    }

    @Override
    public int getIndexOfChild(Object parent, Object child) {
        return 0;
    }

    @Override
    public void addTreeModelListener(TreeModelListener l) {

    }

    @Override
    public void removeTreeModelListener(TreeModelListener l) {

    }
}
