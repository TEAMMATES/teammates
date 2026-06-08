import { Component } from '@angular/core';
import { NotificationTargetUser } from '../../types/api-output';
import { PageComponent } from '../page.component';
import { NavItem } from '../page.model';

/**
 * Base skeleton for instructor pages.
 */
@Component({
  selector: 'tm-instructor-page',
  templateUrl: './instructor-page.component.html',
  imports: [PageComponent],
})
export class InstructorPageComponent {
  navItems: NavItem[] = [
    {
      url: '/web/instructor',
      display: 'Home',
    },
    {
      url: '/web/instructor/courses',
      display: 'Courses',
    },
    {
      url: '/web/instructor/sessions',
      display: 'Sessions',
    },
    {
      url: '/web/instructor/students',
      display: 'Students',
    },
    {
      url: '/web/instructor/search',
      display: 'Search',
    },
    {
      url: '/web/instructor/notifications',
      display: 'Notifications',
    },
    {
      display: 'Help',
      children: [
        {
          url: '/web/instructor/getting-started',
          display: 'Getting Started',
        },
        {
          url: '/web/instructor/help',
          display: 'Instructor Help',
        },
      ],
    },
  ];
  notificationTargetUser: NotificationTargetUser = NotificationTargetUser.INSTRUCTOR;
}
