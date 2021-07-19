import { TestBed } from '@angular/core/testing';

import { HttpClientTestingModule } from '@angular/common/http/testing';
import { ResourceEndpoints } from '../types/api-const';
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
} from '../types/api-output';
import { HttpRequestService } from './http-request.service';
import { InstructorAccountSearchResult, SearchService, StudentAccountSearchResult } from './search.service';

describe('SearchService', () => {
  let spyHttpRequestService: any;
  let service: SearchService;

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
      ResourceEndpoints.SEARCH_STUDENTS,
      paramMap,
    );
  });

  it('should execute GET when searching for instructors', () => {
    service.searchInstructors('YoyoImCoronavirus');
    const paramMap: { [key: string]: string } = {
      searchkey: 'YoyoImCoronavirus',
    };
    expect(spyHttpRequestService.get).toHaveBeenCalledWith(
      ResourceEndpoints.SEARCH_INSTRUCTORS,
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
    expect(result.comments).toBe("This student's name is Alice Betsy");
    expect(result.courseId).toBe('dog.gma-demo');
    expect(result.courseJoinLink).toBe(`${window.location.origin}/web/join?key` +
      '=keyheehee&studentemail=alice.b.tmms%40gmail.tmt&courseid=dog.gma-demo&entitytype=student');
    expect(result.courseName).toBe('Sample Course 101');
    expect(result.email).toBe('alice.b.tmms@gmail.tmt');
    expect(result.manageAccountLink).toBe('/web/admin/accounts?instructorid=alice.b.tmms.sampleData');
  });

  it('should join instructors accurately when calling as admin', () => {
    const result: InstructorAccountSearchResult = service
      .joinAdminInstructor(mockInstructor, mockCourse);
    expect(result.courseId).toBe('dog.gma-demo');
    expect(result.courseJoinLink).toBe(`${window.location.origin}/web/join?entitytype=instructor`);
    expect(result.courseName).toBe('Sample Course 101');
    expect(result.email).toBe('dog@gmail.com');
    expect(result.manageAccountLink).toBe('/web/admin/accounts?instructorid=test%40example.com');
    expect(result.homePageLink).toBe('/web/instructor/home?user=test%40example.com');
  });
});
