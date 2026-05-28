import { Component, inject } from '@angular/core';
import { AuthInfo, NotificationTargetUser } from '../../types/api-output';
import { PageComponent } from '../page.component';
import { AuthService } from '../../services/auth.service';

/**
 * Base skeleton for student pages.
 */
@Component({
  selector: 'tm-student-page',
  templateUrl: './student-page.component.html',
  imports: [PageComponent],
})
export class StudentPageComponent {
  private authService = inject(AuthService);

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
  authInfo: AuthInfo | null = this.authService.authInfo$.value;
}
