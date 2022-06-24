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

const alice: Student = {
  email: 'alice.b.tmms@gmail.tmt',
  courseId: 'test.exa-demo',
  name: 'Alice Betsy',
  comments: "This student's name is Alice Betsy",
  joinState: JoinState.JOINED,
  teamName: 'Team 1',
  sectionName: 'Tutorial Group 1',
};

const benny: Student = {
  email: 'benny.c.tmms@gmail.tmt',
  courseId: 'test.exa-demo',
  name: 'Benny Charles',
  comments: "This student's name is Benny Charles",
  joinState: JoinState.JOINED,
  teamName: 'Team 1',
  sectionName: 'Tutorial Group 1',
};

const charlie: Student = {
  email: 'charlie.d.tmms@gmail.tmt',
  courseId: 'test.exa-demo',
  name: 'Charlie Davis',
  comments: "This student's name is Charlie Davis",
  joinState: JoinState.JOINED,
  teamName: 'Team 2',
  sectionName: 'Tutorial Group 2',
};

const danny: Student = {
  email: 'danny.e.tmms@gmail.tmt',
  courseId: 'test.exa-demo',
  name: 'Danny Engrid',
  comments: "This student's name is Danny Engrid",
  joinState: JoinState.JOINED,
  teamName: 'Team 1',
  sectionName: 'Tutorial Group 1',
};

const emma: Student = {
  email: 'emma.f.tmms@gmail.tmt',
  courseId: 'test.exa-demo',
  name: 'Emma Farrell',
  comments: "This student's name is Emma Farrell",
  joinState: JoinState.JOINED,
  teamName: 'Team 1',
  sectionName: 'Tutorial Group 1',
};

const francis: Student = {
  email: 'francis.g.tmms@gmail.tmt',
  courseId: 'test.exa-demo',
  name: 'Francis Gabriel',
  comments: "This student's name is Francis Gabriel",
  joinState: JoinState.JOINED,
  teamName: 'Team 2',
  sectionName: 'Tutorial Group 2',
};

const gene: Student = {
  email: 'gene.h.tmms@gmail.tmt',
  courseId: 'test.exa-demo',
  name: 'Gene Hudson',
  comments: "This student's name is Gene Hudson",
  joinState: JoinState.JOINED,
  teamName: 'Team 2',
  sectionName: 'Tutorial Group 2',
};

const hugh: Student = {
  email: 'hugh.i.tmms@gmail.tmt',
  courseId: 'test.exa-demo',
  name: 'Hugh Ivanov',
  comments: "This student's name is Hugh Ivanov",
  joinState: JoinState.NOT_JOINED,
  teamName: 'Team 3',
  sectionName: 'Tutorial Group 2',
};

const TestStudents = {
  emptyStudent,
  johnDoe,
  jamie,
  alice,
  benny,
  charlie,
  danny,
  emma,
  francis,
  gene,
  hugh,
};

export default TestStudents;
