import { Component, OnInit } from '@angular/core';
import { TimezoneService } from '../../../services/timezone.service';
import { NotificationTargetUser } from '../../../types/api-output';

/**
 * Component for instructor notifications page.
 */
@Component({
  selector: 'tm-instructor-notifications-page',
  templateUrl: './instructor-notifications-page.component.html',
  styleUrls: ['./instructor-notifications-page.component.scss'],
})
export class InstructorNotificationsPageComponent implements OnInit {

  userType: NotificationTargetUser = NotificationTargetUser.INSTRUCTOR;
  timezone = '';

  constructor(private timezoneService: TimezoneService) { }

  ngOnInit(): void {
    this.timezone = this.timezoneService.guessTimezone();
  }

}
