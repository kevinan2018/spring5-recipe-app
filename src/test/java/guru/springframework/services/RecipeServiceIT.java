package guru.springframework.services;

import guru.springframework.commands.RecipeCommand;
import guru.springframework.converters.RecipeCommandToRecipe;
import guru.springframework.converters.RecipeToRecipeCommand;
import guru.springframework.domain.Recipe;
import guru.springframework.repositories.RecipeRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.Assert.*;
@RunWith(SpringRunner.class)
//@DataJpaTest
@SpringBootTest
public class RecipeServiceIT {
    public static final String NEW_DESCRIPTION = "New Description";

    @Autowired
    RecipeService recipeService;

    @Autowired
    RecipeRepository recipeRepository;

    @Autowired
    RecipeCommandToRecipe recipeCommandToRecipe;

    @Autowired
    RecipeToRecipeCommand recipeToRecipeCommand;

    //@Transactional
    @Test
    public void testSaveOfDescription() throws Exception {
        //given
        Iterable<Recipe> recipes = recipeRepository.findAll();
        Recipe testRecipe = recipes.iterator().next();
        RecipeCommand testRecipeCommand = recipeToRecipeCommand.convert(testRecipe);

        //when
        testRecipeCommand.setDescription(NEW_DESCRIPTION);
        RecipeCommand savedRecipeCommand = recipeService.saveRecipeCommand(testRecipeCommand).block();

        //then
        assertEquals(NEW_DESCRIPTION, savedRecipeCommand.getDescription());
        assertEquals(testRecipe.getId(), savedRecipeCommand.getId());
        assertEquals(testRecipe.getCategories().size(), savedRecipeCommand.getCategories().size());
        assertEquals(testRecipe.getIngredients().size(), savedRecipeCommand.getIngredients().size());
    }
}

/*
Fix: remove @Transactional from test because mongo db has no transaction

*java.lang.IllegalStateException: Failed to retrieve PlatformTransactionManager for @Transactional test:
* [DefaultTestContext@28f2a10f testClass = RecipeServiceIT, testInstance = guru.springframework.services.RecipeServiceIT@4262fdeb,
* testMethod = testSaveOfDescription@RecipeServiceIT, testException = [null],
* mergedContextConfiguration = [WebMergedContextConfiguration@f736069
* testClass = RecipeServiceIT, locations = '{}',
* classes = '{class guru.springframework.Spring5MongoRecipeAppApplication}',
* contextInitializerClasses = '[]', activeProfiles = '{}', propertySourceLocations = '{}',
* propertySourceProperties = '{org.springframework.boot.test.context.SpringBootTestContextBootstrapper=true}',
* contextCustomizers = set[org.springframework.boot.test.autoconfigure.properties.PropertyMappingContextCustomizer@0,
*       org.springframework.boot.test.autoconfigure.web.servlet.WebDriverContextCustomizerFactory$Customizer@2de8284b,
*       org.springframework.boot.test.context.filter.ExcludeFilterContextCustomizer@365185bd,
*       org.springframework.boot.test.json.DuplicateJsonObjectContextCustomizerFactory$DuplicateJsonObjectContextCustomizer@782663d3,
*       org.springframework.boot.test.mock.mockito.MockitoContextCustomizer@0,
*       org.springframework.boot.test.web.client.TestRestTemplateContextCustomizer@77846d2c],
* resourceBasePath = 'src/main/webapp', contextLoader = 'org.springframework.boot.test.context.SpringBootContextLoader',
* parent = [null]],
* attributes = map['org.springframework.test.context.web.ServletTestExecutionListener.activateListener' -> true,
*       'org.springframework.test.context.web.ServletTestExecutionListener.populatedRequestContextHolder' -> true,
*       'org.springframework.test.context.web.ServletTestExecutionListener.resetRequestContextHolder' -> true]]
	at org.springframework.util.Assert.state(Assert.java:94)

 */

/*
Fix: @SpringBootTest: whole spring context
Problem: @DataJpaTest is ONLY the lightweight spring context which cannot find RecipeService

org.springframework.beans.factory.UnsatisfiedDependencyException: Error creating bean with name 'guru.springframework.services.RecipeServiceIT': Unsatisfied dependency expressed through field 'recipeService'; nested exception is org.springframework.beans.factory.NoSuchBeanDefinitionException: No qualifying bean of type 'guru.springframework.services.RecipeService' available: expected at least 1 bean which qualifies as autowire candidate. Dependency annotations: {@org.springframework.beans.factory.annotation.Autowired(required=true)}

	at org.springframework.beans.factory.annotation.AutowiredAnnotationBeanPostProcessor$AutowiredFieldElement.inject(AutowiredAnnotationBeanPostProcessor.java:587)
	at org.springframework.beans.factory.annotation.InjectionMetadata.inject(InjectionMetadata.java:91)
	at org.springframework.beans.factory.annotation.AutowiredAnnotationBeanPostProcessor.postProcessPropertyValues(AutowiredAnnotationBeanPostProcessor.java:373)
	at org.springframework.beans.factory.support.AbstractAutowireCapableBeanFactory.populateBean(AbstractAutowireCapableBeanFactory.java:1350)
	at org.springframework.beans.factory.support.AbstractAutowireCapableBeanFactory.autowireBeanProperties(AbstractAutowireCapableBeanFactory.java:401)
	at org.springframework.test.context.support.DependencyInjectionTestExecutionListener.injectDependencies(DependencyInjectionTestExecutionListener.java:118)
	at org.springframework.test.context.support.DependencyInjectionTestExecutionListener.prepareTestInstance(DependencyInjectionTestExecutionListener.java:83)
	at org.springframework.boot.test.autoconfigure.SpringBootDependencyInjectionTestExecutionListener.prepareTestInstance(SpringBootDependencyInjectionTestExecutionListener.java:44)
	at org.springframework.test.context.TestContextManager.prepareTestInstance(TestContextManager.java:246)
	at org.springframework.test.context.junit4.SpringJUnit4ClassRunner.createTest(SpringJUnit4ClassRunner.java:227)
	at org.springframework.test.context.junit4.SpringJUnit4ClassRunner$1.runReflectiveCall(SpringJUnit4ClassRunner.java:289)
	at org.junit.internal.runners.model.ReflectiveCallable.run(ReflectiveCallable.java:12)
	at org.springframework.test.context.junit4.SpringJUnit4ClassRunner.methodBlock(SpringJUnit4ClassRunner.java:291)
	at org.springframework.test.context.junit4.SpringJUnit4ClassRunner.runChild(SpringJUnit4ClassRunner.java:246)
	at org.springframework.test.context.junit4.SpringJUnit4ClassRunner.runChild(SpringJUnit4ClassRunner.java:97)
	at org.junit.runners.ParentRunner$3.run(ParentRunner.java:290)
	at org.junit.runners.ParentRunner$1.schedule(ParentRunner.java:71)
	at org.junit.runners.ParentRunner.runChildren(ParentRunner.java:288)
	at org.junit.runners.ParentRunner.access$000(ParentRunner.java:58)
	at org.junit.runners.ParentRunner$2.evaluate(ParentRunner.java:268)
	at org.springframework.test.context.junit4.statements.RunBeforeTestClassCallbacks.evaluate(RunBeforeTestClassCallbacks.java:61)
	at org.springframework.test.context.junit4.statements.RunAfterTestClassCallbacks.evaluate(RunAfterTestClassCallbacks.java:70)
	at org.junit.runners.ParentRunner.run(ParentRunner.java:363)
	at org.springframework.test.context.junit4.SpringJUnit4ClassRunner.run(SpringJUnit4ClassRunner.java:190)
	at org.junit.runners.Suite.runChild(Suite.java:128)
	at org.junit.runners.Suite.runChild(Suite.java:27)
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
Caused by: org.springframework.beans.factory.NoSuchBeanDefinitionException: No qualifying bean of type 'guru.springframework.services.RecipeService' available: expected at least 1 bean which qualifies as autowire candidate. Dependency annotations: {@org.springframework.beans.factory.annotation.Autowired(required=true)}
	at org.springframework.beans.factory.support.DefaultListableBeanFactory.raiseNoMatchingBeanFound(DefaultListableBeanFactory.java:1509)
	at org.springframework.beans.factory.support.DefaultListableBeanFactory.doResolveDependency(DefaultListableBeanFactory.java:1104)
	at org.springframework.beans.factory.support.DefaultListableBeanFactory.resolveDependency(DefaultListableBeanFactory.java:1065)
	at org.springframework.beans.factory.annotation.AutowiredAnnotationBeanPostProcessor$AutowiredFieldElement.inject(AutowiredAnnotationBeanPostProcessor.java:584)
	... 36 more


 */