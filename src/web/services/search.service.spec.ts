import { provideHttpClient } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';
import { provideRouter } from '@angular/router';
import { InstructorAccountSearchResult, SearchService, StudentAccountSearchResult } from './search.service';
import { Course, Instructor, InstructorPermissionRole, JoinState, Student } from '../types/api-output';

describe('SearchService', () => {
  let service: SearchService;

  const mockStudent: Student = {
    userId: 'student-alice',
    accountId: '00000000-0000-4000-8000-00000000000a',
    email: 'alice.brown@example.edu',
    courseId: 'cs1010-demo',
    courseName: 'Introduction to Software Engineering',
    institute: 'National University of Singapore',
    name: 'Alice Brown',
    comments: 'Student record used for search service tests',
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

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [provideRouter([]), provideHttpClient(), provideHttpClientTesting()],
    });
    service = TestBed.inject(SearchService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  it('should join students accurately when calling as admin', () => {
    const result: StudentAccountSearchResult = service.joinAdminStudent(mockStudent, mockCourse);
    expect(result.comments).toBe('Student record used for search service tests');
    expect(result.courseId).toBe('cs1010-demo');
    expect(result.courseName).toBe('Introduction to Software Engineering');
    expect(result.email).toBe('alice.brown@example.edu');
    expect(result.manageAccountLink).toBe('/web/admin/accounts/00000000-0000-4000-8000-00000000000a');
  });

  it('should join instructors accurately when calling as admin', () => {
    const result: InstructorAccountSearchResult = service.joinAdminInstructor(mockInstructorA, mockCourse);
    expect(result.courseId).toBe('cs1010-demo');
    expect(result.courseName).toBe('Introduction to Software Engineering');
    expect(result.email).toBe('lee.instructor@example.edu');
    expect(result.manageAccountLink).toBe('/web/admin/accounts/00000000-0000-4000-8000-000000000001');
  });
});
