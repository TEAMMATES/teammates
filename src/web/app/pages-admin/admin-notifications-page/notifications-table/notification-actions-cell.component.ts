import { Component, Input } from '@angular/core';

@Component({
  selector: 'tm-notification-actions-cell',
  templateUrl: './notification-actions-cell.component.html',
})
export class NotificationActionsCellComponent {
  @Input() loadNotificationEditForm: () => void = () => {};
  @Input() deleteNotification: () => void = () => {};
}
