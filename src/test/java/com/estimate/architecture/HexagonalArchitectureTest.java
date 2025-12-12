package com.estimate.architecture;

import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.lang.ArchRule;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;
import static com.tngtech.archunit.library.Architectures.layeredArchitecture;

class HexagonalArchitectureTest {
    
    private static JavaClasses classes;
    
    @BeforeAll
    static void setup() {
        classes = new ClassFileImporter()
                .withImportOption(ImportOption.Predefined.DO_NOT_INCLUDE_TESTS)
                .importPackages("com.estimate");
    }
    
    @Test
    void domainLayerShouldNotDependOnOtherLayers() {
        ArchRule rule = noClasses()
                .that().resideInAPackage("..domain..")
                .should().dependOnClassesThat().resideInAnyPackage(
                        "..application..",
                        "..adapter..",
                        "..infrastructure.."
                );
        
        rule.check(classes);
    }
    
    @Test
    void applicationLayerShouldOnlyDependOnDomain() {
        ArchRule rule = noClasses()
                .that().resideInAPackage("..application..")
                .should().dependOnClassesThat().resideInAnyPackage(
                        "..adapter..",
                        "..infrastructure.."
                );
        
        rule.check(classes);
    }
    
    @Test
    void adaptersShouldNotDependOnEachOther() {
        ArchRule rule = noClasses()
                .that().resideInAPackage("..adapter.in..")
                .should().dependOnClassesThat().resideInAPackage("..adapter.out..");
        
        rule.check(classes);
    }
    
    @Test
    void layeredArchitectureShouldBeRespected() {
        layeredArchitecture()
                .consideringAllDependencies()
                .layer("Domain").definedBy("..domain..")
                .layer("Application").definedBy("..application..")
                .layer("Adapter").definedBy("..adapter..")
                .layer("Infrastructure").definedBy("..infrastructure..")
                
                .whereLayer("Domain").mayOnlyBeAccessedByLayers("Application", "Adapter", "Infrastructure")
                .whereLayer("Application").mayOnlyBeAccessedByLayers("Adapter", "Infrastructure")
                .whereLayer("Adapter").mayOnlyBeAccessedByLayers("Infrastructure")
                
                .check(classes);
    }
    
    @Test
    void portsShouldBeInterfaces() {
        ArchRule rule = classes()
                .that().resideInAPackage("..domain.port..")
                .and().haveSimpleNameNotContaining("Builder")
                .and().haveSimpleNameNotContaining("Command")
                .and().haveSimpleNameNotContaining("Result")
                .and().areTopLevelClasses()
                .should().beInterfaces();
        
        rule.check(classes);
    }
    
    @Test
    void useCasesShouldBeInApplicationLayer() {
        ArchRule rule = classes()
                .that().haveSimpleNameEndingWith("Service")
                .and().resideInAPackage("..usecase..")
                .should().resideInAPackage("..application.usecase..");
        
        rule.check(classes);
    }
}
