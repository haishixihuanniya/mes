package com.qcadoo.mes.view.components.tree;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.qcadoo.mes.model.FieldDefinition;
import com.qcadoo.mes.model.types.TreeType;
import com.qcadoo.mes.view.ComponentDefinition;
import com.qcadoo.mes.view.ComponentState;
import com.qcadoo.mes.view.ViewComponent;
import com.qcadoo.mes.view.patterns.AbstractComponentPattern;
import com.qcadoo.mes.view.xml.ViewDefinitionParser;

@ViewComponent("tree")
public final class TreeComponentPattern extends AbstractComponentPattern {

    private static final String JSP_PATH = "elements/tree.jsp";

    private static final String JS_OBJECT = "QCD.components.elements.Tree";

    // private String correspondingView;
    //
    // private String correspondingComponent;
    //
    // private String nodeLabelExpression;

    private Map<String, TreeDataType> dataTypes = new HashMap<String, TreeDataType>();

    public TreeComponentPattern(final ComponentDefinition componentDefinition) {
        super(componentDefinition);
    }

    @Override
    public ComponentState getComponentStateInstance() {
        return new TreeComponentState(getFieldDefinition(), dataTypes);
    }

    @Override
    public void parse(final Node componentNode, final ViewDefinitionParser parser) {
        super.parse(componentNode, parser);
        NodeList childNodes = componentNode.getChildNodes();
        for (int i = 0; i < childNodes.getLength(); i++) {
            Node child = childNodes.item(i);
            if (child.getNodeType() != Node.ELEMENT_NODE) {
                continue;
            }
            if ("dataType".equals(child.getNodeName())) {
                String dataTypeName = parser.getStringAttribute(child, "name");
                TreeDataType dataType = new TreeDataType(dataTypeName);
                NodeList dataTypeOptionNodes = child.getChildNodes();
                for (int dton = 0; dton < dataTypeOptionNodes.getLength(); dton++) {
                    Node dataTypeOptionNode = dataTypeOptionNodes.item(dton);
                    if (dataTypeOptionNode.getNodeType() != Node.ELEMENT_NODE) {
                        continue;
                    }
                    if (!"option".equals(dataTypeOptionNode.getNodeName())) {
                        throw new IllegalStateException("Tree 'dataType' node can only contains 'option' nodes");
                    }
                    String optionType = parser.getStringAttribute(dataTypeOptionNode, "type");
                    String optionValue = parser.getStringAttribute(dataTypeOptionNode, "value");
                    dataType.setOption(optionType, optionValue);
                }
                dataType.validate();
                dataTypes.put(dataTypeName, dataType);
            } else {
                throw new IllegalStateException("Tree can only contains 'dataType' nodes");
            }
        }
        if (dataTypes.size() == 0) {
            throw new IllegalStateException("Tree must contains at least one 'dataType' node");
        }
    }

    // @Override
    // protected void initializeComponent() throws JSONException {
    // checkFieldDefinition();
    //
    // for (ComponentOption option : getOptions()) {
    // if ("correspondingView".equals(option.getType())) {
    // correspondingView = option.getValue();
    // } else if ("correspondingComponent".equals(option.getType())) {
    // correspondingComponent = option.getValue();
    // } else if ("nodeLabelExpression".equals(option.getType())) {
    // nodeLabelExpression = option.getValue();
    // } else {
    // throw new IllegalStateException("Unknown option for tree: " + option.getType());
    // }
    // }
    // }

    @Override
    protected JSONObject getJsOptions(final Locale locale) throws JSONException {
        JSONObject json = new JSONObject();
        // json.put("correspondingView", correspondingView);
        // json.put("correspondingComponent", correspondingComponent);
        JSONObject dataTypesObject = new JSONObject();
        for (Map.Entry<String, TreeDataType> dataTypeEntry : dataTypes.entrySet()) {
            dataTypesObject.put(dataTypeEntry.getKey(), dataTypeEntry.getValue().toJson());
        }
        json.put("dataTypes", dataTypesObject);

        json.put("belongsToFieldName", getBelongsToFieldDefinition().getName());

        JSONObject translations = new JSONObject();

        translations.put("newButton", getTranslation("newButton", locale));
        translations.put("editButton", getTranslation("editButton", locale));
        translations.put("deleteButton", getTranslation("deleteButton", locale));
        translations.put("confirmDeleteMessage", getTranslation("confirmDeleteMessage", locale));

        translations.put("header", getTranslationService().translate(getTranslationPath() + ".header", locale));

        translations.put("loading", getTranslationService().translate("commons.loading", locale));

        json.put("translations", translations);

        return json;
    }

    private String getTranslation(final String key, final Locale locale) throws JSONException {
        List<String> codes = Arrays.asList(new String[] { getTranslationPath() + "." + key, "core.tree." + key });
        return getTranslationService().translate(codes, locale);
    }

    private void checkFieldDefinition() {
        if (getFieldDefinition() != null) {
            if (TreeType.class.isAssignableFrom(getFieldDefinition().getType().getClass())) {
                return;
            }
        }
        throw new IllegalStateException("Field has to be a tree one");
    }

    private FieldDefinition getBelongsToFieldDefinition() {
        if (getFieldDefinition() != null) {
            if (TreeType.class.isAssignableFrom(getFieldDefinition().getType().getClass())) {
                TreeType treeType = (TreeType) getFieldDefinition().getType();
                return treeType.getDataDefinition().getField(treeType.getJoinFieldName());
            }
        }
        throw new IllegalStateException("Field has to be a tree one");
    }

    @Override
    public String getJspFilePath() {
        return JSP_PATH;
    }

    @Override
    public String getJsFilePath() {
        return JS_PATH;
    }

    @Override
    public String getJsObjectName() {
        return JS_OBJECT;
    }
}
