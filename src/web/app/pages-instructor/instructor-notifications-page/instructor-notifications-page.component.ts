import { Component, OnInit, inject } from '@angular/core';
import { TimezoneService } from '../../../services/timezone.service';
import { NotificationTargetUser } from '../../../types/api-output';
import { UserNotificationsListComponent } from '../../components/user-notifications-list/user-notifications-list.component';

/**
 * Component for instructor notifications page.
 */
@Component({
  selector: 'tm-instructor-notifications-page',
  templateUrl: './instructor-notifications-page.component.html',
  imports: [UserNotificationsListComponent],
})
export class InstructorNotificationsPageComponent implements OnInit {
  private timezoneService = inject(TimezoneService);

  userType: NotificationTargetUser = NotificationTargetUser.INSTRUCTOR;
  timezone = '';

  ngOnInit(): void {
    this.timezone = this.timezoneService.guessTimezone();
  }
}
