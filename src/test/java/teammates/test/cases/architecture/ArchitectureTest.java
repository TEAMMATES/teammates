package teammates.test.cases.architecture;

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

    @Deprecated
    private static final String LEGACY_UI_CONTROLLER_PACKAGE = "teammates.ui.controller";
    @Deprecated
    private static final String LEGACY_PAGEOBJECT_PACKAGE = "teammates.test.pageobjects";
    @Deprecated
    private static final String LEGACY_BROWSERTESTS_PACKAGE = "teammates.test.cases.browsertests";

    private static final JavaClasses ALL_CLASSES = new ClassFileImporter().importPackages("teammates");

    private static final String COMMON_PACKAGE = "teammates.common";
    private static final JavaClasses COMMON_CLASSES =
            new ClassFileImporter().importPackages(COMMON_PACKAGE);

    private static final String STORAGE_PACKAGE = "teammates.storage";
    private static final JavaClasses STORAGE_CLASSES =
            new ClassFileImporter().importPackages(STORAGE_PACKAGE);

    private static final String STORAGE_API_PACKAGE = STORAGE_PACKAGE + ".api";

    private static final String STORAGE_ENTITY_PACKAGE = STORAGE_PACKAGE + ".entity";
    private static final JavaClasses STORAGE_ENTITY_CLASSES =
            new ClassFileImporter().importPackages(STORAGE_ENTITY_PACKAGE);

    private static final String STORAGE_SEARCH_PACKAGE = STORAGE_PACKAGE + ".search";
    private static final JavaClasses STORAGE_SEARCH_CLASSES =
            new ClassFileImporter().importPackages(STORAGE_SEARCH_PACKAGE);

    private static final String LOGIC_PACKAGE = "teammates.logic";
    private static final JavaClasses LOGIC_CLASSES =
            new ClassFileImporter().importPackages(LOGIC_PACKAGE);

    private static final String LOGIC_CORE_PACKAGE = LOGIC_PACKAGE + ".core";
    private static final JavaClasses LOGIC_CORE_CLASSES =
            new ClassFileImporter().importPackages(LOGIC_CORE_PACKAGE);

    private static final String LOGIC_API_PACKAGE = LOGIC_PACKAGE + ".api";

    private static final String UI_PACKAGE = "teammates.ui";
    private static final JavaClasses UI_CLASSES =
            new ClassFileImporter().importPackages(UI_PACKAGE);

    private static final String UI_AUTOMATED_PACKAGE = UI_PACKAGE + ".automated";
    private static final JavaClasses UI_AUTOMATED_CLASSES =
            new ClassFileImporter().importPackages(UI_AUTOMATED_PACKAGE);

    private static final String UI_WEBAPI_PACKAGE = UI_PACKAGE + ".webapi.action";
    private static final JavaClasses UI_WEBAPI_CLASSES =
            new ClassFileImporter().importPackages(UI_WEBAPI_PACKAGE);

    private static final String UI_OUTPUT_PACKAGE = UI_PACKAGE + ".webapi.output";
    private static final JavaClasses UI_OUTPUT_CLASSES =
            new ClassFileImporter().importPackages(UI_OUTPUT_PACKAGE);

    private static final String UI_REQUEST_PACKAGE = UI_PACKAGE + ".webapi.request";
    private static final JavaClasses UI_REQUEST_CLASSES =
            new ClassFileImporter().importPackages(UI_REQUEST_PACKAGE);

    private static final String TEST_PACKAGE = "teammates.test";
    private static final JavaClasses TEST_CLASSES =
            new ClassFileImporter().importPackages(TEST_PACKAGE);

    private static final String TEST_CASES_PACKAGE = TEST_PACKAGE + ".cases";
    private static final JavaClasses TEST_CASES =
            new ClassFileImporter().importPackages(TEST_CASES_PACKAGE);

    private static final String TEST_DRIVER_PACKAGE = TEST_PACKAGE + ".driver";
    private static final JavaClasses TEST_DRIVER_CLASSES =
            new ClassFileImporter().importPackages(TEST_DRIVER_PACKAGE);

    private static final String E2E_PACKAGE = "teammates.e2e";
    private static final JavaClasses E2E_CLASSES =
            new ClassFileImporter().importPackages(E2E_PACKAGE);

    private static final String E2E_CASES_PACKAGE = E2E_PACKAGE + ".cases";
    private static final JavaClasses E2E_TEST_CASES =
            new ClassFileImporter().importPackages(E2E_CASES_PACKAGE);

    private static final String E2E_PAGEOBJECTS_PACKAGE = E2E_PACKAGE + ".pageobjects";

    private static final String E2E_UTIL_PACKAGE = E2E_PACKAGE + ".util";
    private static final JavaClasses E2E_UTIL_CLASSES =
            new ClassFileImporter().importPackages(E2E_UTIL_PACKAGE);

    private static final String CLIENT_PACKAGE = "teammates.client";
    private static final JavaClasses CLIENT_CLASSES =
            new ClassFileImporter().importPackages(CLIENT_PACKAGE);

    private static final String CLIENT_REMOTEAPI_PACKAGE = CLIENT_PACKAGE + ".remoteapi";
    private static final JavaClasses CLIENT_REMOTEAPI_CLASSES =
            new ClassFileImporter().importPackages(CLIENT_REMOTEAPI_PACKAGE);

    private static final String CLIENT_SCRIPTS_PACKAGE = CLIENT_PACKAGE + ".scripts";

    private static final String CLIENT_UTIL_PACKAGE = CLIENT_PACKAGE + ".util";
    private static final JavaClasses CLIENT_UTIL_CLASSES =
            new ClassFileImporter().importPackages(CLIENT_UTIL_PACKAGE);

    private static String includeSubpackages(String pack) {
        return pack + "..";
    }

    @Test
    public void testArchitecture_uiShouldNotTouchStorage() {
        noClasses().that().resideInAPackage(UI_PACKAGE)
                .should().accessClassesThat().resideInAPackage(STORAGE_PACKAGE)
                .check(UI_CLASSES);
    }

    @Test
    public void testArchitecture_logicShouldNotTouchUi() {
        noClasses().that().resideInAPackage(LOGIC_PACKAGE)
                .should().accessClassesThat().resideInAPackage(UI_PACKAGE)
                .check(LOGIC_CLASSES);
    }

    @Test
    public void testArchitecture_storageShouldNotTouchLogic() {
        noClasses().that().resideInAPackage(STORAGE_PACKAGE)
                .should().accessClassesThat().resideInAPackage(LOGIC_PACKAGE)
                .check(STORAGE_CLASSES);
    }

    @Test
    public void testArchitecture_storageShouldNotTouchUi() {
        noClasses().that().resideInAPackage(STORAGE_PACKAGE)
                .should().accessClassesThat().resideInAPackage(UI_PACKAGE)
                .check(STORAGE_CLASSES);
    }

    @Test
    public void testArchitecture_commonShouldNotTouchLogic() {
        noClasses().that().resideInAPackage(COMMON_PACKAGE)
                // TODO fix these violations
                .and().doNotHaveSimpleName("FeedbackMcqQuestionDetails")
                .and().doNotHaveSimpleName("FeedbackMsqQuestionDetails")
                .and().doNotHaveSimpleName("FeedbackConstantSumQuestionDetails")
                .should().accessClassesThat().resideInAPackage(LOGIC_PACKAGE)
                .check(COMMON_CLASSES);
    }

    @Test
    public void testArchitecture_commonShouldNotTouchStorage() {
        noClasses().that().resideInAPackage(COMMON_PACKAGE)
                .should().accessClassesThat().resideInAPackage(STORAGE_API_PACKAGE)
                .orShould().accessClassesThat().resideInAPackage(STORAGE_SEARCH_PACKAGE)
                .check(COMMON_CLASSES);

        noClasses().that().resideInAPackage(COMMON_PACKAGE)
                .and().resideOutsideOfPackage(COMMON_PACKAGE + ".datatransfer.attributes")
                .should().accessClassesThat().resideInAPackage(STORAGE_ENTITY_PACKAGE)
                .check(COMMON_CLASSES);
    }

    @Test
    public void testArchitecture_commonShouldNotTouchUi() {
        noClasses().that().resideInAPackage(COMMON_PACKAGE)
                // TODO fix this violation
                .and().haveSimpleNameNotEndingWith("QuestionDetails")
                .should().accessClassesThat().resideInAPackage(UI_PACKAGE)
                .check(COMMON_CLASSES);
    }

    @Test
    public void testArchitecture_uiShouldNotTouchLogicExceptForApi() {
        noClasses().that().resideInAPackage(UI_PACKAGE)
                .and().resideOutsideOfPackage(UI_AUTOMATED_PACKAGE)
                .and().resideOutsideOfPackage(UI_WEBAPI_PACKAGE)
                .and().resideOutsideOfPackage(LEGACY_UI_CONTROLLER_PACKAGE)
                .should().accessClassesThat().resideInAPackage(LOGIC_API_PACKAGE)
                .check(UI_CLASSES);

        noClasses().that().resideInAPackage(UI_PACKAGE)
                .should().accessClassesThat().resideInAPackage(LOGIC_CORE_PACKAGE)
                .check(UI_CLASSES);
    }

    @Test
    public void testArchitecture_ui_onlyWebApiCanTouchOutput() {
        noClasses().that().resideInAPackage(UI_PACKAGE)
                .and().resideOutsideOfPackage(UI_WEBAPI_PACKAGE)
                .and().resideOutsideOfPackage(UI_REQUEST_PACKAGE)
                .and().resideOutsideOfPackage(UI_OUTPUT_PACKAGE)
                .should().accessClassesThat().resideInAPackage(UI_OUTPUT_PACKAGE)
                .check(UI_CLASSES);
    }

    @Test
    public void testArchitecture_ui_onlyWebApiCanTouchRequest() {
        noClasses().that().resideInAPackage(UI_PACKAGE)
                .and().resideOutsideOfPackage(UI_WEBAPI_PACKAGE)
                .and().resideOutsideOfPackage(UI_REQUEST_PACKAGE)
                .should().accessClassesThat().resideInAPackage(UI_REQUEST_PACKAGE)
                .check(UI_CLASSES);
    }

    @Test
    public void testArchitecture_ui_apiRequestCanOnlyTouchRequestAndOutput() {
        noClasses().that().resideInAPackage(UI_REQUEST_PACKAGE)
                .should().accessClassesThat().resideInAPackage(UI_AUTOMATED_PACKAGE)
                .orShould().accessClassesThat().resideInAPackage(UI_WEBAPI_PACKAGE)
                .check(UI_REQUEST_CLASSES);
    }

    @Test
    public void testArchitecture_ui_apiOutputCanOnlyTouchOutput() {
        noClasses().that().resideInAPackage(UI_OUTPUT_PACKAGE)
                .should().accessClassesThat().resideInAPackage(UI_AUTOMATED_PACKAGE)
                .orShould().accessClassesThat().resideInAPackage(UI_WEBAPI_PACKAGE)
                .orShould().accessClassesThat().resideInAPackage(UI_REQUEST_PACKAGE)
                .check(UI_OUTPUT_CLASSES);
    }

    @Test
    public void testArchitecture_ui_controllerShouldBeSelfContained() {
        noClasses().that().resideInAPackage(UI_PACKAGE)
                .and().resideOutsideOfPackage(UI_WEBAPI_PACKAGE)
                .should().accessClassesThat().resideInAPackage(UI_WEBAPI_PACKAGE)
                .check(UI_WEBAPI_CLASSES);
    }

    @Test
    public void testArchitecture_ui_automatedActionsShouldBeSelfContained() {
        noClasses().that().resideInAPackage(UI_PACKAGE)
                .and().resideOutsideOfPackage(UI_AUTOMATED_PACKAGE)
                .should().accessClassesThat().resideInAPackage(UI_AUTOMATED_PACKAGE)
                .check(UI_AUTOMATED_CLASSES);
    }

    @Test
    public void testArchitecture_ui_automatedActionsShouldNotTouchOtherUiClasses() {
        noClasses().that().resideInAPackage(UI_AUTOMATED_PACKAGE)
                .should().accessClassesThat().resideInAPackage(UI_WEBAPI_PACKAGE)
                .orShould().accessClassesThat().resideInAPackage(UI_OUTPUT_PACKAGE)
                .orShould().accessClassesThat().resideInAPackage(UI_REQUEST_PACKAGE)
                .check(UI_AUTOMATED_CLASSES);
    }

    @Test
    public void testArchitecture_logic_logicCanOnlyAccessStorageApi() {
        noClasses().that().resideInAPackage(LOGIC_PACKAGE)
                .and().resideOutsideOfPackage(LOGIC_CORE_PACKAGE)
                .should().accessClassesThat().resideInAPackage(STORAGE_PACKAGE)
                .check(LOGIC_CLASSES);

        noClasses().that().resideInAPackage(LOGIC_CORE_PACKAGE)
                .should().accessClassesThat().resideInAPackage(STORAGE_ENTITY_PACKAGE)
                .orShould().accessClassesThat().resideInAPackage(STORAGE_SEARCH_PACKAGE)
                .check(LOGIC_CORE_CLASSES);
    }

    @Test
    public void testArchitecture_logic_coreLogicCanOnlyAccessItsCorrespondingDb() {
        for (JavaClass coreLogicClass : LOGIC_CORE_CLASSES) {
            String logicClassName = coreLogicClass.getSimpleName();
            if ("DataBundleLogic".equals(logicClassName)) {
                continue;
            }
            String dbClassName = logicClassName.replace("Logic", "Db");

            noClasses().should().accessClassesThat(new DescribedPredicate<JavaClass>("") {
                @Override
                public boolean apply(JavaClass input) {
                    return input.getPackageName().equals(STORAGE_API_PACKAGE)
                            && !input.getSimpleName().equals(dbClassName);
                }
            }).check(new ClassFileImporter().importClasses(coreLogicClass.reflect()));
        }
    }

    @Test
    public void testArchitecture_logic_coreLogicShouldNotTouchApiLogic() {
        noClasses().that().resideInAPackage(LOGIC_CORE_PACKAGE)
                .should().accessClassesThat().resideInAPackage(LOGIC_API_PACKAGE)
                .check(LOGIC_CORE_CLASSES);
    }

    @Test
    public void testArchitecture_storage_storageSearchShouldNotTouchStorageEntity() {
        noClasses().that().resideInAPackage(STORAGE_SEARCH_PACKAGE)
                .should().accessClassesThat().resideInAPackage(STORAGE_ENTITY_PACKAGE)
                .check(STORAGE_SEARCH_CLASSES);
    }

    @Test
    public void testArchitecture_storage_storageEntityShouldNotTouchOtherStoragePackages() {
        noClasses().that().resideInAPackage(STORAGE_ENTITY_PACKAGE)
                .should().accessClassesThat(new DescribedPredicate<JavaClass>("") {
                    @Override
                    public boolean apply(JavaClass input) {
                        return input.getPackageName().startsWith(STORAGE_PACKAGE)
                                && !input.getPackageName().equals(STORAGE_ENTITY_PACKAGE);
                    }
                }).check(STORAGE_ENTITY_CLASSES);
    }

    @Test
    public void testArchitecture_common_assumptionClassCanOnlyBeAccessedByProductionCode() {
        noClasses().that().resideInAPackage(includeSubpackages(TEST_PACKAGE))
                .and().resideInAPackage(E2E_PACKAGE)
                .and().resideInAPackage(CLIENT_PACKAGE)
                .should().accessClassesThat().haveSimpleName("Assumption")
                .check(ALL_CLASSES);
    }

    @Test
    public void testArchitecture_testClasses_testCasesShouldBeIndependent() {
        noClasses().that(new DescribedPredicate<JavaClass>("") {
            @Override
            public boolean apply(JavaClass input) {
                return input.getPackageName().startsWith(TEST_CASES_PACKAGE) && !input.isInnerClass();
            }
        }).should().accessClassesThat(new DescribedPredicate<JavaClass>("") {
            @Override
            public boolean apply(JavaClass input) {
                return input.getPackageName().startsWith(TEST_CASES_PACKAGE)
                        && !input.getSimpleName().startsWith("Base")
                        && !input.isInnerClass();
            }
        }).check(TEST_CASES);
    }

    @Test
    public void testArchitecture_testClasses_onlySomeTestClassesCanAccessEntities() {
        noClasses().that().resideInAPackage(includeSubpackages(TEST_PACKAGE))
                .and().resideOutsideOfPackage(TEST_CASES_PACKAGE + ".datatransfer")
                .and().resideOutsideOfPackage(TEST_CASES_PACKAGE + ".storage")
                .should().accessClassesThat().resideInAPackage(STORAGE_ENTITY_PACKAGE)
                .check(TEST_CLASSES);
    }

    @Test
    public void testArchitecture_testClasses_onlySomeTestClassesCanAccessLogic() {
        noClasses().that().resideInAPackage(includeSubpackages(TEST_PACKAGE))
                .and().resideOutsideOfPackage(TEST_CASES_PACKAGE + ".storage")
                .and().resideOutsideOfPackage(TEST_CASES_PACKAGE + ".logic")
                .and().resideOutsideOfPackage(TEST_CASES_PACKAGE + ".action")
                .and().resideOutsideOfPackage(TEST_CASES_PACKAGE + ".webapi")
                .and().resideOutsideOfPackage(TEST_CASES_PACKAGE + ".automated")
                .and().resideOutsideOfPackage(TEST_CASES_PACKAGE + ".search")
                .and().doNotHaveSimpleName("BaseComponentTestCase")
                .and().doNotHaveSimpleName("BaseTestCaseWithMinimalGaeEnvironment")
                .should().accessClassesThat().haveSimpleName("GaeSimulation")
                .orShould().accessClassesThat().haveSimpleName("Logic")
                .check(TEST_CLASSES);
    }

    @Test
    public void testArchitecture_testClasses_driverShouldNotHaveAnyDependency() {
        noClasses().that().resideInAPackage(TEST_DRIVER_PACKAGE)
                .should().accessClassesThat(new DescribedPredicate<JavaClass>("") {
                    @Override
                    public boolean apply(JavaClass input) {
                        return input.getPackageName().startsWith(TEST_PACKAGE)
                                && !input.getPackageName().equals(TEST_DRIVER_PACKAGE);
                    }
                }).check(TEST_DRIVER_CLASSES);
    }

    @Test
    public void testArchitecture_e2e_e2eShouldBeSelfContained() {
        noClasses().that().resideOutsideOfPackage(includeSubpackages(E2E_PACKAGE))
                .and().resideOutsideOfPackage(LEGACY_PAGEOBJECT_PACKAGE)
                .and().resideOutsideOfPackage(LEGACY_BROWSERTESTS_PACKAGE)
                .should().accessClassesThat().resideInAPackage(includeSubpackages(E2E_PACKAGE))
                .check(ALL_CLASSES);
    }

    @Test
    public void testArchitecture_e2e_e2eShouldNotTouchProductionCodeExceptCommonAndRequests() {
        noClasses().that().resideInAPackage(includeSubpackages(E2E_PACKAGE))
                .should().accessClassesThat().resideInAPackage(includeSubpackages(STORAGE_PACKAGE))
                .orShould().accessClassesThat().resideInAPackage(includeSubpackages(LOGIC_PACKAGE))
                .orShould().accessClassesThat(new DescribedPredicate<JavaClass>("") {
                    @Override
                    public boolean apply(JavaClass input) {
                        return input.getPackageName().startsWith(UI_PACKAGE)
                                && !input.getPackageName().equals(UI_OUTPUT_PACKAGE)
                                && !input.getPackageName().equals(UI_REQUEST_PACKAGE);
                    }
                }).check(E2E_CLASSES);
    }

    @Test
    public void testArchitecture_e2e_e2eTestCasesShouldBeIndependentOfEachOther() {
        noClasses().that(new DescribedPredicate<JavaClass>("") {
            @Override
            public boolean apply(JavaClass input) {
                return input.getPackageName().startsWith(E2E_CASES_PACKAGE) && !input.isInnerClass();
            }
        }).should().accessClassesThat(new DescribedPredicate<JavaClass>("") {
            @Override
            public boolean apply(JavaClass input) {
                return input.getPackageName().startsWith(E2E_CASES_PACKAGE)
                        && !input.getSimpleName().startsWith("Base")
                        && !input.isInnerClass();
            }
        }).check(E2E_TEST_CASES);
    }

    @Test
    public void testArchitecture_e2e_onlyE2ETestsCanAccessPageObjects() {
        noClasses().that().resideInAPackage(includeSubpackages(E2E_PACKAGE))
                .and().resideOutsideOfPackage(E2E_PAGEOBJECTS_PACKAGE)
                .and().resideOutsideOfPackage(includeSubpackages(E2E_CASES_PACKAGE))
                .should().accessClassesThat().resideInAPackage(E2E_PAGEOBJECTS_PACKAGE)
                .check(E2E_CLASSES);
    }

    @Test
    public void testArchitecture_e2e_utilShouldNotHaveAnyDependency() {
        noClasses().that().resideInAPackage(E2E_UTIL_PACKAGE)
                .should().accessClassesThat(new DescribedPredicate<JavaClass>("") {
                    @Override
                    public boolean apply(JavaClass input) {
                        return input.getPackageName().startsWith(E2E_PACKAGE)
                                && !input.getPackageName().equals(E2E_UTIL_PACKAGE);
                    }
                }).check(E2E_UTIL_CLASSES);
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
                .check(CLIENT_CLASSES);
    }

    @Test
    public void testArchitecture_client_remoteApiShouldNotTouchScripts() {
        noClasses().that().resideInAPackage(CLIENT_REMOTEAPI_PACKAGE)
                .should().accessClassesThat().resideInAPackage(includeSubpackages(CLIENT_SCRIPTS_PACKAGE))
                .check(CLIENT_REMOTEAPI_CLASSES);
    }

    @Test
    public void testArchitecture_client_utilShouldNotHaveAnyDependency() {
        noClasses().that().resideInAPackage(CLIENT_UTIL_PACKAGE)
                .should().accessClassesThat(new DescribedPredicate<JavaClass>("") {
                    @Override
                    public boolean apply(JavaClass input) {
                        return input.getPackageName().startsWith(CLIENT_PACKAGE)
                                && !input.getPackageName().equals(CLIENT_UTIL_PACKAGE);
                    }
                }).check(CLIENT_UTIL_CLASSES);
    }

    @Test
    public void testArchitecture_externalApi_loggingApiCanOnlyBeAccessedByLogger() {
        noClasses().that().doNotHaveSimpleName("Logger")
                .should().accessClassesThat().resideInAPackage("java.util.logging..")
                .check(ALL_CLASSES);
    }

    @Test
    public void testArchitecture_externalApi_searchApiCanOnlyBeAccessedBySomePackages() {
        noClasses().that().resideOutsideOfPackage(STORAGE_API_PACKAGE)
                .and().resideOutsideOfPackage(STORAGE_SEARCH_PACKAGE)
                .and().resideOutsideOfPackage(CLIENT_SCRIPTS_PACKAGE)
                .should().accessClassesThat().resideInAPackage("com.google.appengine.api.search..")
                .check(ALL_CLASSES);
    }

    @Test
    public void testArchitecture_externalApi_gcsApiCanOnlyBeAccessedByGcsHelper() {
        noClasses().that().doNotHaveSimpleName("GoogleCloudStorageHelper")
                .and().resideOutsideOfPackage(CLIENT_SCRIPTS_PACKAGE)
                .should().accessClassesThat().resideInAPackage("com.google.appengine.tools.cloudstorage..")
                .check(ALL_CLASSES);
    }

    @Test
    public void testArchitecture_externalApi_blobstoreApiCanOnlyBeAccessedByGcsHelper() {
        noClasses().that().doNotHaveSimpleName("GoogleCloudStorageHelper")
                .and().resideOutsideOfPackage(STORAGE_ENTITY_PACKAGE)
                .should().accessClassesThat().resideInAPackage("com.google.appengine.api.blobstore..")
                .check(ALL_CLASSES);

        noClasses().that().resideInAPackage(STORAGE_ENTITY_PACKAGE)
                .should().accessClassesThat(new DescribedPredicate<JavaClass>("") {
                    @Override
                    public boolean apply(JavaClass input) {
                        return !"BlobKey".equals(input.getSimpleName())
                                && input.getPackageName().startsWith("com.google.appengine.api.blobstore");
                    }
                }).check(STORAGE_ENTITY_CLASSES);
    }

    @Test
    public void testArchitecture_externalApi_taskQueueApiCanOnlyBeAccessedByTaskQueueLogic() {
        noClasses().that().doNotHaveSimpleName("TaskQueuesLogic")
                .should().accessClassesThat().resideInAPackage("com.google.appengine.api.taskqueue..")
                .check(ALL_CLASSES);
    }

    @Test
    public void testArchitecture_externalApi_remoteApiCanOnlyBeAccessedByRemoteApiClient() {
        noClasses().that().doNotHaveSimpleName("RemoteApiClient")
                .should().accessClassesThat().resideInAPackage("com.google.appengine.tools.remoteapi..")
                .check(ALL_CLASSES);
    }

    @Test
    public void testArchitecture_externalApi_objectifyApiCanOnlyBeAccessedBySomePackages() {
        noClasses().that().resideOutsideOfPackage(STORAGE_API_PACKAGE)
                .and().resideOutsideOfPackage(STORAGE_ENTITY_PACKAGE)
                .and().resideOutsideOfPackage(CLIENT_REMOTEAPI_PACKAGE)
                .and().resideOutsideOfPackage(includeSubpackages(CLIENT_SCRIPTS_PACKAGE))
                .and().doNotHaveSimpleName("BaseTestCaseWithObjectifyAccess")
                .should().accessClassesThat().resideInAPackage("com.googlecode.objectify..")
                .check(ALL_CLASSES);
    }

    @Test
    public void testArchitecture_externalApi_datastoreTypesCanOnlyBeAccessedByEntityClasses() {
        noClasses().that().resideOutsideOfPackage(STORAGE_ENTITY_PACKAGE)
                .should().accessClassesThat().haveFullyQualifiedName("com.google.appengine.api.datastore.Text")
                .check(ALL_CLASSES);
    }

    @Test
    public void testArchitecture_externalApi_servletApiCanOnlyBeAccessedBySomePackages() {
        noClasses().that().doNotHaveSimpleName("ActivityLogEntry")
                .and().doNotHaveSimpleName("GoogleCloudStorageHelper")
                .and().doNotHaveSimpleName("HttpRequestHelper")
                .and().doNotHaveSimpleName("OfyHelper")
                .and().doNotHaveSimpleName("GaeSimulation")
                .and().doNotHaveSimpleName("MockFilterChain")
                .and().doNotHaveSimpleName("MockHttpServletRequest")
                .and().doNotHaveSimpleName("MockHttpServletResponse")
                .and().doNotHaveSimpleName("MockPart")
                .and().resideOutsideOfPackage(UI_AUTOMATED_PACKAGE)
                .and().resideOutsideOfPackage(UI_WEBAPI_PACKAGE)
                .and().resideOutsideOfPackage(LEGACY_UI_CONTROLLER_PACKAGE)
                .should().accessClassesThat().haveFullyQualifiedName("javax.servlet..")
                .check(ALL_CLASSES);
    }

    @Test
    public void testArchitecture_externalApi_assertionApiCanOnlyBeAccessedBySomePackages() {
        noClasses().that().resideOutsideOfPackage(E2E_PAGEOBJECTS_PACKAGE)
                .and().doNotHaveSimpleName("BaseTestCase")
                .and().doNotHaveSimpleName("AssertHelper")
                .and().doNotHaveSimpleName("CsvChecker")
                .and().doNotHaveSimpleName("EmailChecker")
                .and().resideOutsideOfPackage(LEGACY_PAGEOBJECT_PACKAGE)
                // TODO remove the next after migration
                .and().doNotHaveSimpleName("GaeSimulation")
                .should().accessClassesThat().haveFullyQualifiedName("org.junit.Assert")
                .check(ALL_CLASSES);
    }

}
