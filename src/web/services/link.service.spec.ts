import { TestBed } from '@angular/core/testing';

import { RouterTestingModule } from '@angular/router/testing';
import { LinkService } from './link.service';
import { Instructor, InstructorPermissionRole, JoinState, Student } from '../types/api-output';

describe('Link Service', () => {
  let service: LinkService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [RouterTestingModule],
    });
    service = TestBed.inject(LinkService);
  });

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
    key: 'impicklerick',
    role: InstructorPermissionRole.INSTRUCTOR_PERMISSION_ROLE_COOWNER,
    joinState: JoinState.JOINED,
  };

  it('should generate the course join link of the student', () => {
    expect(service.generateCourseJoinLink(mockStudent, 'student'))
      .toBe(`${window.location.origin}/web/join?key=keyheehee&entitytype=student`);
  });

  it('should generate the course join link for instructors', () => {
    expect(service.generateCourseJoinLink(mockInstructor, 'instructor'))
      .toBe(`${window.location.origin}/web/join?key=impicklerick&entitytype=instructor`);
  });

  it('should generate the account registration link of the instructor', () => {
    expect(service.generateAccountRegistrationLink('keyheehee'))
      .toBe(`${window.location.origin}/web/join?iscreatingaccount=true&key=keyheehee`);
  });

  it('should generate the home page link', () => {
    expect(service.generateHomePageLink('blahblah', '/comeseetheopressioninherentinthesystem'))
      .toBe('/web/comeseetheopressioninherentinthesystem?user=blahblah');
  });

  it('should generate the manage account link', () => {
    expect(service.generateManageAccountLink('hello there', '/generalkenobiyouareaboldone'))
      .toBe('/web/generalkenobiyouareaboldone?instructorid=hello%20there');
  });

  it('should generate the student profile page link', () => {
    expect(service.generateProfilePageLink(mockStudent, 'from my point of view the jedi are evil'))
      .toBe('/web/instructor/courses/student/details?courseid=dog.gma-demo&studentemail=alice.'
            + 'b.tmms%40gmail.tmt&user=from%20my%20point%20of%20view%20the%20jedi%20are%20evil');
  });

  it('should generate the submit url', () => {
    expect(service.generateSubmitUrl(mockStudent, 'did you ever hear the tragedy of darth plagueis the wise', false))
      .toBe(`${window.location.origin}/web/sessions/submission?key=keyheehee`
            + '&fsname=did%20you%20ever%20hear%20the%20tragedy%20of%20darth%20plagueis%20the%20wise'
            + '&courseid=dog.gma-demo');

    expect(service.generateSubmitUrl(mockInstructor, 'did you ever hear the tragedy of darth plagueis the wise', true))
      .toBe(`${window.location.origin}/web/sessions/submission?key=impicklerick`
            + '&fsname=did%20you%20ever%20hear%20the%20tragedy%20of%20darth%20plagueis%20the%20wise'
            + '&courseid=dog.gma-demo&entitytype=instructor');
  });

  it('should generate the result url', () => {
    expect(service.generateResultUrl(mockStudent, 'another happy landing', false))
      .toBe(`${window.location.origin}/web/sessions/result?`
            + 'key=keyheehee&fsname=another%20happy%20landing&courseid=dog.gma-demo');

    expect(service.generateResultUrl(mockInstructor, 'another happy landing', true))
      .toBe(`${window.location.origin}/web/sessions/result?`
            + 'key=impicklerick&fsname=another%20happy%20landing&courseid=dog.gma-demo&entitytype=instructor');
  });

  it('filterEmptyParams should filter empty params', () => {
    const params: { [key: string]: string } = { courseId: '#123?123', filterThis: '' };
    service.filterEmptyParams(params);
    expect(Object.keys(params).length).toEqual(1);
  });
});
