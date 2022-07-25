import { HttpClientTestingModule } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';
import { RouterTestingModule } from '@angular/router/testing';
import { ResourceEndpoints } from '../types/api-const';
import {
  AccountRequest,
  AccountRequestStatus,
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
import {
  AccountRequestSearchResult,
  InstructorAccountSearchResult,
  SearchService,
  StudentAccountSearchResult,
} from './search.service';
import { TimezoneService } from './timezone.service';

describe('SearchService', () => {
  let spyHttpRequestService: any;
  let service: SearchService;
  let timezoneService: TimezoneService;

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

  const mockInstructorA: Instructor = {
    googleId: 'test@example.com',
    courseId: 'dog.gma-demo',
    email: 'dog@gmail.com',
    isDisplayedToStudents: true,
    displayedToStudentsAs: 'Instructor',
    name: 'Hi',
    key: 'impicklerick',
    role: InstructorPermissionRole.INSTRUCTOR_PERMISSION_ROLE_COOWNER,
    joinState: JoinState.JOINED,
  };

  const mockInstructorB: Instructor = {
    googleId: 'insB',
    courseId: 'dog.gma-demo',
    email: 'cat@gmail.com',
    isDisplayedToStudents: true,
    displayedToStudentsAs: 'Instructor',
    name: 'Cat',
    key: 'qwertyuiop',
    role: InstructorPermissionRole.INSTRUCTOR_PERMISSION_ROLE_CUSTOM,
    joinState: JoinState.JOINED,
  };

  const mockInstructorC: Instructor = {
    googleId: 'insC',
    courseId: 'dog.gma-demo',
    email: 'animal@gmail.com',
    isDisplayedToStudents: true,
    displayedToStudentsAs: 'Instructor',
    name: 'QWQ',
    key: 'vjvkjsnffwicvvcsc',
    role: InstructorPermissionRole.INSTRUCTOR_PERMISSION_ROLE_CUSTOM,
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
      studentDeadlines: {},
      instructorDeadlines: {},
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
      studentDeadlines: {},
      instructorDeadlines: {},
    },
  ];

  const mockPrivilegeA: InstructorPrivilege = {
    privileges: {
      courseLevel: {
        canModifyCourse: true,
        canModifySession: true,
        canModifyStudent: true,
        canModifyInstructor: true,
        canViewStudentInSections: true,
        canModifySessionCommentsInSections: true,
        canViewSessionInSections: true,
        canSubmitSessionInSections: true,
      },
      sectionLevel: {},
      sessionLevel: {},
    },
    requestId: 'checkyourprivilege',
  };

  const mockPrivilegeB: InstructorPrivilege = {
    privileges: {
      courseLevel: {
        canModifyCourse: true,
        canModifySession: true,
        canModifyStudent: true,
        canModifyInstructor: false,
        canViewStudentInSections: true,
        canModifySessionCommentsInSections: true,
        canViewSessionInSections: true,
        canSubmitSessionInSections: true,
      },
      sectionLevel: {},
      sessionLevel: {},
    },
    requestId: '123gyijuyol56w8refw123ce8f5',
  };

  const mockPrivilegeC: InstructorPrivilege = {
    privileges: {
      courseLevel: {
        canModifyCourse: false,
        canModifySession: false,
        canModifyStudent: false,
        canModifyInstructor: true,
        canViewStudentInSections: false,
        canModifySessionCommentsInSections: false,
        canViewSessionInSections: false,
        canSubmitSessionInSections: false,
      },
      sectionLevel: {},
      sessionLevel: {},
    },
    requestId: '98pa78342kjnk22s1213rsdff4',
  };

  const mockCourse: Course = {
    courseId: 'dog.gma-demo',
    courseName: 'Sample Course 101',
    institute: 'Test Institute',
    timeZone: 'UTC',
    creationTimestamp: 1585487897502,
    deletionTimestamp: 0,
    requestId: '5e80aa3c00007918934385f5',
  };

  const mockAccountRequest: AccountRequest = {
    registrationKey: 'regkey',
    createdAt: 1585487897502,
    name: 'Test Instructor',
    institute: 'Test Institute',
    email: 'test@example.com',
    homePageUrl: '',
    comments: '',
    status: AccountRequestStatus.APPROVED,
    lastProcessedAt: 1585487897502,
  };

  beforeEach(() => {
    spyHttpRequestService = {
      get: jest.fn(),
      post: jest.fn(),
      put: jest.fn(),
      delete: jest.fn(),
    };
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule, RouterTestingModule],
      providers: [
        { provide: HttpRequestService, useValue: spyHttpRequestService },
      ],
    });
    service = TestBed.inject(SearchService);
    timezoneService = TestBed.inject(TimezoneService);
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

  it('should execute GET when searching for account requests', () => {
    service.searchAccountRequests('Account Request');
    const paramMap: { [key: string]: string } = {
      searchkey: 'Account Request',
    };
    expect(spyHttpRequestService.get).toHaveBeenCalledWith(
      ResourceEndpoints.SEARCH_ACCOUNT_REQUESTS,
      paramMap,
    );
  });

  it('should join students accurately when calling as admin', () => {
    const result: StudentAccountSearchResult = service.joinAdminStudent(
      mockStudent,
      { instructors: [mockInstructorA] },
      mockCourse,
      { feedbackSessions: mockSessions },
      [mockPrivilegeA],
    );
    expect(result.comments).toBe("This student's name is Alice Betsy");
    expect(result.courseId).toBe('dog.gma-demo');
    expect(result.courseJoinLink).toBe(`${window.location.origin}/web/join?key=keyheehee&entitytype=student`);
    expect(result.courseName).toBe('Sample Course 101');
    expect(result.email).toBe('alice.b.tmms@gmail.tmt');
    expect(result.manageAccountLink).toBe('/web/admin/accounts?instructorid=alice.b.tmms.sampleData');
  });

  it('should join students with correct profile page link when course has co-owner', () => {
    const result: StudentAccountSearchResult = service.joinAdminStudent(
      mockStudent,
      { instructors: [mockInstructorC, mockInstructorB, mockInstructorA] },
      mockCourse,
      { feedbackSessions: mockSessions },
      [mockPrivilegeC, mockPrivilegeB, mockPrivilegeA],
    );
    expect(result.profilePageLink).toBe('/web/instructor/courses/student/details?'
      + 'courseid=dog.gma-demo&studentemail=alice.b.tmms%40gmail.tmt&user=test%40example.com');
  });

  it('should join students with correct profile page link when course has no co-owner', () => {
    const result: StudentAccountSearchResult = service.joinAdminStudent(
      mockStudent,
      { instructors: [mockInstructorB, mockInstructorC] },
      mockCourse,
      { feedbackSessions: mockSessions },
      [mockPrivilegeB, mockPrivilegeC],
    );
    expect(result.profilePageLink).toBe('/web/instructor/courses/student/details?'
      + 'courseid=dog.gma-demo&studentemail=alice.b.tmms%40gmail.tmt&user=insC');
  });

  it('should join instructors accurately when calling as admin', () => {
    const result: InstructorAccountSearchResult = service
      .joinAdminInstructor(mockInstructorA, mockCourse, { feedbackSessions: mockSessions });
    expect(result.courseId).toBe('dog.gma-demo');
    expect(result.courseJoinLink).toBe(`${window.location.origin}/web/join?key=impicklerick&entitytype=instructor`);
    expect(result.courseName).toBe('Sample Course 101');
    expect(result.email).toBe('dog@gmail.com');
    expect(result.manageAccountLink).toBe('/web/admin/accounts?instructorid=test%40example.com');
    expect(result.homePageLink).toBe('/web/instructor/home?user=test%40example.com');
  });

  it('should join account requests accurately when timezone can be guessed and instructor is registered', () => {
    jest.spyOn(timezoneService, 'guessTimezone').mockReturnValue('Asia/Singapore');
    const accountRequest: AccountRequest = { ...mockAccountRequest, registeredAt: 1685487897502 };
    const result: AccountRequestSearchResult = service.joinAdminAccountRequest(accountRequest);

    expect(result.email).toBe('test@example.com');
    expect(result.institute).toBe('Test Institute');
    expect(result.name).toBe('Test Instructor');
    expect(result.createdAtText).toBe('Sun, 29 Mar 2020, 09:18 PM +08:00');
    expect(result.registeredAtText).toBe('Wed, 31 May 2023, 07:04 AM +08:00');
    expect(result.registrationLink).toBe(`${window.location.origin}/web/join?iscreatingaccount=true&key=regkey`);
  });

  it('should join account requests accurately when timezone cannot be guessed and instructor is not registered', () => {
    jest.spyOn(timezoneService, 'guessTimezone').mockReturnValue('');
    const result: AccountRequestSearchResult = service.joinAdminAccountRequest(mockAccountRequest);

    expect(result.email).toBe('test@example.com');
    expect(result.institute).toBe('Test Institute');
    expect(result.name).toBe('Test Instructor');
    expect(result.createdAtText).toBe('Sun, 29 Mar 2020, 01:18 PM +00:00');
    expect(result.registeredAtText).toBe(null);
    expect(result.registrationLink).toBe(`${window.location.origin}/web/join?iscreatingaccount=true&key=regkey`);
  });
});
