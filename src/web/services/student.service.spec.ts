import { TestBed } from '@angular/core/testing';

import { HttpClientTestingModule } from '@angular/common/http/testing';
import { ResourceEndpoints } from '../types/api-endpoints';
import { StudentUpdateRequest } from '../types/api-request';
import { HttpRequestService } from './http-request.service';
import { StudentService } from './student.service';

const defaultStudentUpdateRequest: StudentUpdateRequest = {
  name: 'John Doe',
  email: 'johndoe@gmail.com',
  team: '',
  section: '',
  comments: '',
  isSessionSummarySendEmail: true,
};

describe('StudentService', () => {
  let spyHttpRequestService: any;
  let service: StudentService;

  beforeEach(() => {
    spyHttpRequestService = {
      get: jest.fn(),
      post: jest.fn(),
      put: jest.fn(),
      delete: jest.fn(),
    };
    TestBed.configureTestingModule({
      imports: [
        HttpClientTestingModule,
      ],
      providers: [
        { provide: HttpRequestService, useValue: spyHttpRequestService },
      ],
    });
    service = TestBed.inject(StudentService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  it('should execute PUT when updating students in a course', () => {
    const paramMap: Record<string, string> = {
      courseid: 'CS3281',
      studentemail: 'johndoe@gmail.com',
    };

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

    service.regenerateStudentCourseLinks(paramMap.courseid, paramMap.studentemail);

    expect(spyHttpRequestService.post)
        .toHaveBeenCalledWith(ResourceEndpoints.STUDENT_COURSE_LINKS_REGENERATION, paramMap);
  });

  it('should execute GET when loading students in a course as CSV', () => {
    const paramMap: Record<string, string> = {
      courseid: 'CS3281',
    };

    const responseType: string = 'text';

    service.loadStudentListAsCsv({
      courseId: paramMap.courseid,
    });

    expect(spyHttpRequestService.get)
        .toHaveBeenCalledWith(ResourceEndpoints.STUDENTS_CSV, paramMap, responseType);
  });
});
