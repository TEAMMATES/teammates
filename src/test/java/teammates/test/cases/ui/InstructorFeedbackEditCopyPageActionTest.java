package teammates.test.cases.ui;

import static org.testng.AssertJUnit.assertTrue;
import static org.testng.AssertJUnit.assertEquals;
import static org.testng.AssertJUnit.assertFalse;

import java.util.ArrayList;
import java.util.List;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import teammates.common.datatransfer.CourseAttributes;
import teammates.common.datatransfer.DataBundle;
import teammates.common.datatransfer.InstructorAttributes;
import teammates.common.util.Const;
import teammates.ui.controller.AjaxResult;
import teammates.ui.controller.InstructorFeedbackEditCopyPageAction;
import teammates.ui.controller.InstructorFeedbackEditCopyPageData;


public class InstructorFeedbackEditCopyPageActionTest extends
        BaseActionTest {
    private static DataBundle dataBundle;
    
    @BeforeClass
    public static void classSetUp() throws Exception {
        printTestClassHeader();
        dataBundle = loadDataBundle("/InstructorFeedbackEditCopyTest.json");
        removeAndRestoreDatastoreFromJson("/InstructorFeedbackEditCopyTest.json");
        
        uri = Const.ActionURIs.INSTRUCTOR_FEEDBACK_EDIT_COPY_PAGE;
    }
    
    @Test
    public void testExecuteAndPostProcess() throws Exception{
        InstructorAttributes instructor = dataBundle.instructors.get("teammates.test.instructor2");
        String instructorId = instructor.googleId;
        
        gaeSimulation.loginAsInstructor(instructorId);
        
        
        ______TS("Successful case");
        
        InstructorFeedbackEditCopyPageAction a = getAction();
        AjaxResult r = getAjaxResult(a);
        
        assertFalse(r.isError);

        InstructorFeedbackEditCopyPageData pageData = 
                (InstructorFeedbackEditCopyPageData) r.data;
        assertEquals(4, pageData.courses.size());
 
        
        List<String> idOfCourses = new ArrayList<String>();
        
        for (CourseAttributes course: pageData.courses) {
            idOfCourses.add(course.id);
        }
        
        assertFalse(idOfCourses.contains("FeedbackEditCopy.CS1101")); // course is archived
        assertFalse(idOfCourses.contains("FeedbackEditCopy.CS2107")); // instructor does not have sufficient permissions
        
        assertTrue(idOfCourses.contains("FeedbackEditCopy.CS2102"));
        assertTrue(idOfCourses.contains("FeedbackEditCopy.CS2103"));
        assertTrue(idOfCourses.contains("FeedbackEditCopy.CS2103R"));
        assertTrue(idOfCourses.contains("FeedbackEditCopy.CS2104"));
        
    }
    
    private InstructorFeedbackEditCopyPageAction getAction(String... params)
            throws Exception {

        return (InstructorFeedbackEditCopyPageAction) (gaeSimulation
                .getActionObject(uri, params));

    }
}
