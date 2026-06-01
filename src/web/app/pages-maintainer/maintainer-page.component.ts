import { Component } from '@angular/core';

import { PageComponent } from '../page.component';
import { PageAuthType } from '../page.authtype';

/**
 * Base skeleton for maintainer pages.
 */
@Component({
  selector: 'tm-maintainer-page',
  templateUrl: './maintainer-page.component.html',
  imports: [PageComponent],
})
export class MaintainerPageComponent {
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
  pageAuthType = PageAuthType.ROLE_BASED_AUTHENTICATED;
}
