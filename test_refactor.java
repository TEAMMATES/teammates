// This is a simple verification that our refactoring follows the same pattern
// 
// BEFORE (old style - multiple test methods with manual login/logout):
/*
    @Test
    void testAccessControl_admin_canAccess() {
        verifyCanAccess();
    }

    @Test
    void testAccessControl_maintainers_canAccess() {
        logoutUser();
        loginAsMaintainer();
        verifyCanAccess();
    }

    @Test
    void testAccessControl_instructor_cannotAccess() {
        logoutUser();
        loginAsInstructor(Const.ParamsNames.INSTRUCTOR_ID);
        verifyCannotAccess();
    }

    @Test
    void testAccessControl_student_cannotAccess() {
        logoutUser();
        loginAsStudent(Const.ParamsNames.STUDENT_ID);
        verifyCannotAccess();
    }

    @Test
    void testAccessControl_loggedOut_cannotAccess() {
        logoutUser();
        verifyCannotAccess();
    }

    @Test
    void testAccessControl_unregistered_cannotAccess() {
        logoutUser();
        loginAsUnregistered(Const.ParamsNames.USER_ID);
        verifyCannotAccess();
    }
*/

// AFTER (new style - single test method using convenient helper methods):
/*
    @Test
    void testAccessControl() {
        verifyOnlyAdminsCanAccess();
        verifyMaintainersCanAccess();
    }
*/

// The new approach is much cleaner and follows the abstraction pattern!