import { TestBed } from '@angular/core/testing';

import { HttpClientTestingModule } from '@angular/common/http/testing';
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

  it('should execute GET', () => {

  });
});
