import { Instructor, InstructorPermissionRole, JoinState } from '../../../types/api-output';
import TestCourses from '../courses';

const hock: Instructor = {
  courseId: TestCourses.cs101.courseId,
  joinState: JoinState.JOINED,
  googleId: 'Hock',
  name: 'Hock',
  email: 'hock@gmail.com',
  role: InstructorPermissionRole.INSTRUCTOR_PERMISSION_ROLE_COOWNER,
  displayedToStudentsAs: 'Hock',
  isDisplayedToStudents: false,
};

const hodor: Instructor = {
  courseId: TestCourses.cs101.courseId,
  joinState: JoinState.JOINED,
  googleId: 'Hodor',
  name: 'Hodor',
  email: 'hodor@gmail.com',
  role: InstructorPermissionRole.INSTRUCTOR_PERMISSION_ROLE_COOWNER,
  displayedToStudentsAs: 'Hodor',
  isDisplayedToStudents: true,
};

const jane: Instructor = {
  courseId: TestCourses.cs101.courseId,
  joinState: JoinState.NOT_JOINED,
  name: 'Jane',
  email: 'jane@gmail.com',
};

const TestInstructors = { hock, hodor, jane };

export default TestInstructors;
