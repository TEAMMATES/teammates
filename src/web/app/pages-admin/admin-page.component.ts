import { Component, inject } from '@angular/core';
import { PageComponent } from '../page.component';
import { AuthService } from '../../services/auth.service';
import { AuthInfo } from '../../types/api-output';

/**
 * Base skeleton for admin pages.
 */
@Component({
  selector: 'tm-admin-page',
  templateUrl: './admin-page.component.html',
  imports: [PageComponent],
})
export class AdminPageComponent {
  private authService = inject(AuthService);

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
  authInfo: AuthInfo | null = this.authService.authInfo$.value;
}
