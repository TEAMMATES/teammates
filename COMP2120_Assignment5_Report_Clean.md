# COMP2120 Assignment 5 - TEAMMATES Project Report

## 📋 Project Information
- **Repository**: TEAMMATES
- **Repository URL**: https://github.com/TEAMMATES/teammates
- **Issue Number**: #13304 - Refactoring for Unit Test Access Controls
- **Issue URL**: https://github.com/TEAMMATES/teammates/issues/13304
- **Progress Tracking**: https://docs.google.com/spreadsheets/d/1IcB-viJMrIOknYoBYasBuRBK9qnwY0-doEK4_Eb24IA/edit?gid=0#gid=0
- **Target Lines**: 71-78 (8 test files total)
- **Team Member Responsible**: [Your Name/GitHub Username]

## 🎯 Issue Analysis and Understanding

### Issue Description
After the completion of most Unit Test migration in #12048, there are many inconsistencies in access control test methods. This is because the convenient, abstracted, and commonly used access control test methods from `ui/webapi/BaseActionTest.java` were not migrated to `sqlui/webapi/BaseActionTest.java`.

After merging PR #13254, all access control test methods in BaseActionTest.java have been migrated. This issue aims to refactor the unit test access controls to use these convenient test methods.

### Problem Analysis
- **Root Cause**: Test files are using old-style manual access control testing
- **Impact**: Code duplication, inconsistency, harder maintenance
- **Solution**: Migrate to use new convenient methods in BaseActionTest.java

### Difficulty Assessment
- **Level**: Medium
- **Reasoning**: Requires understanding of both old and new testing patterns, multiple files to refactor

### Size Assessment
- **Scope**: 8 test files (lines 71-78 in progress tracking sheet)
- **Type**: Code refactoring (no new functionality)

### Time Estimation
- **Per File**: 30-45 minutes (analysis + implementation + verification)
- **Total**: 4-6 hours for all 8 files

### Workload Distribution
- **Individual Work**: All 8 files assigned to one team member
- **Team Support**: Code review and verification by team members

## 🔧 Technical Implementation Details

### Understanding the Codebase

#### Old Pattern (Before Refactoring)
```java
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

// ... more individual test methods
```

#### New Pattern (After Refactoring)
```java
@Test
void testAccessControl() {
    verifyOnlyAdminsCanAccess();
    verifyMaintainersCanAccess();
}
```

#### Available Convenience Methods in BaseActionTest.java
- `verifyOnlyAdminsCanAccess()` - Tests admin access + denies all others
- `verifyMaintainersCanAccess()` - Tests maintainer access
- `verifyOnlyInstructorsCanAccess(Course, params)` - For instructor-only actions
- `verifyOnlyStudentsCanAccess(params)` - For student-only actions
- `verifyAnyUserCanAccess(params)` - For public actions
- `verifyAnyLoggedInUserCanAccess(params)` - For logged-in user actions
- `verifyNoUsersCanAccess(params)` - For blocked actions

#### Why This Refactoring Matters
1. **Consistency**: All test files now use the same access control testing pattern
2. **Maintainability**: Changes to access control logic only need to be made in BaseActionTest.java
3. **Comprehensiveness**: Convenience methods ensure all user types are tested (admin, instructor, student, unregistered, logged-out)
4. **Code Quality**: Eliminates copy-paste code and manual user management
5. **Project Standards**: Aligns with the coding standards established in PR #13254

#### Technical Validation
Each convenience method internally uses the same login/logout mechanisms as the original code:
```java
// Example: verifyAdminsCanAccess() implementation
void verifyAdminsCanAccess(String... params) {
    loginAsAdmin();
    verifyCanAccess(params);
    logoutUser();
}
```
The refactoring doesn't change the underlying test logic - it just abstracts it into reusable, standardized methods.

## 📝 File-by-File Refactoring Records

### File 1: GetUsageStatisticsActionTest.java ✅ COMPLETED

**Location**: `src/test/java/teammates/sqlui/webapi/GetUsageStatisticsActionTest.java`

#### Issue Analysis
- **Before**: 6 separate test methods with manual login/logout
- **Lines of Code**: 136 lines total, reduced to 103 lines (33 lines saved)
- **Pattern**: Manual user login for each user type test

#### Implementation Details

**Before Refactoring**:
```java
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
```

**After Refactoring**:
```java
@Test
void testAccessControl() {
    verifyOnlyAdminsCanAccess();
    verifyMaintainersCanAccess();
}
```

#### Technical Analysis
- **Access Pattern**: Only admins and maintainers should access this action
- **Convenience Methods Used**:
  - `verifyOnlyAdminsCanAccess()`: Comprehensive admin-only testing that internally performs:
    - `verifyAdminsCanAccess()` - Tests admin can access
    - `verifyInstructorsCannotAccess()` - Tests instructors cannot access
    - `verifyStudentsCannotAccess()` - Tests students cannot access
    - `verifyUnregisteredCannotAccess()` - Tests unregistered users cannot access
    - `verifyWithoutLoginCannotAccess()` - Tests logged-out users cannot access
  - `verifyMaintainersCanAccess()`: Tests maintainer access specifically (loginAsMaintainer + verifyCanAccess + logoutUser)
- **Why This Pattern**: Usage statistics are administrative data that should only be accessible to system administrators and maintainers

#### Results
- **Lines Reduced**: From 136 to 103 lines (33 lines saved)
- **Test Methods**: From 6 methods to 1 method
- **Test Coverage**: **Maintained and standardized** - all original user types tested through convenient methods
- **Code Quality**: Eliminated redundant manual login/logout operations
- **Maintainability**: Significantly improved - uses centralized BaseActionTest methods
- **Consistency**: Now follows project-wide testing patterns

#### Verification Steps
1. **Compilation Check**: ✅ `./gradlew compileTestJava` - SUCCESS
2. **Syntax Validation**: ✅ No errors found
3. **Pattern Consistency**: ✅ Matches other refactored files
4. **Method Availability**: ✅ Confirmed methods exist in BaseActionTest.java

---

### File 2: InstructorCourseJoinEmailWorkerActionTest.java ✅ COMPLETED

**Location**: `src/test/java/teammates/sqlui/webapi/InstructorCourseJoinEmailWorkerActionTest.java`

#### Issue Analysis
- **Before**: 2 separate test methods with manual login/logout operations
- **Lines of Code**: 152 lines total, reduced to 132 lines (20 lines saved)
- **Pattern**: Manual verification of admin access and manual testing of other user types

#### Implementation Details

**Before Refactoring**:
```java
@Test
public void testSpecificAccessControl_isAdmin_canAccess() {
    String[] params = {
            Const.ParamsNames.COURSE_ID, course.getId(),
            Const.ParamsNames.INSTRUCTOR_EMAIL, instructor.getEmail(),
            Const.ParamsNames.IS_INSTRUCTOR_REJOINING, "false",
            Const.ParamsNames.INVITER_ID, inviter.getGoogleId(),
    };

    verifyCanAccess(params);
}

@Test
public void testSpecificAccessControl_notAdmin_cannotAccess() {
    String[] params = {
            Const.ParamsNames.COURSE_ID, course.getId(),
            Const.ParamsNames.INSTRUCTOR_EMAIL, instructor.getEmail(),
            Const.ParamsNames.IS_INSTRUCTOR_REJOINING, "false",
            Const.ParamsNames.INVITER_ID, inviter.getGoogleId(),
    };

    loginAsInstructor("user-id");
    verifyCannotAccess(params);

    loginAsStudent("user-id");
    verifyCannotAccess(params);

    logoutUser();
    verifyCannotAccess(params);
}
```

**After Refactoring**:
```java
@Test
public void testAccessControl() {
    String[] params = {
            Const.ParamsNames.COURSE_ID, course.getId(),
            Const.ParamsNames.INSTRUCTOR_EMAIL, instructor.getEmail(),
            Const.ParamsNames.IS_INSTRUCTOR_REJOINING, "false",
            Const.ParamsNames.INVITER_ID, inviter.getGoogleId(),
    };

    verifyOnlyAdminsCanAccess(params);
}
```

#### Technical Analysis
- **Access Pattern**: Only administrators should access this worker action (email sending functionality)
- **Convenience Methods Used**:
  - `verifyOnlyAdminsCanAccess(params)`: Comprehensive access control testing that internally performs:
    - `verifyAdminsCanAccess()` - Tests admin can access
    - `verifyInstructorsCannotAccess()` - Tests instructors cannot access
    - `verifyStudentsCannotAccess()` - Tests students cannot access  
    - `verifyUnregisteredCannotAccess()` - Tests unregistered users cannot access
    - `verifyWithoutLoginCannotAccess()` - Tests logged-out users cannot access
- **Parameters Required**: This action requires course ID, instructor email, rejoining flag, and inviter ID
- **Test Coverage Enhancement**: The new approach actually provides **more comprehensive** testing than the original code by including unregistered user verification

#### Results
- **Lines Reduced**: From 152 to 132 lines (20 lines saved)
- **Test Methods**: From 2 methods to 1 method
- **Test Coverage**: **Enhanced** - now includes unregistered user testing (5 user types vs original 4)
- **Code Quality**: Eliminated manual login/logout operations in favor of standardized BaseActionTest methods
- **Maintainability**: Significantly improved - centralized access control logic
- **Consistency**: Now follows project-wide testing patterns established in BaseActionTest.java

#### Verification Steps
1. **Compilation Check**: ✅ `./gradlew compileTestJava` - SUCCESS
2. **Syntax Validation**: ✅ No errors found
3. **Pattern Consistency**: ✅ Matches established refactoring pattern
4. **Method Availability**: ✅ Confirmed `verifyOnlyAdminsCanAccess(params)` exists in BaseActionTest.java

---

### File 3: InstructorSearchIndexingWorkerActionTest.java ✅ COMPLETED

**Location**: `src/test/java/teammates/sqlui/webapi/InstructorSearchIndexingWorkerActionTest.java`

#### Issue Analysis
- **Before**: 1 large test method with multiple test sections and manual login/logout operations
- **Lines of Code**: 148 lines total, reduced to 122 lines (26 lines saved)
- **Pattern**: Manual verification using ______TS("description") sections for different user types

#### Implementation Details

**Before Refactoring**:
```java
@Test
void testAccessControl_onlyAdminCanAccess() {

    ______TS("Non-logged-in users cannot access");
    logoutUser();
    verifyCannotAccess();

    ______TS("Non-registered users cannot access");
    loginAsUnregistered("unregistered user");
    verifyCannotAccess();

    ______TS("Students cannot access");
    loginAsStudent(getTypicalStudent().getGoogleId());
    verifyCannotAccess();

    ______TS("Instructors cannot access");
    loginAsInstructor(getTypicalInstructor().getGoogleId());
    verifyCannotAccess();

    ______TS("Admin can access");
    loginAsAdmin();
    verifyCanAccess();
}
```

**After Refactoring**:
```java
@Test
void testAccessControl() {
    verifyOnlyAdminsCanAccess();
}
```

#### Technical Analysis
- **Access Pattern**: Only administrators should access this worker action (search indexing functionality)
- **Convenience Methods Used**:
  - `verifyOnlyAdminsCanAccess()`: Comprehensive admin-only testing that internally performs:
    - `verifyAdminsCanAccess()` - Tests admin can access
    - `verifyInstructorsCannotAccess()` - Tests instructors cannot access
    - `verifyStudentsCannotAccess()` - Tests students cannot access
    - `verifyUnregisteredCannotAccess()` - Tests unregistered users cannot access
    - `verifyWithoutLoginCannotAccess()` - Tests logged-out users cannot access
- **Why This Pattern**: Search indexing is a background administrative task that should only be triggered by system administrators
- **Original Test Structure**: Used TEAMMATES' ______TS() pattern for test sections, which is perfectly valid but verbose for access control

#### Results
- **Lines Reduced**: From 148 to 122 lines (26 lines saved)
- **Test Methods**: From 1 large method to 1 concise method
- **Test Coverage**: **Maintained** - all original user types tested through convenient methods
- **Code Quality**: Eliminated verbose test section markers and manual user management
- **Maintainability**: Significantly improved - uses centralized BaseActionTest methods
- **Consistency**: Now follows project-wide testing patterns

#### Verification Steps
1. **Compilation Check**: ✅ `./gradlew compileTestJava` - SUCCESS
2. **Syntax Validation**: ✅ No errors found
3. **Pattern Consistency**: ✅ Matches established refactoring pattern
4. **Method Availability**: ✅ Confirmed `verifyOnlyAdminsCanAccess()` exists in BaseActionTest.java

---

### File 4: JoinCourseActionTest.java ✅ COMPLETED

**Location**: `src/test/java/teammates/sqlui/webapi/JoinCourseActionTest.java`

#### Issue Analysis
- **Before**: 2 separate test methods with manual login/logout operations for different user types
- **Lines of Code**: 262 lines total, reduced to 241 lines (21 lines saved)
- **Pattern**: Manual verification of multiple logged-in user types plus separate test for logged-out users

#### Implementation Details

**Before Refactoring**:
```java
@Test
void testSpecificAccessControl_loggedIn_canAccess() {
    loginAsUnregistered("unreg-user");
    String[] params = {};
    verifyCanAccess(params);

    logoutUser();
    loginAsAdmin();
    verifyCanAccess(params);

    logoutUser();
    loginAsInstructor("instructor");
    verifyCanAccess(params);

    logoutUser();
    loginAsStudent("student");
    verifyCanAccess(params);
}

@Test
void testSpecificAccessControl_loggedOut_cannotAccess() {
    logoutUser();
    String[] params = {};
    verifyCannotAccess(params);
}
```

**After Refactoring**:
```java
@Test
void testAccessControl() {
    String[] params = {};
    verifyAnyLoggedInUserCanAccess(params);
}
```

#### Technical Analysis
- **Access Pattern**: Any logged-in user can access this action (unregistered, admin, instructor, student), but logged-out users cannot
- **Convenience Methods Used**:
  - `verifyAnyLoggedInUserCanAccess(params)`: Comprehensive testing that internally performs:
    - `verifyAdminsCanAccess()` - Tests admin can access
    - `verifyInstructorsCanAccess()` - Tests instructors can access
    - `verifyStudentsCanAccess()` - Tests students can access
    - `verifyUnregisteredCanAccess()` - Tests unregistered users can access
    - `verifyWithoutLoginCannotAccess()` - Tests logged-out users cannot access
- **Why This Pattern**: Join course functionality should be available to any authenticated user who has a valid registration key
- **Business Logic**: Makes sense that any logged-in user (regardless of current role) should be able to join a course

#### Results
- **Lines Reduced**: From 262 to 241 lines (21 lines saved)
- **Test Methods**: From 2 methods to 1 method
- **Test Coverage**: **Maintained** - all original user types tested through convenient methods
- **Code Quality**: Eliminated repetitive manual login/logout cycles
- **Maintainability**: Significantly improved - uses centralized BaseActionTest methods
- **Consistency**: Now follows project-wide testing patterns

#### Verification Steps
1. **Compilation Check**: ✅ `./gradlew compileTestJava` - SUCCESS
2. **Syntax Validation**: ✅ No errors found
3. **Pattern Consistency**: ✅ Matches established refactoring pattern
4. **Method Availability**: ✅ Confirmed `verifyAnyLoggedInUserCanAccess(params)` exists in BaseActionTest.java

---

### File 5: StudentCourseJoinEmailWorkerActionTest.java ✅ COMPLETED

**Location**: `src/test/java/teammates/sqlui/webapi/StudentCourseJoinEmailWorkerActionTest.java`

#### Issue Analysis
- **Before**: 2 separate test methods with manual login/logout operations for different user types
- **Lines of Code**: 145 lines total, reduced to 125 lines (20 lines saved)
- **Pattern**: Manual verification of admin access and manual testing of other user types (instructor, student, logged-out)

#### Implementation Details

**Before Refactoring**:
```java
@Test
public void testSpecificAccessControl_isAdmin_canAccess() {
    String[] params = {
            Const.ParamsNames.COURSE_ID, course.getId(),
            Const.ParamsNames.STUDENT_EMAIL, student.getEmail(),
            Const.ParamsNames.IS_STUDENT_REJOINING, "false",
    };

    verifyCanAccess(params);
}

@Test
public void testSpecificAccessControl_notAdmin_cannotAccess() {
    String[] params = {
            Const.ParamsNames.COURSE_ID, course.getId(),
            Const.ParamsNames.STUDENT_EMAIL, student.getEmail(),
            Const.ParamsNames.IS_STUDENT_REJOINING, "false",
    };

    loginAsInstructor("user-id");
    verifyCannotAccess(params);

    loginAsStudent("user-id");
    verifyCannotAccess(params);

    logoutUser();
    verifyCannotAccess(params);
}
```

**After Refactoring**:
```java
@Test
public void testAccessControl() {
    String[] params = {
            Const.ParamsNames.COURSE_ID, course.getId(),
            Const.ParamsNames.STUDENT_EMAIL, student.getEmail(),
            Const.ParamsNames.IS_STUDENT_REJOINING, "false",
    };

    verifyOnlyAdminsCanAccess(params);
}
```

#### Technical Analysis
- **Access Pattern**: Only administrators should access this worker action (student course join email sending functionality)
- **Convenience Methods Used**:
  - `verifyOnlyAdminsCanAccess(params)`: Comprehensive access control testing that internally performs:
    - `verifyAdminsCanAccess()` - Tests admin can access
    - `verifyInstructorsCannotAccess()` - Tests instructors cannot access
    - `verifyStudentsCannotAccess()` - Tests students cannot access  
    - `verifyUnregisteredCannotAccess()` - Tests unregistered users cannot access
    - `verifyWithoutLoginCannotAccess()` - Tests logged-out users cannot access
- **Parameters Required**: This action requires course ID, student email, and student rejoining flag
- **Test Coverage Enhancement**: The new approach provides **more comprehensive** testing by including unregistered user verification (5 user types vs original 4)
- **Why This Pattern**: Student course join email is a background administrative task that should only be triggered by system administrators

#### Results
- **Lines Reduced**: From 145 to 125 lines (20 lines saved)
- **Test Methods**: From 2 methods to 1 method
- **Test Coverage**: **Enhanced** - now includes unregistered user testing (5 user types vs original 4)
- **Code Quality**: Eliminated manual login/logout operations in favor of standardized BaseActionTest methods
- **Maintainability**: Significantly improved - centralized access control logic
- **Consistency**: Now follows project-wide testing patterns established in BaseActionTest.java

#### Verification Steps
1. **Compilation Check**: ✅ `./gradlew compileTestJava` - SUCCESS
2. **Syntax Validation**: ✅ No errors found
3. **Pattern Consistency**: ✅ Matches established refactoring pattern (similar to InstructorCourseJoinEmailWorkerActionTest.java)
4. **Method Availability**: ✅ Confirmed `verifyOnlyAdminsCanAccess(params)` exists in BaseActionTest.java

---

### File 6: [Next File Name] 🔄 IN PROGRESS

**Location**: `src/test/java/teammates/sqlui/webapi/[FileName]`

#### Issue Analysis
- **Before**: [To be filled]
- **Pattern**: [To be analyzed]

#### Implementation Details
[To be documented during refactoring]

---

### File 6: [Next File Name] ⏳ PENDING

**Location**: `src/test/java/teammates/sqlui/webapi/[FileName]`

[To be documented]

---

### File 7: [Next File Name] ⏳ PENDING

[To be documented]

---

### File 8: [Next File Name] ⏳ PENDING

[To be documented]

## 🔍 Project Standards Compliance

### TEAMMATES Contributing Guidelines
- **Location**: `docs/CONTRIBUTING.md`
- **Code Style**: Following existing project conventions
- **Testing Standards**: Using BaseActionTest convenience methods as per project guidelines
- **Commit Message Format**: Following conventional commit patterns

### Pull Request Requirements
- **Title Format**: "Part of Refactoring for Unit Test Access Controls #13304"
- **Description**: Must link to original issue
- **Review Process**: Minimum one team member review required
- **Testing**: All tests must pass before submission

### Code Quality Standards
- **Consistency**: Using established patterns from BaseActionTest.java
- **Readability**: Simplified test methods with clear intent
- **Maintainability**: Reduced code duplication
- **Performance**: No performance impact (testing only)

## 👥 Team Collaboration

### Individual Contributions
- **[Your Name]**: Responsible for all 8 files in lines 71-78
  - Issue analysis and understanding
  - Code refactoring implementation
  - Documentation and verification
  - Pull request preparation

### Team Support
- **Team Member B**: Code review and validation
- **Team Member C**: Final testing and PR approval
- **Team Member D**: Documentation review

### Communication Process
- Regular updates on progress
- Code review sessions for each file
- Team discussion of any challenges encountered

## 🔗 Evidence and Screenshots

### Project Selection Evidence
- [ ] Email to tutor with issue selection
- [ ] Tutor confirmation screenshot
- [ ] Wattle quiz completion

### Development Process Evidence
- [ ] Git commit history screenshots
- [ ] Compilation success screenshots
- [ ] Test execution results
- [ ] Code review discussions

### Pull Request Evidence
- [ ] PR creation screenshot
- [ ] PR description linking to issue
- [ ] Review process documentation
- [ ] Merge confirmation (if accepted)

## 🚀 Next Steps

### Immediate Tasks
1. Complete File 5 refactoring
2. Document implementation details
3. Verify compilation and tests
4. Update this report

### Upcoming Tasks
1. Continue with Files 6-8
2. Prepare comprehensive PR
3. Team code review session
4. Final testing and validation

### Final Deliverables
1. Complete pull request
2. Finalized documentation
3. Team presentation preparation
4. Assignment report submission

## 📈 Progress Tracking

| File # | File Name | Status | Lines Saved | Completion Date |
|--------|-----------|--------|-------------|-----------------|
| 1 | GetUsageStatisticsActionTest.java | ✅ Complete | 33 lines | 2025-10-14 |
| 2 | InstructorCourseJoinEmailWorkerActionTest.java | ✅ Complete | 20 lines | 2025-10-14 |
| 3 | InstructorSearchIndexingWorkerActionTest.java | ✅ Complete | 26 lines | 2025-10-14 |
| 4 | JoinCourseActionTest.java | ✅ Complete | 21 lines | 2025-10-14 |
| 5 | StudentCourseJoinEmailWorkerActionTest.java | ✅ Complete | 20 lines | 2025-10-16 |
| 6 | [File Name] | 🔄 In Progress | TBD | TBD |
| 7 | [File Name] | ⏳ Pending | TBD | TBD |
| 8 | [File Name] | ⏳ Pending | TBD | TBD |

**Total Progress**: 5/8 files completed (62.5%)
**Total Lines Saved**: 120 lines (33+20+26+21+20)

---

*Last Updated: 2025-10-15*
*Document Version: 2.0*