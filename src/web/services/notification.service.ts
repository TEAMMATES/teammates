import { Injectable, inject } from '@angular/core';
import { Observable } from 'rxjs';
import { HttpRequestService } from './http-request.service';
import { ResourceEndpoints } from '../types/api-const';
import { MessageOutput, Notification, Notifications, ReadNotifications } from '../types/api-output';
import {
  MarkNotificationAsReadRequest,
  NotificationCreateRequest,
  NotificationUpdateRequest,
} from '../types/api-request';

export interface GetNotificationsParams {
  targetUsers: string[];
  isFetchingActive: boolean;
}

/**
 * Handles notification related logic injection
 */
@Injectable({
  providedIn: 'root',
})
export class NotificationService {
  private httpRequestService = inject(HttpRequestService);

  /**
   * Creates a notification by calling API.
   */
  createNotification(request: NotificationCreateRequest): Observable<Notification> {
    return this.httpRequestService.post(ResourceEndpoints.NOTIFICATION, {}, request);
  }

  /**
   * Retrieves all notifications by calling API.
   */
  getNotifications(params: GetNotificationsParams): Observable<Notifications> {
    const paramMap: Record<string, string | string[]> = {
      usertype: params.targetUsers,
      isfetchingactive: String(params.isFetchingActive),
    };
    return this.httpRequestService.get(ResourceEndpoints.NOTIFICATIONS, paramMap);
  }

  /**
   * Updates a notification by calling API.
   */
  updateNotification(request: NotificationUpdateRequest, notificationId: string): Observable<Notification> {
    const paramMap: Record<string, string> = {
      notificationid: notificationId,
    };
    return this.httpRequestService.put(ResourceEndpoints.NOTIFICATION, paramMap, request);
  }

  /**
   * Deletes a notification by calling API.
   */
  deleteNotification(notificationId: string): Observable<MessageOutput> {
    const paramMap: Record<string, string> = {
      notificationid: notificationId,
    };
    return this.httpRequestService.delete(ResourceEndpoints.NOTIFICATION, paramMap);
  }

  /**
   * Marks a notification as read.
   */
  markNotificationAsRead(request: MarkNotificationAsReadRequest): Observable<ReadNotifications> {
    return this.httpRequestService.post(ResourceEndpoints.NOTIFICATION_READ, {}, request);
  }

  /**
   * Retrieves read notifications for the user.
   */
  getReadNotifications(): Observable<ReadNotifications> {
    return this.httpRequestService.get(ResourceEndpoints.NOTIFICATION_READ);
  }
}
