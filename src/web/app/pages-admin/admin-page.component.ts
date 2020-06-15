import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
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
  institute?: string = '';
  isInstructor: boolean = false;
  isStudent: boolean = false;
  isAdmin: boolean = false;
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
      url: '/web/admin/timezone',
      display: 'Timezone Listing',
    },
  ];
  isFetchingAuthDetails: boolean = false;

  private backendUrl: string = environment.backendUrl;

  constructor(private router: Router, private authService: AuthService, private navigationService: NavigationService) {}

  ngOnInit(): void {
    this.isFetchingAuthDetails = true;
    this.authService.getAuthUser().subscribe((res: AuthInfo) => {
      if (res.user) {
        this.user = res.user.id;
        this.institute = res.institute;
        this.isInstructor = res.user.isInstructor;
        this.isStudent = res.user.isStudent;
        this.isAdmin = res.user.isAdmin;
        if (!this.isAdmin) {
          // User is not a valid admin; redirect to home page.
          // This should not happen in production server as the /web/admin/* routing is protected,
          // and a 403 error page will be shown instead.
          this.navigationService.navigateWithErrorMessage(this.router, '/web',
              'You are not authorized to view the page.');
        }
      } else {
        window.location.href = `${this.backendUrl}${res.adminLoginUrl}`;
      }
      this.isFetchingAuthDetails = false;
    }, () => {
      this.navigationService.navigateWithErrorMessage(this.router, '/web',
          'You are not authorized to view the page.');
    });
  }

}
