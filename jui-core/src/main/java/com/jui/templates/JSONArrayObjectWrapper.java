package com.jui.templates;

import com.fasterxml.jackson.databind.node.ArrayNode;

import freemarker.template.DefaultObjectWrapper;
import freemarker.template.TemplateModel;
import freemarker.template.TemplateModelException;
import freemarker.template.TemplateSequenceModel;

@SuppressWarnings("deprecation")
public class JSONArrayObjectWrapper extends DefaultObjectWrapper {

    @Override
    public TemplateModel handleUnknownType (Object obj) throws TemplateModelException {

        if (obj instanceof ArrayNode) {
            return new JSONArraySequenceModel((ArrayNode) obj);
        }

        return super.handleUnknownType(obj);
    }


    public class JSONArraySequenceModel implements TemplateSequenceModel {

        private ArrayNode jsonArray;

        public JSONArraySequenceModel(ArrayNode jsonArray) {
            this.jsonArray = jsonArray;
        }

        @Override
        public TemplateModel get(int index) throws TemplateModelException {
            TemplateModel model = null;
            try {

                model = wrap(jsonArray.get(index));
            } catch (Exception e) {
                e.printStackTrace();
            }
            return model;
        }

        @Override
        public int size() throws TemplateModelException {
            return jsonArray.size();
        }

    }


}