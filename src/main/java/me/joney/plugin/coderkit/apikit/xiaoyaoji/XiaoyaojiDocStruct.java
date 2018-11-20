package me.joney.plugin.coderkit.apikit.xiaoyaoji;

import java.util.ArrayList;
import java.util.List;
import lombok.Data;

/**
 * Created by yang.qiang on 2018/10/07.
 */
@Data
public class XiaoyaojiDocStruct {

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
            if (isFolder(xiaoyaojiDocStruct)) {
                allList.add(xiaoyaojiDocStruct);
                if (xiaoyaojiDocStruct.getChildren() != null && !xiaoyaojiDocStruct.getChildren().isEmpty()) {
                    allList.addAll(getAllFolder(xiaoyaojiDocStruct.getChildren()));
                }
            }
        }
        return allList;
    }

    public static boolean isFolder(XiaoyaojiDocStruct docStruct) {
        return ("sys.doc.md".equals(docStruct.getType()) || "sys.folder".equals(docStruct.getType()));
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (int i = 1; i < level; i++) {
            sb.append("|-");
        }
        return sb.append(name).toString();
    }
}
