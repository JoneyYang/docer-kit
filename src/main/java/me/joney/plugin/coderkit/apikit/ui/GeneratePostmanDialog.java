package me.joney.plugin.coderkit.apikit.ui;

import com.alibaba.fastjson.JSON;
import com.intellij.openapi.fileChooser.FileChooser;
import com.intellij.openapi.fileChooser.FileChooserDescriptor;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.openapi.ui.Splitter;
import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.ui.ScrollPaneFactory;
import com.intellij.ui.table.JBTable;
import java.awt.Dimension;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.event.TableModelListener;
import javax.swing.table.TableModel;
import lombok.Data;
import me.joney.plugin.coderkit.apikit.bean.RestApiDoc;
import me.joney.plugin.coderkit.apikit.postman.KeyValuePair;
import me.joney.plugin.coderkit.apikit.postman.PostmanCollection;
import me.joney.plugin.coderkit.apikit.postman.PostmanCollection.PostmanInfo;
import me.joney.plugin.coderkit.apikit.postman.PostmanCollection.PostmanItem;
import me.joney.plugin.coderkit.apikit.postman.PostmanRequest;
import me.joney.plugin.coderkit.apikit.postman.PostmanRequest.RequestBody;
import me.joney.plugin.coderkit.apikit.postman.PostmanRequest.RequestParameter;
import me.joney.plugin.coderkit.apikit.postman.PostmanRequest.RequestUrl;
import me.joney.plugin.coderkit.apikit.store.PostmanStore;
import org.apache.commons.lang.StringUtils;
import org.jetbrains.annotations.Nullable;

/**
 * Created by yang.qiang on 2018/11/26.
 */
public class GeneratePostmanDialog extends DialogWrapper {

    //    private PostmanStore postmanStore;
    private Module module;
    private Project project;
    private JPanel contentPanel;
    private JTextField filePathTextField;
    private JButton chooserButton;
    private JTextField prefixTextField;
    private JTable headsTable;
    private JBTable replaceTable;
    private JButton headsAddButton;
    private JButton replaceAddButton;
    private JButton replaceDelButton;
    private JButton headsDelButton;
    private JPanel headPanel;
    private JPanel replacePanel;
    private List<RestApiDoc> docs;

    private String path;

    public GeneratePostmanDialog(@Nullable Project project, List<RestApiDoc> docs, Module module) {
        super(project, true);
        this.project = project;
        this.docs = docs;
        this.module = module;
        PostmanStore postmanStore = PostmanStore.getInstance(project);

        init();

        prefixTextField.setText(StringUtils.trimToNull(postmanStore.getPrefixMap().get(module.getName())));
        filePathTextField.setText(StringUtils.trimToNull(postmanStore.getPath()));

        {
            ParameterTableModel replacesTableModel = new ParameterTableModel();
            replacesTableModel.setModelList(postmanStore.getReplaceList());

            /// 设置replaces table 组件
            replaceTable = new JBTable(replacesTableModel);

            JScrollPane replacesScrollPane = ScrollPaneFactory.createScrollPane(replaceTable);
            replacesScrollPane.setPreferredSize(new Dimension(150, 125));

            Splitter replaceSplitter = new Splitter(false, (float) 0.6);
            replaceSplitter.setFirstComponent(replacesScrollPane);

            replacePanel.add(replacesScrollPane);


        }

        {
            ParameterTableModel headsTableModel = new ParameterTableModel();
            headsTableModel.setModelList(postmanStore.getHeadList());

            /// 设置heads table组件
            headsTable = new JBTable(headsTableModel);

            Splitter headsSplitter = new Splitter(false, (float) 0.6);
            JScrollPane headsScrollPane = ScrollPaneFactory.createScrollPane(headsTable);
            headsScrollPane.setPreferredSize(new Dimension(150, 125));
            headsSplitter.setFirstComponent(headsScrollPane);
            headPanel.add(headsScrollPane);

        }

        /// 添加事件
        chooserButton.addActionListener(e -> {
            VirtualFile fileByIoFile = null;
            if (StringUtils.isNotBlank(path)) {
                fileByIoFile = LocalFileSystem.getInstance().findFileByIoFile(new File(path));
            }
            FileChooserDescriptor descriptor = new FileChooserDescriptor(false, true, false, false, false, false);
            VirtualFile virtualFile = FileChooser.chooseFile(descriptor, project, fileByIoFile);
            if (virtualFile != null) {
                path = virtualFile.getPath();
                filePathTextField.setText(virtualFile.getPath());
            }

        });
        replaceAddButton.addActionListener(e -> {
            List<KeyValuePair> modelList = ((ParameterTableModel) replaceTable.getModel()).getModelList();
            ArrayList<KeyValuePair> newModelList = new ArrayList<>(modelList);
            newModelList.add(new KeyValuePair().setKey("").setValue("").setSelected(true));
            replaceTable.setModel(new ParameterTableModel(newModelList));
            replaceTable.changeSelection(newModelList.size() - 1, 0, false, false);
        });
        replaceDelButton.addActionListener(e -> {
            List<KeyValuePair> modelList = ((ParameterTableModel) replaceTable.getModel()).getModelList();
            ArrayList<KeyValuePair> newModelList = new ArrayList<>(modelList);
            int selectedRow = replaceTable.getSelectedRow();
            newModelList.remove(selectedRow);
            replaceTable.setModel(new ParameterTableModel(newModelList));

            int changeRow = selectedRow >= (newModelList.size() - 1) ? newModelList.size() - 1 : selectedRow;
            replaceTable.changeSelection(changeRow, 0, false, false);
        });

        headsAddButton.addActionListener(e -> {
            List<KeyValuePair> modelList = ((ParameterTableModel) headsTable.getModel()).getModelList();
            ArrayList<KeyValuePair> newModelList = new ArrayList<>(modelList);
            newModelList.add(new KeyValuePair().setKey("").setValue("").setSelected(true));
            headsTable.setModel(new ParameterTableModel(newModelList));
            headsTable.changeSelection(newModelList.size() - 1, 0, false, false);
        });
        headsDelButton.addActionListener(e -> {
            List<KeyValuePair> modelList = ((ParameterTableModel) headsTable.getModel()).getModelList();
            ArrayList<KeyValuePair> newModelList = new ArrayList<>(modelList);
            int selectedRow = headsTable.getSelectedRow();
            newModelList.remove(selectedRow);
            headsTable.setModel(new ParameterTableModel(newModelList));

            int changeRow = selectedRow >= (newModelList.size() - 1) ? newModelList.size() - 1 : selectedRow;
            headsTable.changeSelection(changeRow, 0, false, false);
        });

        contentPanel.setSize(new Dimension(400, 550));


    }



    @Data
    private class ParameterTableModel implements TableModel {

        private List<KeyValuePair> modelList;

        public ParameterTableModel() {
            modelList = new ArrayList<>();
        }

        public ParameterTableModel(List<KeyValuePair> modelList) {
            this.modelList = modelList;
        }

        @Override
        public int getRowCount() {
            return modelList.size();
        }

        @Override
        public int getColumnCount() {
            return 3;
        }

        @Override
        public String getColumnName(int columnIndex) {
            switch (columnIndex) {
                case 0:
                    return "Enable";
                case 1:
                    return "key";
                case 2:
                    return "value";
                default:
                    return "";
            }
        }

        @Override
        public Class<?> getColumnClass(int columnIndex) {
            switch (columnIndex) {
                case 0:
                    return Boolean.class;
                case 1:
                    return String.class;
                case 2:
                    return String.class;
                default:
                    return Object.class;
            }
        }

        @Override
        public boolean isCellEditable(int rowIndex, int columnIndex) {
            return true;
        }

        @Override
        public Object getValueAt(int rowIndex, int columnIndex) {
            KeyValuePair pair = modelList.get(rowIndex);

            switch (columnIndex) {
                case 0:
                    return pair.getSelected();
                case 1:
                    return pair.getKey();
                case 2:
                    return pair.getValue();
                default:
                    return null;
            }
        }

        @Override
        public void setValueAt(Object value, int rowIndex, int columnIndex) {
            KeyValuePair pair = modelList.get(rowIndex);

            switch (columnIndex) {
                case 0:
                    pair.setSelected((boolean) value);
                    break;
                case 1:
                    pair.setKey((String) value);
                    break;
                case 2:
                    pair.setValue((String) value);
                    break;
                default:
                    break;
            }


        }

        @Override
        public void addTableModelListener(TableModelListener l) {

        }

        @Override
        public void removeTableModelListener(TableModelListener l) {

        }
    }


    @Override
    protected void doOKAction() {
        super.doOKAction();
        onApply();

        PostmanStore postmanStore = PostmanStore.getInstance(project);
        PostmanCollection collection = new PostmanCollection();

        PostmanInfo info = new PostmanInfo();
        info.setPostmanId("dd36b8ed-603f-4e98-8565-c1030de5d9c3");
        info.setName("Export from Intellij");
        info.setSchema("https://schema.getpostman.com/json/collection/v2.1.0/collection.json");
        collection.setInfo(info);

        List<PostmanItem> items = docs.stream().map(doc -> {
            PostmanItem item = new PostmanItem();
            item.setName(doc.getName());

            PostmanRequest request = new PostmanRequest();
            request.setMethod(doc.getMethod());

            StringBuilder descBuilder = new StringBuilder();

            descBuilder.append(doc.getDescription()).append("\n\n");

            if (!doc.getRequestPathParam().isEmpty()) {
                descBuilder.append("\n\n路径参数:");
                doc.getRequestPathParam().forEach(param -> {
                    descBuilder.append(param.getName()).append(":").append(param.getType()).append("\t\t").append(param.getDescription());
                    descBuilder.append("\n");
                });
            }

            if (!doc.getRequestBodyParams().isEmpty()) {
                descBuilder.append("\n\n请求体参数:");
                doc.getRequestBodyParams().forEach(param -> {
                    descBuilder.append(param.getName()).append(":").append(param.getType()).append("\t\t").append(param.getDescription());
                    descBuilder.append("\n");
                });
            }

            if (!doc.getRequestBodyParams().isEmpty()) {
                descBuilder.append("\n\n请求体参数:");
                doc.getRequestBodyParams().forEach(param -> {
                    descBuilder.append(param.getName()).append(":").append(param.getType()).append("\t\t").append(param.getDescription());
                    descBuilder.append("\n");
                });
            }

            request.setDescription(descBuilder.toString());
            item.setRequest(request);

            List<RequestParameter> heads = doc.getRequestHeadParams().stream().map(head -> {
                RequestParameter postmanHead = new RequestParameter();
                postmanHead.setKey(head.getName());
                postmanHead.setValue("");
                postmanHead.setDescription(head.getDescription());
                return postmanHead;
            }).collect(Collectors.toList());

            List<KeyValuePair> headList = postmanStore.getHeadList();
            List<RequestParameter> storeHeads = headList.stream().filter(KeyValuePair::getSelected).map(head -> {
                RequestParameter postmanHead = new RequestParameter();
                postmanHead.setKey(head.getKey());
                postmanHead.setValue(head.getValue());
                return postmanHead;
            }).collect(Collectors.toList());
            heads.addAll(storeHeads);




            request.setHeader(heads);

            RequestBody body = new RequestBody();
            body.setMode("formdata");
            body.setRaw("");
//            List<RequestParameter> bodyList = doc.getRequestBodyParams().stream()
//                .map(restParam ->
//                    new RequestParameter().setDescription(restParam.getDescription()).setKey(restParam.getName()).setValue("")
//                )
//                .collect(Collectors.toList());
//
//            body.setFormdata(bodyList);


            request.setBody(body);

            String docUrl = doc.getUrl();

            List<KeyValuePair> replaceList = postmanStore.getReplaceList();
            for (KeyValuePair replace : replaceList) {
                if (replace.getSelected()) {
                    docUrl = docUrl.replace(replace.getKey(), replace.getValue());
                }
            }

            RequestUrl url = new RequestUrl();
            url.setRaw(docUrl);
            url.setHost(new String[]{postmanStore.getPrefixMap().get(module.getName())});
            url.setPath(docUrl.split("[/\\\\]"));
            List<RequestParameter> queryList = doc.getRequestQueryParams().stream()
                .map(query -> new RequestParameter().setKey(query.getName()).setValue("").setDescription(query.getDescription()))
                .collect(Collectors.toList());
            url.setQuery(queryList);
            request.setUrl(url);

            return item;
        }).collect(Collectors.toList());

        ArrayList<PostmanItem> rootFolder = new ArrayList<>();
        rootFolder.add(new PostmanItem().setName("Requests").setItem(items));

        collection.setItem(rootFolder);

        try {
            OutputStreamWriter stream = new OutputStreamWriter(new FileOutputStream(filePathTextField.getText() + "/intellij-postman-export.json"),
                StandardCharsets.UTF_8);
            stream.write(JSON.toJSONString(collection));
            stream.close();

            System.out.println();

        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    @Nullable
    @Override
    protected JComponent createCenterPanel() {

        return contentPanel;
    }

    private void createUIComponents() {
        // TODO: place custom component creation code here
    }


    private void onApply() {
        PostmanStore postmanStore = PostmanStore.getInstance(project);
        postmanStore.setPath(filePathTextField.getText());
        Map<String, String> prefixMap = postmanStore.getPrefixMap();
        prefixMap.put(module.getName(), prefixTextField.getText());
        postmanStore.setPrefixMap(prefixMap);

        postmanStore.setHeadListJson(JSON.toJSONString(((ParameterTableModel) headsTable.getModel()).getModelList()));
        postmanStore.setReplaceListJson(JSON.toJSONString(((ParameterTableModel) replaceTable.getModel()).getModelList()));

    }

}
