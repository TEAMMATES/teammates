import { TestBed } from '@angular/core/testing';

import { HttpClientTestingModule } from '@angular/common/http/testing';
import { of } from 'rxjs';
import DoneCallback = jest.DoneCallback;
import { ResourceEndpoints } from '../types/api-endpoints';
import { Instructor, InstructorPermissionRole, Instructors, JoinState } from '../types/api-output';
import { HttpRequestService } from './http-request.service';
import { InstructorService } from './instructor.service';

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
    service = TestBed.get(InstructorService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  it('should execute GET', () => {
    service.getInstructorsFromCourse('CS3281');
    const paramMap: { [key: string]: string } = {
      courseid: 'CS3281',
    };
    expect(spyHttpRequestService.get).toHaveBeenCalledWith(ResourceEndpoints.INSTRUCTORS, paramMap);
  });

  it('should send the correct course id', (done: DoneCallback) => {
    const mockInstructors: Instructors = {
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

    spyHttpRequestService.get.mockImplementation((endpoint: string, paramMap: { [key: string]: string }) => {
      expect(endpoint).toEqual(ResourceEndpoints.INSTRUCTORS);
      const courseid: string = paramMap.courseid;
      return of<Instructors>({
        instructors: mockInstructors.instructors.filter((instructor: Instructor) => instructor.courseId === courseid),
      });
    });

    service.getInstructorsFromCourse('CS3281').subscribe((instructors: Instructors) => {
      expect(instructors.instructors).toEqual(mockInstructors.instructors
        .filter((instructor: Instructor) => instructor.courseId === 'CS3281'));
      done();
    });
  });
});
