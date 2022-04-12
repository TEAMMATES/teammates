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
  notificationTargetUser: NotificationTargetUser = NotificationTargetUser.GENERAL;

  isShown: boolean = true;
  notifications: Notification[] = [];

  constructor(private notificationService: NotificationService) { }

  ngOnInit(): void {
    if (this.notificationTargetUser !== NotificationTargetUser.GENERAL) {
      this.fetchNotifications();
    }
  }

  fetchNotifications(): void {
    this.notificationService.getUnreadNotificationsForTargetUser(this.notificationTargetUser)
      .subscribe((response: Notifications) => {
        this.notifications = response.notifications;
      });
  }

  closeNotification(): void {
    this.isShown = false;
  }
}
