package me.joney.plugin.coderkit.apikit.ui;

import com.intellij.openapi.module.Module;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JTextField;
import me.joney.plugin.coderkit.apikit.bean.RestApiDoc;
import me.joney.plugin.coderkit.apikit.store.XiaoyaojiConfigInfo;
import me.joney.plugin.coderkit.apikit.xiaoyaoji.XiaoyaojiClient;
import me.joney.plugin.coderkit.apikit.xiaoyaoji.XiaoyaojiConvert;
import me.joney.plugin.coderkit.apikit.xiaoyaoji.XiaoyaojiDoc;
import me.joney.plugin.coderkit.apikit.xiaoyaoji.XiaoyaojiDocStruct;
import me.joney.plugin.coderkit.apikit.xiaoyaoji.XiaoyaojiProject;
import org.apache.commons.lang.StringUtils;
import org.jetbrains.annotations.Nullable;

/**
 * Created by yang.qiang on 2018/09/29.
 */
public class GenerateXiaoyaojiDocDialog extends DialogWrapper {

    private RestApiDoc apiDoc;
    private Project project;
    private Module module;
    private JPanel contentPanel;
    private JTextField urlPrefixField;
    private JTextField hostField;
    private JTextField passwordField;
    private JTextField usernameField;
    private JButton connectButton;
    private JComboBox<XiaoyaojiProject> projectComboBox;
    private JButton selectButton;
    private JTextField folderField;
    private JButton button1;
    private XiaoyaojiClient xiaoyaojiClient;
    private XiaoyaojiDocStruct selectDocStruct;

    public GenerateXiaoyaojiDocDialog(Project project, Module module, RestApiDoc doc) {
        super(project);
        this.project = project;
        this.module = module;
        this.apiDoc = doc;

        XiaoyaojiConfigInfo xiaoyaojiConfigInfo = XiaoyaojiConfigInfo.getInstance(project);

        hostField.setText(xiaoyaojiConfigInfo.getHost());
        usernameField.setText(xiaoyaojiConfigInfo.getUsername());
        passwordField.setText(xiaoyaojiConfigInfo.getPassword());

        Map<String, String> urlPrefixMap = xiaoyaojiConfigInfo.getUrlPrefixMap();
        urlPrefixField.setText(urlPrefixMap.get(module.getName()));

        setTitle("Generate Xiaoyaoji Doc");

        selectButton.addActionListener(e -> onSelect());
        connectButton.addActionListener(e -> onConnect());
        folderField.addActionListener(e -> onSelect());

        if (StringUtils.isNotBlank(hostField.getText())
            && StringUtils.isNotBlank(usernameField.getText())
            && StringUtils.isNotBlank(passwordField.getText())) {
            try {
                onConnect();
            } catch (Exception e1) {
                e1.printStackTrace();
            }
        }

        init();
    }

    private void onConnect() {

        XiaoyaojiConfigInfo configInfo = XiaoyaojiConfigInfo.getInstance(project);

        xiaoyaojiClient = new XiaoyaojiClient();
        try {
            boolean loginSuccess = xiaoyaojiClient.login(usernameField.getText(), passwordField.getText(), hostField.getText());
            if (loginSuccess) {
                projectComboBox.removeAllItems();

                projectComboBox.addActionListener(e -> {
                    selectDocStruct = null;
                    folderField.setText("");
                    Object selectItem = projectComboBox.getSelectedItem();
                    if (selectItem != null) {
                        try {
                            XiaoyaojiProject selectedProject = (XiaoyaojiProject) selectItem;
                            List<XiaoyaojiDocStruct> docStructs = xiaoyaojiClient.docList(hostField.getText(), selectedProject.getId());
                            List<XiaoyaojiDocStruct> allFolder = XiaoyaojiDocStruct.getAllFolder(docStructs);

                            for (XiaoyaojiDocStruct xiaoyaojiDocStruct : allFolder) {

                                Map<String, String> selectParentMap = configInfo.getSelectParentMap();
                                String folderId = selectParentMap.get(selectedProject.getId());

                                if (xiaoyaojiDocStruct.getId().equals(folderId)) {
                                    selectDocStruct = xiaoyaojiDocStruct;
                                    folderField.setText(xiaoyaojiDocStruct.getName());
                                }
                            }

                        } catch (Exception e1) {
                            e1.printStackTrace();
                        }
                    }


                });

                List<XiaoyaojiProject> projectList = xiaoyaojiClient.projectList(hostField.getText());
                for (XiaoyaojiProject xiaoyaojiProject : projectList) {
                    if ("YES".equals(xiaoyaojiProject.getEditable())) {
                        projectComboBox.addItem(xiaoyaojiProject);
                        if (xiaoyaojiProject.getId().equals(configInfo.getProjectId())) {
                            projectComboBox.setSelectedItem(xiaoyaojiProject);
                        }
                    }
                }
            }
        } catch (Exception ex) {
            throw new RuntimeException();
        }
    }

    private void onOK() {
        onApply();

        XiaoyaojiDoc xiaoyaojiDoc = XiaoyaojiConvert.convertDoc(apiDoc);

        Object selectedItem = projectComboBox.getSelectedItem();
        XiaoyaojiProject xiaoyaojiProject = (XiaoyaojiProject) selectedItem;

        new Thread(() -> {
            try {
                xiaoyaojiClient
                    .createDoc(xiaoyaojiDoc, hostField.getText(), xiaoyaojiProject.getId(), selectDocStruct.getId(), urlPrefixField.getText());
            } catch (IOException e) {
                e.printStackTrace();
            }

        }).start();

        dispose();
    }

    private void onApply() {
        XiaoyaojiConfigInfo xiaoyaojiConfigInfo = XiaoyaojiConfigInfo.getInstance(project);

        Object selectedProjectItem = projectComboBox.getSelectedItem();
        if (selectedProjectItem != null) {
            XiaoyaojiProject selectedProject = (XiaoyaojiProject) selectedProjectItem;
            xiaoyaojiConfigInfo.setProjectId(selectedProject.getId());
            if (selectDocStruct != null) {
                Map<String, String> selectParentMap = xiaoyaojiConfigInfo.getSelectParentMap();
                selectParentMap.put(selectedProject.getId(), selectDocStruct.getId());
            }
        }

        xiaoyaojiConfigInfo.setUrlPrefix(urlPrefixField.getText());
        xiaoyaojiConfigInfo.setUsername(usernameField.getText());
        xiaoyaojiConfigInfo.setHost(hostField.getText());
        xiaoyaojiConfigInfo.setPassword(passwordField.getText());

        Map<String, String> urlPrefixMap = xiaoyaojiConfigInfo.getUrlPrefixMap();
        urlPrefixMap.put(module.getName(), urlPrefixField.getText());
    }

//    private void onCancel() {
//        dispose();
//    }

    private void onSelect() {
        Object selectItem = projectComboBox.getSelectedItem();

        if (selectItem != null) {
            try {
                XiaoyaojiProject selectedProject = (XiaoyaojiProject) selectItem;
                List<XiaoyaojiDocStruct> docStructs = xiaoyaojiClient.docList(hostField.getText(), selectedProject.getId());
                List<XiaoyaojiDocStruct> allFolder = XiaoyaojiDocStruct.getAllFolder(docStructs);
                //    private JButton applyButton;
                TreeChooserDialog folderChooserDialog = new TreeChooserDialog(project, allFolder, "Choose folder", "Choose a folder");
                folderChooserDialog.show();
                List<XiaoyaojiDocStruct> chosenElements = folderChooserDialog.getChosenElements();
                if (!chosenElements.isEmpty()) {
                    selectDocStruct = chosenElements.get(0);
                    folderField.setText(selectDocStruct.getName());
                }

            } catch (Exception e1) {
                e1.printStackTrace();
            }
        }

        this.toFront();
    }

    @Nullable
    @Override
    protected JComponent createCenterPanel() {
        onConnect();
        return contentPanel;
    }

    @Override
    protected void doOKAction() {
        onOK();
        super.doOKAction();
    }

//    @Nullable
//    @Override
//    protected JComponent createCenterPanel() {
//        contentPanel.add(cancelButton);
//        return contentPanel;
//    }
}
