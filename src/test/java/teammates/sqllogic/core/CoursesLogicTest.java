package teammates.sqllogic.core;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import teammates.storage.sqlapi.CoursesDb;
import teammates.test.BaseTestCase;

/**
 * SUT: {@code CoursesLogic}
 */
public class CoursesLogicTest extends BaseTestCase {

    private CoursesLogic coursesLogic = CoursesLogic.inst();
    private CoursesDb coursesDb;

    @BeforeMethod 
    public void setUp() {
        coursesDb = mock(CoursesDb.class);
    }
}
