import { TestBed } from '@angular/core/testing';

import { HttpClientTestingModule } from '@angular/common/http/testing';
import { ResourceEndpoints } from '../types/api-const';
import { StudentProfileUpdateRequest } from '../types/api-request';
import { HttpRequestService } from './http-request.service';
import { StudentProfileService } from './student-profile.service';

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
    service = TestBed.inject(StudentProfileService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  it('should execute GET on student profile endpoint with params', () => {
    const studentEmail: string = 'test@123.com';
    const courseId: string = 'test-course';
    const paramsMap: Record<string, string> = {
      studentemail: studentEmail,
      courseid: courseId,
    };
    service.getStudentProfile(studentEmail, courseId);
    expect(spyHttpRequestService.get).toHaveBeenCalledWith(ResourceEndpoints.STUDENT_PROFILE, paramsMap);
  });

  it('should execute GET on student profile endpoint without email', () => {
    const studentEmail: string = 'test@123.com';
    service.getStudentProfile(studentEmail, '');
    expect(spyHttpRequestService.get).toHaveBeenCalledWith(ResourceEndpoints.STUDENT_PROFILE);
  });

  it('should execute GET on student profile endpoint without params', () => {
    service.getStudentProfile();
    expect(spyHttpRequestService.get).toHaveBeenCalledWith(ResourceEndpoints.STUDENT_PROFILE);
  });

  it('should execute GET on student profile picture endpoint', () => {
    service.getProfilePicture();
    expect(spyHttpRequestService.get).toHaveBeenCalledWith(ResourceEndpoints.STUDENT_PROFILE_PICTURE, {}, 'blob');
  });

  it('should execute PUT on student profile endpoint', () => {
    const googleId: string = '';
    const requestBody: StudentProfileUpdateRequest = new class implements StudentProfileUpdateRequest {
      email: string = '';
      existingNationality: string = '';
      gender: string = '';
      institute: string = '';
      moreInfo: string = '';
      nationality: string = '';
      shortName: string = '';
    };
    const paramsMap: Record<string, string> = {
      googleid: googleId,
    };
    service.updateStudentProfile(googleId, requestBody);
    expect(spyHttpRequestService.put).toHaveBeenCalledWith(ResourceEndpoints.STUDENT_PROFILE, paramsMap, requestBody);
  });

  it('should execute POST on student profile picture endpoint', () => {
    const formData: FormData = new FormData();
    service.postProfilePicture(formData);
    expect(spyHttpRequestService.post).toHaveBeenCalledWith(ResourceEndpoints.STUDENT_PROFILE_PICTURE, {}, formData);
  });

  it('should execute DELETE on student profile picture endpoint', () => {
    const paramsMap: Record<string, string> = {};
    service.deleteProfilePicture(paramsMap);
    expect(spyHttpRequestService.delete).toHaveBeenCalledWith(ResourceEndpoints.STUDENT_PROFILE_PICTURE, paramsMap);
  });
});
