import { TestBed } from '@angular/core/testing';

import { HttpClientTestingModule } from '@angular/common/http/testing';
import { ResourceEndpoints } from '../types/api-endpoints';
import { CourseService } from './course.service';
import { HttpRequestService } from './http-request.service';

describe('CourseService', () => {
  let spyHttpRequestService: any;
  let service: CourseService;

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
    service = TestBed.get(CourseService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  it('should execute GET when getting all courses as student', () => {
    service.getAllCoursesAsStudent();
    const paramMap: { [key: string]: string } = {
      entitytype: 'student',
    };
    expect(spyHttpRequestService.get).toHaveBeenCalledWith(ResourceEndpoints.COURSES, paramMap);
  });

  it('should execute PUT when joining course', () => {
    const paramMap: { [key: string]: string } = {
      key: '123',
      entitytype: 'student',
      instructorinstitution: 'National University of Singapore',
    };
    service.joinCourse(paramMap.key, paramMap.entitytype, paramMap.instructorinstitution);
    expect(spyHttpRequestService.put).toHaveBeenCalledWith(ResourceEndpoints.JOIN, paramMap);
  });
});
