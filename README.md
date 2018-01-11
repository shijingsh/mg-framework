### metadata 元数据框架
spring + hibernate + groovy + report

### how to use

Add dependency to your project
~~~xml
  <dependencies>
    <dependency>
      <groupId>com.github.mg365</groupId>
      <artifactId>mg-framework</artifactId>
      <version>1.0</version>
      <type>pom</type>
    </dependency>
    <dependency>
      <groupId>com.github.mg365</groupId>
      <artifactId>mg-entity</artifactId>
      <version>1.0</version>
      <type>jar</type>
    </dependency>
    <dependency>
      <groupId>com.github.mg365</groupId>
      <artifactId>mg-fw</artifactId>
      <version>1.0</version>
      <type>jar</type>
    </dependency>
    <dependency>
      <groupId>com.github.mg365</groupId>
      <artifactId>mg-common</artifactId>
      <version>1.0</version>
      <type>jar</type>
    </dependency>
    <dependency>
      <groupId>com.github.mg365</groupId>
      <artifactId>mg-groovy</artifactId>
      <version>1.0</version>
      <type>jar</type>
    </dependency>
    <dependency>
      <groupId>com.github.mg365</groupId>
      <artifactId>mg-report</artifactId>
      <version>1.0</version>
      <type>jar</type>
    </dependency>
  </dependencies>
  
  ~~~
  
  add plus for generate-sources
  
  ~~~xml
  <plugins>
        <plugin>
          <groupId>com.mysema.maven</groupId>
          <artifactId>apt-maven-plugin</artifactId>
          <version>1.1.2</version>
          <executions>
            <execution>
              <id>querydsl-process</id>
              <!--<phase>clean</phase>-->
              <phase>generate-sources</phase>
              <goals>
                <goal>process</goal>
              </goals>
              <configuration>
                <outputDirectory>
                  target/generated-sources/java
                </outputDirectory>
                <processor>
                  com.mysema.query.apt.jpa.JPAAnnotationProcessor
                </processor>
                <!--<showWarnings>true</showWarnings>-->
                <logOnlyOnError>true</logOnlyOnError>
              </configuration>
            </execution>
          </executions>
        </plugin>
  
        <plugin>
          <groupId>org.codehaus.mojo</groupId>
          <artifactId>build-helper-maven-plugin</artifactId>
          <version>1.7</version>
          <executions>
            <execution>
              <id>add-source</id>
              <phase>generate-sources</phase>
              <goals>
                <goal>add-source</goal>
              </goals>
              <configuration>
                <sources>
                  <source>target/generated-sources/java</source>
                </sources>
              </configuration>
            </execution>
          </executions>
        </plugin>
    </plugins>
  ~~~
  
  ### generator db sql
  
  find the class "com.mg.common.tools.HibernateDDLGenerator"

  debug or running the main method,The SQL statement will be entered at the console
