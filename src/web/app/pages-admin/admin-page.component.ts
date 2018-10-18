import { Component, OnInit } from '@angular/core';
import { environment } from '../../environments/environment';
import { AuthService } from '../../services/auth.service';

/**
 * Base skeleton for admin pages.
 */
@Component({
  selector: 'tm-admin-page',
  template: '<tm-page [navItems]="navItems" [logoutUrl]="logoutUrl" [isValidUser]="isValidUser"></tm-page>',
})
export class AdminPageComponent implements OnInit {

  logoutUrl: string = '';
  isValidUser: boolean = false;
  navItems: any[] = [
    {
      url: '/web/admin',
      display: 'Home',
    },
  ];

  private backendUrl: string = environment.backendUrl;

  constructor(private authService: AuthService) {}

  ngOnInit(): void {
    this.authService.getAuthUser().subscribe((res: any) => {
      if (res.logoutUrl) {
        this.logoutUrl = `${this.backendUrl}${res.logoutUrl}`;
      }
      if (res.user) {
        this.isValidUser = res.user.isAdmin;
        if (!this.isValidUser) {
          // User is not a valid admin; redirect to home page.
          // This should not happen in production server as the /web/admin/* routing is protected,
          // and a 403 error page will be shown instead.
          window.location.href = '/web';
        }
      } else {
        window.location.href = `${this.backendUrl}${res.adminLoginUrl}`;
      }
    }, () => {
      // TODO
    });
  }

}
