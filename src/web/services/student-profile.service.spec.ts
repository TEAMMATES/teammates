import { TestBed } from '@angular/core/testing';

import { HttpClientTestingModule } from '@angular/common/http/testing';
import { StudentProfileService } from './student-profile.service';
import { HttpRequestService } from "./http-request.service";
import { ResourceEndpoints } from "../types/api-endpoints";

describe('StudentProfileService', () => {
  let spyHttpRequestService: any;
  let service: StudentProfileService;

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
    service = TestBed.get(StudentProfileService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  it('should execute GET on student profile picture endpoint', () => {
    service.getProfilePicture();
    expect(spyHttpRequestService.get).toHaveBeenCalledWith(ResourceEndpoints.STUDENT_PROFILE_PICTURE, {}, 'blob');
  });

  it('should execute POST on student profile picture endpoint', () => {
    // TODO
    spyOn(FormData.prototype, "append");
    const formData: {} = {};
    service.postProfilePicture(formData);
    expect(spyHttpRequestService.get).toHaveBeenCalledWith(ResourceEndpoints.STUDENT_PROFILE_PICTURE, {}, formData);
  });
});
