import { Component } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { environment } from '../environments/environment';
import { AuthService } from '../services/auth.service';

/**
 * Component for publicly available pages.
 */
@Component({
  selector: 'tm-public-page',
  template: '<tm-page [navItems]="navItems" [isValidUser]="true" [hideAuthInfo]="true"></tm-page>',
})
export class PublicPageComponent {

  navItems: any[] = [
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

  constructor(private route: ActivatedRoute,
              private authService: AuthService) {
    if (environment.maintenance) {
      return;
    }
    this.route.queryParams.subscribe((queryParams: any) => {
      this.authService.getAuthUser(queryParams.user).subscribe(() => {
        // No need to do anything with result; this is necessary to get CSRF token
      });
    });
  }

}