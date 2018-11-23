package me.joney.plugin.coderkit.apikit.ui;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.openapi.ui.Splitter;
import com.intellij.ui.ScrollPaneFactory;
import com.intellij.ui.treeStructure.Tree;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.util.List;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.tree.DefaultMutableTreeNode;
import me.joney.plugin.coderkit.apikit.xiaoyaoji.XiaoyaojiDocStruct;
import org.jetbrains.annotations.Nullable;

/**
 * Created by yang.qiang on 2018/10/07.
 */
public class DocStructChooser extends DialogWrapper {

    private XiaoyaojiDocStruct selectedItem;
    private Project myProject;
    private JComponent content;
    private Tree myTree;
    private List<XiaoyaojiDocStruct> structList;

    protected DocStructChooser(@Nullable Project project, boolean canBeParent,
        List<XiaoyaojiDocStruct> structList) {
        super(project, canBeParent);
        myProject = project;
        this.structList = structList;

        // 创建根节点
        DefaultMutableTreeNode rootNode = new DefaultMutableTreeNode();
        fillNode(rootNode, structList);
        // 使用根节点创建树组件
        myTree = new Tree(rootNode);
        // 设置树显示根节点句柄(三角符号, 用于表示展开/折叠状态)
        myTree.setShowsRootHandles(true);
        // 是否显示根节点(默认显示)
        myTree.setRootVisible(false);
        // 设置树节点可编辑
        myTree.setEditable(false);

        // 设置节点选中监听器
        myTree.addTreeSelectionListener(e -> {
            DefaultMutableTreeNode lastPathComponent = (DefaultMutableTreeNode) e.getPath().getLastPathComponent();
            selectedItem = (XiaoyaojiDocStruct) lastPathComponent.getUserObject();
        });

        Splitter splitter = new Splitter(false, (float) 0.6);
        JPanel result = new JPanel(new BorderLayout());

        JScrollPane scrollPane = ScrollPaneFactory.createScrollPane(myTree);
        splitter.setFirstComponent(scrollPane);
        result.add(splitter);
        result.setPreferredSize(new Dimension(450, 400));

        this.content = result;

        init();
    }

    XiaoyaojiDocStruct getSelected() {
        return selectedItem;
    }


    void fillNode(DefaultMutableTreeNode parentNode, List<XiaoyaojiDocStruct> structs) {
        for (XiaoyaojiDocStruct struct : structs) {
            if (!struct.isFolder()) {
                continue;
            }

            DefaultMutableTreeNode childNode = new DefaultMutableTreeNode(struct);

            childNode.getUserObject();
            parentNode.add(childNode);
            if (struct.getChildren() != null) {
                fillNode(childNode, struct.getChildren());
            }
        }

    }

    @Nullable
    @Override
    protected JComponent createCenterPanel() {
        return content;
    }

}
