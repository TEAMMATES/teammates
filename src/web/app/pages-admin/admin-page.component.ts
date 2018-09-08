import { Component } from '@angular/core';

/**
 * Base skeleton for admin pages.
 */
@Component({
  selector: 'tm-admin-page',
  templateUrl: './admin-page.component.html',
  styleUrls: ['./admin-page.component.scss'],
})
export class AdminPageComponent {

  navItems: any[] = [
    {
      url: '/web/admin',
      display: 'Home',
    },
  ];

  constructor() {}

}
