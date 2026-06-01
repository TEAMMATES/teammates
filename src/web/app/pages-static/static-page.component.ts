import { Component } from '@angular/core';
import { PageComponent } from '../page.component';
import { PageAuthType } from '../page.authtype';

/**
 * Base skeleton for static pages.
 */
@Component({
  selector: 'tm-static-page',
  templateUrl: './static-page.component.html',
  imports: [PageComponent],
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
    {
      display: 'Help',
      children: [
        {
          url: '/web/front/help/student',
          display: 'Help for Students',
        },
        {
          url: '/web/front/help/instructor',
          display: 'Help for Instructors',
        },
        {
          url: '/web/front/help/session-links-recovery',
          display: 'Recover Session Links',
        },
      ],
    },
  ];
  pageAuthType = PageAuthType.PUBLIC;
}
