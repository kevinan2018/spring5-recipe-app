package guru.springframework.domain;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import javax.persistence.*;
import java.util.Set;

@Data
@EqualsAndHashCode(exclude = {"recipes"})
@ToString(exclude = {"recipe"})
@Entity
public class Category {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String description;

    @ManyToMany(mappedBy = "categories")
    private Set<Recipe> recipes;

/*    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Set<Recipe> getRecipes() {
        return recipes;
    }

    public void setRecipes(Set<Recipe> recipes) {
        this.recipes = recipes;
    }*/
}

/*
Fix: exclude the bidirectional reference from HashCode implementation by lombok
Problem:  Recipes <--> Categories Many:Many relationship causes circular references!

org.hibernate.LazyInitializationException: failed to lazily initialize a collection of role: guru.springframework.domain.Category.recipes, could not initialize proxy - no Session
	at org.hibernate.collection.internal.AbstractPersistentCollection.throwLazyInitializationException(AbstractPersistentCollection.java:582) ~[hibernate-core-5.2.17.Final.jar:5.2.17.Final]
	at org.hibernate.collection.internal.AbstractPersistentCollection.withTemporarySessionIfNeeded(AbstractPersistentCollection.java:201) ~[hibernate-core-5.2.17.Final.jar:5.2.17.Final]
	at org.hibernate.collection.internal.AbstractPersistentCollection.initialize(AbstractPersistentCollection.java:561) ~[hibernate-core-5.2.17.Final.jar:5.2.17.Final]
	at org.hibernate.collection.internal.AbstractPersistentCollection.read(AbstractPersistentCollection.java:132) ~[hibernate-core-5.2.17.Final.jar:5.2.17.Final]
	at org.hibernate.collection.internal.PersistentSet.hashCode(PersistentSet.java:430) ~[hibernate-core-5.2.17.Final.jar:5.2.17.Final]
	at guru.springframework.domain.Category.hashCode(Category.java:9) ~[classes/:na]
	at java.util.HashMap.hash(HashMap.java:339) ~[na:1.8.0_171]
	at java.util.HashMap.put(HashMap.java:612) ~[na:1.8.0_171]
	at java.util.HashSet.add(HashSet.java:220) ~[na:1.8.0_171]
	at guru.springframework.bootstrap.RecipeBootstrap.getRecipes(RecipeBootstrap.java:163) ~[classes/:na]
	at guru.springframework.bootstrap.RecipeBootstrap.onApplicationEvent(RecipeBootstrap.java:33) ~[classes/:na]
	at guru.springframework.bootstrap.RecipeBootstrap.onApplicationEvent(RecipeBootstrap.java:17) ~[classes/:na]
	at org.springframework.context.event.SimpleApplicationEventMulticaster.doInvokeListener(SimpleApplicationEventMulticaster.java:172) ~[spring-context-5.0.7.RELEASE.jar:5.0.7.RELEASE]
	at org.springframework.context.event.SimpleApplicationEventMulticaster.invokeListener(SimpleApplicationEventMulticaster.java:165) ~[spring-context-5.0.7.RELEASE.jar:5.0.7.RELEASE]
	at org.springframework.context.event.SimpleApplicationEventMulticaster.multicastEvent(SimpleApplicationEventMulticaster.java:139) ~[spring-context-5.0.7.RELEASE.jar:5.0.7.RELEASE]
	at org.springframework.context.support.AbstractApplicationContext.publishEvent(AbstractApplicationContext.java:400) ~[spring-context-5.0.7.RELEASE.jar:5.0.7.RELEASE]
	at org.springframework.context.support.AbstractApplicationContext.publishEvent(AbstractApplicationContext.java:354) ~[spring-context-5.0.7.RELEASE.jar:5.0.7.RELEASE]
	at org.springframework.context.support.AbstractApplicationContext.finishRefresh(AbstractApplicationContext.java:888) ~[spring-context-5.0.7.RELEASE.jar:5.0.7.RELEASE]
	at org.springframework.boot.web.servlet.context.ServletWebServerApplicationContext.finishRefresh(ServletWebServerApplicationContext.java:161) ~[spring-boot-2.0.3.RELEASE.jar:2.0.3.RELEASE]
	at org.springframework.context.support.AbstractApplicationContext.refresh(AbstractApplicationContext.java:553) ~[spring-context-5.0.7.RELEASE.jar:5.0.7.RELEASE]
	at org.springframework.boot.web.servlet.context.ServletWebServerApplicationContext.refresh(ServletWebServerApplicationContext.java:140) ~[spring-boot-2.0.3.RELEASE.jar:2.0.3.RELEASE]
	at org.springframework.boot.SpringApplication.refresh(SpringApplication.java:759) [spring-boot-2.0.3.RELEASE.jar:2.0.3.RELEASE]
	at org.springframework.boot.SpringApplication.refreshContext(SpringApplication.java:395) [spring-boot-2.0.3.RELEASE.jar:2.0.3.RELEASE]
	at org.springframework.boot.SpringApplication.run(SpringApplication.java:327) [spring-boot-2.0.3.RELEASE.jar:2.0.3.RELEASE]
	at org.springframework.boot.SpringApplication.run(SpringApplication.java:1255) [spring-boot-2.0.3.RELEASE.jar:2.0.3.RELEASE]
	at org.springframework.boot.SpringApplication.run(SpringApplication.java:1243) [spring-boot-2.0.3.RELEASE.jar:2.0.3.RELEASE]
	at guru.springframework.Spring5RecipeAppApplication.main(Spring5RecipeAppApplication.java:10) [classes/:na]
	at sun.reflect.NativeMethodAccessorImpl.invoke0(Native Method) ~[na:1.8.0_171]
	at sun.reflect.NativeMethodAccessorImpl.invoke(NativeMethodAccessorImpl.java:62) ~[na:1.8.0_171]
	at sun.reflect.DelegatingMethodAccessorImpl.invoke(DelegatingMethodAccessorImpl.java:43) ~[na:1.8.0_171]
	at java.lang.reflect.Method.invoke(Method.java:498) ~[na:1.8.0_171]
	at org.springframework.boot.devtools.restart.RestartLauncher.run(RestartLauncher.java:49) [spring-boot-devtools-2.0.3.RELEASE.jar:2.0.3.RELEASE]

 */