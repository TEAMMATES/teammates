import { Student } from '../../../types/api-output';

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

const TestStudents = { emptyStudent, johnDoe };

export default TestStudents;
