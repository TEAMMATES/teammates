import { TestBed } from '@angular/core/testing';

import { Instructor, InstructorPermissionRole, JoinState, Student } from '../types/api-output';
import { LinkService } from './link.service';

describe('Link Service', () => {
  let service: LinkService;

  beforeEach(() => {
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
    expect(service.generateCourseJoinLinkStudent(mockStudent))
      .toBe(`${window.location.origin}/web/join?key=keyheehee&studentemail=alice.b.t`
            + 'mms%40gmail.tmt&courseid=dog.gma-demo&entitytype=student');
  });

  it('should generate the course join link for instructors', () => {
    expect(service.generateCourseJoinLinkInstructor(mockInstructor))
      .toBe(`${window.location.origin}/web/join?key=impicklerick&entitytype=instructor`);
  });

  it('should generate the home page link', () => {
    expect(service.generateHomePageLink('blahblah', '/comeseetheopressioninherentinthesystem'))
      .toBe('/web/comeseetheopressioninherentinthesystem?user=blahblah');
  });

  it('should generate the manage account link', () => {
    expect(service.generateManageAccountLink('hello there', '/generalkenobiyouareaboldone'))
      .toBe('/web/generalkenobiyouareaboldone?instructorid=hello%20there');
  });

  it('should generate the record page link', () => {
    expect(service.generateRecordsPageLink(mockStudent, 'from my point of view the jedi are evil'))
      .toBe(`${window.location.origin}/web/instructor/students/records?courseid=dog.gma-demo&studentemail=alice.`
            + 'b.tmms%40gmail.tmt&user=from%20my%20point%20of%20view%20the%20jedi%20are%20evil');
  });

  it('should generate the submit url', () => {
    expect(service.generateSubmitUrl(mockStudent, 'did you ever hear the tragedy of darth plagueis the wise'))
      .toBe(`${window.location.origin}/web/sessions/submission?courseid=dog.gma-demo&key=keyheehe`
            + 'e&studentemail=alice.b.tmms%40gmail.tmt&fsname=did%20you%20'
            + 'ever%20hear%20the%20tragedy%20of%20darth%20plagueis%20the%20wise');
  });

  it('should generate the result url', () => {
    expect(service.generateResultUrl(mockStudent, 'another happy landing'))
      .toBe(`${window.location.origin}/web/sessions/result?courseid`
            + '=dog.gma-demo&key=keyheehee&studentemail=alice.b.tmms%40gmail.tmt&fsname=another%20happy%20landing');
  });

  it('filterEmptyParams should filter empty params', () => {
    const params: {[key: string]: string} = { courseId: '#123?123', filterThis: '' };
    service.filterEmptyParams(params);
    expect(Object.keys(params).length).toEqual(1);
  });
});
