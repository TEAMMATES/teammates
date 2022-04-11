import { Component } from '@angular/core';
import { NotificationTargetUser } from '../../../types/api-output';

/**
 * Component for student notifications page.
 */
@Component({
  selector: 'tm-student-notifications-page',
  templateUrl: './student-notifications-page.component.html',
  styleUrls: ['./student-notifications-page.component.scss'],
})
export class StudentNotificationsPageComponent {
    userType: NotificationTargetUser = NotificationTargetUser.STUDENT;
}
