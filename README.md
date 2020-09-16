Citrus-Swagger-Coverage
=======================
Адаптация библиотеки `Swagger-Coverage 1.3.1` под Citrus Framework  
Ссылка на оригинальный проект https://github.com/viclovsky/swagger-coverage  

Как добавить в проект
--------------------
Вместо зависимости `swagger-coverage-rest-assured` необходимо указать зависимость:  
```xml
        <dependency>
            <groupId>ru.lanit</groupId>
            <artifactId>citrus-swagger-coverage</artifactId>
            <version>1.0.4</version>
        </dependency>
```
И так же указать репозиторий
```xml
        <repository>
            <id>citrus-coverage-mvn-repo</id>
            <url>https://raw.github.com/lanit-izh/citrus-coverage/mvn-repo/</url>
            <snapshots>
                <enabled>true</enabled>
                <updatePolicy>always</updatePolicy>
            </snapshots>
        </repository>
```
Далее в `citrus-context.xml` добавить бин перехватчика
```xml
<beans>
    <citrus-http:client id="httpClient"
                        request-url="https://petstore.swagger.io"
                        interceptors="interceptor"/>

    <bean id="interceptor" class="ru.lanit.interceptor.CitrusHttpInterceptor"></bean> 
</beans>
```
Вместо добавления фильтра, как это реализовано для Rest-Assured:  
`RestAssured.given().filter(new SwaggerCoverageRestAssured())`  
 
Необходимо добавить кодировщик:  
`.get(InterceptorHandler.getPath(/api/user))`  

Далее следуйте следуйте оригинальной иструкции.