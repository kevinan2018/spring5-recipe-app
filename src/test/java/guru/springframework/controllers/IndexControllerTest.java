package guru.springframework.controllers;

import guru.springframework.domain.Recipe;
import guru.springframework.services.RecipeService;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MockMvcBuilder;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.ui.Model;

import java.util.HashSet;
import java.util.Set;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.anySet;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

public class IndexControllerTest {

    IndexController indexController;

    @Mock
    RecipeService recipeService;

    @Mock
    Model model;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        indexController = new IndexController(recipeService);
    }

    @Test
    public void testMocMVC() throws Exception {
        MockMvc mockMvc = MockMvcBuilders.standaloneSetup(indexController).build();

        mockMvc.perform(get("/"))
                .andExpect(status().isOk())
                .andExpect(view().name("index"));
    }

    @Test
    public void getIndexPage() {
        //given
        Set<Recipe> recipes = new HashSet<>();
        recipes.add(new Recipe());
        Recipe recipe = new Recipe();
        recipe.setId("100");
        recipes.add(recipe);

        when(recipeService.getRecipes()).thenReturn(recipes);

        // argument captor of the 2nd argument of model.addAttribute("recipes", recipeService.getRecipes());
        ArgumentCaptor<Set<Recipe>> argumentCaptor = ArgumentCaptor.forClass(Set.class);

        //when
        String viewName = indexController.getIndexPage(model);

        //then
        assertEquals("index", viewName);

        verify(recipeService, times(1)).getRecipes();

        //default argument matcher anySet()
        //verify(model, times(1)).addAttribute(eq("recipes"), anySet());
        verify(model, times(1)).addAttribute(eq("recipes"), argumentCaptor.capture());

        Set<Recipe> setInController = argumentCaptor.getValue();
        assertEquals(2, setInController.size());

    }
}

/*
Fix: use eq("raw string") matcher

Problem:
/usr/lib/jvm/java-8-openjdk-amd64/bin/java -ea -Didea.test.cyclic.buffer.size=1048576 -javaagent:/opt/idea180104/lib/idea_rt.jar=39092:/opt/idea180104/bin -Dfile.encoding=UTF-8 -classpath /opt/idea180104/lib/idea_rt.jar:/opt/idea180104/plugins/junit/lib/junit-rt.jar:/opt/idea180104/plugins/junit/lib/junit5-rt.jar:/usr/lib/jvm/java-8-openjdk-amd64/jre/lib/charsets.jar:/usr/lib/jvm/java-8-openjdk-amd64/jre/lib/ext/cldrdata.jar:/usr/lib/jvm/java-8-openjdk-amd64/jre/lib/ext/dnsns.jar:/usr/lib/jvm/java-8-openjdk-amd64/jre/lib/ext/icedtea-sound.jar:/usr/lib/jvm/java-8-openjdk-amd64/jre/lib/ext/jaccess.jar:/usr/lib/jvm/java-8-openjdk-amd64/jre/lib/ext/localedata.jar:/usr/lib/jvm/java-8-openjdk-amd64/jre/lib/ext/nashorn.jar:/usr/lib/jvm/java-8-openjdk-amd64/jre/lib/ext/sunec.jar:/usr/lib/jvm/java-8-openjdk-amd64/jre/lib/ext/sunjce_provider.jar:/usr/lib/jvm/java-8-openjdk-amd64/jre/lib/ext/sunpkcs11.jar:/usr/lib/jvm/java-8-openjdk-amd64/jre/lib/ext/zipfs.jar:/usr/lib/jvm/java-8-openjdk-amd64/jre/lib/jce.jar:/usr/lib/jvm/java-8-openjdk-amd64/jre/lib/jsse.jar:/usr/lib/jvm/java-8-openjdk-amd64/jre/lib/management-agent.jar:/usr/lib/jvm/java-8-openjdk-amd64/jre/lib/resources.jar:/usr/lib/jvm/java-8-openjdk-amd64/jre/lib/rt.jar:/home/kevin/tmp/spring_framework/spring5-recipe-app/target/test-classes:/home/kevin/tmp/spring_framework/spring5-recipe-app/target/classes:/home/kevin/.m2/repository/org/springframework/boot/spring-boot-starter-data-jpa/2.0.3.RELEASE/spring-boot-starter-data-jpa-2.0.3.RELEASE.jar:/home/kevin/.m2/repository/org/springframework/boot/spring-boot-starter/2.0.3.RELEASE/spring-boot-starter-2.0.3.RELEASE.jar:/home/kevin/.m2/repository/org/springframework/boot/spring-boot-starter-logging/2.0.3.RELEASE/spring-boot-starter-logging-2.0.3.RELEASE.jar:/home/kevin/.m2/repository/ch/qos/logback/logback-classic/1.2.3/logback-classic-1.2.3.jar:/home/kevin/.m2/repository/ch/qos/logback/logback-core/1.2.3/logback-core-1.2.3.jar:/home/kevin/.m2/repository/org/apache/logging/log4j/log4j-to-slf4j/2.10.0/log4j-to-slf4j-2.10.0.jar:/home/kevin/.m2/repository/org/apache/logging/log4j/log4j-api/2.10.0/log4j-api-2.10.0.jar:/home/kevin/.m2/repository/org/slf4j/jul-to-slf4j/1.7.25/jul-to-slf4j-1.7.25.jar:/home/kevin/.m2/repository/javax/annotation/javax.annotation-api/1.3.2/javax.annotation-api-1.3.2.jar:/home/kevin/.m2/repository/org/yaml/snakeyaml/1.19/snakeyaml-1.19.jar:/home/kevin/.m2/repository/org/springframework/boot/spring-boot-starter-aop/2.0.3.RELEASE/spring-boot-starter-aop-2.0.3.RELEASE.jar:/home/kevin/.m2/repository/org/springframework/spring-aop/5.0.7.RELEASE/spring-aop-5.0.7.RELEASE.jar:/home/kevin/.m2/repository/org/aspectj/aspectjweaver/1.8.13/aspectjweaver-1.8.13.jar:/home/kevin/.m2/repository/org/springframework/boot/spring-boot-starter-jdbc/2.0.3.RELEASE/spring-boot-starter-jdbc-2.0.3.RELEASE.jar:/home/kevin/.m2/repository/com/zaxxer/HikariCP/2.7.9/HikariCP-2.7.9.jar:/home/kevin/.m2/repository/org/springframework/spring-jdbc/5.0.7.RELEASE/spring-jdbc-5.0.7.RELEASE.jar:/home/kevin/.m2/repository/org/hibernate/hibernate-core/5.2.17.Final/hibernate-core-5.2.17.Final.jar:/home/kevin/.m2/repository/org/jboss/logging/jboss-logging/3.3.2.Final/jboss-logging-3.3.2.Final.jar:/home/kevin/.m2/repository/org/hibernate/javax/persistence/hibernate-jpa-2.1-api/1.0.2.Final/hibernate-jpa-2.1-api-1.0.2.Final.jar:/home/kevin/.m2/repository/org/javassist/javassist/3.22.0-GA/javassist-3.22.0-GA.jar:/home/kevin/.m2/repository/antlr/antlr/2.7.7/antlr-2.7.7.jar:/home/kevin/.m2/repository/org/jboss/jandex/2.0.3.Final/jandex-2.0.3.Final.jar:/home/kevin/.m2/repository/com/fasterxml/classmate/1.3.4/classmate-1.3.4.jar:/home/kevin/.m2/repository/dom4j/dom4j/1.6.1/dom4j-1.6.1.jar:/home/kevin/.m2/repository/org/hibernate/common/hibernate-commons-annotations/5.0.1.Final/hibernate-commons-annotations-5.0.1.Final.jar:/home/kevin/.m2/repository/javax/transaction/javax.transaction-api/1.2/javax.transaction-api-1.2.jar:/home/kevin/.m2/repository/org/springframework/data/spring-data-jpa/2.0.8.RELEASE/spring-data-jpa-2.0.8.RELEASE.jar:/home/kevin/.m2/repository/org/springframework/data/spring-data-commons/2.0.8.RELEASE/spring-data-commons-2.0.8.RELEASE.jar:/home/kevin/.m2/repository/org/springframework/spring-orm/5.0.7.RELEASE/spring-orm-5.0.7.RELEASE.jar:/home/kevin/.m2/repository/org/springframework/spring-context/5.0.7.RELEASE/spring-context-5.0.7.RELEASE.jar:/home/kevin/.m2/repository/org/springframework/spring-tx/5.0.7.RELEASE/spring-tx-5.0.7.RELEASE.jar:/home/kevin/.m2/repository/org/springframework/spring-beans/5.0.7.RELEASE/spring-beans-5.0.7.RELEASE.jar:/home/kevin/.m2/repository/org/slf4j/slf4j-api/1.7.25/slf4j-api-1.7.25.jar:/home/kevin/.m2/repository/org/springframework/spring-aspects/5.0.7.RELEASE/spring-aspects-5.0.7.RELEASE.jar:/home/kevin/.m2/repository/org/springframework/boot/spring-boot-starter-thymeleaf/2.0.3.RELEASE/spring-boot-starter-thymeleaf-2.0.3.RELEASE.jar:/home/kevin/.m2/repository/org/thymeleaf/thymeleaf-spring5/3.0.9.RELEASE/thymeleaf-spring5-3.0.9.RELEASE.jar:/home/kevin/.m2/repository/org/thymeleaf/thymeleaf/3.0.9.RELEASE/thymeleaf-3.0.9.RELEASE.jar:/home/kevin/.m2/repository/org/attoparser/attoparser/2.0.4.RELEASE/attoparser-2.0.4.RELEASE.jar:/home/kevin/.m2/repository/org/unbescape/unbescape/1.1.5.RELEASE/unbescape-1.1.5.RELEASE.jar:/home/kevin/.m2/repository/org/thymeleaf/extras/thymeleaf-extras-java8time/3.0.1.RELEASE/thymeleaf-extras-java8time-3.0.1.RELEASE.jar:/home/kevin/.m2/repository/org/springframework/boot/spring-boot-starter-web/2.0.3.RELEASE/spring-boot-starter-web-2.0.3.RELEASE.jar:/home/kevin/.m2/repository/org/springframework/boot/spring-boot-starter-json/2.0.3.RELEASE/spring-boot-starter-json-2.0.3.RELEASE.jar:/home/kevin/.m2/repository/com/fasterxml/jackson/core/jackson-databind/2.9.6/jackson-databind-2.9.6.jar:/home/kevin/.m2/repository/com/fasterxml/jackson/core/jackson-annotations/2.9.0/jackson-annotations-2.9.0.jar:/home/kevin/.m2/repository/com/fasterxml/jackson/core/jackson-core/2.9.6/jackson-core-2.9.6.jar:/home/kevin/.m2/repository/com/fasterxml/jackson/datatype/jackson-datatype-jdk8/2.9.6/jackson-datatype-jdk8-2.9.6.jar:/home/kevin/.m2/repository/com/fasterxml/jackson/datatype/jackson-datatype-jsr310/2.9.6/jackson-datatype-jsr310-2.9.6.jar:/home/kevin/.m2/repository/com/fasterxml/jackson/module/jackson-module-parameter-names/2.9.6/jackson-module-parameter-names-2.9.6.jar:/home/kevin/.m2/repository/org/springframework/boot/spring-boot-starter-tomcat/2.0.3.RELEASE/spring-boot-starter-tomcat-2.0.3.RELEASE.jar:/home/kevin/.m2/repository/org/apache/tomcat/embed/tomcat-embed-core/8.5.31/tomcat-embed-core-8.5.31.jar:/home/kevin/.m2/repository/org/apache/tomcat/embed/tomcat-embed-el/8.5.31/tomcat-embed-el-8.5.31.jar:/home/kevin/.m2/repository/org/apache/tomcat/embed/tomcat-embed-websocket/8.5.31/tomcat-embed-websocket-8.5.31.jar:/home/kevin/.m2/repository/org/hibernate/validator/hibernate-validator/6.0.10.Final/hibernate-validator-6.0.10.Final.jar:/home/kevin/.m2/repository/javax/validation/validation-api/2.0.1.Final/validation-api-2.0.1.Final.jar:/home/kevin/.m2/repository/org/springframework/spring-web/5.0.7.RELEASE/spring-web-5.0.7.RELEASE.jar:/home/kevin/.m2/repository/org/springframework/spring-webmvc/5.0.7.RELEASE/spring-webmvc-5.0.7.RELEASE.jar:/home/kevin/.m2/repository/org/springframework/spring-expression/5.0.7.RELEASE/spring-expression-5.0.7.RELEASE.jar:/home/kevin/.m2/repository/org/springframework/boot/spring-boot-devtools/2.0.3.RELEASE/spring-boot-devtools-2.0.3.RELEASE.jar:/home/kevin/.m2/repository/org/springframework/boot/spring-boot/2.0.3.RELEASE/spring-boot-2.0.3.RELEASE.jar:/home/kevin/.m2/repository/org/springframework/boot/spring-boot-autoconfigure/2.0.3.RELEASE/spring-boot-autoconfigure-2.0.3.RELEASE.jar:/home/kevin/.m2/repository/com/h2database/h2/1.4.197/h2-1.4.197.jar:/home/kevin/.m2/repository/org/projectlombok/lombok/1.16.22/lombok-1.16.22.jar:/home/kevin/.m2/repository/org/springframework/boot/spring-boot-starter-test/2.0.3.RELEASE/spring-boot-starter-test-2.0.3.RELEASE.jar:/home/kevin/.m2/repository/org/springframework/boot/spring-boot-test/2.0.3.RELEASE/spring-boot-test-2.0.3.RELEASE.jar:/home/kevin/.m2/repository/org/springframework/boot/spring-boot-test-autoconfigure/2.0.3.RELEASE/spring-boot-test-autoconfigure-2.0.3.RELEASE.jar:/home/kevin/.m2/repository/com/jayway/jsonpath/json-path/2.4.0/json-path-2.4.0.jar:/home/kevin/.m2/repository/net/minidev/json-smart/2.3/json-smart-2.3.jar:/home/kevin/.m2/repository/net/minidev/accessors-smart/1.2/accessors-smart-1.2.jar:/home/kevin/.m2/repository/org/ow2/asm/asm/5.0.4/asm-5.0.4.jar:/home/kevin/.m2/repository/junit/junit/4.12/junit-4.12.jar:/home/kevin/.m2/repository/org/assertj/assertj-core/3.9.1/assertj-core-3.9.1.jar:/home/kevin/.m2/repository/org/mockito/mockito-core/2.15.0/mockito-core-2.15.0.jar:/home/kevin/.m2/repository/net/bytebuddy/byte-buddy/1.7.11/byte-buddy-1.7.11.jar:/home/kevin/.m2/repository/net/bytebuddy/byte-buddy-agent/1.7.11/byte-buddy-agent-1.7.11.jar:/home/kevin/.m2/repository/org/objenesis/objenesis/2.6/objenesis-2.6.jar:/home/kevin/.m2/repository/org/hamcrest/hamcrest-core/1.3/hamcrest-core-1.3.jar:/home/kevin/.m2/repository/org/hamcrest/hamcrest-library/1.3/hamcrest-library-1.3.jar:/home/kevin/.m2/repository/org/skyscreamer/jsonassert/1.5.0/jsonassert-1.5.0.jar:/home/kevin/.m2/repository/com/vaadin/external/google/android-json/0.0.20131108.vaadin1/android-json-0.0.20131108.vaadin1.jar:/home/kevin/.m2/repository/org/springframework/spring-core/5.0.7.RELEASE/spring-core-5.0.7.RELEASE.jar:/home/kevin/.m2/repository/org/springframework/spring-jcl/5.0.7.RELEASE/spring-jcl-5.0.7.RELEASE.jar:/home/kevin/.m2/repository/org/springframework/spring-test/5.0.7.RELEASE/spring-test-5.0.7.RELEASE.jar:/home/kevin/.m2/repository/org/xmlunit/xmlunit-core/2.5.1/xmlunit-core-2.5.1.jar com.intellij.rt.execution.junit.JUnitStarter -ideVersion5 -junit4 guru.springframework.controllers.IndexControllerTest

org.mockito.exceptions.misusing.InvalidUseOfMatchersException:
Invalid use of argument matchers!
2 matchers expected, 1 recorded:
-> at guru.springframework.controllers.IndexControllerTest.getIndexPage(IndexControllerTest.java:38)

This exception may occur if matchers are combined with raw values:
    //incorrect:
    someMethod(anyObject(), "raw String");
When using matchers, all arguments have to be provided by matchers.
For example:
    //correct:
    someMethod(anyObject(), eq("String by matcher"));

For more info see javadoc for Matchers class.


	at guru.springframework.controllers.IndexControllerTest.getIndexPage(IndexControllerTest.java:38)
	at sun.reflect.NativeMethodAccessorImpl.invoke0(Native Method)
	at sun.reflect.NativeMethodAccessorImpl.invoke(NativeMethodAccessorImpl.java:62)
	at sun.reflect.DelegatingMethodAccessorImpl.invoke(DelegatingMethodAccessorImpl.java:43)
	at java.lang.reflect.Method.invoke(Method.java:498)
	at org.junit.runners.model.FrameworkMethod$1.runReflectiveCall(FrameworkMethod.java:50)
	at org.junit.internal.runners.model.ReflectiveCallable.run(ReflectiveCallable.java:12)
	at org.junit.runners.model.FrameworkMethod.invokeExplosively(FrameworkMethod.java:47)
	at org.junit.internal.runners.statements.InvokeMethod.evaluate(InvokeMethod.java:17)
	at org.junit.internal.runners.statements.RunBefores.evaluate(RunBefores.java:26)
	at org.junit.runners.ParentRunner.runLeaf(ParentRunner.java:325)
	at org.junit.runners.BlockJUnit4ClassRunner.runChild(BlockJUnit4ClassRunner.java:78)
	at org.junit.runners.BlockJUnit4ClassRunner.runChild(BlockJUnit4ClassRunner.java:57)
	at org.junit.runners.ParentRunner$3.run(ParentRunner.java:290)
	at org.junit.runners.ParentRunner$1.schedule(ParentRunner.java:71)
	at org.junit.runners.ParentRunner.runChildren(ParentRunner.java:288)
	at org.junit.runners.ParentRunner.access$000(ParentRunner.java:58)
	at org.junit.runners.ParentRunner$2.evaluate(ParentRunner.java:268)
	at org.junit.runners.ParentRunner.run(ParentRunner.java:363)
	at org.junit.runner.JUnitCore.run(JUnitCore.java:137)
	at com.intellij.junit4.JUnit4IdeaTestRunner.startRunnerWithArgs(JUnit4IdeaTestRunner.java:68)
	at com.intellij.rt.execution.junit.IdeaTestRunner$Repeater.startRunnerWithArgs(IdeaTestRunner.java:47)
	at com.intellij.rt.execution.junit.JUnitStarter.prepareStreamsAndStart(JUnitStarter.java:242)
	at com.intellij.rt.execution.junit.JUnitStarter.main(JUnitStarter.java:70)


Process finished with exit code 255

*/
