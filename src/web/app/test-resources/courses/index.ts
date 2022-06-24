import { Course } from '../../../types/api-output';

const cs9999: Course = {
  courseId: 'CS9999',
  courseName: 'CS9999',
  institute: 'Test Institute',
  timeZone: 'Asia/Singapore',
  creationTimestamp: 0,
  deletionTimestamp: 0,
  privileges: {
    canModifyCourse: true,
    canModifySession: true,
    canModifyStudent: true,
    canModifyInstructor: true,
    canViewStudentInSections: true,
    canModifySessionCommentsInSections: true,
    canViewSessionInSections: true,
    canSubmitSessionInSections: true,
  },
};

const ma1234: Course = {
  courseId: 'MA1234',
  courseName: 'MA1234',
  institute: 'Test Institute',
  timeZone: 'Asia/Singapore',
  creationTimestamp: 0,
  deletionTimestamp: 0,
  privileges: {
    canModifyCourse: true,
    canModifySession: true,
    canModifyStudent: true,
    canModifyInstructor: true,
    canViewStudentInSections: true,
    canModifySessionCommentsInSections: true,
    canViewSessionInSections: true,
    canSubmitSessionInSections: true,
  },
};

const ee1111: Course = {
  courseId: 'EE1111',
  courseName: 'EE1111',
  institute: 'Test Institute',
  timeZone: 'Asia/Singapore',
  creationTimestamp: 0,
  deletionTimestamp: 0,
  privileges: {
    canModifyCourse: false,
    canModifySession: false,
    canModifyStudent: false,
    canModifyInstructor: false,
    canViewStudentInSections: true,
    canModifySessionCommentsInSections: true,
    canViewSessionInSections: true,
    canSubmitSessionInSections: true,
  },
};

const TestCourses = { cs9999, ma1234, ee1111 };

export default TestCourses;
