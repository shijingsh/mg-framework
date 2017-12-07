package com.mg.report.service;

import com.mg.framework.entity.metadata.*;
import com.mysema.query.jpa.impl.JPAQuery;
import com.mg.common.metadata.service.MetaDataExpressService;
import com.mg.common.metadata.service.MetaDataQueryService;
import com.mg.common.metadata.util.MetaDataUtils;
import com.mg.common.metadata.util.MirrorPropertyComparator;
import com.mg.common.utils.LazyLoadUtil;
import com.mg.framework.utils.UserHolder;
import com.mg.report.dao.ReportDao;
import com.mg.report.entity.*;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by liukefu on 2015/10/24.
 */
@Service
public class ReportServiceImpl implements ReportService {

    @PersistenceContext
    private EntityManager entityManager;
    @Autowired
    ReportDao reportDao;
    @Autowired
    MetaDataQueryService metaDataQueryService;
    @Autowired
    MetaDataExpressService metaDataExpressService;

    /**
     * 保存报表
     *
     * @param reportEntity
     */
    @Transactional
    public void saveReport(ReportEntity reportEntity) {

        if (StringUtils.isNotBlank(reportEntity.getReportId())) {
            ReportEntity reportDb = reportDao.findOne(reportEntity.getReportId());

            for (ReportDimenEntity reportDimenEntity : reportDb.getColumnDimens()) {
                removeDimens(reportDimenEntity);
            }
            reportDb.setColumnDimens(null);
            for (ReportViewerEntity viewerEntity : reportDb.getViewerList()) {
                entityManager.remove(viewerEntity);
            }
            reportDb.setViewerList(new ArrayList<ReportViewerEntity>());
            for (ReportColumnEntity columnEntity : reportDb.getColumns()) {
                entityManager.remove(columnEntity);
            }
            reportDb.setColumns(new ArrayList<ReportColumnEntity>());
            //删除旧数据
            reportDao.delete(reportDb);
        }
        for (ReportViewerEntity viewerEntity : reportEntity.getViewerList()) {
            viewerEntity.setBelongReport(reportEntity);
        }
        for (ReportColumnEntity columnEntity : reportEntity.getColumns()) {
            columnEntity.setBelongReport(reportEntity);
        }
        for (ReportDimenEntity reportDimenEntity : reportEntity.getColumnDimens()) {
            setReportDimen(reportEntity, reportDimenEntity);
        }
        if (StringUtils.isBlank(reportEntity.getUserId())) {
            reportEntity.setUserId(UserHolder.getLoginUserId());
        }
        if(reportEntity.getExpressGroup()!=null){
            MExpressGroupEntity expressGroupEntity = metaDataExpressService.initExpressBeforeSave(reportEntity.getExpressGroup());
            MObjectEntity mObjectEntity = metaDataQueryService.findMObjectById(reportEntity.getObjectId());
            expressGroupEntity.setMetaObject(mObjectEntity);
            reportEntity.setExpressGroup(expressGroupEntity);
        }

        reportDao.save(reportEntity);
    }

    private void removeDimens(ReportDimenEntity reportDimenEntity){

        List<ReportDimenItemEntity> itemList = reportDimenEntity.getItemList();
        if(itemList!=null){
            for(ReportDimenItemEntity item:itemList){
                removeDimensItem(item);
            }
            reportDimenEntity.setItemList(null);
        }
        entityManager.remove(reportDimenEntity);
    }
    private void removeDimensItem(ReportDimenItemEntity dimenItemEntity){

        List<ReportDimenEntity> dimenList = dimenItemEntity.getDimenList();
        if(dimenList!=null){
            for(ReportDimenEntity dimenEntity:dimenList){
                removeDimens(dimenEntity);
            }
            dimenItemEntity.setDimenList(null);
        }
        entityManager.remove(dimenItemEntity);
    }

    private void setReportDimen(ReportEntity reportEntity, ReportDimenEntity reportDimenEntity) {
        if(reportDimenEntity.getBelongItem()==null) {
            reportDimenEntity.setBelongReport(reportEntity);
        }
        List<ReportDimenItemEntity> dimenItemEntities = reportDimenEntity.getItemList();
        for (ReportDimenItemEntity itemEntity : dimenItemEntities) {
            itemEntity.setBelongReport(reportEntity);
            itemEntity.setBelongDimen(reportDimenEntity);
            itemEntity.setDimenLev(reportDimenEntity.getDimenLev());
            if (itemEntity.getDimenList()!=null && itemEntity.getDimenList().size()>0){
                itemEntity.setIsLeaf(false);
                for (ReportDimenEntity dimenEntity : itemEntity.getDimenList()) {
                    dimenEntity.setBelongItem(itemEntity);
                    setReportDimen(reportEntity, dimenEntity);
                }
            }else{
                itemEntity.setIsLeaf(true);
            }

        }
    }

    /**
     * 查询报表
     *
     * @param reportId
     * @return
     */
    @Transactional
    public ReportEntity findReport(String reportId) {

        ReportEntity reportEntity = reportDao.findOne(reportId);
        LazyLoadUtil.fullLoad(reportEntity);

        List<ReportDimenEntity> dimenEntities = reportEntity.getColumnDimens();
        ReportDimenEntity rowDimen = null;
        List<ReportDimenEntity> columnDimens = new ArrayList<>();

        for (ReportDimenEntity reportDimenEntity : dimenEntities) {
            if (reportDimenEntity.getDimenLev() == 0) {
                rowDimen = reportDimenEntity;
            } else {
                columnDimens.add(reportDimenEntity);
            }
        }
        //转存到Transient 属性
        reportEntity.setDimenList(columnDimens);
        reportEntity.setRowDimen(rowDimen);

        return reportEntity;
    }

    /**
     * 查询我能查看的报表
     *
     * @return
     */
    public List<ReportEntity> findMyReports() {

        JPAQuery query = new JPAQuery(entityManager);
        QReportEntity report = new QReportEntity("mpro");

        String userId = UserHolder.getLoginUserId();
        List<ReportEntity> list = query.from(report)
/*                .where(
                        report.userId.eq(userId)
                                .or(report.viewerList.any().userId.eq(userId))
                )*/
                .list(report);

        return list;
    }

    /**
     * 根据对象id，查询对象下面的行维度列表
     *
     * @param objectId
     * @return
     */
    public List<MirrorPropertyEntity> findRowDimen(String objectId) {

        MObjectEntity metaObject = metaDataQueryService.findMObjectById(objectId);

        List<MirrorPropertyEntity> list = metaDataQueryService.findMPropertyByRootMObject(metaObject);
        List<MirrorPropertyEntity> rtList = new ArrayList<>();

        for (MirrorPropertyEntity mirrorPropertyEntity : list) {
            if (!MetaDataUtils.isSystemFields(mirrorPropertyEntity.getFieldName())
                    && mirrorPropertyEntity.getInVisibleType() != MInVisibleTypeEnum.invisibleAll) {
                if (MetaDataUtils.isObjectField(mirrorPropertyEntity)
                        || mirrorPropertyEntity.getControllerType() == MControllerTypeEnum.mEnum
                        ) {
                    rtList.add(mirrorPropertyEntity);
                }

            }
        }
        Collections.sort(rtList, new MirrorPropertyComparator());
        return rtList;
    }

    /**
     * 获取报表中，最后一层的小项列表
     * 维度树形中的所有叶子节点
     * @param reportEntity
     */
    public List<ReportDimenItemEntity> getReportLastItemList(ReportEntity reportEntity){
        List<ReportDimenItemEntity> list = new ArrayList<>();
        List<ReportDimenEntity> columnDimens  = reportEntity.getColumnDimens();
        for (ReportDimenEntity dimenEntity:columnDimens){
            getDimenItemList(dimenEntity,list);
        }

        return list;
    }

    public void getDimenItemList(ReportDimenEntity dimenEntity, List<ReportDimenItemEntity> list){

        List<ReportDimenItemEntity> itemList = dimenEntity.getItemList();
        if(itemList != null){
            for (ReportDimenItemEntity itemEntity:itemList){
                List<ReportDimenEntity> dimenList  = itemEntity.getDimenList();
                if(dimenList==null || dimenList.size()==0){
                    //找到了叶子节点维度
                    list.add(itemEntity);
                }else{
                    for (ReportDimenEntity dimen:dimenList){
                        getDimenItemList(dimen,list);
                    }
                }
            }
        }
    }

    /**
     * 查询报表下的所有小项
     * @param report
     * @return
     */
    public List<ReportDimenItemEntity> findDimenItemsByReports(ReportEntity report) {

        JPAQuery query = new JPAQuery(entityManager);
        QReportDimenItemEntity item = new QReportDimenItemEntity("mpro");

        String userId = UserHolder.getLoginUserId();
        List<ReportDimenItemEntity> list = query.from(item)
                .where(
                        item.belongReport.eq(report)
                ).orderBy(item.dimenLev.asc())
                .list(item);

        return list;
    }

    @Transactional
    public void delete(String id) {
        ReportEntity reportDb = reportDao.findOne(id);

        for (ReportDimenEntity reportDimenEntity : reportDb.getColumnDimens()) {
            removeDimens(reportDimenEntity);
        }
        reportDb.setColumnDimens(null);
        for (ReportViewerEntity viewerEntity : reportDb.getViewerList()) {
            entityManager.remove(viewerEntity);
        }
        reportDb.setViewerList(new ArrayList<ReportViewerEntity>());
        for (ReportColumnEntity columnEntity : reportDb.getColumns()) {
            entityManager.remove(columnEntity);
        }
        reportDb.setColumns(new ArrayList<ReportColumnEntity>());
        //删除旧数据
        reportDao.delete(reportDb);
    }
}
