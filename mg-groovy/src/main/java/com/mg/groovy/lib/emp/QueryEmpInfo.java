package com.mg.groovy.lib.emp;

import com.mg.groovy.define.bean.GMethod;
import com.mg.groovy.define.bean.GSentenceBase;
import com.mg.groovy.define.bean.GSentenceSimple;
import com.mg.groovy.define.bean.GVariable;
import com.mg.groovy.define.interfaces.GImport;

import java.util.List;

public class QueryEmpInfo extends GMethod implements GImport{

	public static final String name = "queryEmpInfo";
	
	public QueryEmpInfo(){
		super.setgMethodName(name);
		super.addGParam(new GVariable("empId"));
		super.addGParam(new GVariable("entitiPath"));
		
		StringBuilder sb = new StringBuilder();
		sb.append(" def queryEmpInfo (empId,entitiPath) {");
		sb.append(" com.qihangedu.tms.hr.metadata.EmployeeEntity emp = (com.qihangedu.tms.hr.metadata.EmployeeEntity)empEntity;");
		sb.append(" com.qihangedu.tms.hr.repository.EmployeeRepository empRep = com.qihangedu.tms.common.TMSApplicationContext.getBean(com.qihangedu.tms.hr.repository.EmployeeRepository);");
		sb.append(" empRep.updateLr(empId, entitiPath );");
		sb.append("");
		sb.append("");
		sb.append("");
		sb.append(" } ");
		super.addGSentence(new GSentenceSimple(sb.toString()));	
	}
	
	@Override
	public String toGroovy() {

		return super.toGroovy();
	}

	@Override
	public List<GSentenceBase> getImports() {
		
		return null;
	}

	@Override
	public void addImports(GSentenceBase lib) {
		
	}

}
