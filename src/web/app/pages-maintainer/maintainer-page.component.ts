import { Component } from '@angular/core';

import { PageComponent } from '../page.component';

interface NavItem {
  url: string;
  display: string;
}

/**
 * Base skeleton for maintainer pages.
 */
@Component({
  selector: 'tm-maintainer-page',
  templateUrl: './maintainer-page.component.html',
  imports: [PageComponent],
})
export class MaintainerPageComponent {
  navItems: NavItem[] = [
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
}
