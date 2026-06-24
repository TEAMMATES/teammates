import { Component } from '@angular/core';
import { PageComponent } from '../page.component';
import { NavItem } from '../page.model';

/**
 * Base skeleton for admin pages.
 */
@Component({
  selector: 'tm-admin-page',
  templateUrl: './admin-page.component.html',
  imports: [PageComponent],
})
export class AdminPageComponent {
  navItems: NavItem[] = [
    {
      url: '/web/admin',
      display: 'Home',
    },
    {
      url: '/web/admin/search',
      display: 'Search',
    },
    {
      url: '/web/admin/sessions',
      display: 'Sessions',
    },
    {
      url: '/web/admin/notifications',
      display: 'Notifications',
    },
    {
      display: 'More',
      children: [
        {
          url: '/web/admin/account-verification-requests',
          display: 'Verification Requests',
        },
        {
          url: '/web/admin/timezone',
          display: 'Timezone Listing',
        },
        {
          url: '/web/admin/stats',
          display: 'Usage Statistics',
        },
      ],
    },
  ];
}
