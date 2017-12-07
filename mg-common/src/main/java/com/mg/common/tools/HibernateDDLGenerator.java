package com.mg.common.tools;

import com.mg.framework.sys.JPAImprovedNamingStrategy;
import org.hibernate.cfg.Configuration;
import org.hibernate.cfg.Environment;
import org.hibernate.dialect.Dialect;
import org.hibernate.tool.hbm2ddl.DatabaseMetadata;
import org.hibernate.tool.hbm2ddl.SchemaUpdateScript;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.core.type.classreading.MetadataReader;
import org.springframework.core.type.classreading.MetadataReaderFactory;
import org.springframework.core.type.classreading.SimpleMetadataReaderFactory;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.List;

/**
 * ddl generator
 * @author Tan Liang (Bred Tan)
 * @since 2015/2/26
 */
public class HibernateDDLGenerator {
    private static Logger logger = LoggerFactory.getLogger(HibernateDDLGenerator.class);



    public static void main(String[] args) throws IOException, ClassNotFoundException {
//        ApplicationContext ac = new ClassPathXmlApplicationContext("applicationContext.xml");
        MetadataReaderFactory factory = new SimpleMetadataReaderFactory();
        ResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
        Resource[] resources = resolver.getResources(ResourcePatternResolver.CLASSPATH_ALL_URL_PREFIX + "com/mg/**/*Entity.class");

//        Properties properties = PropertiesLoaderUtils.loadProperties(resolver.getResource("/db-connection.properties"));
//        String url = properties.getProperty("connection.url");//"jdbc:mysql://172.16.90.114:3306/shaoxing?useUnicode=true&characterEncoding=utf-8";
//        String username = properties.getProperty("connection.username"); //"root";
//        String password = properties.getProperty("connection.password"); //"123456";

        String url = "jdbc:mysql://localhost:3306/mg_system?useUnicode=true&characterEncoding=utf-8";
        String username = "root";
        String password = "123456";



        Configuration configuration = new Configuration();
//        configuration.setProperty(Environment.CONNECTION_PROVIDER, "");
        configuration.setProperty(Environment.URL, url);
        configuration.setProperty(Environment.DRIVER, "com.mysql.jdbc.Driver");
        configuration.setProperty(Environment.USER, username);
        configuration.setProperty(Environment.PASS, password);
        configuration.setProperty(Environment.DIALECT, "org.hibernate.dialect.MySQL5Dialect");
        configuration.setProperty("hibernate.auditable", "true");
        configuration.setProperty("org.hibernate.envers.audit_table_suffix", "_history");
        configuration.setProperty("org.hibernate.envers.audit_strategy", "org.hibernate.envers.strategy.ValidityAuditStrategy");
        configuration.setProperty("org.hibernate.envers.audit_strategy_validity_store_revend_timestamp", "true");
        configuration.setProperty("org.hibernate.envers.store_data_at_delete", "true");
//        configuration.setProperty(Environment.DEFAULT_CATALOG, "qihangedu");
//        configuration.setProperty(Environment.DEFAULT_SCHEMA, "qihangedu");
//        configuration.setProperty("hibernate.ejb.naming_strategy", "com.qihangedu.tms.common.TMSJPAImprovedNamingStrategy");
        configuration.setProperty(Environment.GLOBALLY_QUOTED_IDENTIFIERS, "true");
        Class.forName("com.mysql.jdbc.Driver");

        for (Resource res : resources) {
            //通过 MetadataReader得到ClassMeta信息,打印类名
//            logger.debug("{}", res.getFilename());
            MetadataReader meta = factory.getMetadataReader(res);
            //System.out.println(meta.getClassMetadata().getClassName());
            configuration.addAnnotatedClass(Class.forName(meta.getClassMetadata().getClassName()));
        }
//        new HibernateDDLGenerator().execute(Dialect.getDialect().);
        try {
            Connection con = DriverManager.getConnection(url, username, password);
            configuration.setNamingStrategy(new JPAImprovedNamingStrategy());
            configuration.buildMappings();
            List<SchemaUpdateScript> result = configuration.generateSchemaUpdateScriptList(Dialect.getDialect(configuration.getProperties()), new DatabaseMetadata(con, Dialect.getDialect(configuration.getProperties()), configuration));
//            logger.debug("{}", result);
            for(SchemaUpdateScript script : result) {
//                logger.info(script.getScript());
                System.out.println(script.getScript() + ";");
            }
        } catch (SQLException se) {
            System.out.println("数据库连接失败！");
            se.printStackTrace();
        }
    }

//    private void execute(Class<?>... classes) {
//        Configuration configuration = new Configuration();
//        configuration.setProperty(Environment.DIALECT, "org.hibernate.dialect.MySQL5Dialect");
//        for (Class<?> entityClass : classes) {
//            configuration.addAnnotatedClass(entityClass);
//        }
//
//        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
//        String dateString = sdf.format(new Date());
//
//        SchemaExport schemaExport = new SchemaExport(configuration);
//        configuration.generateSchemaUpdateScriptList()
//        schemaExport.setDelimiter(";");
//        schemaExport.setOutputFile(String.format(".\\ddl_output\\ddl_%s.sql ",  dateString));
//        boolean consolePrint = true;
//        boolean exportInDatabase = false;
//        schemaExport.create(consolePrint, exportInDatabase);
//    }

}
