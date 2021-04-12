import { TestBed } from '@angular/core/testing';

import { HttpClientTestingModule } from '@angular/common/http/testing';
import { of } from 'rxjs';
import { ResourceEndpoints } from '../types/api-const';
import { Course, Students } from '../types/api-output';
import { StudentUpdateRequest } from '../types/api-request';
import { CourseService } from './course.service';
import { HttpRequestService } from './http-request.service';
import { StudentService } from './student.service';
import DoneCallback = jest.DoneCallback;

const defaultStudentUpdateRequest: StudentUpdateRequest = {
  name: 'John Doe',
  email: 'johndoe@gmail.com',
  team: '',
  section: '',
  comments: '',
  isSessionSummarySendEmail: true,
};

const studentCsvListTester:
    (courseId: string, service: StudentService, spyCourseService: any, done: DoneCallback) => void =
    (courseId: string, service: StudentService, spyCourseService: any, done: DoneCallback): void => {
      const course: Course = require(`./test-data/${courseId}`).course;
      const students: Students = require(`./test-data/${courseId}`).students;
      spyOn(spyCourseService, 'getCourseAsInstructor').and.returnValue(of(course));
      spyOn(service, 'getStudentsFromCourse').and.returnValue(of(students));
      service.loadStudentListAsCsv({ courseId }).subscribe((csvResult: string) => {
        expect(csvResult).toMatchSnapshot();
        done();
      });
    };

describe('StudentService', () => {
  let spyHttpRequestService: any;
  let spyCourseService: any;
  let service: StudentService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [
        HttpClientTestingModule,
      ],
      providers: [HttpRequestService, CourseService],
    });
    service = TestBed.inject(StudentService);
    spyHttpRequestService = TestBed.inject(HttpRequestService);
    spyCourseService = TestBed.inject(CourseService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  it('should execute PUT when updating students in a course', () => {
    const paramMap: Record<string, string> = {
      courseid: 'CS3281',
      studentemail: 'johndoe@gmail.com',
    };
    spyOn(spyHttpRequestService, 'put').and.stub();

    service.updateStudent({
      courseId: paramMap.courseid,
      studentEmail: paramMap.studentemail,
      requestBody: defaultStudentUpdateRequest,
    });

    expect(spyHttpRequestService.put)
        .toHaveBeenCalledWith(ResourceEndpoints.STUDENT, paramMap, defaultStudentUpdateRequest);
  });

  it('should execute DELETE when deleting all students in a course', () => {
    const paramMap: Record<string, string> = {
      courseid: 'CS3281',
    };
    spyOn(spyHttpRequestService, 'delete').and.stub();

    service.deleteAllStudentsFromCourse({
      courseId: paramMap.courseid,
    });

    expect(spyHttpRequestService.delete)
        .toHaveBeenCalledWith(ResourceEndpoints.STUDENTS, paramMap);
  });

  it('should execute POST when regenerating key of a student in a course', () => {
    const paramMap: Record<string, string> = {
      courseid: 'CS3281',
      studentemail: 'johndoe@gmail.com',
    };
    spyOn(spyHttpRequestService, 'post').and.stub();

    service.regenerateStudentCourseLinks(paramMap.courseid, paramMap.studentemail);

    expect(spyHttpRequestService.post)
        .toHaveBeenCalledWith(ResourceEndpoints.STUDENT_COURSE_LINKS_REGENERATION, paramMap);
  });

  it('should generate course student list with section as csv', (done: DoneCallback) => {
    studentCsvListTester('studentCsvListWithSection', service, spyCourseService, done);
  });

  it('should generate course student list without section as csv', (done: DoneCallback) => {
    studentCsvListTester('studentCsvListWithoutSection', service, spyCourseService, done);
  });
});
