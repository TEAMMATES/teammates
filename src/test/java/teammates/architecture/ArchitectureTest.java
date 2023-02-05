package teammates.architecture;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;

import org.testng.annotations.Test;

import com.tngtech.archunit.base.DescribedPredicate;
import com.tngtech.archunit.core.domain.JavaClass;
import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.importer.ClassFileImporter;

/**
 * Checks for the system's architectural integrity.
 */
public class ArchitectureTest {

    private static final JavaClasses ALL_CLASSES = forClasses("teammates");

    private static final String COMMON_PACKAGE = "teammates.common";

    private static final String STORAGE_PACKAGE = "teammates.storage";
    private static final String STORAGE_API_PACKAGE = STORAGE_PACKAGE + ".api";
    private static final String STORAGE_ENTITY_PACKAGE = STORAGE_PACKAGE + ".entity";
    private static final String STORAGE_SEARCH_PACKAGE = STORAGE_PACKAGE + ".search";

    private static final String LOGIC_PACKAGE = "teammates.logic";

    private static final String LOGIC_CORE_PACKAGE = LOGIC_PACKAGE + ".core";
    private static final String LOGIC_API_PACKAGE = LOGIC_PACKAGE + ".api";
    private static final String LOGIC_EXTERNAL_PACKAGE = LOGIC_PACKAGE + ".external";

    private static final String UI_PACKAGE = "teammates.ui";
    private static final String UI_WEBAPI_PACKAGE = UI_PACKAGE + ".webapi";
    private static final String UI_SERVLETS_PACKAGE = UI_PACKAGE + ".servlets";
    private static final String UI_OUTPUT_PACKAGE = UI_PACKAGE + ".output";
    private static final String UI_REQUEST_PACKAGE = UI_PACKAGE + ".request";

    private static final String MAIN_PACKAGE = "teammates.main";

    private static final String TEST_DRIVER_PACKAGE = "teammates.test";

    private static final String E2E_PACKAGE = "teammates.e2e";

    private static final String E2E_CASES_PACKAGE = E2E_PACKAGE + ".cases";
    private static final String E2E_PAGEOBJECTS_PACKAGE = E2E_PACKAGE + ".pageobjects";
    private static final String E2E_UTIL_PACKAGE = E2E_PACKAGE + ".util";

    private static final String LNP_PACKAGE = "teammates.lnp";

    private static final String LNP_CASES_PACKAGE = LNP_PACKAGE + ".cases";
    private static final String LNP_UTIL_PACKAGE = LNP_PACKAGE + ".util";

    private static final String CLIENT_PACKAGE = "teammates.client";
    private static final String CLIENT_CONNECTOR_PACKAGE = CLIENT_PACKAGE + ".connector";
    private static final String CLIENT_SCRIPTS_PACKAGE = CLIENT_PACKAGE + ".scripts";
    private static final String CLIENT_UTIL_PACKAGE = CLIENT_PACKAGE + ".util";

    private static final String TEST_FILE_SUFFIX = "Test";

    private static String includeSubpackages(String pack) {
        return pack + "..";
    }

    private static JavaClasses forClasses(String... packageNames) {
        return new ClassFileImporter().importPackages(packageNames);
    }

    @Test
    public void testArchitecture_uiShouldNotTouchStorage() {
        noClasses().that().resideInAPackage(includeSubpackages(UI_PACKAGE))
                .should().accessClassesThat().resideInAPackage(includeSubpackages(STORAGE_PACKAGE))
                .check(forClasses(UI_PACKAGE, STORAGE_PACKAGE));
    }

    @Test
    public void testArchitecture_mainShouldNotTouchProductionCodeExceptCommon() {
        noClasses().that().resideInAPackage(MAIN_PACKAGE)
                .should().accessClassesThat().resideInAPackage(includeSubpackages(STORAGE_PACKAGE))
                .orShould().accessClassesThat().resideInAPackage(includeSubpackages(LOGIC_PACKAGE))
                .orShould().accessClassesThat(new DescribedPredicate<>("") {
                    @Override
                    public boolean apply(JavaClass input) {
                        return input.getPackageName().startsWith(UI_PACKAGE)
                                && !input.getSimpleName().endsWith("Servlet");
                    }
                }).check(forClasses(MAIN_PACKAGE));
    }

    @Test
    public void testArchitecture_logicShouldNotTouchUi() {
        noClasses().that().resideInAPackage(includeSubpackages(LOGIC_PACKAGE))
                .and().doNotHaveSimpleName("TaskQueuer")
                .should().accessClassesThat().resideInAPackage(includeSubpackages(UI_PACKAGE))
                .check(forClasses(LOGIC_PACKAGE, UI_PACKAGE));

        noClasses().that().resideInAPackage(includeSubpackages(LOGIC_PACKAGE))
                .and().haveSimpleName("TaskQueuer")
                .should().accessClassesThat().resideInAPackage(includeSubpackages(UI_WEBAPI_PACKAGE))
                .orShould().accessClassesThat().resideInAPackage(includeSubpackages(UI_OUTPUT_PACKAGE))
                .check(forClasses(LOGIC_PACKAGE, UI_PACKAGE));
    }

    @Test
    public void testArchitecture_storageShouldNotTouchLogic() {
        noClasses().that().resideInAPackage(includeSubpackages(STORAGE_PACKAGE))
                .should().accessClassesThat().resideInAPackage(includeSubpackages(LOGIC_PACKAGE))
                .check(forClasses(STORAGE_PACKAGE, LOGIC_PACKAGE));
    }

    @Test
    public void testArchitecture_storageShouldNotTouchUi() {
        noClasses().that().resideInAPackage(includeSubpackages(STORAGE_PACKAGE))
                .should().accessClassesThat().resideInAPackage(includeSubpackages(UI_PACKAGE))
                .check(forClasses(STORAGE_PACKAGE, UI_PACKAGE));
    }

    @Test
    public void testArchitecture_commonShouldNotTouchLogic() {
        noClasses().that().resideInAPackage(includeSubpackages(COMMON_PACKAGE))
                .should().accessClassesThat().resideInAPackage(includeSubpackages(LOGIC_PACKAGE))
                .check(forClasses(COMMON_PACKAGE, LOGIC_PACKAGE));
    }

    @Test
    public void testArchitecture_commonShouldNotTouchStorage() {
        noClasses().that().resideInAPackage(includeSubpackages(COMMON_PACKAGE))
                .should().accessClassesThat().resideInAPackage(includeSubpackages(STORAGE_API_PACKAGE))
                .orShould().accessClassesThat().resideInAPackage(includeSubpackages(STORAGE_SEARCH_PACKAGE))
                .check(forClasses(COMMON_PACKAGE, STORAGE_PACKAGE));

        noClasses().that().resideInAPackage(includeSubpackages(COMMON_PACKAGE))
                .and().resideOutsideOfPackage(includeSubpackages(COMMON_PACKAGE + ".datatransfer.attributes"))
                .should().accessClassesThat().resideInAPackage(includeSubpackages(STORAGE_ENTITY_PACKAGE))
                .check(forClasses(COMMON_PACKAGE, STORAGE_PACKAGE));
    }

    @Test
    public void testArchitecture_commonShouldNotTouchUi() {
        noClasses().that().resideInAPackage(includeSubpackages(COMMON_PACKAGE))
                .should().accessClassesThat().resideInAPackage(includeSubpackages(UI_PACKAGE))
                .check(forClasses(COMMON_PACKAGE, UI_PACKAGE));
    }

    @Test
    public void testArchitecture_uiShouldNotTouchLogicExceptForApi() {
        noClasses().that().resideInAPackage(includeSubpackages(UI_PACKAGE))
                .and().resideOutsideOfPackage(includeSubpackages(UI_WEBAPI_PACKAGE))
                .should().accessClassesThat().resideInAPackage(includeSubpackages(LOGIC_API_PACKAGE))
                .check(forClasses(UI_PACKAGE, LOGIC_PACKAGE));

        noClasses().that().resideInAPackage(includeSubpackages(UI_PACKAGE))
                .should().accessClassesThat().resideInAPackage(includeSubpackages(LOGIC_CORE_PACKAGE))
                .check(forClasses(UI_PACKAGE, LOGIC_PACKAGE));
    }

    @Test
    public void testArchitecture_ui_onlyWebApiCanTouchOutput() {
        noClasses().that().resideInAPackage(includeSubpackages(UI_PACKAGE))
                .and().resideOutsideOfPackage(includeSubpackages(UI_WEBAPI_PACKAGE))
                .and().resideOutsideOfPackage(includeSubpackages(UI_REQUEST_PACKAGE))
                .and().resideOutsideOfPackage(includeSubpackages(UI_OUTPUT_PACKAGE))
                .should().accessClassesThat().resideInAPackage(includeSubpackages(UI_OUTPUT_PACKAGE))
                .check(forClasses(UI_PACKAGE));
    }

    @Test
    public void testArchitecture_ui_onlyWebApiCanTouchRequest() {
        noClasses().that().resideInAPackage(includeSubpackages(UI_PACKAGE))
                .and().resideOutsideOfPackage(includeSubpackages(UI_WEBAPI_PACKAGE))
                .and().resideOutsideOfPackage(includeSubpackages(UI_REQUEST_PACKAGE))
                .should().accessClassesThat().resideInAPackage(includeSubpackages(UI_REQUEST_PACKAGE))
                .check(forClasses(UI_PACKAGE));
    }

    @Test
    public void testArchitecture_ui_apiRequestCanOnlyTouchRequestAndOutput() {
        noClasses().that().resideInAPackage(includeSubpackages(UI_REQUEST_PACKAGE))
                .should().accessClassesThat().resideInAPackage(includeSubpackages(UI_WEBAPI_PACKAGE))
                .check(forClasses(UI_PACKAGE));
    }

    @Test
    public void testArchitecture_ui_apiOutputCanOnlyTouchOutput() {
        noClasses().that().resideInAPackage(includeSubpackages(UI_OUTPUT_PACKAGE))
                .should().accessClassesThat().resideInAPackage(includeSubpackages(UI_WEBAPI_PACKAGE))
                .orShould().accessClassesThat().resideInAPackage(includeSubpackages(UI_REQUEST_PACKAGE))
                .check(forClasses(UI_PACKAGE));
    }

    @Test
    public void testArchitecture_ui_controllerShouldBeSelfContained() {
        noClasses().that().resideInAPackage(includeSubpackages(UI_PACKAGE))
                .and().resideOutsideOfPackage(includeSubpackages(UI_WEBAPI_PACKAGE))
                .and().resideOutsideOfPackage(includeSubpackages(UI_SERVLETS_PACKAGE))
                .should().accessClassesThat().resideInAPackage(includeSubpackages(UI_WEBAPI_PACKAGE))
                .check(forClasses(UI_PACKAGE));

        noClasses().that().resideInAPackage(includeSubpackages(UI_PACKAGE))
                .and().resideOutsideOfPackage(includeSubpackages(UI_SERVLETS_PACKAGE))
                .should().accessClassesThat().resideInAPackage(includeSubpackages(UI_SERVLETS_PACKAGE))
                .check(forClasses(UI_PACKAGE));
    }

    @Test
    public void testArchitecture_logic_logicCanOnlyAccessStorageApi() {
        noClasses().that().resideInAPackage(includeSubpackages(LOGIC_PACKAGE))
                .and().resideOutsideOfPackage(includeSubpackages(LOGIC_CORE_PACKAGE))
                .should().accessClassesThat().resideInAPackage(includeSubpackages(STORAGE_PACKAGE))
                .check(forClasses(LOGIC_PACKAGE, STORAGE_PACKAGE));

        noClasses().that().resideInAPackage(includeSubpackages(LOGIC_CORE_PACKAGE))
                .should().accessClassesThat().resideInAPackage(includeSubpackages(STORAGE_ENTITY_PACKAGE))
                .orShould().accessClassesThat().resideInAPackage(includeSubpackages(STORAGE_SEARCH_PACKAGE))
                .check(forClasses(LOGIC_PACKAGE, STORAGE_PACKAGE));
    }

    @Test
    public void testArchitecture_logic_coreLogicCanOnlyAccessItsCorrespondingDb() {
        for (JavaClass coreLogicClass : forClasses(LOGIC_CORE_PACKAGE)) {
            String logicClassName = coreLogicClass.getSimpleName();
            if ("DataBundleLogic".equals(logicClassName)) {
                continue;
            }
            if (logicClassName.endsWith(TEST_FILE_SUFFIX)) {
                continue;
            }
            String dbClassName = logicClassName.replace("Logic", "Db");

            noClasses()
                    .that().resideInAPackage(includeSubpackages(LOGIC_CORE_PACKAGE))
                    .and().doNotHaveSimpleName(logicClassName)
                    .and().doNotHaveSimpleName(logicClassName + TEST_FILE_SUFFIX)
                    .and().doNotHaveSimpleName("DataBundleLogic")
                    .should()
                    .accessClassesThat(new DescribedPredicate<>("") {
                        @Override
                        public boolean apply(JavaClass input) {
                            return input.getPackageName().startsWith(STORAGE_API_PACKAGE)
                                    && input.getSimpleName().equals(dbClassName);
                        }
                    })
                    .check(forClasses(LOGIC_CORE_PACKAGE, STORAGE_API_PACKAGE));
        }
    }

    @Test
    public void testArchitecture_logic_coreLogicShouldNotTouchApiLogic() {
        noClasses().that().resideInAPackage(includeSubpackages(LOGIC_CORE_PACKAGE))
                .should().accessClassesThat().resideInAPackage(includeSubpackages(LOGIC_API_PACKAGE))
                .check(forClasses(LOGIC_PACKAGE));
    }

    @Test
    public void testArchitecture_logic_coreLogicShouldNotTouchExternalLogic() {
        noClasses().that().resideInAPackage(includeSubpackages(LOGIC_CORE_PACKAGE))
                .should().accessClassesThat().resideInAPackage(includeSubpackages(LOGIC_EXTERNAL_PACKAGE))
                .check(forClasses(LOGIC_PACKAGE));
    }

    @Test
    public void testArchitecture_logic_externalLogicShouldNotTouchCoreLogic() {
        noClasses().that().resideInAPackage(includeSubpackages(LOGIC_EXTERNAL_PACKAGE))
                .should().accessClassesThat().resideInAPackage(includeSubpackages(LOGIC_CORE_PACKAGE))
                .check(forClasses(LOGIC_PACKAGE));
    }

    @Test
    public void testArchitecture_storage_storageSearchShouldNotTouchStorageEntity() {
        noClasses().that().resideInAPackage(includeSubpackages(STORAGE_SEARCH_PACKAGE))
                .should().accessClassesThat().resideInAPackage(includeSubpackages(STORAGE_ENTITY_PACKAGE))
                .check(forClasses(STORAGE_PACKAGE));
    }

    @Test
    public void testArchitecture_storage_storageEntityShouldNotTouchOtherStoragePackages() {
        noClasses().that().resideInAPackage(includeSubpackages(STORAGE_ENTITY_PACKAGE))
                .should().accessClassesThat(new DescribedPredicate<>("") {
                    @Override
                    public boolean apply(JavaClass input) {
                        return input.getPackageName().startsWith(STORAGE_PACKAGE)
                                && !STORAGE_ENTITY_PACKAGE.equals(input.getPackageName());
                    }
                }).check(forClasses(STORAGE_PACKAGE));
    }

    @Test
    public void testArchitecture_testClasses_testCasesShouldBeIndependent() {
        noClasses().that().haveSimpleNameEndingWith(TEST_FILE_SUFFIX)
                .should().accessClassesThat(new DescribedPredicate<>("") {
                    @Override
                    public boolean apply(JavaClass input) {
                        return input.getSimpleName().endsWith(TEST_FILE_SUFFIX)
                                && !input.getSimpleName().startsWith("Base");
                    }
                }).check(ALL_CLASSES);
    }

    @Test
    public void testArchitecture_testClasses_driverShouldNotHaveAnyDependency() {
        noClasses().that().resideInAPackage(includeSubpackages(TEST_DRIVER_PACKAGE))
                .should().accessClassesThat().haveSimpleNameEndingWith(TEST_FILE_SUFFIX)
                .check(forClasses(TEST_DRIVER_PACKAGE));

        noClasses().that().resideInAPackage(includeSubpackages(TEST_DRIVER_PACKAGE))
                .should().accessClassesThat(new DescribedPredicate<>("") {
                    @Override
                    public boolean apply(JavaClass input) {
                        return input.getPackageName().startsWith(STORAGE_PACKAGE)
                                && !"OfyHelper".equals(input.getSimpleName())
                                && !"AccountRequestSearchManager".equals(input.getSimpleName())
                                && !"InstructorSearchManager".equals(input.getSimpleName())
                                && !"StudentSearchManager".equals(input.getSimpleName())
                                && !"SearchManagerFactory".equals(input.getSimpleName());
                    }
                })
                .orShould().accessClassesThat(new DescribedPredicate<>("") {
                    @Override
                    public boolean apply(JavaClass input) {
                        return input.getPackageName().startsWith(LOGIC_CORE_PACKAGE)
                                && !"LogicStarter".equals(input.getSimpleName());
                    }
                })
                .orShould().accessClassesThat(new DescribedPredicate<>("") {
                    @Override
                    public boolean apply(JavaClass input) {
                        return input.getPackageName().startsWith(UI_WEBAPI_PACKAGE)
                                && !"Action".equals(input.getSimpleName())
                                && !"ActionFactory".equals(input.getSimpleName());
                    }
                })
                .check(forClasses(TEST_DRIVER_PACKAGE));
    }

    @Test
    public void testArchitecture_e2e_e2eShouldBeSelfContained() {
        noClasses().that().resideOutsideOfPackage(includeSubpackages(E2E_PACKAGE))
                .should().accessClassesThat().resideInAPackage(includeSubpackages(E2E_PACKAGE))
                .check(ALL_CLASSES);
    }

    @Test
    public void testArchitecture_e2e_e2eShouldNotTouchProductionCodeExceptCommon() {
        noClasses().that().resideInAPackage(includeSubpackages(E2E_PACKAGE))
                .should().accessClassesThat().resideInAPackage(includeSubpackages(STORAGE_PACKAGE))
                .orShould().accessClassesThat().resideInAPackage(includeSubpackages(LOGIC_PACKAGE))
                .orShould().accessClassesThat().resideInAPackage(includeSubpackages(UI_PACKAGE))
                .check(forClasses(E2E_PACKAGE));

        noClasses().that().resideInAPackage(includeSubpackages(E2E_PACKAGE))
                .should().accessClassesThat().haveSimpleName("Config")
                .check(forClasses(E2E_PACKAGE));
    }

    @Test
    public void testArchitecture_e2e_e2eTestCasesShouldBeIndependentOfEachOther() {
        noClasses().that(new DescribedPredicate<>("") {
            @Override
            public boolean apply(JavaClass input) {
                return input.getPackageName().startsWith(E2E_CASES_PACKAGE) && !input.isInnerClass();
            }
        }).should().accessClassesThat(new DescribedPredicate<>("") {
            @Override
            public boolean apply(JavaClass input) {
                return input.getPackageName().startsWith(E2E_CASES_PACKAGE)
                        && !input.getSimpleName().startsWith("Base")
                        && !input.isInnerClass();
            }
        }).check(forClasses(E2E_CASES_PACKAGE));
    }

    @Test
    public void testArchitecture_e2e_onlyE2ETestsCanAccessPageObjects() {
        noClasses().that().resideInAPackage(includeSubpackages(E2E_PACKAGE))
                .and().resideOutsideOfPackage(includeSubpackages(E2E_PAGEOBJECTS_PACKAGE))
                .and().resideOutsideOfPackage(includeSubpackages(E2E_CASES_PACKAGE))
                .should().accessClassesThat().resideInAPackage(includeSubpackages(E2E_PAGEOBJECTS_PACKAGE))
                .check(forClasses(E2E_PACKAGE));
    }

    @Test
    public void testArchitecture_e2e_utilShouldNotHaveAnyDependency() {
        noClasses().that().resideInAPackage(includeSubpackages(E2E_UTIL_PACKAGE))
                .should().accessClassesThat(new DescribedPredicate<>("") {
                    @Override
                    public boolean apply(JavaClass input) {
                        return input.getPackageName().startsWith(E2E_PACKAGE)
                                && !E2E_UTIL_PACKAGE.equals(input.getPackageName());
                    }
                }).check(forClasses(E2E_PACKAGE));
    }

    @Test
    public void testArchitecture_lnp_lnpShouldBeSelfContained() {
        noClasses().that().resideOutsideOfPackage(includeSubpackages(LNP_PACKAGE))
                .should().accessClassesThat().resideInAPackage(includeSubpackages(LNP_PACKAGE))
                .check(ALL_CLASSES);
    }

    @Test
    public void testArchitecture_lnp_lnpShouldNotTouchProductionCodeExceptCommonAndRequests() {
        noClasses().that().resideInAPackage(includeSubpackages(LNP_PACKAGE))
                .should().accessClassesThat().resideInAPackage(includeSubpackages(STORAGE_PACKAGE))
                .orShould().accessClassesThat().resideInAPackage(includeSubpackages(LOGIC_PACKAGE))
                .orShould().accessClassesThat(new DescribedPredicate<>("") {
                    @Override
                    public boolean apply(JavaClass input) {
                        return input.getPackageName().startsWith(UI_PACKAGE)
                                && !UI_OUTPUT_PACKAGE.equals(input.getPackageName())
                                && !UI_REQUEST_PACKAGE.equals(input.getPackageName());
                    }
                }).check(ALL_CLASSES);
    }

    @Test
    public void testArchitecture_lnp_lnpTestCasesShouldBeIndependentOfEachOther() {
        noClasses().that(new DescribedPredicate<>("") {
            @Override
            public boolean apply(JavaClass input) {
                return input.getPackageName().startsWith(LNP_CASES_PACKAGE) && !input.isInnerClass();
            }
        }).should().accessClassesThat(new DescribedPredicate<>("") {
            @Override
            public boolean apply(JavaClass input) {
                return input.getPackageName().startsWith(LNP_CASES_PACKAGE)
                        && !input.getSimpleName().startsWith("Base")
                        && !input.isInnerClass();
            }
        }).check(forClasses(LNP_CASES_PACKAGE));
    }

    @Test
    public void testArchitecture_lnp_lnpShouldNotHaveAnyDependency() {
        noClasses().that().resideInAPackage(includeSubpackages(LNP_UTIL_PACKAGE))
                .should().accessClassesThat(new DescribedPredicate<>("") {
                    @Override
                    public boolean apply(JavaClass input) {
                        return input.getPackageName().startsWith(LNP_PACKAGE)
                                && !LNP_UTIL_PACKAGE.equals(input.getPackageName());
                    }
                }).check(forClasses(LNP_PACKAGE));
    }

    @Test
    public void testArchitecture_client_clientShouldBeSelfContained() {
        noClasses().that().resideOutsideOfPackage(includeSubpackages(CLIENT_PACKAGE))
                .should().accessClassesThat().resideInAPackage(includeSubpackages(CLIENT_PACKAGE))
                .check(ALL_CLASSES);
    }

    @Test
    public void testArchitecture_client_clientShouldNotTouchUiComponent() {
        noClasses().that().resideInAPackage(includeSubpackages(CLIENT_PACKAGE))
                .should().accessClassesThat().resideInAPackage(includeSubpackages(UI_PACKAGE))
                .check(forClasses(CLIENT_PACKAGE, UI_PACKAGE));
    }

    @Test
    public void testArchitecture_client_connectorShouldNotTouchScripts() {
        noClasses().that().resideInAPackage(includeSubpackages(CLIENT_CONNECTOR_PACKAGE))
                .should().accessClassesThat().resideInAPackage(includeSubpackages(CLIENT_SCRIPTS_PACKAGE))
                .check(forClasses(CLIENT_PACKAGE));
    }

    @Test
    public void testArchitecture_client_utilShouldNotHaveAnyDependency() {
        noClasses().that().resideInAPackage(includeSubpackages(CLIENT_UTIL_PACKAGE))
                .should().accessClassesThat(new DescribedPredicate<>("") {
                    @Override
                    public boolean apply(JavaClass input) {
                        return input.getPackageName().startsWith(CLIENT_PACKAGE)
                                && !CLIENT_UTIL_PACKAGE.equals(input.getPackageName());
                    }
                }).check(forClasses(CLIENT_PACKAGE));
    }

    @Test
    public void testArchitecture_externalApi_loggingApiCanOnlyBeAccessedByLogger() {
        noClasses().that().doNotHaveSimpleName("Logger")
                .and().doNotHaveSimpleName("StdOutConsoleHandler")
                .should().accessClassesThat().resideInAPackage("java.util.logging..")
                .check(ALL_CLASSES);
    }

    @Test
    public void testArchitecture_externalApi_solrApiCanOnlyBeAccessedBySearchManagerClasses() {
        noClasses().that().doNotHaveSimpleName("SearchManager")
                .and().doNotHaveSimpleName("AccountRequestSearchManager")
                .and().doNotHaveSimpleName("InstructorSearchManager")
                .and().doNotHaveSimpleName("StudentSearchManager")
                .should().accessClassesThat().resideInAPackage("org.apache.solr..")
                .check(ALL_CLASSES);
    }

    @Test
    public void testArchitecture_externalApi_cloudTasksApiCanOnlyBeAccessedByCloudTasksService() {
        noClasses().that().doNotHaveSimpleName("GoogleCloudTasksService")
                .should().accessClassesThat().resideInAPackage("com.google.cloud.tasks.v2..")
                .check(ALL_CLASSES);
    }

    @Test
    public void testArchitecture_externalApi_cloudLoggingApiCanOnlyBeAccessedByCloudLoggingService() {
        noClasses().that().doNotHaveSimpleName("GoogleCloudLoggingService")
                .should().accessClassesThat().resideInAPackage("com.google.cloud.logging..")
                .check(ALL_CLASSES);
    }

    @Test
    public void testArchitecture_externalApi_objectifyApiCanOnlyBeAccessedBySomePackages() {
        noClasses().that().resideOutsideOfPackage(includeSubpackages(STORAGE_API_PACKAGE))
                .and().resideOutsideOfPackage(includeSubpackages(STORAGE_ENTITY_PACKAGE))
                .and().resideOutsideOfPackage(includeSubpackages(CLIENT_CONNECTOR_PACKAGE))
                .and().resideOutsideOfPackage(includeSubpackages(CLIENT_SCRIPTS_PACKAGE))
                .and().doNotHaveSimpleName("BaseTestCaseWithLocalDatabaseAccess")
                .should().accessClassesThat().resideInAPackage("com.googlecode.objectify..")
                .check(ALL_CLASSES);
    }

    @Test
    public void testArchitecture_externalApi_servletApiCanOnlyBeAccessedBySomePackages() {
        noClasses().that().doNotHaveSimpleName("HttpRequestHelper")
                .and().doNotHaveSimpleName("OfyHelper")
                .and().doNotHaveSimpleName("MockFilterChain")
                .and().doNotHaveSimpleName("MockHttpServletRequest")
                .and().doNotHaveSimpleName("MockHttpServletResponse")
                .and().doNotHaveSimpleName("MockPart")
                .and().resideOutsideOfPackage(includeSubpackages(UI_WEBAPI_PACKAGE))
                .should().accessClassesThat().haveFullyQualifiedName("javax.servlet..")
                .check(ALL_CLASSES);
    }

    @Test
    public void testArchitecture_externalApi_jettyApiCanOnlyBeAccessedBySomePackages() {
        noClasses().that().resideOutsideOfPackage(MAIN_PACKAGE)
                .should().accessClassesThat().haveFullyQualifiedName("org.eclipse.jetty..")
                .check(ALL_CLASSES);
    }

    @Test
    public void testArchitecture_externalApi_assertionApiCanOnlyBeAccessedBySomePackages() {
        noClasses().that().resideOutsideOfPackage(includeSubpackages(E2E_PAGEOBJECTS_PACKAGE))
                .and().doNotHaveSimpleName("BaseTestCase")
                .and().doNotHaveSimpleName("AssertHelper")
                .and().doNotHaveSimpleName("EmailChecker")
                .should().accessClassesThat().haveFullyQualifiedName("org.junit.Assert")
                .check(ALL_CLASSES);
    }

    @Test
    public void testArchitecture_externalApi_seleniumApiCanOnlyBeAccessedByPageObjects() {
        noClasses().that().resideOutsideOfPackage(E2E_PAGEOBJECTS_PACKAGE)
                .should().accessClassesThat().resideInAPackage("org.openqa.selenium..")
                .check(ALL_CLASSES);
    }

}
