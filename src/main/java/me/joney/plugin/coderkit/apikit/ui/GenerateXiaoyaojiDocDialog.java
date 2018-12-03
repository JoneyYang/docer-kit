package me.joney.plugin.coderkit.apikit.ui;

import com.intellij.openapi.module.Module;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.progress.ProgressManager;
import com.intellij.openapi.progress.Task.Backgroundable;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.openapi.ui.MessageType;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
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
import me.joney.plugin.coderkit.util.MessageUtil;
import org.apache.commons.lang.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Created by yang.qiang on 2018/09/29.
 */
public class GenerateXiaoyaojiDocDialog extends DialogWrapper {

    private List<RestApiDoc> apiDocs;
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
    private XiaoyaojiClient xiaoyaojiClient;
    private XiaoyaojiDocStruct selectDocStruct;

    public GenerateXiaoyaojiDocDialog(Project project, Module module, List<RestApiDoc> doc) {
        super(project);
        this.project = project;
        this.module = module;
        this.apiDocs = doc;
        init();

        initPanel(project, module);
        initListener();
        initConnection();

    }

    private void initConnection() {
        if (StringUtils.isNotBlank(hostField.getText())
            && StringUtils.isNotBlank(usernameField.getText())
            && StringUtils.isNotBlank(passwordField.getText())) {
            try {
                onConnect();
            } catch (Exception e1) {
                e1.printStackTrace();
            }
        }
    }

    private void initPanel(Project project, Module module) {
        XiaoyaojiConfigInfo xiaoyaojiConfigInfo = XiaoyaojiConfigInfo.getInstance(project);
        hostField.setText(xiaoyaojiConfigInfo.getHost());
        usernameField.setText(xiaoyaojiConfigInfo.getUsername());
        passwordField.setText(xiaoyaojiConfigInfo.getPassword());

        Map<String, String> urlPrefixMap = xiaoyaojiConfigInfo.getUrlPrefixMap();
        urlPrefixField.setText(urlPrefixMap.get(module.getName()));
        setTitle("Generate Xiaoyaoji Doc");
    }

    private void initListener() {
        selectButton.addActionListener(e -> onSelect());
        connectButton.addActionListener(e -> onConnect());
        folderField.addActionListener(e -> onSelect());
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
        dispose();

        ProgressManager.getInstance().run(new Backgroundable(project, "Generate Xiaoyaoji Docs") {
            @Override
            public void run(@NotNull ProgressIndicator indicator) {
                List<XiaoyaojiDoc> docs = apiDocs.stream().map(XiaoyaojiConvert::convertDoc).collect(Collectors.toList());
                Object selectedItem = projectComboBox.getSelectedItem();
                XiaoyaojiProject xiaoyaojiProject = (XiaoyaojiProject) selectedItem;
                try {
                    for (XiaoyaojiDoc xiaoyaojiDoc : docs) {
                        xiaoyaojiClient.createDoc(xiaoyaojiDoc, hostField.getText(), xiaoyaojiProject.getId(), selectDocStruct.getId(),
                            urlPrefixField.getText());

                    }
                } catch (IOException e) {
                    MessageUtil.popup(project, "Generate Xiaoyaoji Doc Failure", MessageType.ERROR);
                }
                MessageUtil.popup(project, "Generate Xiaoyaoji Doc Success", MessageType.INFO);
            }
        });


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


    private void onSelect() {
        Object selectItem = projectComboBox.getSelectedItem();

        if (selectItem != null) {
            try {
                XiaoyaojiProject selectedProject = (XiaoyaojiProject) selectItem;
                List<XiaoyaojiDocStruct> docStruct = xiaoyaojiClient.docList(hostField.getText(), selectedProject.getId());
                DocStructChooser chooser = new DocStructChooser(project, true, docStruct);
                chooser.show();
                XiaoyaojiDocStruct selected = chooser.getSelected();
                if (selected != null) {
                    folderField.setText(selected.getName());
                }
                selectDocStruct = selected;
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

}
