import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
// import { ResourceEndpoints } from '../types/api-const';
import { MessageOutput } from '../types/api-output';
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
}
