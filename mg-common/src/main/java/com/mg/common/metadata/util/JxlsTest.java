package com.mg.common.metadata.util;

import net.sf.jxls.transformer.XLSTransformer;
import org.apache.poi.hssf.usermodel.HSSFClientAnchor;
import org.apache.poi.hssf.usermodel.HSSFPatriarch;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by liukefu on 2016/4/21.
 */
public class JxlsTest {
    //C:\jira\empprinttemlete.xls
    //C:\jira\template-simple.xlsx
    private static String xlsTemplateFileName = "C:\\jira\\employeeExport.xls";
    private static String outputFileName = "C:\\jira\\empprinttemlete2.xls";

    public static void main(String args[]) throws IOException, InvalidFormatException {

        Map<String, Object> map = new HashMap<>();

        List<Map<String, Object>> list = new ArrayList<>();

        Map<String, Object> map1 = new HashMap<>();
        map1.put("name", "电视");
        map1.put("price", "3000");
        map1.put("desc", "3D电视机");
        map1.put("备注", "中文测试");
        Map<String, Object> map2 = new HashMap<>();
        map2.put("name", "空调");
        map2.put("price", "2000");
        map2.put("desc", "变频空调");
        map1.put("备注", "测试中文");
        list.add(map1);
        list.add(map2);

        map.put("obj", list);

        List<List> objects = new ArrayList<>();
        objects.add(list);
        objects.add(list);
        objects.add(list);
        objects.add(list);
        //sheet的名称
        List<String> listSheetNames = new ArrayList<>();
        listSheetNames.add("1");
        listSheetNames.add("2");
        listSheetNames.add("3");
        listSheetNames.add("4");

        XLSTransformer transformer = new XLSTransformer();

        InputStream is = new FileInputStream(xlsTemplateFileName);//模板文件流
        HSSFWorkbook workBook =  (HSSFWorkbook) transformer.transformMultipleSheetsList(is, objects, listSheetNames, "obj", new HashMap(), 0);
        for(int i=0;i<4;i++){
            HSSFSheet sheet = workBook.getSheetAt(i);

            String picture = "C:\\jira\\person.png";  //要插入的图片，可为png、jpg格式
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            BufferedImage BufferImg = ImageIO.read(new File(picture));
            ImageIO.write(BufferImg, "PNG", bos);
            HSSFPatriarch patriarch = sheet.createDrawingPatriarch();//创建绘图工具对象
            /**
             col1：起始单元格列序号，从0开始计算；
             row1：起始单元格行序号，从0开始计算，如例子中col1=0,row1=0就表示起始单元格为A1；
             col2：终止单元格列序号，从0开始计算；
             row2：终止单元格行序号，从0开始计算，如例子中col2=2,row2=2就表示起始单元格为C3；
             */
            HSSFClientAnchor anchor = new HSSFClientAnchor(0,0,0,0,(short)6,2,(short)8,6); //设置图片显示区域
            patriarch.createPicture(anchor, workBook.addPicture(bos.toByteArray(), workBook.PICTURE_TYPE_PNG));
        }

        OutputStream os = new FileOutputStream(outputFileName); //导出文件流
        workBook.write(os);  //写导出文件
        is.close();
        os.flush();
        os.close();


        //transformer.transformXLS(xlsTemplateFileName, beans, outputFileName);


    }
}
