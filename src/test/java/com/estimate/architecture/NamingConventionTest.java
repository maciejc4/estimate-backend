package com.estimate.architecture;

import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.lang.ArchRule;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;

class NamingConventionTest {
    
    private static JavaClasses classes;
    
    @BeforeAll
    static void setup() {
        classes = new ClassFileImporter()
                .withImportOption(ImportOption.Predefined.DO_NOT_INCLUDE_TESTS)
                .importPackages("com.estimate");
    }
    
    @Test
    void useCasesShouldHaveProperNaming() {
        ArchRule rule = classes()
                .that().resideInAPackage("..application.usecase..")
                .and().areNotInterfaces()
                .should().haveSimpleNameEndingWith("Service");
        
        rule.check(classes);
    }
    
    @Test
    void controllersShouldHaveProperNaming() {
        ArchRule rule = classes()
                .that().resideInAPackage("..adapter.in.web..")
                .and().areNotInterfaces()
                .and().areNotAnnotations()
                .and().areNotEnums()
                .and().areTopLevelClasses()
                .and().haveSimpleNameNotContaining("$")
                .and().resideOutsideOfPackage("..dto..")
                .and().resideOutsideOfPackage("..exception..")
                .should().haveSimpleNameEndingWith("Controller");
        
        rule.check(classes);
    }
    
    @Test
    void repositoryAdaptersShouldHaveProperNaming() {
        ArchRule rule = classes()
                .that().resideInAPackage("..adapter.out.persistence..")
                .and().resideInAPackage("..adapter..")
                .and().areNotInterfaces()
                .and().areNotEnums()
                .and().areNotAnnotations()
                .and().haveSimpleNameNotContaining("Entity")
                .and().haveSimpleNameNotContaining("Mapper")
                .should().haveSimpleNameEndingWith("Adapter")
                .orShould().haveSimpleNameEndingWith("Repository");
        
        rule.check(classes);
    }
    
    @Test
    void useCasePortsShouldHaveProperNaming() {
        ArchRule rule = classes()
                .that().resideInAPackage("..domain.port.in..")
                .and().areInterfaces()
                .should().haveSimpleNameEndingWith("UseCase");
        
        rule.check(classes);
    }
    
    @Test
    void repositoryPortsShouldHaveProperNaming() {
        ArchRule rule = classes()
                .that().resideInAPackage("..domain.port.out..")
                .and().areInterfaces()
                .and().haveSimpleNameContaining("Repository")
                .should().haveSimpleNameEndingWith("Port");
        
        rule.check(classes);
    }
    
    @Test
    void commandsShouldHaveProperNaming() {
        ArchRule rule = classes()
                .that().resideInAPackage("..domain.port.in..")
                .and().areNotInterfaces()
                .and().areNotEnums()
                .and().areTopLevelClasses()
                .and().haveSimpleNameNotContaining("Builder")
                .and().haveSimpleNameNotEndingWith("Result")
                .should().haveSimpleNameEndingWith("Command");
        
        rule.check(classes);
    }
}
