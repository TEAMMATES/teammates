import { JoinState, Student } from '../../../types/api-output';

const emptyStudent: Student = {
  courseId: '',
  email: '',
  name: '',
  sectionName: '',
  teamName: '',
};

const johnDoe: Student = {
  email: 'doejohn@email.com',
  courseId: 'CS9999',
  name: 'Doe John',
  teamName: 'team 1',
  sectionName: 'section 1',
};

const jamie: Student = {
  name: 'Jamie',
  email: 'jamie@gmail.com',
  joinState: JoinState.NOT_JOINED,
  teamName: 'Team 1',
  sectionName: 'Tutorial Group 1',
  courseId: 'CS101',
};

const TestStudents = { emptyStudent, johnDoe, jamie };

export default TestStudents;
