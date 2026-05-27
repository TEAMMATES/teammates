import { Injectable, inject } from '@angular/core';
import { Observable } from 'rxjs';
import { HttpRequestService } from './http-request.service';
import { ResourceEndpoints } from '../types/api-const';
import {
  MessageOutput,
  Notification,
  Notifications,
  NotificationTargetUser,
  ReadNotifications,
} from '../types/api-output';
import {
  MarkNotificationAsReadRequest,
  NotificationCreateRequest,
  NotificationUpdateRequest,
} from '../types/api-request';

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
  getNotifications(): Observable<Notifications> {
    return this.httpRequestService.get(ResourceEndpoints.NOTIFICATIONS);
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
   * Retrieves all active notifications for specific target user types.
   */
  getNotificationsForTargetUsers(userTypes: NotificationTargetUser[]): Observable<Notifications> {
    const paramMap: Record<string, string[]> = {
      usertype: userTypes,
    };
    return this.httpRequestService.get(ResourceEndpoints.NOTIFICATIONS, paramMap);
  }

  /**
   * Retrieves read notifications for the user.
   */
  getReadNotifications(): Observable<ReadNotifications> {
    return this.httpRequestService.get(ResourceEndpoints.NOTIFICATION_READ);
  }
}
