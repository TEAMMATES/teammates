import { JoinState, Student } from '../../../../types/api-output';
import { SearchStudentsListRowTable } from '../../../pages-instructor/instructor-search-page/student-result-table/student-result-table.component';

/**
 * Structure of example of student attributes.
 */
export const EXAMPLE_STUDENT_ATTRIBUTES: Student = {
  userId: '00000000-0000-4000-9000-000000000001',
  email: 'alice@email.com',
  courseId: 'test.exa-demo',
  name: 'Alice Betsy',
  comments: 'Alice is a transfer student.',
  teamId: 'team-a',
  teamName: 'Team A',
  sectionId: 'section-a',
  sectionName: 'Section A',
  joinState: JoinState.JOINED,
  institute: 'NUS',
  courseName: 'CS3281',
};
/**
 * Structure of example of student result table of a student.
 */
export const EXAMPLE_SINGLE_STUDENT_RESULT_TABLES: SearchStudentsListRowTable[] = [
  {
    courseId: 'Course name appears here',
    students: [
      {
        student: EXAMPLE_STUDENT_ATTRIBUTES,
        isAllowedToModifyStudent: true,
      },
    ],
  },
];
/**
 * Structure of example of student result tables of multiple students.
 */
export const EXAMPLE_MULTIPLE_STUDENT_RESULT_TABLES: SearchStudentsListRowTable[] = [
  {
    courseId: 'Course name appears here',
    students: [
      {
        student: {
          userId: '00000000-0000-4000-9000-000000000001',
          sectionName: 'Section A',
          name: 'Alice Betsy',
          email: 'alice@email.com',
          joinState: JoinState.JOINED,
          teamId: 'team-a',
          teamName: 'Team A',
          sectionId: 'section-a',
          courseId: 'Course name appears here',
          institute: '',
          courseName: '',
        },
        isAllowedToModifyStudent: true,
      },
      {
        student: {
          userId: '00000000-0000-4000-9000-000000000002',
          name: 'Jean Grey',
          email: 'jean@email.com',
          joinState: JoinState.JOINED,
          teamId: 'team-a',
          teamName: 'Team A',
          sectionId: 'section-a',
          sectionName: 'Section A',
          courseId: 'Course name appears here',
          institute: '',
          courseName: '',
        },
        isAllowedToModifyStudent: true,
      },
      {
        student: {
          userId: '00000000-0000-4000-9000-000000000003',
          name: 'Oliver Gates',
          email: 'oliver@email.com',
          joinState: JoinState.JOINED,
          teamId: 'team-b',
          teamName: 'Team B',
          sectionId: 'section-b',
          sectionName: 'Section B',
          courseId: 'Course name appears here',
          institute: '',
          courseName: '',
        },
        isAllowedToModifyStudent: true,
      },
      {
        student: {
          userId: '00000000-0000-4000-9000-000000000004',
          name: 'Thora Parker',
          email: 'thora@email.com',
          joinState: JoinState.JOINED,
          teamId: 'team-b',
          teamName: 'Team B',
          sectionId: 'section-b',
          sectionName: 'Section B',
          courseId: 'Course name appears here',
          institute: '',
          courseName: '',
        },
        isAllowedToModifyStudent: true,
      },
      {
        student: {
          userId: '00000000-0000-4000-9000-000000000005',
          name: 'Jack Wayne',
          email: 'jack@email.com',
          joinState: JoinState.JOINED,
          teamId: 'team-c',
          teamName: 'Team C',
          sectionId: 'section-c',
          sectionName: 'Section C',
          courseId: 'Course name appears here',
          institute: '',
          courseName: '',
        },
        isAllowedToModifyStudent: true,
      },
    ],
  },
];
