import { Course } from '../../../types/api-output';
import { date1, date2, date3, date4, date5, date6 } from '../dates';

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

const cs101: Course = {
  courseId: 'CS101',
  courseName: 'Introduction to CS',
  timeZone: '',
  institute: 'Test Institute',
  creationTimestamp: 0,
  deletionTimestamp: 0,
};

const cs1231: Course = {
  courseId: 'CS1231',
  courseName: 'Discrete Structures',
  creationTimestamp: date1.getTime(),
  deletionTimestamp: 0,
  timeZone: 'UTC',
  institute: 'Test Institute',
};

const cs3281: Course = {
  courseId: 'CS3281',
  courseName: 'Thematic Systems Project I',
  creationTimestamp: date3.getTime(),
  deletionTimestamp: date4.getTime(),
  timeZone: 'UTC',
  institute: 'Test Institute',
};

const cs3282: Course = {
  courseId: 'CS3282',
  courseName: 'Thematic Systems Project II',
  creationTimestamp: date5.getTime(),
  deletionTimestamp: date6.getTime(),
  timeZone: 'UTC',
  institute: 'Test Institute',
};

const st4234: Course = {
  courseId: 'ST4234',
  courseName: 'Bayesian Statistics',
  creationTimestamp: date2.getTime(),
  deletionTimestamp: 0,
  timeZone: 'UTC',
  institute: 'Test Institute',
};

const TestCourses = {
  cs9999,
  ma1234,
  ee1111,
  cs101,
  cs1231,
  cs3281,
  cs3282,
  st4234,
};

export default TestCourses;
