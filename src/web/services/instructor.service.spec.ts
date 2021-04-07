import { TestBed } from '@angular/core/testing';

import { HttpClientTestingModule } from '@angular/common/http/testing';
import { of } from 'rxjs';
import { ResourceEndpoints } from '../types/api-const';
import { Instructor, Instructors, JoinState } from '../types/api-output';
import { InstructorCreateRequest, InstructorPermissionRole } from '../types/api-request';
import { HttpRequestService } from './http-request.service';
import { InstructorService } from './instructor.service';
import DoneCallback = jest.DoneCallback;

const defaultRequestBody: InstructorCreateRequest = {
  id: '123',
  name: 'John Doe',
  email: 'johndoe@gmail.com',
  role: InstructorPermissionRole.INSTRUCTOR_PERMISSION_ROLE_COOWNER,
  displayName: 'John Doe',
  isDisplayedToStudent: true,
} as InstructorCreateRequest;

const defaultInstructors: Instructors = {
  instructors: [{
    googleId: '',
    courseId: 'CS3281',
    email: '',
    isDisplayedToStudents: true,
    displayedToStudentsAs: '',
    name: '',
    role: InstructorPermissionRole.INSTRUCTOR_PERMISSION_ROLE_COOWNER,
    joinState: JoinState.JOINED,
  }, {
    googleId: '',
    courseId: 'CS3282',
    email: '',
    isDisplayedToStudents: true,
    displayedToStudentsAs: '',
    name: '',
    role: InstructorPermissionRole.INSTRUCTOR_PERMISSION_ROLE_COOWNER,
    joinState: JoinState.JOINED,
  }],
};

describe('InstructorService', () => {
  let spyHttpRequestService: any;
  let service: InstructorService;

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
    service = TestBed.inject(InstructorService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  it('should execute GET when getting instructors for a course', () => {
    service.loadInstructors({ courseId: 'CS3281' });
    const paramMap: Record<string, string> = {
      courseid: 'CS3281',
    };
    expect(spyHttpRequestService.get).toHaveBeenCalledWith(ResourceEndpoints.INSTRUCTORS, paramMap);
  });

  it('should execute POST when creating an instructor for a course', () => {
    service.createInstructor({ courseId: 'CS3281', requestBody: defaultRequestBody });
    const paramMap: Record<string, string> = {
      courseid: 'CS3281',
    };
    expect(spyHttpRequestService.post).toHaveBeenCalledWith(ResourceEndpoints.INSTRUCTOR, paramMap, defaultRequestBody);
  });

  it('should execute PUT when updating an instructor for a course', () => {
    service.updateInstructor({ courseId: 'CS3281', requestBody: defaultRequestBody });
    const paramMap: Record<string, string> = {
      courseid: 'CS3281',
    };
    expect(spyHttpRequestService.put).toHaveBeenCalledWith(ResourceEndpoints.INSTRUCTOR, paramMap, defaultRequestBody);
  });

  it('should execute DELETE when deleting an instructor for a course', () => {
    service.deleteInstructor({ courseId: 'CS3281', instructorEmail: 'John Doe' });
    const paramMap: Record<string, string> = {
      courseid: 'CS3281',
      instructoremail: 'John Doe',
    };
    expect(spyHttpRequestService.delete).toHaveBeenCalledWith(ResourceEndpoints.INSTRUCTOR, paramMap);
  });

  it('should send the correct course id', (done: DoneCallback) => {
    spyHttpRequestService.get.mockImplementation((endpoint: string, paramMap: Record<string, string>) => {
      expect(endpoint).toEqual(ResourceEndpoints.INSTRUCTORS);
      const courseid: string = paramMap.courseid;
      return of<Instructors>({
        instructors: defaultInstructors.instructors
            .filter((instructor: Instructor) => instructor.courseId === courseid),
      });
    });

    service.loadInstructors({ courseId: 'CS3281' }).subscribe((instructors: Instructors) => {
      expect(instructors.instructors).toEqual(defaultInstructors.instructors
        .filter((instructor: Instructor) => instructor.courseId === 'CS3281'));
      done();
    });
  });

  it('should call get when loading instructor privileges', () => {
    const paramMap: Record<string, string> = {
      courseid: 'CS3281',
    };

    service.loadInstructorPrivilege({ courseId: paramMap.courseid });
    expect(spyHttpRequestService.get).toHaveBeenCalledWith(ResourceEndpoints.INSTRUCTOR_PRIVILEGE, paramMap);
  });

  it('should call put when updating instructor privileges', () => {
    const paramMap: Record<string, string> = {
      courseid: 'CS3281',
      instructoremail: 'johndoe@gmail.com',
    };

    service.updateInstructorPrivilege({
      courseId: paramMap.courseid,
      instructorEmail: paramMap.instructoremail,
      requestBody: {},
    });
    expect(spyHttpRequestService.put).toHaveBeenCalledWith(ResourceEndpoints.INSTRUCTOR_PRIVILEGE, paramMap, {});
  });
});
