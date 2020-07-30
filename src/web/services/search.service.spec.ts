import { TestBed } from '@angular/core/testing';

import { HttpClientTestingModule } from '@angular/common/http/testing';
import { of } from 'rxjs';
import { StudentListRowModel } from '../app/components/student-list/student-list.component';
import {
  SearchStudentsListRowTable,
} from '../app/pages-instructor/instructor-search-page/student-result-table/student-result-table.component';
import { ResourceEndpoints } from '../types/api-endpoints';
import {
  Course,
  FeedbackSession,
  FeedbackSessionPublishStatus,
  FeedbackSessionSubmissionStatus,
  Instructor,
  InstructorPermissionRole,
  InstructorPrivilege,
  JoinState,
  ResponseVisibleSetting,
  SessionVisibleSetting,
  Student,
  Students,
} from '../types/api-output';
import { HttpRequestService } from './http-request.service';
import { InstructorAccountSearchResult, SearchService, StudentAccountSearchResult } from './search.service';

describe('SearchService', () => {
  let spyHttpRequestService: any;
  let service: SearchService;

  const mockStudents: Students = {
    students: [
      {
        email: 'alice@example.com',
        courseId: 'CS3281',
        name: 'Alice',
        joinState: JoinState.JOINED,
        teamName: 'Team 1',
        sectionName: 'Section 1',
      },
      {
        email: 'bob@example.com',
        courseId: 'CS3281',
        name: 'Bob',
        joinState: JoinState.JOINED,
        teamName: 'Team 1',
        sectionName: 'Section 1',
      },
      {
        email: 'chloe@example.com',
        courseId: 'CS3281',
        name: 'Chloe',
        joinState: JoinState.JOINED,
        teamName: 'Team 1',
        sectionName: 'Section 2',
      },
      {
        email: 'david@example.com',
        courseId: 'CS3282',
        name: 'David',
        joinState: JoinState.JOINED,
        teamName: 'Team 1',
        sectionName: 'Section 2',
      },
    ],
  };
  let coursesWithStudents: SearchStudentsListRowTable[];

  const mockStudent: Student = {
    email: 'alice.b.tmms@gmail.tmt',
    courseId: 'dog.gma-demo',
    name: 'Alice Betsy',
    googleId: 'alice.b.tmms.sampleData',
    comments: "This student's name is Alice Betsy",
    key: 'keyheehee',
    institute: 'NUS',
    joinState: JoinState.JOINED,
    teamName: 'Team 1',
    sectionName: 'Tutorial Group 1',
  };

  const mockInstructor: Instructor = {
    googleId: 'test@example.com',
    courseId: 'dog.gma-demo',
    email: 'dog@gmail.com',
    isDisplayedToStudents: true,
    displayedToStudentsAs: 'Instructor',
    name: 'Hi',
    role: InstructorPermissionRole.INSTRUCTOR_PERMISSION_ROLE_COOWNER,
    joinState: JoinState.JOINED,
  };

  const mockSessions: FeedbackSession[] = [
    {
      courseId: 'dog.gma-demo',
      timeZone: 'Asia/Singapore',
      feedbackSessionName: 'First team feedback session',
      instructions: 'Please give your feedback based on the following questions.',
      submissionStartTimestamp: 1333295940000,
      submissionEndTimestamp: 1333382340000,
      submissionStatus: FeedbackSessionSubmissionStatus.CLOSED,
      publishStatus: FeedbackSessionPublishStatus.PUBLISHED,
      createdAtTimestamp: 1333324740000,
      gracePeriod: 1,
      sessionVisibleSetting: SessionVisibleSetting.CUSTOM,
      responseVisibleSetting: ResponseVisibleSetting.CUSTOM,
      isClosingEmailEnabled: false,
      isPublishedEmailEnabled: false,
    },
    {
      courseId: 'dog.gma-demo',
      timeZone: 'Asia/Singapore',
      feedbackSessionName: 'Second team feedback session',
      instructions: 'Please give your feedback based on the following questions.',
      submissionStartTimestamp: 1333295940000,
      submissionEndTimestamp: 2122300740000,
      submissionStatus: FeedbackSessionSubmissionStatus.OPEN,
      publishStatus: FeedbackSessionPublishStatus.NOT_PUBLISHED,
      createdAtTimestamp: 1333324740000,
      gracePeriod: 1,
      sessionVisibleSetting: SessionVisibleSetting.CUSTOM,
      responseVisibleSetting: ResponseVisibleSetting.CUSTOM,
      isClosingEmailEnabled: false,
      isPublishedEmailEnabled: false,
    },
  ];

  const mockPrivileges: InstructorPrivilege[] = [{
    canModifyCourse: true,
    canModifySession: true,
    canModifyStudent: true,
    canModifyInstructor: true,
    canViewStudentInSections: true,
    canModifySessionCommentsInSections: true,
    canViewSessionInSections: true,
    canSubmitSessionInSections: true,
    requestId: 'checkyourprivilege',
  }];

  const mockCourse: Course = {
    courseId: 'dog.gma-demo',
    courseName: 'Sample Course 101',
    timeZone: 'UTC',
    creationTimestamp: 1585487897502,
    deletionTimestamp: 0,
    requestId: '5e80aa3c00007918934385f5',
  };

  beforeEach(() => {
    spyHttpRequestService = {
      get: jest.fn(),
      post: jest.fn(),
      put: jest.fn(),
      delete: jest.fn(),
    };
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
      providers: [
        { provide: HttpRequestService, useValue: spyHttpRequestService },
      ],
    });
    service = TestBed.inject(SearchService);
    coursesWithStudents = service.getCoursesWithStudents(mockStudents);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  it('should execute GET when searching for students', () => {
    service.searchStudents('Alice', 'instructor');
    const paramMap: { [key: string]: string } = {
      searchkey: 'Alice',
      entitytype: 'instructor',
    };
    expect(spyHttpRequestService.get).toHaveBeenCalledWith(
      '/search/students',
      paramMap,
    );
  });

  it('should parse students into courses with sections correctly', () => {
    const { students }: { students: Student[] } = mockStudents;

    // Number of courses should match
    expect(coursesWithStudents.length).toEqual(
      Array.from(new Set(students.map((s: Student) => s.courseId))).length,
    );

    // Number of sections in a course should match
    expect(
      Array.from(
        new Set(
          coursesWithStudents
            .filter((t: SearchStudentsListRowTable) => t.courseId === students[0].courseId)[0]
            .students.map((studentModel: StudentListRowModel) => studentModel.student.sectionName),
        ),
      ).length,
    ).toEqual(
      Array.from(
        new Set(
          students
            .filter((s: Student) => s.courseId === students[0].courseId)
            .map((s: Student) => s.sectionName),
        ),
      ).length,
    );

    // Number of students in a section should match
    expect(
        coursesWithStudents
          .filter((t: SearchStudentsListRowTable) => t.courseId === students[0].courseId)[0]
          .students.filter((s: StudentListRowModel) => s.student.sectionName === students[0].sectionName)
          .length,
    ).toEqual(
      students.filter((s: Student) => s.sectionName === students[0].sectionName).length,
    );
  });

  it('should execute GET when fetching privileges', () => {
    spyHttpRequestService.get.mockImplementation((endpoint: string) => {
      expect(endpoint).toEqual(ResourceEndpoints.INSTRUCTOR_PRIVILEGE);
      return of<InstructorPrivilege>({
        canModifyCourse: true,
        canModifySession: true,
        canModifyStudent: true,
        canModifyInstructor: true,
        canViewStudentInSections: true,
        canModifySessionCommentsInSections: true,
        canViewSessionInSections: true,
        canSubmitSessionInSections: true,
      });
    });
    service.getPrivileges(coursesWithStudents);

    for (const course of coursesWithStudents) {
      for (const studentModel of course.students) {
        expect(spyHttpRequestService.get).toHaveBeenCalledWith(
          ResourceEndpoints.INSTRUCTOR_PRIVILEGE,
          {
            courseid: course.courseId,
            sectionname: studentModel.student.sectionName,
          },
        );
      }
    }
  });

  it('should combine privileges and course data correctly', () => {
    const basePrivilege: InstructorPrivilege = {
      canModifyCourse: true,
      canModifySession: true,
      canModifyStudent: true,
      canModifyInstructor: true,
      canViewStudentInSections: true,
      canModifySessionCommentsInSections: true,
      canViewSessionInSections: true,
      canSubmitSessionInSections: true,
    };
    const mockPrivilegesArray: InstructorPrivilege[] = [
      basePrivilege,
      {
        ...basePrivilege,
        canViewStudentInSections: false,
        canModifyStudent: true,
      },
      {
        ...basePrivilege,
        canViewStudentInSections: true,
        canModifyStudent: false,
      },
      {
        ...basePrivilege,
        canViewStudentInSections: false,
        canModifyStudent: false,
      },
    ];
    service.combinePrivileges([coursesWithStudents, mockPrivilegesArray]);

    const course1Student1: StudentListRowModel = coursesWithStudents[0].students[0];
    expect(course1Student1.isAllowedToViewStudentInSection).toEqual(true);
    expect(course1Student1.isAllowedToModifyStudent).toEqual(true);

    const course1Student2: StudentListRowModel = coursesWithStudents[0].students[1];
    expect(course1Student2.isAllowedToViewStudentInSection).toEqual(false);
    expect(course1Student2.isAllowedToModifyStudent).toEqual(true);

    const course1Student3: StudentListRowModel = coursesWithStudents[0].students[2];
    expect(course1Student3.isAllowedToViewStudentInSection).toEqual(true);
    expect(course1Student3.isAllowedToModifyStudent).toEqual(false);

    const course2Student1: StudentListRowModel = coursesWithStudents[1].students[0];
    expect(course2Student1.isAllowedToViewStudentInSection).toEqual(false);
    expect(course2Student1.isAllowedToModifyStudent).toEqual(false);

    expect(mockPrivilegesArray.length).toEqual(0);
  });

  it('should execute GET when searching for instructors', () => {
    service.searchInstructors('YoyoImCoronavirus');
    const paramMap: { [key: string]: string } = {
      searchkey: 'YoyoImCoronavirus',
    };
    expect(spyHttpRequestService.get).toHaveBeenCalledWith(
      '/search/instructors',
      paramMap,
    );
  });

  it('should join students accurately when calling as admin', () => {
    const result: StudentAccountSearchResult = service.joinAdminStudent(
      mockStudent,
      { instructors: [mockInstructor] },
      mockCourse,
      { feedbackSessions: mockSessions },
      mockPrivileges,
    );
    // Not using snapshots because of issues with time zones
    expect(result.comments).toBe("This student's name is Alice Betsy");
    expect(result.courseId).toBe('dog.gma-demo');
    expect(result.courseJoinLink).toBe('http://localhost:4200/web/join?key' +
      '=keyheehee&studentemail=alice.b.tmms%40gmail.tmt&courseid=dog.gma-demo&entitytype=student');
    expect(result.courseName).toBe('Sample Course 101');
    expect(result.email).toBe('alice.b.tmms@gmail.tmt');
    expect(result.manageAccountLink).toBe('/web/admin/accounts?instructorid=alice.b.tmms.sampleData');
  });

  it('should join instructors accurately when calling as admin', () => {
    const result: InstructorAccountSearchResult = service
      .joinAdminInstructor(mockInstructor, mockCourse);
    expect(result).toMatchSnapshot();
  });
});
