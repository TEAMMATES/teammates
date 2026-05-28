import { Component, inject } from '@angular/core';

import { PageComponent } from '../page.component';
import { AuthService } from '../../services/auth.service';
import { AuthInfo } from '../../types/api-output';

/**
 * Base skeleton for maintainer pages.
 */
@Component({
  selector: 'tm-maintainer-page',
  templateUrl: './maintainer-page.component.html',
  imports: [PageComponent],
})
export class MaintainerPageComponent {
  private authService = inject(AuthService);

  navItems: any[] = [
    {
      url: '/web/maintainer',
      display: 'Home',
    },
    {
      url: '/web/maintainer/timezone',
      display: 'Timezone Listing',
    },
    {
      url: '/web/maintainer/stats',
      display: 'Usage Statistics',
    },
  ];
  authInfo: AuthInfo | null = this.authService.authInfo$.value;
}
