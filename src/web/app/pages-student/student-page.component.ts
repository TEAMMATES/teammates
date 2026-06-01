import { Component } from '@angular/core';
import { NotificationTargetUser } from '../../types/api-output';
import { PageComponent } from '../page.component';
import { PageAuthType } from '../page.authtype';

/**
 * Base skeleton for student pages.
 */
@Component({
  selector: 'tm-student-page',
  templateUrl: './student-page.component.html',
  imports: [PageComponent],
})
export class StudentPageComponent {
  navItems: any[] = [
    {
      url: '/web/student',
      display: 'Home',
    },
    {
      url: '/web/student/notifications',
      display: 'Notifications',
    },
    {
      url: '/web/student/help',
      display: 'Help',
    },
  ];
  notificationTargetUser: NotificationTargetUser = NotificationTargetUser.STUDENT;
  pageAuthType = PageAuthType.ROLE_BASED_AUTHENTICATED;
}
