import { HttpClientTestingModule } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';
import { HttpRequestService } from './http-request.service';
import { NotificationService } from './notification.service';
import createSpyFromClass from '../test-helpers/create-spy-from-class';
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
  let spyHttpRequestService: any;
  let service: NotificationService;

  beforeEach(() => {
    spyHttpRequestService = createSpyFromClass(HttpRequestService);
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
      providers: [{ provide: HttpRequestService, useValue: spyHttpRequestService }],
    });
    service = TestBed.inject(NotificationService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  it('should execute GET when retrieving notifications', () => {
    service.getNotifications();
    expect(spyHttpRequestService.get).toHaveBeenCalledWith(
      ResourceEndpoints.NOTIFICATIONS,
    );
  });

  it('should execute POST when creating notifications', () => {
    const paramsMap: Record<string, string> = {};
    service.createNotification(requestBody);
    expect(spyHttpRequestService.post).toHaveBeenCalledWith(
      ResourceEndpoints.NOTIFICATION,
      paramsMap,
      requestBody,
    );
  });

  it('should execute PUT when updating notifications', () => {
    const paramsMap: Record<string, string> = {
      notificationid: 'notification1',
    };
    service.updateNotification(requestBody, 'notification1');
    expect(spyHttpRequestService.put).toHaveBeenCalledWith(
      ResourceEndpoints.NOTIFICATION,
      paramsMap,
      requestBody,
    );
  });

  it('should execute DELETE when deleting notifications', () => {
    const paramsMap: Record<string, string> = {
      notificationid: 'notification1',
    };
    service.deleteNotification('notification1');
    expect(spyHttpRequestService.delete).toHaveBeenCalledWith(
      ResourceEndpoints.NOTIFICATION,
      paramsMap,
    );
  });
});
