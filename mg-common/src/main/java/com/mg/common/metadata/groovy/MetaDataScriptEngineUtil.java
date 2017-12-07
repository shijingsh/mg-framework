package com.mg.common.metadata.groovy;

import com.mg.groovy.lib.GroovyFun;
import com.mg.groovy.util.ScriptEngineUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.script.ScriptException;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 薪酬模块，执行groovy脚本的统一入口
 * 包含了薪酬函数
 */
public class MetaDataScriptEngineUtil {

    private static Logger logger = LoggerFactory.getLogger(MetaDataScriptEngineUtil.class);

    static {
        //初始化薪酬函数
        GroovyFun.initClassFunctions(GroovyMetaDataUtils.class);
    }

    public static void init(){
        logger.debug("MetaDataScriptEngineUtil init");
    }
    /**
     * 执行脚本并返回运算结果
     *
     * @param script 脚本代码
     * @param engineParam  运行脚本所需参数
     * @return
     * @throws ScriptException
     */
    public static Object execGroovyScript(String script, Map<String, Object> engineParam) throws ScriptException {
        script = parseScript(script, "获取元数据\\[([^\\]]+)\\]", "获取元数据(%s,param)");
        script = parseScript(script, "更新元数据\\[([^\\]]+)\\]", "更新元数据(%s,param)");
        script = parseScript(script, "生成历史记录\\[([^\\]]+)\\]", "生成历史记录(%s,param)");
        if(engineParam.get("param")==null){
            Map<String ,Object> execParam = new HashMap<>();
            execParam.put("param", engineParam);
        }
        return ScriptEngineUtil.execGroovyScript(script, engineParam);
    }
    protected static String parseScript(String script, String pattern, String replaceString) {
        StringBuffer result = new StringBuffer();
        Pattern patternEmployee = Pattern.compile(pattern);
        Matcher matcher = patternEmployee.matcher(script);
        while (matcher.find()) {

            String replacement = String.format(replaceString, matcher.group(1));
            matcher.appendReplacement(result, replacement);
        }
        matcher.appendTail(result);
        if(logger.isDebugEnabled()) {
            logger.debug("transform script: \"{}\" -> \"{}\" ==> \"{}\"", script, pattern, result.toString());
        }
        return result.toString();
    }
    /**
     * 测试
     * @param args
     * @throws ScriptException
     */
    public static void main(String args[]) throws ScriptException {

        StringBuilder sb = new StringBuilder();
        Map<String ,Object> map = new HashMap<>();
        map.put("name","name");
        Map<String ,Object> param = new HashMap<>();
        param.put("param", map);//获取元数据("人员","照片",param)  生成头像(获取元数据("人员","姓名",param),获取元数据("人员","照片",param))
        sb.append(" sum(max(3,4),min(1,2))  ");
        //sb.append(" max(3,4) min(1,2)  ");
        //System.out.print(parseScript("薪酬项(基本宫)", "薪酬项\\(([^\\]]+)\\)", "薪酬项(\"%s\")"));
        MetaDataScriptEngineUtil.execGroovyScript(sb.toString(), param);
    }
}
