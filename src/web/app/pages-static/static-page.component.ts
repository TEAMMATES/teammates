import { Component } from '@angular/core';

/**
 * Base skeleton for static pages.
 */
@Component({
  selector: 'tm-static-page',
  templateUrl: './static-page.component.html',
  styleUrls: ['./static-page.component.scss'],
})
export class StaticPageComponent {

  navItems: any[] = [
    {
      url: '/web/front',
      display: 'Home',
    },
    {
      url: '/web/front/features',
      display: 'Features',
    },
    {
      url: '/web/front/about',
      display: 'About',
    },
    {
      url: '/web/front/contact',
      display: 'Contact',
    },
    {
      url: '/web/front/terms',
      display: 'Terms',
    },
  ];

  constructor() {}

}
