import { provideHttpClient } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';
import { provideRouter } from '@angular/router';
import { HttpRequestService } from './http-request.service';
import { createMockHttpRequestService, type MockHttpRequestService } from '../test-helpers/mock-http-request';
import {
  AccountVerificationRequestSearchResult,
  InstructorAccountSearchResult,
  SearchService,
  StudentAccountSearchResult,
} from './search.service';
import { TimezoneService } from './timezone.service';
import { ResourceEndpoints } from '../types/api-const';
import {
  AccountVerificationRequest,
  AccountVerificationRequestStatus,
  Course,
  Instructor,
  InstructorPermissionRole,
  InstructorPrivilege,
  JoinState,
  Student,
} from '../types/api-output';

describe('SearchService', () => {
  let spyHttpRequestService: MockHttpRequestService;
  let service: SearchService;
  let timezoneService: TimezoneService;

  const mockStudent: Student = {
    userId: 'student-alice',
    accountId: '00000000-0000-4000-8000-00000000000a',
    email: 'alice.brown@example.edu',
    courseId: 'cs1010-demo',
    courseName: 'Introduction to Software Engineering',
    institute: 'National University of Singapore',
    name: 'Alice Brown',
    comments: 'Student record used for search service tests',
    key: 'student-key-001',
    joinState: JoinState.JOINED,
    teamName: 'Team 1',
    teamId: 'team-1',
    sectionName: 'Tutorial Group 1',
    sectionId: 'section-1',
  };

  const mockInstructorA: Instructor = {
    userId: '00000000-0000-4000-8000-000000000001',
    accountId: '00000000-0000-4000-8000-000000000001',
    courseId: 'cs1010-demo',
    courseName: 'Introduction to Software Engineering',
    institute: 'National University of Singapore',
    email: 'lee.instructor@example.edu',
    isDisplayedToStudents: true,
    displayedToStudentsAs: 'Instructor',
    name: 'Lee Wong',
    role: InstructorPermissionRole.COOWNER,
    joinState: JoinState.JOINED,
  };

  const mockInstructorB: Instructor = {
    userId: '00000000-0000-4000-8000-000000000002',
    accountId: '00000000-0000-4000-8000-000000000002',
    courseId: 'cs1010-demo',
    courseName: 'Introduction to Software Engineering',
    institute: 'National University of Singapore',
    email: 'brown.instructor@example.edu',
    isDisplayedToStudents: true,
    displayedToStudentsAs: 'Instructor',
    name: 'Brown Taylor',
    role: InstructorPermissionRole.CUSTOM,
    joinState: JoinState.JOINED,
  };

  const mockInstructorC: Instructor = {
    userId: '00000000-0000-4000-8000-000000000003',
    accountId: '00000000-0000-4000-8000-000000000003',
    courseId: 'cs1010-demo',
    courseName: 'Introduction to Software Engineering',
    institute: 'National University of Singapore',
    email: 'chen.instructor@example.edu',
    isDisplayedToStudents: true,
    displayedToStudentsAs: 'Instructor',
    name: 'Chen Lim',
    role: InstructorPermissionRole.CUSTOM,
    joinState: JoinState.JOINED,
  };

  const mockPrivilegeA: InstructorPrivilege = {
    privileges: {
      courseLevel: {
        canModifyCourse: true,
        canModifySession: true,
        canModifyStudent: true,
        canModifyInstructor: true,
        canViewStudent: true,
        canModifySessionComments: true,
        canViewSession: true,
        canSubmitSession: true,
      },
      sectionLevel: {},
      sessionLevel: {},
    },
  };

  const mockPrivilegeB: InstructorPrivilege = {
    privileges: {
      courseLevel: {
        canModifyCourse: true,
        canModifySession: true,
        canModifyStudent: true,
        canModifyInstructor: false,
        canViewStudent: true,
        canModifySessionComments: true,
        canViewSession: true,
        canSubmitSession: true,
      },
      sectionLevel: {},
      sessionLevel: {},
    },
  };

  const mockPrivilegeC: InstructorPrivilege = {
    privileges: {
      courseLevel: {
        canModifyCourse: false,
        canModifySession: false,
        canModifyStudent: false,
        canModifyInstructor: true,
        canViewStudent: false,
        canModifySessionComments: false,
        canViewSession: false,
        canSubmitSession: false,
      },
      sectionLevel: {},
      sessionLevel: {},
    },
  };

  const mockCourse: Course = {
    courseId: 'cs1010-demo',
    courseName: 'Introduction to Software Engineering',
    institute: 'National University of Singapore',
    country: 'SG',
    instituteId: 'test-institute-id',
    timeZone: 'UTC',
    creationTimestamp: 1585487897502,
    deletionTimestamp: 0,
  };

  const mockAccountVerificationRequest: AccountVerificationRequest = {
    accountVerificationRequestId: '132efa02-b208-4195-a262-a8eae25ceb95',
    createdAt: 1585487897502,
    name: 'Jordan Tan',
    institute: 'National University of Singapore',
    country: 'SG',
    email: 'jordan.tan@example.edu',
    comments: 'Account verification request used for search service tests',
    status: AccountVerificationRequestStatus.APPROVED,
  };

  beforeEach(() => {
    spyHttpRequestService = createMockHttpRequestService();
    TestBed.configureTestingModule({
      providers: [
        { provide: HttpRequestService, useValue: spyHttpRequestService },
        provideRouter([]),
        provideHttpClient(),
        provideHttpClientTesting(),
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
    expect(spyHttpRequestService.get).toHaveBeenCalledWith(ResourceEndpoints.SEARCH_STUDENTS, paramMap);
  });

  it('should execute GET when searching for instructors', () => {
    service.searchInstructors('Lee Wong');
    const paramMap: { [key: string]: string } = {
      searchkey: 'Lee Wong',
    };
    expect(spyHttpRequestService.get).toHaveBeenCalledWith(ResourceEndpoints.SEARCH_INSTRUCTORS, paramMap);
  });

  it('should execute GET when searching for account verification requests', () => {
    service.searchAccountVerificationRequests('Account Verification Request');
    const paramMap: { [key: string]: string } = {
      searchkey: 'Account Verification Request',
    };
    expect(spyHttpRequestService.get).toHaveBeenCalledWith(
      ResourceEndpoints.SEARCH_ACCOUNT_VERIFICATION_REQUESTS,
      paramMap,
    );
  });

  it('should join students accurately when calling as admin', () => {
    const result: StudentAccountSearchResult = service.joinAdminStudent(
      mockStudent,
      { instructors: [mockInstructorA] },
      mockCourse,
      [mockPrivilegeA],
    );
    expect(result.comments).toBe('Student record used for search service tests');
    expect(result.courseId).toBe('cs1010-demo');
    expect(result.courseName).toBe('Introduction to Software Engineering');
    expect(result.email).toBe('alice.brown@example.edu');
    expect(result.manageAccountLink).toBe('/web/admin/accounts?accountid=00000000-0000-4000-8000-00000000000a');
  });

  it('should join students with correct profile page link when course has co-owner', () => {
    const result: StudentAccountSearchResult = service.joinAdminStudent(
      mockStudent,
      { instructors: [mockInstructorC, mockInstructorB, mockInstructorA] },
      mockCourse,
      [mockPrivilegeC, mockPrivilegeB, mockPrivilegeA],
    );
    expect(result.profilePageLink).toBe(
      '/web/instructor/courses/student/details?' +
        'courseid=cs1010-demo&userid=student-alice&masqueradeaccountid=00000000-0000-4000-8000-000000000001',
    );
  });

  it('should join students with correct profile page link when course has no co-owner', () => {
    const result: StudentAccountSearchResult = service.joinAdminStudent(
      mockStudent,
      { instructors: [mockInstructorB, mockInstructorC] },
      mockCourse,
      [mockPrivilegeB, mockPrivilegeC],
    );
    expect(result.profilePageLink).toBe(
      '/web/instructor/courses/student/details?' +
        'courseid=cs1010-demo&userid=student-alice&masqueradeaccountid=00000000-0000-4000-8000-000000000003',
    );
  });

  it('should join instructors accurately when calling as admin', () => {
    const result: InstructorAccountSearchResult = service.joinAdminInstructor(mockInstructorA, mockCourse);
    expect(result.courseId).toBe('cs1010-demo');
    expect(result.courseName).toBe('Introduction to Software Engineering');
    expect(result.email).toBe('lee.instructor@example.edu');
    expect(result.manageAccountLink).toBe('/web/admin/accounts?accountid=00000000-0000-4000-8000-000000000001');
  });

  it('should join account verification requests accurately when timezone can be guessed and instructor is registered', () => {
    vi.spyOn(timezoneService, 'guessTimezone').mockReturnValue('Asia/Singapore');
    const accountVerificationRequest: AccountVerificationRequest = {
      ...mockAccountVerificationRequest,
      createdDemoCourseAt: 1685487897502,
      status: AccountVerificationRequestStatus.APPROVED,
    };
    const result: AccountVerificationRequestSearchResult =
      service.joinAdminAccountVerificationRequest(accountVerificationRequest);

    expect(result.accountVerificationRequestId).toBe('132efa02-b208-4195-a262-a8eae25ceb95');
    expect(result.email).toBe('jordan.tan@example.edu');
    expect(result.institute).toBe('National University of Singapore');
    expect(result.name).toBe('Jordan Tan');
    expect(result.createdAtText).toBe('Sun, 29 Mar 2020, 09:18 PM +08:00');
    expect(result.createdDemoCourseAtText).toBe('Wed, 31 May 2023, 07:04 AM +08:00');
    expect(result.registrationLink).toBe(
      `${globalThis.location.origin}/web/instructor-welcome?accountVerificationRequestId=132efa02-b208-4195-a262-a8eae25ceb95`,
    );
  });

  it('should join account verification requests accurately when timezone cannot be guessed and instructor is not registered', () => {
    vi.spyOn(timezoneService, 'guessTimezone').mockReturnValue('');
    const result: AccountVerificationRequestSearchResult =
      service.joinAdminAccountVerificationRequest(mockAccountVerificationRequest);

    expect(result.email).toBe('jordan.tan@example.edu');
    expect(result.institute).toBe('National University of Singapore');
    expect(result.name).toBe('Jordan Tan');
    expect(result.createdAtText).toBe('Sun, 29 Mar 2020, 01:18 PM +00:00');
    expect(result.createdDemoCourseAtText).toBe(null);
    expect(result.registrationLink).toBe(
      `${globalThis.location.origin}/web/instructor-welcome?accountVerificationRequestId=132efa02-b208-4195-a262-a8eae25ceb95`,
    );
  });
});
