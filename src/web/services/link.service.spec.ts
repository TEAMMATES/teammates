import { TestBed } from '@angular/core/testing';

import { provideRouter } from '@angular/router';
import { LinkService } from './link.service';
import { JoinState, Student } from '../types/api-output';

describe('Link Service', () => {
  let service: LinkService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [provideRouter([])],
    });
    service = TestBed.inject(LinkService);
  });

  const mockStudent: Student = {
    userId: 'student-alice',
    email: 'alice.brown@example.edu',
    courseId: 'cs1010-demo',
    courseName: 'Introduction to Software Engineering',
    institute: 'National University of Singapore',
    name: 'Alice Brown',
    comments: 'Student record used for link generation tests',
    key: 'student-key-001',
    joinState: JoinState.JOINED,
    teamName: 'Team 1',
    teamId: 'team-1',
    sectionName: 'Tutorial Group 1',
    sectionId: 'section-1',
  };

  it('should generate the account registration link of the instructor', () => {
    expect(service.generateAccountRegistrationLink('student-key-001')).toBe(
      `${globalThis.location.origin}/web/instructor-welcome?accountVerificationRequestId=student-key-001`,
    );
  });

  it('should generate the home page link', () => {
    expect(service.generateHomePageLink('account-123', '/course-dashboard')).toBe(
      '/web/course-dashboard?masqueradeaccountid=account-123',
    );
  });

  it('should generate the manage account link', () => {
    expect(service.generateManageAccountLink('account 123', '/manage-account')).toBe(
      '/web/manage-account?accountid=account%20123',
    );
  });

  it('should generate the student profile page link', () => {
    expect(service.generateProfilePageLink(mockStudent, 'account-admin-01')).toBe(
      '/web/instructor/courses/student/details?courseid=cs1010-demo&userid=student-alice' +
        '&masqueradeaccountid=account-admin-01',
    );
  });

  it('filterEmptyParams should filter empty params', () => {
    const params: { [key: string]: string } = { courseId: '#123?123', filterThis: '' };
    service.filterEmptyParams(params);
    expect(Object.keys(params).length).toEqual(1);
  });
});
