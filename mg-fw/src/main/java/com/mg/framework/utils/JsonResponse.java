package com.mg.framework.utils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializeConfig;
import com.alibaba.fastjson.serializer.SerializeFilter;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.alibaba.fastjson.serializer.SimpleDateFormatSerializer;
import com.mg.framework.log.FastjsonSimpleFilter;
import com.mg.framework.log.ResponseBody;
import com.mg.framework.log.FastjsonFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;

public class JsonResponse {
    private static Logger logger = LoggerFactory.getLogger(JsonResponse.class);

    /**
     * 将实体类对象转换为json格式字符串.
     *
     * 考虑到延迟加载的问题，这里对没有延迟加载的属性不做转换，直接跳过。
     * 对循环引用，这里采用fastjson缺省的方式，即一个对象只出现一次，避免死循环。
     *
     * @param obj entity实体对象
     * @return JSON格式字符串
     */
    public synchronized static String entityToJson(Object obj) {
        FastjsonFilter filter = new FastjsonFilter();
        return JSON.toJSONString(obj,
                filter,
                SerializerFeature.WriteMapNullValue,
                SerializerFeature.WriteNullStringAsEmpty,
                SerializerFeature.WriteSlashAsSpecial);

    }

    /**
     * 完整的输出json对象，所有对象只要引用到了都将重复出现。为了避免死循环导致的栈溢
     * 出，需要将循环引用的属性作过滤。
     * @param obj 需要转换的对象
     * @param filterPropertyNames 需要过滤的属性名。以"类名.属性名"的格式出现，也可
     *                            以单独制定属性名，则表示所有类的这个属性名都被过滤
     *                            这里的属性名为属性开始的名字。多个过滤条件以分号
     *                            分割：
     *                            a)ModelEntity.belong，ModelEntity类的以belong开始
     *                            的属性都不出现在json字串中
     *                            b)belong, 所有obj及其关联属性对象中，只要有belong
     *                            开始的属性，都不出现在json字串中
     *                            c)ModelEntity.belong;createDate，ModelEntity类
     *                            的以belong开始的属性，或者以createDate开始的属性，
     *                            都不出现在json字串中
     * @return JSON格式字符串
     */
    public synchronized static String entityToJsonFull(Object obj, String filterPropertyNames) {
        FastjsonFilter filter = new FastjsonFilter(filterPropertyNames);
        return JSON.toJSONString(obj,
                filter,
                SerializerFeature.DisableCircularReferenceDetect,
                SerializerFeature.WriteMapNullValue,
                SerializerFeature.WriteNullStringAsEmpty,
                SerializerFeature.WriteSlashAsSpecial);
    }

    /**
     * 将实体类对象转换为json格式字符串（不再将驼峰转换为下划线）.
     * <p/>
     * 考虑到延迟加载的问题，这里对没有延迟加载的属性不做转换，直接跳过。
     * 对循环引用，这里采用fastjson缺省的方式，即一个对象只出现一次，避免死循环。
     *
     * @param obj entity实体对象
     * @return JSON格式字符串
     */
    public synchronized static String entityToJsonSimple(Object obj) {
        FastjsonSimpleFilter filter = new FastjsonSimpleFilter();
        return JSON.toJSONString(obj,
                filter,
                SerializerFeature.WriteMapNullValue,
                SerializerFeature.WriteNullStringAsEmpty,
                SerializerFeature.WriteSlashAsSpecial);

    }

    /**
     * 完整的输出json对象，所有对象只要引用到了都将重复出现。为了避免死循环导致的栈溢
     * 出，需要将循环引用的属性作过滤。（不再将驼峰转换为下划线）
     *
     * @param obj                 需要转换的对象
     * @param filterPropertyNames 需要过滤的属性名。以"类名.属性名"的格式出现，也可
     *                            以单独制定属性名，则表示所有类的这个属性名都被过滤
     *                            这里的属性名为属性开始的名字。多个过滤条件以分号
     *                            分割：
     *                            a)ModelEntity.belong，ModelEntity类的以belong开始
     *                            的属性都不出现在json字串中
     *                            b)belong, 所有obj及其关联属性对象中，只要有belong
     *                            开始的属性，都不出现在json字串中
     *                            c)ModelEntity.belong;createDate，ModelEntity类
     *                            的以belong开始的属性，或者以createDate开始的属性，
     *                            都不出现在json字串中
     * @return JSON格式字符串
     */
    public synchronized static String entityToJsonFullSimple(Object obj, String filterPropertyNames) {
        FastjsonSimpleFilter filter = new FastjsonSimpleFilter(filterPropertyNames);
        return JSON.toJSONString(obj,
                filter,
                SerializerFeature.DisableCircularReferenceDetect,
                SerializerFeature.WriteMapNullValue,
                SerializerFeature.WriteNullStringAsEmpty,
                SerializerFeature.WriteSlashAsSpecial);
    }

    public synchronized static String error(int code, String message) {
        return error(code, message, null);
    }

    public synchronized static String error(int code, String message, Object obj) {
        ResponseBody body = new ResponseBody(code, message, obj);
        return entityToJsonSimple(body);
    }

    /**
     * 逻辑上表示成功，组合成需要返回的JSON字符串
     * @param obj 对象
     * @param isRefDetect 是否 DisableCircularReferenceDetect。
     *                    设置为false则不会带有ref[0]这样的属性值输出，但会有循环引用的风险，需要设置过滤属性名
     * @param filterPropNames 过滤属性名的数组
     * @param isCamelToUnderline 是否需要将驼峰命名法转换为下划线命名法
     * @return JSON字符串
     */
    public synchronized static String success(Object obj, boolean isRefDetect, String[] filterPropNames, boolean isCamelToUnderline) {
        ResponseBody body = new ResponseBody(0, "", obj);
        SerializeFilter filter;
        if(isCamelToUnderline) {
            filter = new FastjsonFilter(filterPropNames);
        }
        else {
            filter = new FastjsonSimpleFilter(filterPropNames);
        }

        if (isRefDetect)
            return JSON.toJSONString(body,
                    filter,
                    SerializerFeature.WriteMapNullValue,
                    SerializerFeature.WriteNullStringAsEmpty,
                    SerializerFeature.WriteSlashAsSpecial);
        else
            return JSON.toJSONString(body,
                    filter,
                    SerializerFeature.DisableCircularReferenceDetect,
                    SerializerFeature.QuoteFieldNames,
                    SerializerFeature.WriteMapNullValue,
                    SerializerFeature.WriteNullStringAsEmpty,
                    SerializerFeature.WriteSlashAsSpecial);
    }

    /**
     * 转json 自定义日期类型的转化
     * @param obj
     * @param dateFormat
     * @return
     */
    public synchronized static String successWithDate(Object obj, String dateFormat) {
        ResponseBody body = new ResponseBody(0, "", obj);

        SerializeConfig mapping = new SerializeConfig();
        mapping.put(Date.class, new SimpleDateFormatSerializer(dateFormat));
        mapping.put(java.sql.Date.class, new SimpleDateFormatSerializer(dateFormat));
        return JSON.toJSONString(body,mapping,
                SerializerFeature.DisableCircularReferenceDetect,
                SerializerFeature.QuoteFieldNames,
                SerializerFeature.WriteMapNullValue,
                SerializerFeature.WriteNullStringAsEmpty,
                SerializerFeature.WriteSlashAsSpecial);

    }
    //1. 是否带ref
    //2. 是否带过滤属性
    //3. 是否驼峰转下划线命名

    public synchronized static String success() {
        return success(null, null);
    }

    public synchronized static String success(Object obj) {
        return success(obj, false, null, false);
    }

    public synchronized static String success(Object obj, String[] filterPropNames) {
        return success(obj, false, filterPropNames, false);
    }

    public static String toJson(Object obj, String[] filterPropNames) {
        return toJson(obj, false, filterPropNames, false);
    }
    public static String toJson(Object obj,boolean isRefDetect, String[] filterPropNames,boolean isCamelToUnderline) {
        SerializeFilter filter;
        if (isCamelToUnderline) {
            filter = new FastjsonFilter(filterPropNames);
        } else {
            filter = new FastjsonSimpleFilter(filterPropNames);
        }

        if (isRefDetect)
            return JSON.toJSONString(obj,
                    filter,
                    SerializerFeature.WriteMapNullValue,
                    SerializerFeature.WriteNullStringAsEmpty,
                    SerializerFeature.WriteSlashAsSpecial);
        else
            return JSON.toJSONString(obj,
                    filter,
                    SerializerFeature.DisableCircularReferenceDetect,
                    SerializerFeature.QuoteFieldNames,
                    SerializerFeature.WriteMapNullValue,
                    SerializerFeature.WriteNullStringAsEmpty,
                    SerializerFeature.WriteSlashAsSpecial);
    }
    
    
    /**
     * 如果后台只是想让前台弹出一个成功的提示框,没有别的数据给前台，可以直接使用此函数
     * 前台会有拦截器拦截具有successText字符串的相应
     * @param message 信息提示框的内容
     * @return
     */
    public synchronized static String successMessage(String message) {
        return successMessage(message,null,null);
    }
    
    public synchronized static String successMessage(String message,Object data) {
    	return successMessage(message,data,null);
    }
    
    public synchronized static String successMessage(String message,Object data,String[] filterPropNames) {
    	ResponseBody body = new ResponseBody(ResponseBody.SUCCESS, null, data);
    	body.setSuccessText(message);
    	return toJson(body, filterPropNames);
    }
}
