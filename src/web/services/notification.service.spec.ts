import { provideHttpClient } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';
import { HttpRequestService } from './http-request.service';
import { NotificationService } from './notification.service';
import { createMockHttpRequestService, type MockHttpRequestService } from '../test-helpers/mock-http-request';
import { ResourceEndpoints } from '../types/api-const';
import { NotificationBasicRequest, NotificationStyle, NotificationTargetUser } from '../types/api-request';

const requestBody: NotificationBasicRequest = {
  startTimestamp: 0,
  endTimestamp: 0,
  style: NotificationStyle.SECONDARY,
  targetUser: NotificationTargetUser.GENERAL,
  title: '',
  message: '',
};

describe('NotificationService', () => {
  let spyHttpRequestService: MockHttpRequestService;
  let service: NotificationService;

  beforeEach(() => {
    spyHttpRequestService = createMockHttpRequestService();
    TestBed.configureTestingModule({
      providers: [
        { provide: HttpRequestService, useValue: spyHttpRequestService },
        provideHttpClient(),
        provideHttpClientTesting(),
      ],
    });
    service = TestBed.inject(NotificationService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  it('should execute GET when retrieving notifications', () => {
    const paramsMap: Record<string, string | string[]> = {
      usertype: [NotificationTargetUser.STUDENT, NotificationTargetUser.GENERAL],
      isfetchingactive: 'true',
    };
    service.getNotifications({
      targetUsers: [NotificationTargetUser.STUDENT, NotificationTargetUser.GENERAL],
      isFetchingActive: true,
    });
    expect(spyHttpRequestService.get).toHaveBeenCalledWith(ResourceEndpoints.NOTIFICATIONS, paramsMap);
  });

  it('should execute POST when creating notifications', () => {
    const paramsMap: Record<string, string> = {};
    service.createNotification(requestBody);
    expect(spyHttpRequestService.post).toHaveBeenCalledWith(ResourceEndpoints.NOTIFICATION, paramsMap, requestBody);
  });

  it('should execute PUT when updating notifications', () => {
    const paramsMap: Record<string, string> = {
      notificationid: 'notification1',
    };
    service.updateNotification(requestBody, 'notification1');
    expect(spyHttpRequestService.put).toHaveBeenCalledWith(ResourceEndpoints.NOTIFICATION, paramsMap, requestBody);
  });

  it('should execute DELETE when deleting notifications', () => {
    const paramsMap: Record<string, string> = {
      notificationid: 'notification1',
    };
    service.deleteNotification('notification1');
    expect(spyHttpRequestService.delete).toHaveBeenCalledWith(ResourceEndpoints.NOTIFICATION, paramsMap);
  });
});
