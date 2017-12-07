package com.mg.common.shiro.util;

import com.mg.common.entity.PermissionEntity;
import com.mg.common.entity.vo.PermissionActionEnum;
import com.mg.framework.entity.metadata.MObjectEntity;
import org.apache.shiro.authz.Permission;

import java.util.*;

public class WildcardPermissionEx implements Permission {

	protected static final String WILDCARD_TOKEN = "*";
	protected static final String DIVIDER_TOKEN = ",";

    /**
     * 操作名(表名)
     * {
     * 		hi_employee：{
     * 			update："",
     * 			view：""
     * 		}
     * }
     */
    private Map<String, Map<String, Object>> tableActionMap = new HashMap<>();
	/**
	 * 字段集权限（表名--字段名）
	 * hi_employee：{
	 * 	    employee_id:view:"",
     * 	    employee_id:update:"",
	 * 		employee_name：{
     * 			update："",
     * 			view：""
	 * 		}
	 * }
	 */
    private Map<String, Map<String, Map<String, Object>>> tablePropertyActionMap = new HashMap<>();
	/**
	 * 数据范围权限
	 * 	 	hi_employee：{
	 * 			update：["update,view","update"]
	 * 		}
	 */
	private Map<String, Set<String>> dataScopeMap  = new HashMap<>();
	/**
	 * 功能权限
	 */
	private Set<String> funcPermissions = new HashSet<>();

	@Override
	public boolean implies(Permission p) {

/*		if (p instanceof EmpScopePermission) {
			EmpScopePermission ep = (EmpScopePermission) p;
			String empId = ep.getEmpId();
			@SuppressWarnings("unchecked")
			Set<String> empScope = (Set<String>) TMSCurrentUser.getAttribute(TMSConstants.CURRENT_EMP_SCOPE);
			if (null == empScope || false == empScope.contains(empId)) {
				return false;
			}
			return true;
		}*/

		if (p instanceof StringPermission) {
			StringPermission sp = (StringPermission) p;

			if (sp.getPartSize() == 1) {
				Set<String> otherFirstPart = sp.getPart(1);
				if (funcPermissions.containsAll(otherFirstPart)) {
					return true;
				}
				if (funcPermissions.contains(WILDCARD_TOKEN)) {
					return true;
				}
				return false;
			}

			if (sp.getPartSize() == 2) {

				return tablePermissionImplies(sp);
			}
            if (sp.getPartSize() == 4) {

                return propertyPermissionImplies(sp);
            }
		}

		return false;
	}

    public boolean tablePermissionImplies(StringPermission sp){

        Set<String> part1 = sp.getPart(1);
        Set<String> part2 = sp.getPart(2);
        for (String part:part1){
            Map<String, Object> actionMap = tableActionMap.get(part);
            if(actionMap==null){
                return false;
            }else{
                if(!containAll(actionMap,part2)){
                    return false;
                }
            }
        }

        return true;
    }

    public boolean propertyPermissionImplies(StringPermission sp){
        Set<String> part1 = sp.getPart(1);
        Set<String> part2 = sp.getPart(2);
        Set<String> part3 = sp.getPart(3);
        Set<String> part4 = sp.getPart(4);

        for (String part:part1){
            //表对象权限
            Map<String, Map<String, Object>> propertyMap = tablePropertyActionMap.get(part);
            if(propertyMap==null){
                return false;
            }else{
                for (String p2:part2){
                    //字段权限
                    Map<String, Object> map = propertyMap.get(p2);
                    if(map==null){
                        return false;
                    }else{
                        //字段操作权限
                        if(map.get(PermissionActionEnum.action_update.name())!=null){
                            //先判断是否有修改权限
                        }else if(!containAll(map,part3)){
                            return  false;
                        }
                    }
                }
            }
        }

        //数据范围权限
        for (String part:part1){
            Set<String> idList = dataScopeMap.get(part);
            if(!idList.containsAll(part4)){
                return false;
            }
        }

        return true;
    }

    public boolean containAll(Map<String, ?> map,Set<String> parts){
        for(String part:parts){
            if(map.get(part)==null){
                return false;
            }
        }

        return true;
    }

	public void addFunctionPermission(String p) {
		if (p != null) {
			// 一律不区分大小写
			funcPermissions.add(p.toLowerCase());
		}
	}

	public void addFunctionPermissions(Collection<String> ps) {
		if (ps != null) {
			// 一律不区分大小写
			for (String p : ps) {
				addFunctionPermission(p);
			}
		}
	}

	public void addPropertyPermission(PermissionEntity permissionEntity, Collection<String> ids) {
		//所属对象
		MObjectEntity belongMObject = permissionEntity.getBelongMObject();
		String property = permissionEntity.getProperty().getPropertyPath();
		String actions = permissionEntity.getAction().name();
		if (property != null) {
			property = property.toLowerCase();
		}
		if (actions != null) {
			actions = actions.toLowerCase();
		}
        //数据表对象操作权限 hi_employee:update
        String objectName = belongMObject.getTableName();
        objectName = objectName.toLowerCase();
        Map<String, Object> tableAction  = tableActionMap.get(objectName);
        if (tableActionMap.get(objectName) == null) {
            tableAction = new HashMap<>();
            tableActionMap.put(objectName, tableAction);
        }
        if (tableAction.get(actions) == null) {
            tableAction.put(actions, "");
        }
		//字段的操作权限 hi_employee:emp_name:update
        Map<String, Map<String, Object>> propertyActionMap = tablePropertyActionMap.get(objectName);
        if(propertyActionMap==null){
            propertyActionMap = new HashMap<>();
            tablePropertyActionMap.put(objectName,propertyActionMap);
        }
		Map<String, Object> propertyAction = propertyActionMap.get(property);
		if (propertyAction == null) {
            propertyAction = new HashMap<>();
            propertyActionMap.put(property, propertyAction);
		}
		if (propertyAction.get(actions) == null) {
            propertyAction.put(actions, "");
		}

		//数据范围
		Set<String> dataScopeIds = dataScopeMap.get(objectName);
		if (dataScopeIds == null) {
			dataScopeIds = new HashSet<>();
			dataScopeMap.put(objectName, dataScopeIds);
		}
        if(ids!=null && ids.size()>0) {
            dataScopeIds.addAll(ids);
        }
	}


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        WildcardPermissionEx that = (WildcardPermissionEx) o;

        if (tableActionMap != null ? !tableActionMap.equals(that.tableActionMap) : that.tableActionMap != null)
            return false;
        if (tablePropertyActionMap != null ? !tablePropertyActionMap.equals(that.tablePropertyActionMap) : that.tablePropertyActionMap != null)
            return false;
        if (dataScopeMap != null ? !dataScopeMap.equals(that.dataScopeMap) : that.dataScopeMap != null) return false;
        return !(funcPermissions != null ? !funcPermissions.equals(that.funcPermissions) : that.funcPermissions != null);

    }

    @Override
    public int hashCode() {
        int result = tableActionMap != null ? tableActionMap.hashCode() : 0;
        result = 31 * result + (tablePropertyActionMap != null ? tablePropertyActionMap.hashCode() : 0);
        result = 31 * result + (dataScopeMap != null ? dataScopeMap.hashCode() : 0);
        result = 31 * result + (funcPermissions != null ? funcPermissions.hashCode() : 0);
        return result;
    }
}
