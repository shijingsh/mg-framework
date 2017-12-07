package com.mg.common.metadata.freeMarker.directive;

import com.mg.common.entity.vo.PermissionActionEnum;
import com.mg.common.metadata.util.MetaDataUtils;
import com.mg.framework.entity.metadata.MirrorPropertyEntity;
import freemarker.core.Environment;
import freemarker.template.*;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;

import java.io.IOException;
import java.util.Map;

/**
 * Created by liukefu on 2015/9/30.
 */
public abstract class BaseDirective  implements TemplateDirectiveModel {

    @Override
    public void execute(Environment environment, Map map, TemplateModel[] templateModels, TemplateDirectiveBody templateDirectiveBody) throws TemplateException, IOException {

    }

    public String createLabel(MirrorPropertyEntity mirrorProperty){
        StringBuilder sb = new StringBuilder();
        sb.append("<label class=\"template_title\">");
        if(!mirrorProperty.getIsNullable()) {
            sb.append("<span class=\"cm-required\">*</span>");
        }
        sb.append(mirrorProperty.getName()).append(":");
        sb.append("</label>");

        return sb.toString();
    }

    public String createField(MirrorPropertyEntity mPropertyEntity){
        StringBuilder sb = new StringBuilder();
        sb.append("<div>");
        switch (mPropertyEntity.getControllerType()){
            case text:
                sb.append("<input type=\"text\"").append("class=\"input_m\"")
                        .append(" ng-model=\"object.").append(mPropertyEntity.getPropertyPath()).append("\"");
                if(!mPropertyEntity.getIsNullable()){
                    sb.append(" cm-validator=\"required\"");
                }
                sb.append(" property=").append(mPropertyEntity.getId());
                sb.append(" />");
                break;
            case number:
                sb.append("<input type=\"number\"").append(" class=\"input_m\"")
                        .append(" ng-model=\"object.").append(mPropertyEntity.getPropertyPath()).append("\"");
                if(!mPropertyEntity.getIsNullable()){
                    sb.append(" cm-validator=\"required\"");
                }
                sb.append(" property=").append(mPropertyEntity.getId());
                sb.append(" />");
                break;
            case bool:
                sb.append("<input type=\"checkbox\"").append(" class=\"checkbox\"")
                        .append(" ng-model=\"object.").append(mPropertyEntity.getPropertyPath()).append("\"");
                sb.append(" property=").append(mPropertyEntity.getId());
                sb.append(" />");
                break;
            case date:
                sb.append("<input type=\"text\"").append(" class=\"input_m\"")
                        .append(" ng-model=\"object.").append(mPropertyEntity.getPropertyPath()).append("\"");
                if(!mPropertyEntity.getIsNullable()){
                    sb.append(" cm-validator=\"required\" ");
                }
                sb.append(" property=").append(mPropertyEntity.getId());
                sb.append(" cm-datepicker />");
                break;
            case mEnum:
                sb.append("<select ").append(" class=\"select\"")
                        .append(" ng-options=\"opt.key as opt.name for opt in selects.").append(mPropertyEntity.getFieldName()).append("\"")
                        .append(" ng-model=\"object.").append(mPropertyEntity.getPropertyPath()).append("\"");
                if(!mPropertyEntity.getIsNullable()){
                    sb.append(" cm-validator=\"selectRequired\"");
                }
                sb.append(" property=").append(mPropertyEntity.getId());
                sb.append(" />");
                break;
            case object:
                //隐藏域
                sb.append("<input type=\"hidden\"");
                sb.append(" ng-model=\"object.").append(mPropertyEntity.getPropertyPath()).append("\" />");
                //名称域
                sb.append("<input type=\"text\"").append(" class=\"input_m\"");
                sb.append(" ng-model=\"object.").append(MetaDataUtils.getObjectFieldValue(mPropertyEntity)).append("\"");
                sb.append(" select-object apisetter=\"objectSelect.apiSetter\" listeners=\"objectSelect.listeners\" options=\"objectSelect.options\" ");
                sb.append(" ng-click=\"objectSelect.api.popSelectWindow('"+mPropertyEntity.getMetaProperty().getMetaObject().getId()+"','"+mPropertyEntity.getPropertyPath()+"')\" ");
                if(!mPropertyEntity.getIsNullable()){
                    sb.append(" cm-validator=\"required\"");
                }
                sb.append(" property=").append(mPropertyEntity.getId());
                sb.append(" />");
                break;
        }
        sb.append("</div>");
        return sb.toString();
    }

    public String createViewField(Environment env,MirrorPropertyEntity mPropertyEntity){
        boolean isPermitted = isPermitted(mPropertyEntity, PermissionActionEnum.action_update,getObjectId(env));
        StringBuilder sb = new StringBuilder();
        sb.append("<div class=\"content_ceil\">");
        sb.append(" <div ng-hide=\"editStatus.").append(mPropertyEntity.getPropertyPath()).append("\" can_edit> ");
        sb.append(viewFieldValue(env,mPropertyEntity));
        if(isPermitted) {
            sb.append("     <a class='icon_edit'  ng-click=\"editInfo('").append(mPropertyEntity.getPropertyPath()).append("')\" title=\"编辑\"></a>");
        }
        sb.append(" </div>");

        if(isPermitted){
            sb.append(" <div ng-if=\"editStatus.").append(mPropertyEntity.getPropertyPath()).append("\"> ");
            sb.append(createField(mPropertyEntity));
            sb.append("  <a class=\"icon_ok\"  ng-click=\"save('")
                    .append(mPropertyEntity.getPropertyPath()).append("','")
                    .append(mPropertyEntity.getId()).append("')\" title=\"确定\"></a> ");
            sb.append(" <a class=\"icon_cancel\"  ng-click=\"cancelEdit('").append(mPropertyEntity.getPropertyPath()).append("')\" title=\"取消\"></a> ");
            sb.append(" </div>");
        }
        sb.append("</div>");
        return sb.toString();
    }

    public String viewFieldValue(Environment env,MirrorPropertyEntity mPropertyEntity){
        StringBuilder sb = new StringBuilder();
        if(isPermitted(mPropertyEntity,PermissionActionEnum.action_view,getObjectId(env))) {
            switch (mPropertyEntity.getControllerType()) {
                case text:
                case number:
                case date:
                    sb.append("{{object.").append(mPropertyEntity.getPropertyPath()).append("}}");
                    break;
                case bool:
                    sb.append("{{object.").append(mPropertyEntity.getPropertyPath()).append(" | booleanFilter}}");
                    break;
                case mEnum:
                    sb.append("{{object.").append(mPropertyEntity.getPropertyPath()).append(" | selectFilter:selects.").append(mPropertyEntity.getPropertyPath()).append("}}");
                    break;
                case object:
                    sb.append("{{object.").append(MetaDataUtils.getObjectFieldValue(mPropertyEntity)).append("}}");
                    break;
            }
        }
        return sb.toString();
    }

    protected String getObjectId(Environment environment){
        try {
            TemplateHashModel model = environment.getDataModel();
            return ((SimpleScalar) model.get("id")).getAsString();
        } catch (TemplateModelException e) {
            e.printStackTrace();
            return null;
        }
    }

    public  String getPermissionName(MirrorPropertyEntity mPropertyEntity, PermissionActionEnum action, String id){

        StringBuilder sb = new StringBuilder();
        sb.append(mPropertyEntity.getBelongMObject().getTableName()).append(":")
                .append(mPropertyEntity.getFieldName()).append(":")
                .append(action).append(":").append(id);

        return sb.toString();
    }

    public  boolean isPermitted(MirrorPropertyEntity mPropertyEntity, PermissionActionEnum action, String id){

        if(mPropertyEntity==null){
            return false;
        }
        String permission = getPermissionName(mPropertyEntity,action,id);

        Subject subject = SecurityUtils.getSubject();

        return subject.isPermitted(permission);
    }
}
