import { HttpClientTestingModule } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';
import { CourseService } from './course.service';
import { HttpRequestService } from './http-request.service';
import createSpyFromClass from '../test-helpers/create-spy-from-class';
import { ResourceEndpoints } from '../types/api-const';
import { CourseArchiveRequest, CourseCreateRequest, CourseUpdateRequest } from '../types/api-request';

describe('CourseService', () => {
  let spyHttpRequestService: any;
  let service: CourseService;

  beforeEach(() => {
    spyHttpRequestService = createSpyFromClass(HttpRequestService);
    TestBed.configureTestingModule({
      imports: [
        HttpClientTestingModule,
      ],
      providers: [
        { provide: HttpRequestService, useValue: spyHttpRequestService },
      ],
    });
    service = TestBed.inject(CourseService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  it('should execute GET on courses endpoint with course status as an instructor', () => {
    const courseStatus: string = 'active';
    const paramMap: { [key: string]: string } = {
      entitytype: 'instructor',
      coursestatus: courseStatus,
    };
    service.getAllCoursesAsInstructor(courseStatus);
    expect(spyHttpRequestService.get).toHaveBeenCalledWith(ResourceEndpoints.COURSES, paramMap);
  });

  it('should execute GET on course endpoint with course id as an instructor', () => {
    const courseId: string = 'test-id';
    const paramMap: { [key: string]: string } = {
      entitytype: 'instructor',
      courseid: courseId,
    };
    service.getCourseAsInstructor(courseId);
    expect(spyHttpRequestService.get).toHaveBeenCalledWith(ResourceEndpoints.COURSE, paramMap);
  });

  it('should execute GET when getting all courses as student', () => {
    const paramMap: { [key: string]: string } = {
      entitytype: 'student',
    };
    service.getAllCoursesAsStudent();
    expect(spyHttpRequestService.get).toHaveBeenCalledWith(ResourceEndpoints.COURSES, paramMap);
  });

  it('should execute GET when getting specific course as student', () => {
    const courseId: string = 'test-id';
    const paramMap: Record<string, string> = {
      entitytype: 'student',
      courseid: courseId,
    };
    service.getCourseAsStudent(courseId);
    expect(spyHttpRequestService.get).toHaveBeenCalledWith(ResourceEndpoints.COURSE, paramMap);
  });

  it('should GET student courses data of a given google id in masquerade mode', () => {
    const googleId: string = 'test-id';
    const paramMap: { [key: string]: string } = {
      entitytype: 'student',
      user: googleId,
    };
    service.getStudentCoursesInMasqueradeMode(googleId);
    expect(spyHttpRequestService.get).toHaveBeenCalledWith(ResourceEndpoints.COURSES, paramMap);
  });

  it('should GET instructor courses data of a given google id in masquerade mode', () => {
    const googleId: string = 'test-id';
    const activeCoursesParamMap: { [key: string]: string } = {
      coursestatus: 'active',
      entitytype: 'instructor',
      user: googleId,
    };
    const archivedCoursesParamMap: { [key: string]: string } = {
      coursestatus: 'archived',
      entitytype: 'instructor',
      user: googleId,
    };
    service.getInstructorCoursesInMasqueradeMode(googleId);
    expect(spyHttpRequestService.get).toHaveBeenCalledWith(ResourceEndpoints.COURSES, activeCoursesParamMap);
    expect(spyHttpRequestService.get).toHaveBeenCalledWith(ResourceEndpoints.COURSES, archivedCoursesParamMap);
  });

  it('should execute GET when getting all active instructor courses', () => {
    const paramMap: Record<string, string> = {
      entitytype: 'instructor',
      coursestatus: 'active',
    };
    service.getInstructorCoursesThatAreActive();
    expect(spyHttpRequestService.get).toHaveBeenCalledWith(ResourceEndpoints.COURSES, paramMap);
  });

  it('should execute POST to create course', () => {
    const request: CourseCreateRequest = {
      courseId: 'test-id',
      courseName: 'test-name',
      timeZone: 'test-zone',
    };
    const paramMap: { [key: string]: string } = {
      instructorinstitution: 'test-institute',
    };
    service.createCourse('test-institute', request);
    expect(spyHttpRequestService.post).toHaveBeenCalledWith(ResourceEndpoints.COURSE, paramMap, request);
  });

  it('should execute PUT to update course', () => {
    const courseid: string = 'test-id';
    const request: CourseUpdateRequest = {
      courseName: 'test-name',
      timeZone: 'test-zone',
    };
    const paramMap: { [key: string]: string } = { courseid };
    service.updateCourse(courseid, request);
    expect(spyHttpRequestService.put).toHaveBeenCalledWith(ResourceEndpoints.COURSE, paramMap, request);
  });

  it('should execute DELETE to delete course', () => {
    const courseid: string = 'test-id';
    const paramMap: { [key: string]: string } = { courseid };
    service.deleteCourse(courseid);
    expect(spyHttpRequestService.delete).toHaveBeenCalledWith(ResourceEndpoints.COURSE, paramMap);
  });

  it('should execute PUT to archive course', () => {
    const courseid: string = 'test-id';
    const request: CourseArchiveRequest = {
      archiveStatus: true,
    };
    const paramMap: { [key: string]: string } = { courseid };
    service.changeArchiveStatus(courseid, request);
    expect(spyHttpRequestService.put)
        .toHaveBeenCalledWith(ResourceEndpoints.COURSE_ARCHIVE, paramMap, request);
  });

  it('should execute PUT to bin course', () => {
    const courseid: string = 'test-id';
    const paramMap: { [key: string]: string } = { courseid };
    service.binCourse(courseid);
    expect(spyHttpRequestService.put).toHaveBeenCalledWith(ResourceEndpoints.BIN_COURSE, paramMap);
  });

  it('should execute DELETE to restore course', () => {
    const courseid: string = 'test-id';
    const paramMap: { [key: string]: string } = { courseid };
    service.restoreCourse(courseid);
    expect(spyHttpRequestService.delete).toHaveBeenCalledWith(ResourceEndpoints.BIN_COURSE, paramMap);
  });

  it('should execute GET to retrieve join course status', () => {
    const regKey: string = 'ABC';
    const entityType: string = 'instructor';
    const paramMap: { [key: string]: string } = {
      key: regKey,
      entitytype: entityType,
    };
    service.getJoinCourseStatus(regKey, entityType, false);
    expect(spyHttpRequestService.get).toHaveBeenCalledWith(ResourceEndpoints.JOIN, paramMap);

    paramMap['iscreatingaccount'] = 'true';
    service.getJoinCourseStatus(regKey, entityType, true);
    expect(spyHttpRequestService.get).toHaveBeenCalledWith(ResourceEndpoints.JOIN, paramMap);
  });

  it('should execute PUT when joining course', () => {
    const paramMap: Record<string, string> = {
      key: '123',
      entitytype: 'instructor',
    };
    service.joinCourse(paramMap['key'], paramMap['entitytype']);
    expect(spyHttpRequestService.put).toHaveBeenCalledWith(ResourceEndpoints.JOIN, paramMap);
  });

  it('should execute POST to remind unregistered students of a course', () => {
    const courseid: string = 'test-id';
    const paramMap: { [key: string]: string } = { courseid };
    service.remindUnregisteredStudentsForJoin(courseid);
    expect(spyHttpRequestService.post).toHaveBeenCalledWith(ResourceEndpoints.JOIN_REMIND, paramMap);
  });

  it('should execute POST to remind particular student', () => {
    const courseId: string = 'test-id';
    const studentEmail: string = 'test@example.com';
    const paramMap: { [key: string]: string } = {
      courseid: courseId,
      studentemail: studentEmail,
    };
    service.remindStudentForJoin(courseId, studentEmail);
    expect(spyHttpRequestService.post).toHaveBeenCalledWith(ResourceEndpoints.JOIN_REMIND, paramMap);
  });

  it('should execute POST to remind particular instructor', () => {
    const courseId: string = 'test-id';
    const instructorEmail: string = 'test@example.com';
    const paramMap: { [key: string]: string } = {
      courseid: courseId,
      instructoremail: instructorEmail,
    };
    service.remindInstructorForJoin(courseId, instructorEmail);
    expect(spyHttpRequestService.post).toHaveBeenCalledWith(ResourceEndpoints.JOIN_REMIND, paramMap);
  });

  it('should execute GET to check responses for course', () => {
    const courseId: string = 'test-id';
    const paramMap: { [key: string]: string } = {
      entitytype: 'instructor',
      courseid: courseId,
    };
    service.hasResponsesForCourse(courseId);
    expect(spyHttpRequestService.get).toHaveBeenCalledWith(ResourceEndpoints.HAS_RESPONSES, paramMap);
  });

  it('should execute DELETE to remove student from course', () => {
    const courseId: string = 'test-id';
    const studentEmail: string = 'test@example.com';
    const paramsMap: { [key: string]: string } = {
      courseid: courseId,
      studentemail: studentEmail,
    };
    service.removeStudentFromCourse(courseId, studentEmail);
    expect(spyHttpRequestService.delete).toHaveBeenCalledWith(ResourceEndpoints.STUDENT, paramsMap);
  });

  it('should execute GET when getting course section names', () => {
    const paramMap: Record<string, string> = {
      courseid: 'CS3281',
    };
    service.getCourseSectionNames(paramMap['courseid']);
    expect(spyHttpRequestService.get).toHaveBeenCalledWith(ResourceEndpoints.COURSE_SECTIONS, paramMap);
  });
});
