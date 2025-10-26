import { Component, OnInit } from '@angular/core';
import { environment } from '../../environments/environment';
import { AuthService } from '../../services/auth.service';
import { NavigationService } from '../../services/navigation.service';
import { AuthInfo } from '../../types/api-output';

/**
 * Base skeleton for admin pages.
 */
@Component({
  selector: 'tm-admin-page',
  templateUrl: './admin-page.component.html',
})
export class AdminPageComponent implements OnInit {

  user: string = '';
  isInstructor: boolean = false;
  isStudent: boolean = false;
  isAdmin: boolean = false;
  isMaintainer: boolean = false;
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
      url: '/web/admin/logs',
      display: 'Logs',
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
  isFetchingAuthDetails: boolean = false;

  private backendUrl: string = environment.backendUrl;

  constructor(private authService: AuthService, private navigationService: NavigationService) {}

  ngOnInit(): void {
    this.isFetchingAuthDetails = true;
    this.authService.getAuthUser().subscribe({
      next: (res: AuthInfo) => {
        if (res.user) {
          this.user = res.user.id;
          this.isInstructor = res.user.isInstructor;
          this.isStudent = res.user.isStudent;
          this.isAdmin = res.user.isAdmin;
          this.isMaintainer = res.user.isMaintainer;
          if (!this.isAdmin) {
            // User is not a valid admin; redirect to home page.
            this.navigationService.navigateWithErrorMessage('/web',
                'You are not authorized to view the page.');
          }
        } else {
          window.location.href = `${this.backendUrl}${res.adminLoginUrl}`;
        }
        this.isFetchingAuthDetails = false;
      },
      error: () => {
        this.navigationService.navigateWithErrorMessage('/web',
            'You are not authorized to view the page.');
      },
    });
  }

}
