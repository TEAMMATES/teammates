import { Component } from '@angular/core';
import { NotificationTargetUser } from '../../../types/api-output';

/**
 * Component for instructor notifications page.
 */
@Component({
  selector: 'tm-instructor-notifications-page',
  templateUrl: './instructor-notifications-page.component.html',
  styleUrls: ['./instructor-notifications-page.component.scss'],
})
export class InstructorNotificationsPageComponent {

  userType: NotificationTargetUser = NotificationTargetUser.INSTRUCTOR;

}
