package com.mg.groovy.define.bean;


import com.mg.groovy.define.interfaces.GImport;

import java.util.ArrayList;
import java.util.List;

/** 
 * groovy 默认导入的类库 
 * 例如：默认导入 
 * 	java.math 
 * 	java.util
 * @author: liukefu
 * @date: 2015年2月4日 上午9:59:22  
 */
public class GImportDefault implements GImport {
	
	private List<GSentenceBase> imports = new ArrayList<>();
	
	public GImportDefault() {
		super();
		imports.add(new GSentenceSimple("import java.math.*;") );
		imports.add(new GSentenceSimple("import java.util.*;") );
	}

	public String toGroovy() {
		StringBuilder sb = new StringBuilder();
		
		for(GSentenceBase imp:imports){
			sb.append(imp.toGroovy());
		}
		
		return sb.toString();
	}


	@Override
	public void addImports(GSentenceBase lib) {
		imports.add(lib);
	}


	@Override
	public List<GSentenceBase> getImports() {
		
		return imports;
	}

}
