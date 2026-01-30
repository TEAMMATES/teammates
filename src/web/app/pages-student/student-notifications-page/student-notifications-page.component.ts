import { Component, OnInit } from '@angular/core';
import { TimezoneService } from '../../../services/timezone.service';
import { NotificationTargetUser } from '../../../types/api-output';
import { UserNotificationsListComponent } from '../../components/user-notifications-list/user-notifications-list.component';

/**
 * Component for student notifications page.
 */
@Component({
  selector: 'tm-student-notifications-page',
  templateUrl: './student-notifications-page.component.html',
  styleUrls: ['./student-notifications-page.component.scss'],
  imports: [UserNotificationsListComponent],
})
export class StudentNotificationsPageComponent implements OnInit {

  userType: NotificationTargetUser = NotificationTargetUser.STUDENT;
  timezone = '';

  constructor(private timezoneService: TimezoneService) { }

  ngOnInit(): void {
    this.timezone = this.timezoneService.guessTimezone();
  }

}
