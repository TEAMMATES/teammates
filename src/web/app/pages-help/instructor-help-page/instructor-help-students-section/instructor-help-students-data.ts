import { JoinState, Student } from '../../../../types/api-output';
import {
  SearchStudentsListRowTable,
} from '../../../pages-instructor/instructor-search-page/student-result-table/student-result-table.component';

/**
 * Structure of example of student attributes.
 */
export const EXAMPLE_STUDENT_ATTRIBUTES: Student = {
  email: 'alice@email.com',
  courseId: 'test.exa-demo',
  name: 'Alice Betsy',
  comments: 'Alice is a transfer student.',
  teamName: 'Team A',
  sectionName: 'Section A',
  joinState: JoinState.JOINED,
};
/**
 * Structure of example of student result table of a student.
 */
export const EXAMPLE_SINGLE_STUDENT_RESULT_TABLES: SearchStudentsListRowTable[] = [{
  courseId: 'Course name appears here',
  students: [{
    student: EXAMPLE_STUDENT_ATTRIBUTES,
    isAllowedToViewStudentInSection: true,
    isAllowedToModifyStudent: true,
  }],
}];
/**
 * Structure of example of student result tables of multiple students.
 */
export const EXAMPLE_MULTIPLE_STUDENT_RESULT_TABLES: SearchStudentsListRowTable[] = [{
  courseId: 'Course name appears here',
  students: [
    {
      student: {
        sectionName: 'Section A',
        name: 'Alice Betsy',
        email: 'alice@email.com',
        joinState: JoinState.JOINED,
        teamName: 'Team A',
        courseId: 'Course name appears here',
      },
      isAllowedToViewStudentInSection: true,
      isAllowedToModifyStudent: true,

    },
    {
      student: {
        name: 'Jean Grey',
        email: 'jean@email.com',
        joinState: JoinState.JOINED,
        teamName: 'Team A',
        sectionName: 'Section A',
        courseId: 'Course name appears here',
      },
      isAllowedToViewStudentInSection: true,
      isAllowedToModifyStudent: true,

    },
    {
      student: {
        name: 'Oliver Gates',
        email: 'oliver@email.com',
        joinState: JoinState.JOINED,
        teamName: 'Team B',
        sectionName: 'Section B',
        courseId: 'Course name appears here',
      },
      isAllowedToViewStudentInSection: true,
      isAllowedToModifyStudent: true,
    },
    {
      student: {
        name: 'Thora Parker',
        email: 'thora@email.com',
        joinState: JoinState.JOINED,
        teamName: 'Team B',
        sectionName: 'Section B',
        courseId: 'Course name appears here',
      },
      isAllowedToViewStudentInSection: true,
      isAllowedToModifyStudent: true,
    },
    {
      student: {
        name: 'Jack Wayne',
        email: 'jack@email.com',
        joinState: JoinState.JOINED,
        teamName: 'Team C',
        sectionName: 'Section C',
        courseId: 'Course name appears here',
      },
      isAllowedToViewStudentInSection: true,
      isAllowedToModifyStudent: true,
    },
  ],
}];
