import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
// import { ResourceEndpoints } from '../types/api-const';
import { ApiOutput, MessageOutput } from '../types/api-output';
import { BasicRequest } from '../types/api-request';
// import { CreateNotificationRequest } from '../types/api-request';
import { HttpRequestService } from './http-request.service';

// FIXME: This is a temporary solution to declare classes and constants
// They will be auto generated in api-output and api-request after POST/GET route is merged
export interface CreateNotificationRequest extends BasicRequest {
  startTimestamp: number;
  endTimestamp: number;
  notificationType: string;
  targetUser: string;
  title: string;
  message: string;
}
export enum ResourceEndpoints {
  NOTIFICATION = '/webapi/notification',
}
export interface Notification extends ApiOutput {
  notificationId: string;
  startTimestamp: number;
  endTimestamp: number;
  notificationType: string;
  targetUser: string;
  title: string;
  message: string;
  shown: boolean;
  createTimestamp: number;
}
export interface Notifications extends ApiOutput {
  notifications: Notification[];
}

/**
 * Handles notification related logic injection
 */
@Injectable({
  providedIn: 'root',
})
export class NotificationService {

  constructor(private httpRequestService: HttpRequestService) { }

  /**
   * Creates a notification by calling API.
   */
  createNotification(request: CreateNotificationRequest): Observable<MessageOutput> {
    return this.httpRequestService.post(ResourceEndpoints.NOTIFICATION, {}, request);
  }

  /**
   * Retrieve all notifications by calling API.
   */
  getNotifications(): Observable<Notifications> {
    // TODO: Probably move the isFetchingAll parameter to constants
    const paramMap: Record<string, string> = {
      isFetchingAll: '1',
    };
    return this.httpRequestService.get(ResourceEndpoints.NOTIFICATION, paramMap);
  }
}
