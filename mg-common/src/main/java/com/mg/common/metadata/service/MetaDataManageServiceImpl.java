package com.mg.common.metadata.service;

import com.mg.common.metadata.dao.*;
import com.mg.common.metadata.freeMarker.defaulTemplate.TempleSourceCreator;
import com.mg.common.metadata.util.MPropertyComparator;
import com.mg.common.metadata.util.MetaDataUtils;
import com.mg.common.metadata.freeMarker.defaulTemplate.DefaultTemplateCreator;
import com.mg.framework.entity.metadata.*;
import com.mg.framework.entity.vo.PageTableVO;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.Session;
import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.hibernate.metadata.ClassMetadata;
import org.hibernate.type.Type;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by liukefu on 2015/9/5.
 */
@Service
public class MetaDataManageServiceImpl implements MetaDataManageService {
    @Autowired
    private MObjectDao mObjectDao;
    @Autowired
    private MPropertyDao mPropertyDao;
    @Autowired
    public MObjectDaoCustom mObjectDaoCustom;
    @Autowired
    private MetaDataCoreService metaDataCoreService;
    @Autowired
    private MirrorPropertyDao mirrorPropertyDao;
    @Autowired
    private MirrorPropertyCustomDao mirrorPropertyCustomDao;
    @Autowired
    private DefaultTemplateCreator defaultTemplateCreator;
    @Autowired
    private TempleSourceCreator templeSourceCreator;
    @Autowired
    private MTemplateService mTemplateService;
    @PersistenceContext
    EntityManager entityManager;
    /**
     * 查询所有元数据对象
     * @return
     */
    public List<MObjectEntity> findAllObject(){
        return  mObjectDao.findByIsEnable(true);
    }
    /**
     * 元数据对象列表 分页
     * @param map
     * @return
     */
    public PageTableVO findPageList(Map<String, Object> map) {
        Integer pageNo = (Integer)map.get("pageNo");
        Integer pageSize = (Integer)map.get("pageSize");

        List<MObjectEntity> list = mObjectDaoCustom.findPageList(map);
        Long totalCount = mObjectDaoCustom.findCount(map);
        PageTableVO vo = new PageTableVO();
        vo.setRowData(list);
        vo.setTotalCount(totalCount);
        vo.setPageNo(pageNo);
        vo.setPageSize(pageSize);
        return vo;
    }
    /**
     * 保存元数据对象
     * @param objectEntity
     * @return
     */
    @Transactional
    public MObjectEntity saveObject(MObjectEntity objectEntity){
        boolean isNew = StringUtils.isBlank(objectEntity.getId());
        MObjectEntity mObjectEntity = mObjectDao.save(objectEntity);

        if(isNew){
            //创建元数据对象时，创建表并且默认创建ID属性
            metaDataCoreService._createTabel(mObjectEntity);

            createNormalColumns(objectEntity);
        }
        return mObjectEntity;
    }

    private void createNormalColumns(MObjectEntity mObjectEntity){
        MPropertyEntity mPropertyEntity = new MPropertyEntity();
        mPropertyEntity.setName("主键");
        mPropertyEntity.setBelongMObject(mObjectEntity);
        mPropertyEntity.setIsEnable(true);
        mPropertyEntity.setFieldName(MetaDataUtils.META_FIELD_ID);
        mPropertyEntity.setFieldLength(30);
        mPropertyEntity.setFieldType(MFieldTypeEnum.VARCHAR);
        mPropertyEntity.setIsNullable(false);
        mPropertyEntity.setIsPrimaryKey(true);
        mPropertyEntity.setTypeEnum(MTypeEnum.normal);
        //保存属性
        saveProperty(mPropertyEntity);
        //创建通用字段：新建人，新建时间，修改人，修改时间
        MPropertyEntity createById = new MPropertyEntity("创建人ID",MetaDataUtils.META_FIELD_CREATED_ID,MFieldTypeEnum.VARCHAR,30);
        MPropertyEntity createByName = new MPropertyEntity("创建人",MetaDataUtils.META_FIELD_CREATED_NAME,MFieldTypeEnum.VARCHAR,255);
        MPropertyEntity createByDate = new MPropertyEntity("创建时间",MetaDataUtils.META_FIELD_CREATED_DATE,MFieldTypeEnum.DATETIME,10);
        MPropertyEntity updateById = new MPropertyEntity("最后更新人ID",MetaDataUtils.META_FIELD_UPDATED_ID,MFieldTypeEnum.VARCHAR,30);
        MPropertyEntity updateByName = new MPropertyEntity("最后更新人",MetaDataUtils.META_FIELD_UPDATED_NAME,MFieldTypeEnum.VARCHAR,255);
        MPropertyEntity updateByDate = new MPropertyEntity("最后更新时间",MetaDataUtils.META_FIELD_UPDATED_DATE,MFieldTypeEnum.DATETIME,10);
        MPropertyEntity name = new MPropertyEntity(mObjectEntity.getName()+"名称", MetaDataUtils.META_FIELD_NAME,MFieldTypeEnum.VARCHAR,500);
        //MPropertyEntity status = new MPropertyEntity("状态", MetaDataUtils.META_FIELD_STATUS,MFieldTypeEnum.INTEGER,4);
        name.setBelongMObject(mObjectEntity);
        //status.setBelongMObject(mObjectEntity);
        createById.setBelongMObject(mObjectEntity);
        createByName.setBelongMObject(mObjectEntity);
        createByDate.setBelongMObject(mObjectEntity);
        updateById.setBelongMObject(mObjectEntity);
        updateByName.setBelongMObject(mObjectEntity);
        updateByDate.setBelongMObject(mObjectEntity);
        saveProperty(createById);
        saveProperty(createByName);
        saveProperty(createByDate);
        saveProperty(updateById);
        saveProperty(updateByName);
        saveProperty(updateByDate);
        saveProperty(name);
        //saveProperty(status);
    }
    /**
     * 查询所有元数据
     * @return
     */
    public List<MPropertyEntity> findAllProperty(MObjectEntity objectEntity){
        List<MPropertyEntity> list =  mPropertyDao.findByBelongMObjectAndIsEnable(objectEntity, true);
        Collections.sort(list, new MPropertyComparator());
        return list;
    }
    /**
     * 查询所有用户元数据
     * @return
     */
    public List<MPropertyEntity> findAllCustomProperty(MObjectEntity objectEntity){
        List<MPropertyEntity>  list =  mPropertyDao.findByBelongMObjectAndIsEnable(objectEntity, true);
        List<MPropertyEntity>  listCustom = new ArrayList<>();
        for(MPropertyEntity propertyEntity:list){
            if(propertyEntity.getSort()>0){
                listCustom.add(propertyEntity);
            }
        }
        Collections.sort(listCustom, new MPropertyComparator());
        return listCustom;
    }
    /**
     * 查询所有元数据
     * @return
     */
    public List<MPropertyEntity> findAllProperty(){
        List<MPropertyEntity> list =   mPropertyDao.findByIsEnable(true);
        Collections.sort(list, new MPropertyComparator());
        return list;
    }
    /**
     * 保存元数据
     * @param propertyEntity
     * @return
     */
    @Transactional
    public MPropertyEntity saveProperty(MPropertyEntity propertyEntity){
        //新增元数据，增加对应表字段，主键不增加字段，因为主键和表是一起创建的
        if(StringUtils.isBlank(propertyEntity.getId()) && !propertyEntity.getIsPrimaryKey()) {
            metaDataCoreService._createFields(propertyEntity);
        }
/*        if(StringUtils.isNotBlank(propertyEntity.getId())) {
            MPropertyEntity dbProperty = mPropertyDao.findOne(propertyEntity.getId());
            if(!StringUtils.equals(dbProperty.getFieldName(),propertyEntity.getFieldName())){
                //字段名称不一样时，删除旧字段
                metaDataCoreService._dropFields(dbProperty);
            }
        }*/
        if(StringUtils.isNotBlank(propertyEntity.getId()) &&!propertyEntity.getIsPrimaryKey()) {
            metaDataCoreService._updateFields(propertyEntity);
        }
        if(propertyEntity.getSort()==0 && !MetaDataUtils.isSystemFields(propertyEntity.getFieldName())){
            //自动生成排序
            int sort = mPropertyDao.maxSort(propertyEntity.getBelongMObject());
            propertyEntity.setSort(sort + 2);
        }

        MPropertyEntity mPropertyEntity = mPropertyDao.save(propertyEntity);
        //重新设置排序
        if(StringUtils.isNotBlank(propertyEntity.getId())){
            reSortAllProperties(propertyEntity.getBelongMObject());
        }
        //刷新镜像元数据
        refreshMirrorProperty(propertyEntity.getBelongMObject());

        return mPropertyEntity;
    }

    /**
     * 对所有非默认字段重新排序
     * @param objectEntity
     */
    public void reSortAllProperties(MObjectEntity objectEntity){
        int sort = 2;
        List<MPropertyEntity> list = findAllCustomProperty(objectEntity);
        for(MPropertyEntity property:list){
            property.setSort(sort);
            sort = sort + 2;
        }
        mPropertyDao.save(list);
    }
    /**
     * 根据元数据ID查询元数据
     * @param mPropertyId
     * @return
     */
    public MPropertyEntity findMPropertyById(String mPropertyId){
        return mPropertyDao.findOne(mPropertyId);
    }

    /**
     * 根据上级镜像和当前元数据，生成元数据的路径
     * @param parentProperty
     * @param mPropertyEntity
     * @return
     */
    public String getPropertyPath(MirrorPropertyEntity parentProperty, MPropertyEntity mPropertyEntity){
        StringBuilder sb = new StringBuilder();
        if(parentProperty!=null){
            sb.append(parentProperty.getPropertyPath()).append(MetaDataUtils.SQL_UNDERLINE).append(mPropertyEntity.getFieldName());
        }else{
            sb.append(mPropertyEntity.getFieldName());
        }
        return sb.toString();
    }
    /**
     * 创建元数据镜像
     * @param objectEntity
     */
    @Transactional
    public void createMirrorProperties(MObjectEntity objectEntity){
        refreshMirrorProperty(objectEntity);
    }

    private void refreshMirrorProperty(MObjectEntity objectEntity){
        objectEntity = mObjectDao.findOne(objectEntity.getId());
        //检测默认的镜像是否存在,不存在则创建
        createDefaultMirrorProperties(objectEntity);
        //检测被删除的镜像
        dropMirrorProperties(objectEntity);
        //更新镜像
        List<MPropertyEntity> mPropertyEntityList = mPropertyDao.findByBelongMObjectAndIsEnable(objectEntity, true);
        Collections.sort(mPropertyEntityList, new MPropertyComparator());
        AtomicInteger sort = new AtomicInteger(0);
        for(MPropertyEntity mPropertyEntity:mPropertyEntityList){
            refreshMirrorProperty(objectEntity, null, mPropertyEntity, sort);
        }

        createDefaultTemplate(objectEntity);

        List<MTemplateEntity> templateList = objectEntity.getTemplates();
        for (MTemplateEntity templateEntity:templateList){
            if(!templateEntity.getIsSystem()){
                //刷新自定义模板
                mTemplateService.saveTemplate(templateEntity);
            }
        }
    }

    /**
     * 创建元数据对象
     * 默认的编辑模板，查看模板
     * @param objectEntity
     */
    public void createDefaultTemplate(MObjectEntity objectEntity){
        //新增数据模板
        createTemplate(objectEntity, MTemplateTypeEnum.DataEntry);
        //查看数据模板
        createTemplate(objectEntity, MTemplateTypeEnum.DataView);
        //list数据模板
        createTemplate(objectEntity, MTemplateTypeEnum.DataList);

        mObjectDao.save(objectEntity);
    }

    public MTemplateEntity createTemplate(MObjectEntity objectEntity, MTemplateTypeEnum templateTypeEnum){
        String sourceTemplate = templeSourceCreator.create(objectEntity,templateTypeEnum);
        String templeStr = defaultTemplateCreator.createTemple(objectEntity, sourceTemplate,templateTypeEnum);

        String templeName = null;
        if(templateTypeEnum == MTemplateTypeEnum.DataEntry){
            templeName = "新建"+objectEntity.getName();
        }else if(templateTypeEnum == MTemplateTypeEnum.DataView){
            templeName = "查看"+objectEntity.getName();
        }else{
            templeName = objectEntity.getName()+"列表";
        }
        MTemplateEntity templateEntity = null;
        if(objectEntity.getTemplates()!=null && objectEntity.getTemplates().size()>0){
           for(MTemplateEntity template:objectEntity.getTemplates()){
               if(template.getTemplateType()==templateTypeEnum && template.getIsSystem()){
                   templateEntity = template;
                   break;
               }
           }
        }
        if( templateEntity ==null){
            templateEntity = new MTemplateEntity(objectEntity,templeName,templeStr);
        }
        templateEntity.setTemplate(templeStr);
        templateEntity.setTemplateSource(sourceTemplate);
        templateEntity.setTemplateType(templateTypeEnum);
        templateEntity.setIsSystem(true);
        if(templateEntity.getId()==null){
            objectEntity.addTemplate(templateEntity);
        }

        return templateEntity;
    }

    /**
     * 创建元数据镜像
     * @param mPropertyEntity
     */
    public void refreshMirrorProperty(MObjectEntity rootObjectEntity, MirrorPropertyEntity parentProperty, MPropertyEntity mPropertyEntity, AtomicInteger sort){
        //判断树形结构，避免死循环
        if(parentProperty!=null && parentProperty.getBelongMObject().getId().equals(mPropertyEntity.getBelongMObject().getId())){
            MirrorPropertyEntity parentParentProperty = parentProperty.getParentProperty();
            if(parentParentProperty!=null && parentParentProperty.getBelongMObject().getId().equals(parentProperty.getBelongMObject().getId())){
               //当有三层对象相同的时候，认为是循环
               return;
            }
        }
        //普通元数据
        String propertyPath = getPropertyPath(parentProperty,mPropertyEntity);
        List<MirrorPropertyEntity> mirrorPropertyEntities = mirrorPropertyDao.findByRootMObjectAndPropertyPath(rootObjectEntity,propertyPath);

        MirrorPropertyEntity mirrorPropertyEntity = new MirrorPropertyEntity();
        if(mirrorPropertyEntities!=null&&mirrorPropertyEntities.size()>0){
            mirrorPropertyEntity = mirrorPropertyEntities.get(0);
        }
        mirrorPropertyEntity.setBelongMObject(mPropertyEntity.getBelongMObject());
        mirrorPropertyEntity.setDeep(0);
        if(parentProperty!=null){
            mirrorPropertyEntity.setDeep(parentProperty.getDeep()+1);
        }
        mirrorPropertyEntity.setMetaProperty(mPropertyEntity);
        mirrorPropertyEntity.setParentProperty(parentProperty);
        mirrorPropertyEntity.setPropertyPath(propertyPath);
        mirrorPropertyEntity.setRootMObject(rootObjectEntity);
        if(mPropertyEntity.getMetaObject()!=null){
            String propertyObjectId = mPropertyEntity.getMetaObject().getId();
            mirrorPropertyEntity.setPropertyObjectId(propertyObjectId);
        }
        //数据冗余部分
        mirrorPropertyEntity.setName(mPropertyEntity.getName());
        mirrorPropertyEntity.setSecondName(mPropertyEntity.getSecondName());
        mirrorPropertyEntity.setIsNullable(mPropertyEntity.getIsNullable());
        mirrorPropertyEntity.setControllerType(getControllerTypeByProperty(mPropertyEntity));
        mirrorPropertyEntity.setEnumName(mPropertyEntity.getEnumName());
        mirrorPropertyEntity.setFieldName(mPropertyEntity.getFieldName());
        mirrorPropertyEntity.setIsSearchCondition(mPropertyEntity.getIsSearchCondition());
        mirrorPropertyEntity.setIsReadOnly(mPropertyEntity.getIsReadOnly());
        mirrorPropertyEntity.setInVisibleType(mPropertyEntity.getInVisibleType());
        mirrorPropertyEntity.setFormula(mPropertyEntity.getFormula());
        mirrorPropertyEntity.setSort(sort.incrementAndGet());
        mirrorPropertyDao.save(mirrorPropertyEntity);
        //元数据对象类型或结构化字段，刷新关联属性
        if(MetaDataUtils.isObjectField(mPropertyEntity)){
            MObjectEntity objectEntity = mPropertyEntity.getMetaObject();
            List<MPropertyEntity> mPropertyEntityList = mPropertyDao.findByBelongMObjectAndIsEnable(objectEntity, true);
            if(mPropertyEntityList.size()>0){
                //关联属性生成镜像
                for(MPropertyEntity property:mPropertyEntityList){
                    refreshMirrorProperty(rootObjectEntity,mirrorPropertyEntity,property,sort);
                }
            }
        }
    }

    /**
     * 根据元数据，获取默认的显示类型
     * @param mPropertyEntity
     * @return
     */
    private MControllerTypeEnum getControllerTypeByProperty(MPropertyEntity mPropertyEntity) {

        if(mPropertyEntity.getTypeEnum()==MTypeEnum.mEnum){
            return MControllerTypeEnum.mEnum;
        }else if(mPropertyEntity.getTypeEnum()==MTypeEnum.subType){
            return MControllerTypeEnum.subType;
        }else if(MetaDataUtils.isObjectField(mPropertyEntity)){
            return MControllerTypeEnum.object;
        }else if(mPropertyEntity.getTypeEnum()==MTypeEnum.headPortrait){
            return MControllerTypeEnum.headPortrait;
        }else if(mPropertyEntity.getTypeEnum()==MTypeEnum.image){
            return MControllerTypeEnum.image;
        }else if(mPropertyEntity.getTypeEnum()==MTypeEnum.file){
            return MControllerTypeEnum.file;
        }else {
                switch (mPropertyEntity.getFieldType()){
                    case BOOL:
                        return MControllerTypeEnum.bool;
                    case INTEGER:
                    case LONG:
                    case DOUBLE:
                    case DECIMAL:
                        return MControllerTypeEnum.number;
                    case DATE:
                    case DATETIME:
                        return MControllerTypeEnum.date;
                }
        }
        return MControllerTypeEnum.text;
    }

    /**
     * 删除元数据镜像
     * 只删除不启用的元数据
     * @param objectEntity
     */
    public void dropMirrorProperties(MObjectEntity objectEntity){
        //检测被删除的镜像
        List<MirrorPropertyEntity> mirrorPropertyEntities =  mirrorPropertyCustomDao.findByRootMObject(objectEntity);
        for(MirrorPropertyEntity mirrorPropertyEntity:mirrorPropertyEntities){
            //根据字段名称判断元数据是否存在
             List<MPropertyEntity> list = mPropertyDao.findByBelongMObjectAndFieldNameAndIsEnable(mirrorPropertyEntity.getMetaProperty().getBelongMObject(),
                    mirrorPropertyEntity.getMetaProperty().getFieldName(), true);
            if(list==null || list.size()==0){
                //不存在，则删除镜像
                dropMirrorProperties(mirrorPropertyEntity);
            }
        }
    }

    /**
     * 创建或修改默认的元数据
     * @param objectEntity
     */
    public void createDefaultMirrorProperties(MObjectEntity objectEntity){
/*        List<MPropertyEntity> list = mPropertyDao.findByBelongMObjectAndFieldName(objectEntity, MetaDataUtils.META_FIELD_STATUS);
        if(list.size()<=0){
            MPropertyEntity  property = new MPropertyEntity("状态", MetaDataUtils.META_FIELD_STATUS,MFieldTypeEnum.INTEGER, 4);
            property.setBelongMObject(objectEntity);
            mPropertyDao.save(property);
            metaDataCoreService._createFields(property);
        }*/
    }
    /**
     * 删除元数据
     * @param mirrorPropertyEntity
     */
    private void dropMirrorProperties(MirrorPropertyEntity mirrorPropertyEntity){
        //删除的是对象类型，则删除对象下面的属性
        if(mirrorPropertyEntity.getControllerType()==MControllerTypeEnum.object){
            List<MirrorPropertyEntity> mirrorPropertyEntities =  mirrorPropertyCustomDao.findByParentProperty(mirrorPropertyEntity);
            if(mirrorPropertyEntities.size()>0){
                for(MirrorPropertyEntity propertyEntity:mirrorPropertyEntities){
                    dropMirrorProperties(propertyEntity);
                }
            }
        }

        mirrorPropertyDao.delete(mirrorPropertyEntity);
    }
    /**
     * 根据元数据对象，重新创建表结构
     * @param objectEntity
     * @return
     */
    @Transactional
    public boolean refreshTables(MObjectEntity objectEntity){
        if(MetaDataUtils.isSelf(objectEntity)){
            //不刷新元数据的元数据
            return false;
        }
        metaDataCoreService._createTabel(objectEntity);
        //创建元数据对象时，创建表并且默认创建ID属性
        List<MPropertyEntity> propertyEntityList = mPropertyDao.findByBelongMObjectAndIsEnable(objectEntity, true);
        for(MPropertyEntity propertyEntity:propertyEntityList){
            if(!propertyEntity.getIsPrimaryKey()) {
                metaDataCoreService._createFields(propertyEntity);
            }
        }
        return true;
    }

    /**
     * 根据实体类创建元数据
     * @param entityClazz
     */
    @Transactional
    public boolean createMObjectFromEntityClass(Class entityClazz){
        Session factorySession = (org.hibernate.Session) entityManager.getDelegate();
        SessionFactoryImplementor sessionFactory = (SessionFactoryImplementor) factorySession.getSessionFactory();

        ClassMetadata entityMetaInfo = sessionFactory.getClassMetadata(entityClazz);
        String[] propertyNames = entityMetaInfo.getPropertyNames();
        for (int i = 0, n = propertyNames.length; i < n; i++)
        {
            String propertyName = propertyNames[i];
            Type propType = entityMetaInfo.getPropertyType(propertyName);//propType.sqlTypes(idPropType);;
            System.out.println(propertyName + "字段类型为" + propType.getReturnedClass().getName());
        }
        if (entityMetaInfo.hasIdentifierProperty()){
            String idPropName = entityMetaInfo.getIdentifierPropertyName();
            Type idPropType = entityMetaInfo.getIdentifierType();
            System.out.println("主键字段为:" + idPropName + "类型为"
                    + idPropType.getReturnedClass().getName());
        } else {
            System.out.println("此实体无主键");
        }


        return true;
    }

    private void save(MObjectEntity mObjectEntity, MPropertyEntity mPropertyEntity){
        mPropertyEntity.setBelongMObject(mObjectEntity);
        if(mPropertyEntity.getSort()==0 && !MetaDataUtils.isSystemFields(mPropertyEntity.getFieldName())){
            mPropertyEntity.setSort(1);
        }
        mPropertyDao.save(mPropertyEntity);
    }

    /**
     * 生成元数据的元数据
     */
    @Transactional
    public boolean createSelf(){
        //如果存在，则删除
        removeOldSelf();
        //元数据对象的元数据对象
        MObjectEntity objectEntity = new MObjectEntity();
        objectEntity.setName("对象");
        objectEntity.setTableName(MetaDataUtils.DEFAULT_META_TABLE_NAME);
        mObjectDao.save(objectEntity);

        //创建元数据对象的属性
        createNormalColumns(objectEntity);
        createObjectSelfColumns(objectEntity);

        //元数据属性对象
        MObjectEntity metaObjectEntity = new MObjectEntity();
        metaObjectEntity.setName("元数据");
        metaObjectEntity.setTableName(MetaDataUtils.DEFAULT_META_PROPERTY_TABLE_NAME);
        mObjectDao.save(metaObjectEntity);
        //创建属性
        createNormalColumns(metaObjectEntity);
        createPropertySelfColumns(metaObjectEntity);
        //刷新
        refreshMirrorProperty(objectEntity);
        reSortAllProperties(objectEntity);
        refreshMirrorProperty(metaObjectEntity);
        reSortAllProperties(metaObjectEntity);
        return true;
    }

    private void removeOldSelf(){
        List<MObjectEntity> list = mObjectDao.findByTableNameAndIsEnable(MetaDataUtils.DEFAULT_META_TABLE_NAME, true);
        if (list.size()>0){
            MObjectEntity objectEntity = list.get(0);
            List<MirrorPropertyEntity> mirrorPropertyEntities =  mirrorPropertyCustomDao.findByRootMObject(objectEntity);
            for(MirrorPropertyEntity propertyEntity:mirrorPropertyEntities){
                mirrorPropertyDao.delete(propertyEntity);
            }
            List<MPropertyEntity>  propertyList = mPropertyDao.findByBelongMObjectAndIsEnable(objectEntity, true);
            for(MPropertyEntity propertyEntity:propertyList){
                mPropertyDao.delete(propertyEntity);
            }
            mObjectDao.delete(objectEntity);
        }
        List<MObjectEntity> list2 = mObjectDao.findByTableNameAndIsEnable(MetaDataUtils.DEFAULT_META_PROPERTY_TABLE_NAME,true);
        if (list2.size()>0){
            MObjectEntity objectEntity = list2.get(0);
            List<MirrorPropertyEntity> mirrorPropertyEntities =  mirrorPropertyCustomDao.findByRootMObject(objectEntity);
            for(MirrorPropertyEntity propertyEntity:mirrorPropertyEntities){
                mirrorPropertyDao.delete(propertyEntity);
            }
            List<MPropertyEntity>  propertyList = mPropertyDao.findByBelongMObjectAndIsEnable(objectEntity, true);
            for(MPropertyEntity propertyEntity:propertyList){
                mPropertyDao.delete(propertyEntity);
            }
            mObjectDao.delete(objectEntity);
        }
    }

    private void createObjectSelfColumns(MObjectEntity mObjectEntity){
        MPropertyEntity secondName = new MPropertyEntity("别名", "second_name",MFieldTypeEnum.VARCHAR,255);
        MPropertyEntity moduleName = new MPropertyEntity("模块名称", "module_name",MFieldTypeEnum.VARCHAR,255);
        MPropertyEntity tableName = new MPropertyEntity("表名称", "table_name",MFieldTypeEnum.VARCHAR,255);
        MPropertyEntity isEnable = new MPropertyEntity("是否激活", "is_enable",MFieldTypeEnum.BOOL,2);
        MPropertyEntity remark = new MPropertyEntity("备注", "remark",MFieldTypeEnum.VARCHAR,255);
        MPropertyEntity isManage = new MPropertyEntity("启用维护界面", "is_manage",MFieldTypeEnum.BOOL,2);
        MPropertyEntity isTree = new MPropertyEntity("是否树形", "is_tree",MFieldTypeEnum.BOOL,2);
        MPropertyEntity isHistory = new MPropertyEntity("是否历史表", "is_history",MFieldTypeEnum.BOOL,2);
        save(mObjectEntity,secondName);
        save(mObjectEntity,moduleName);
        save(mObjectEntity,tableName);
        save(mObjectEntity,isEnable);
        save(mObjectEntity,remark);
        save(mObjectEntity,isManage);
        save(mObjectEntity,isTree);
        save(mObjectEntity,isHistory);
    }

    private void createPropertySelfColumns(MObjectEntity mObjectEntity){

        MPropertyEntity secondName = new MPropertyEntity("别名", "second_name",MFieldTypeEnum.VARCHAR,500);
        MPropertyEntity fieldName = new MPropertyEntity("字段名称", "field_name",MFieldTypeEnum.VARCHAR,255);
        MPropertyEntity fieldDefaultValue = new MPropertyEntity("字段默认值", "field_default_value",MFieldTypeEnum.VARCHAR,255);
        MPropertyEntity fieldType = new MPropertyEntity("字段类型", "field_type",MFieldTypeEnum.VARCHAR,255);
        MPropertyEntity fieldLength = new MPropertyEntity("字段长度", "field_length",MFieldTypeEnum.INTEGER,11);
        MPropertyEntity fieldPrecision = new MPropertyEntity("字段精度", "field_precision",MFieldTypeEnum.INTEGER,11);
        MPropertyEntity isPrimaryKey = new MPropertyEntity("是否主键", "is_primary_key",MFieldTypeEnum.BOOL,2);
        MPropertyEntity isNullable = new MPropertyEntity("是否可空", "is_nullable",MFieldTypeEnum.BOOL,2);
        MPropertyEntity isEnable = new MPropertyEntity("是否激活", "is_enable",MFieldTypeEnum.BOOL,2);
        MPropertyEntity isSearchCondition = new MPropertyEntity("是否检索条件", "is_search_condition",MFieldTypeEnum.BOOL,2);
        MPropertyEntity inVisibleType = new MPropertyEntity("隐藏类型", "in_visible_type",MFieldTypeEnum.VARCHAR,255);
        MPropertyEntity isReadOnly = new MPropertyEntity("是否只读", "is_read_only",MFieldTypeEnum.BOOL,2);
        MPropertyEntity belongMObject = new MPropertyEntity("所属对象", "belong_mobject_id",MFieldTypeEnum.VARCHAR,30);
        MPropertyEntity typeEnum = new MPropertyEntity("类型", "type_enum",MFieldTypeEnum.VARCHAR,255);
        MPropertyEntity metaObject = new MPropertyEntity("对象", "mobject_id",MFieldTypeEnum.VARCHAR,255);
        MPropertyEntity enumName = new MPropertyEntity("枚举名称", "enum_name",MFieldTypeEnum.VARCHAR,255);
        MPropertyEntity subMObject = new MPropertyEntity("关联属性", "mapped_by_property",MFieldTypeEnum.VARCHAR,30);
        MPropertyEntity sort = new MPropertyEntity("排序", "sort",MFieldTypeEnum.INTEGER,255);

        save(mObjectEntity,secondName);
        save(mObjectEntity,fieldName);
        save(mObjectEntity,fieldDefaultValue);
        save(mObjectEntity,fieldType);
        save(mObjectEntity,fieldLength);
        save(mObjectEntity,fieldPrecision);
        save(mObjectEntity,isPrimaryKey);
        save(mObjectEntity,isNullable);
        save(mObjectEntity,isEnable);
        save(mObjectEntity,isSearchCondition);
        save(mObjectEntity,inVisibleType);
        save(mObjectEntity,belongMObject);
        save(mObjectEntity,isReadOnly);
        save(mObjectEntity,typeEnum);
        save(mObjectEntity,isNullable);
        save(mObjectEntity,metaObject);
        save(mObjectEntity,enumName);
        save(mObjectEntity,subMObject);
        save(mObjectEntity,sort);
    }
}
