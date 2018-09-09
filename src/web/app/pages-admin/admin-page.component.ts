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
      this.isValidUser = res.user && res.user.isAdmin;
    }, () => {
      // TODO
    });
  }

}
