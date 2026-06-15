import { provideHttpClient } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';
import { CourseService } from './course.service';
import { HttpRequestService } from './http-request.service';
import { createMockHttpRequestService, type MockHttpRequestService } from '../test-helpers/mock-http-request';
import { ResourceEndpoints } from '../types/api-const';
import { CourseCreateRequest, CourseUpdateRequest, RegKeyRequest } from '../types/api-request';

describe('CourseService', () => {
  let spyHttpRequestService: MockHttpRequestService;
  let service: CourseService;

  beforeEach(() => {
    spyHttpRequestService = createMockHttpRequestService();
    TestBed.configureTestingModule({
      providers: [
        { provide: HttpRequestService, useValue: spyHttpRequestService },
        provideHttpClient(),
        provideHttpClientTesting(),
      ],
    });
    service = TestBed.inject(CourseService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  it('should execute GET on courses endpoint with course status as an instructor', () => {
    const courseStatus = 'active';
    const paramMap: { [key: string]: string } = {
      entitytype: 'instructor',
      coursestatus: courseStatus,
    };
    service.getAllCoursesAsInstructor(courseStatus);
    expect(spyHttpRequestService.get).toHaveBeenCalledWith(ResourceEndpoints.COURSES, paramMap);
  });

  it('should execute GET on course endpoint with course id as an instructor', () => {
    const courseId = 'test-id';
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
    const courseId = 'test-id';
    const paramMap: Record<string, string> = {
      entitytype: 'student',
      courseid: courseId,
    };
    service.getCourseAsStudent(courseId);
    expect(spyHttpRequestService.get).toHaveBeenCalledWith(ResourceEndpoints.COURSE, paramMap);
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
      instituteId: 'test-institute',
    };
    service.createCourse(request);
    expect(spyHttpRequestService.post).toHaveBeenCalledWith(ResourceEndpoints.COURSE, {}, request);
  });

  it('should execute PUT to update course', () => {
    const courseid = 'test-id';
    const request: CourseUpdateRequest = {
      courseName: 'test-name',
      timeZone: 'test-zone',
    };
    const paramMap: { [key: string]: string } = { courseid };
    service.updateCourse(courseid, request);
    expect(spyHttpRequestService.put).toHaveBeenCalledWith(ResourceEndpoints.COURSE, paramMap, request);
  });

  it('should execute DELETE to delete course', () => {
    const courseid = 'test-id';
    const paramMap: { [key: string]: string } = { courseid };
    service.deleteCourse(courseid);
    expect(spyHttpRequestService.delete).toHaveBeenCalledWith(ResourceEndpoints.COURSE, paramMap);
  });

  it('should execute PUT to bin course', () => {
    const courseid = 'test-id';
    const paramMap: { [key: string]: string } = { courseid };
    service.binCourse(courseid);
    expect(spyHttpRequestService.put).toHaveBeenCalledWith(ResourceEndpoints.BIN_COURSE, paramMap);
  });

  it('should execute DELETE to restore course', () => {
    const courseid = 'test-id';
    const paramMap: { [key: string]: string } = { courseid };
    service.restoreCourse(courseid);
    expect(spyHttpRequestService.delete).toHaveBeenCalledWith(ResourceEndpoints.BIN_COURSE, paramMap);
  });

  it('should execute GET to retrieve join course status', () => {
    const regKey = 'ABC';
    const paramMap: { [key: string]: string } = {
      key: regKey,
    };
    service.getJoinCourseStatus(regKey);
    expect(spyHttpRequestService.get).toHaveBeenCalledWith(ResourceEndpoints.JOIN, paramMap);
  });

  it('should execute PUT when joining course', () => {
    const regKeyRequest: RegKeyRequest = {
      key: '123',
    };
    service.joinCourse(regKeyRequest);
    expect(spyHttpRequestService.put).toHaveBeenCalledWith(ResourceEndpoints.JOIN, {}, regKeyRequest);
  });

  it('should execute POST to remind unregistered students of a course', () => {
    const courseid = 'test-id';
    const paramMap: { [key: string]: string } = { courseid };
    service.remindUnregisteredStudentsForJoin(courseid);
    expect(spyHttpRequestService.post).toHaveBeenCalledWith(ResourceEndpoints.JOIN_REMIND, paramMap);
  });

  it('should execute POST to remind particular user', () => {
    const userId = 'test-user-id';
    const paramMap: { [key: string]: string } = {
      userid: userId,
    };
    service.remindUserForJoin(userId);
    expect(spyHttpRequestService.post).toHaveBeenCalledWith(ResourceEndpoints.JOIN_REMIND, paramMap);
  });

  it('should execute GET when getting course sections', () => {
    const paramMap: Record<string, string> = {
      courseid: 'CS3281',
    };
    service.getCourseSections(paramMap['courseid']);
    expect(spyHttpRequestService.get).toHaveBeenCalledWith(ResourceEndpoints.COURSE_SECTIONS, paramMap);
  });

  it('should execute POST on demo course endpoint with timezone string', () => {
    const testId = 'test-id';
    const testTimezone = 'UTC';
    const paramMap: Record<string, string> = {
      id: testId,
      timezone: testTimezone,
    };
    service.createDemoCourse({ accountVerificationRequestId: testId, timezone: testTimezone });
    expect(spyHttpRequestService.post).toHaveBeenCalledWith(ResourceEndpoints.DEMO_COURSE, paramMap);
  });
});
