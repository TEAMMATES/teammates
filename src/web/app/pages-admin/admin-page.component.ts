import { Component } from '@angular/core';
import { PageComponent } from '../page.component';

/**
 * Base skeleton for admin pages.
 */
@Component({
  selector: 'tm-admin-page',
  templateUrl: './admin-page.component.html',
  imports: [PageComponent],
})
export class AdminPageComponent {
  navItems: any[] = [
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
