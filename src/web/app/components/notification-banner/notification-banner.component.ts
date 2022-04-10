import { Component, Input, OnInit } from '@angular/core';
import { NotificationService } from '../../../services/notification.service';
import { Notification, Notifications, NotificationTargetUser } from '../../../types/api-output';
import { collapseAnim } from '../teammates-common/collapse-anim';

/**
 * Banner used to display notifications to the user.
 */
@Component({
  selector: 'tm-notification-banner',
  templateUrl: './notification-banner.component.html',
  styleUrls: ['./notification-banner.component.scss'],
  animations: [collapseAnim],
})
export class NotificationBannerComponent implements OnInit {

  @Input()
  isStudent: boolean = false;

  @Input()
  isInstructor: boolean = false;

  isShown: boolean = true;
  userType: NotificationTargetUser = NotificationTargetUser.GENERAL;
  notifications: Notification[] = [];

  constructor(private notificationService: NotificationService) { }

  ngOnInit(): void {
    if (this.isStudent) {
      this.userType = NotificationTargetUser.STUDENT;
    }
    if (this.isInstructor) {
      this.userType = NotificationTargetUser.INSTRUCTOR;
    }
    if (this.userType !== NotificationTargetUser.GENERAL) {
      this.fetchNotifications();
    }
  }

  fetchNotifications(): void {
    this.notificationService.getNotificationsByTargetUser(this.userType)
      .subscribe((response: Notifications) => {
        this.notifications = response.notifications;
      });
  }

  closeNotification(): void {
    this.isShown = false;
  }
}
