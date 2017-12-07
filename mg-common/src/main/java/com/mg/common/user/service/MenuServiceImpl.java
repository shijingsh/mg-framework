package com.mg.common.user.service;

import com.mg.common.entity.*;
import com.mg.common.entity.vo.MenuTypeEnum;
import com.mg.framework.utils.StatusEnum;
import com.mysema.query.jpa.impl.JPAQuery;
import com.mysema.query.types.expr.BooleanExpression;
import com.mg.common.entity.vo.MenuDeepComparator;
import com.mg.common.user.dao.MenuDao;
import com.mg.common.user.dao.RoleDao;
import com.mg.framework.utils.UserHolder;
import org.hibernate.Hibernate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.util.*;

/**
 * 菜单服务类
 * Created by liukefu on 2016/3/14.
 */
@Service
public class MenuServiceImpl implements MenuService {

    @PersistenceContext
    private EntityManager entityManager;
    @Autowired
    private RoleDao roleDao;
    @Autowired
    private MenuDao menuDao;

    @Transactional(readOnly = true)
    public MenuEntity get(String menuId) {
        MenuEntity menuEntity = menuDao.findOne(menuId);
        Hibernate.initialize(menuEntity.getMenus());

        return menuEntity;
    }


    @Transactional(readOnly = true)
    public List<MenuEntity> findALL() {
        QMenuEntity entity = QMenuEntity.menuEntity;

        BooleanExpression ex = entity.status.eq(StatusEnum.STATUS_VALID);

        JPAQuery query = new JPAQuery(entityManager);
        List<MenuEntity> list = query.from(entity)
                .where(
                        ex
                )
                .list(entity);

        return list;
    }

    @Transactional(readOnly = true)
    public List<MenuEntity> findMenuALL() {

        List<MenuEntity> list = findMenuByType(MenuTypeEnum.menu);
        Collections.sort(list, new MenuDeepComparator());

        settingAuthority(list, MenuTypeEnum.menu);
        return list;
    }

    @Transactional(readOnly = true)
    public List<MenuEntity> findResourceALL() {
        List<MenuEntity> list = findMenuByType(MenuTypeEnum.resource);
        Collections.sort(list, new MenuDeepComparator());

        settingAuthority(list, MenuTypeEnum.resource);

        return list;
    }

    @Transactional(readOnly = true)
    public void setMenuChecked(String roleId, List<MenuEntity> list) {

        Map<String, MenuEntity> map = new HashMap<>();
        RoleEntity roleEntity = roleDao.findOne(roleId);
        for (UrlResourcesEntity urlResourcesEntity : roleEntity.getUrlResources()) {
            map.put(urlResourcesEntity.getMenu().getId(), urlResourcesEntity.getMenu());
        }

        for (MenuEntity menuEntity : list) {
            if (map.get(menuEntity.getId()) != null) {
                menuEntity.setChecked(true);
            }
        }
    }

    private void settingAuthority(List<MenuEntity> list, MenuTypeEnum menuTypeEnum) {
        UserEntity user = UserHolder.getLoginUser();
        List<RoleEntity> roleEntities = user.getRoles();

        List<MenuEntity> menuEntityList = findUserMenuByType(menuTypeEnum, roleEntities);
        Map<String, MenuEntity> map = new HashMap<>();
        Iterator<MenuEntity> iterator = menuEntityList.iterator();
        while (iterator.hasNext()) {
            MenuEntity menuEntity = iterator.next();
            map.put(menuEntity.getId(), menuEntity);
        }
        //若自己的菜单列表中有这个菜单，则有权限
        for (MenuEntity menuEntity : list) {
            if (user.isAdmin() || map.get(menuEntity.getId()) != null) {
                menuEntity.setIsAuthority(true);
            }
        }
    }

    @Transactional(readOnly = true)
    public List<MenuEntity> findMyMenu() {
        UserEntity user = UserHolder.getLoginUser();
        List<RoleEntity> roleEntities = user.getRoles();
        List<MenuEntity> list;
        if (user.isAdmin()) {
            list = findMenuByType(MenuTypeEnum.menu);
        } else {
            list = findUserMenuByType(MenuTypeEnum.menu, roleEntities);
        }

        return buildMenuTree(list);
    }

    @Transactional(readOnly = true)
    public List<MenuEntity> findMyResource() {
        UserEntity user = UserHolder.getLoginUser();
        List<RoleEntity> roleEntities = user.getRoles();

        return findUserMenuByType(MenuTypeEnum.menu, roleEntities);
    }

    @Transactional(readOnly = true)
    public List<MenuEntity> findMyUrls() {
        UserEntity user = UserHolder.getLoginUser();

        List<MenuEntity> list ;
        if(user.isAdmin()){
            list = findMenuByType(null);
        }else{
            List<RoleEntity> roleEntities = user.getRoles();
            list = findUserMenuByType(null, roleEntities);
        }

        return list;
    }

    @Transactional(readOnly = true)
    public Map<String,Object> findAllUrls() {

        List<MenuEntity> list = findMenuByType(null);

        Map<String,Object> urlAllMap = new HashMap<>();
        for(MenuEntity menuEntity:list){
            urlAllMap.put(menuEntity.getPath(),"1");
        }

        return urlAllMap;
    }

    private List<MenuEntity> findUserMenuByType(MenuTypeEnum typeEnum, List<RoleEntity> roleList) {
        QUrlResourcesEntity entity = QUrlResourcesEntity.urlResourcesEntity;

        BooleanExpression ex = entity.belongRole.isNotNull();
        if(roleList != null) {
            ex = entity.belongRole.in(roleList);
        }
        if(typeEnum!=null){
            ex = ex.and(entity.menu.type.eq(typeEnum));
        }
        JPAQuery query = new JPAQuery(entityManager);
        List<UrlResourcesEntity> list = query.from(entity)
                .where(
                        ex
                )
                .list(entity);

        List<MenuEntity> menuList = new ArrayList<>();
        for (UrlResourcesEntity urlResourcesEntity : list) {
            if(typeEnum==null){
                menuList.add(urlResourcesEntity.getMenu());
            }else if (urlResourcesEntity.getMenu().getType() == typeEnum) {
                menuList.add(urlResourcesEntity.getMenu());
            }
        }

        return menuList;
    }

    private List<MenuEntity> findMenuByType(MenuTypeEnum typeEnum) {
        QMenuEntity entity = QMenuEntity.menuEntity;

        BooleanExpression ex = entity.status.eq(StatusEnum.STATUS_VALID);
        if(typeEnum!=null){
            ex = ex.and(entity.type.eq(typeEnum));
        }
        JPAQuery query = new JPAQuery(entityManager);
        List<MenuEntity> list = query.from(entity)
                .where(
                        ex
                )
                .list(entity);
        return list;
    }

    private List<MenuEntity> buildMenuTree(List<MenuEntity> list) {

        Map<String, MenuEntity> recordMap = new HashMap<>();
        for (MenuEntity menuEntity : list) {
            recordMap.put(menuEntity.getId(), menuEntity);
        }

        Map<String, MenuEntity> map = new HashMap<>();
        for (MenuEntity menuEntity : list) {
            if (menuEntity.getBelongMenu() == null) {
                //根菜单
                map.put(menuEntity.getId(), menuEntity);
            } else {
                MenuEntity parentMenu = menuEntity.getBelongMenu();
                MenuEntity parentMenuTree = recordMap.get(parentMenu.getId());
                if (parentMenuTree != null) {
                    parentMenuTree.getChildList().add(menuEntity);
                }
            }
        }

        Collection<MenuEntity> collection = map.values();
        List<MenuEntity> rtList = new ArrayList<>(collection);
        Collections.sort(rtList, new MenuDeepComparator());
        return rtList;
    }

    @Transactional
    public void save(MenuEntity menuEntity) {

        if (menuEntity.getBelongMenu() != null) {
            menuEntity.setDeep(menuEntity.getBelongMenu().getDeep() + 1);
            List<MenuEntity> childList = menuEntity.getBelongMenu().getMenus();
            if (menuEntity.getSort() == 0) {
                if (childList.size() > 0) {
                    Integer sort = childList.get(childList.size() - 1).getSort();
                    if (sort == null) {
                        sort = 0;
                    }
                    menuEntity.setSort(sort + 2);
                } else {
                    menuEntity.setSort(2);
                }
            }
        }

        menuDao.saveAndFlush(menuEntity);

        if (menuEntity.getBelongMenu() != null) {
            List<MenuEntity> childList = menuEntity.getBelongMenu().getMenus();
            Collections.sort(childList, new MenuDeepComparator());
            Integer sort = 2;
            for (MenuEntity menu : childList) {
                menu.setSort(sort);
                sort = sort + 2;
                menuDao.save(menu);
            }
        }
    }


    public List<MenuEntity> refreshResource(HttpServletRequest req) {

        List<String> excludeList = new ArrayList<>();
        excludeList.add("WEB-INF");
        excludeList.add("mobileweb");
        excludeList.add("login");
        excludeList.add("jsp");
        excludeList.add("demo");
        excludeList.add("test");
        excludeList.add("metadata");
        excludeList.add("instance");
        excludeList.add("help");
        String rootPath = req.getSession().getServletContext().getRealPath("/");
        File[] files = new File(rootPath).listFiles();
        for(File file : files){

            if(file.isDirectory() && !isExcludeDirectory(excludeList,file)){
                String directoryPath = "/" + file.getName();

                MenuEntity parentMenu = createResource(null,"",file);

                File[] fileDeep1 =  file.listFiles();
                for(File d1 : fileDeep1){
                    if(d1.isDirectory()){
                        if(isExcludeDirectory(excludeList,d1)){
                            continue;
                        }
                        File[] fileDeep2 =  d1.listFiles();
                        for(File d2 : fileDeep2){
                            if(d2.getPath().endsWith(".jsp")){
                                String jspPath = directoryPath +  "/" + d1.getName();
                                createResource(parentMenu,jspPath,d2);
                            }
                        }
                    }else{
                        if(d1.getPath().endsWith(".jsp")) {
                            createResource(parentMenu,directoryPath,d1);
                        }
                    }
                }
            }
        }

        return findResourceALL();
    }

    private MenuEntity createResource(MenuEntity parentMenu,String rootPath,File file){
        String jspPath = rootPath +  "/" + file.getName();
        List<MenuEntity> list  = menuDao.findByPath(jspPath);
        MenuEntity  menuEntity;
        if(list.isEmpty()){
            menuEntity = new MenuEntity();
            menuEntity.setPath(jspPath);
            menuEntity.setType(MenuTypeEnum.resource);
            menuEntity.setBelongMenu(parentMenu);

            menuDao.save(menuEntity);
        }else{
            menuEntity =list.get(0);
        }

        return menuEntity;
    }

    private Boolean isExcludeDirectory(List<String> excludeList,File file){

        if(file!=null){
            if(excludeList.contains(file.getName())){
                return true;
            }
        }
        return false;
    }

}
