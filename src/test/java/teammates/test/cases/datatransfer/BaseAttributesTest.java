package teammates.test.cases.datatransfer;

import teammates.test.cases.BaseTestCaseWithObjectifyAccess;

/**
 * Base class for attributes tests. Requires Objectify access for entities, which are generated in {@code toEntity} tests.
 */
public abstract class BaseAttributesTest extends BaseTestCaseWithObjectifyAccess {

    /**
     * Tests construction of entity object from attributes.
     */
    public abstract void testToEntity();
}
