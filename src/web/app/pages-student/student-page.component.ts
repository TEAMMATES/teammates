import { Component, OnInit } from '@angular/core';
import { environment } from '../../environments/environment';
import { AuthService } from '../../services/auth.service';

/**
 * Base skeleton for student pages.
 */
@Component({
  selector: 'tm-student-page',
  template: '<tm-page [navItems]="navItems" [logoutUrl]="logoutUrl" [isValidUser]="isValidUser"></tm-page>',
})
export class StudentPageComponent implements OnInit {

  logoutUrl: string = '';
  isValidUser: boolean = false;
  navItems: any[] = [
    {
      url: '/web/student',
      display: 'Home',
    },
    {
      url: '/web/student/profile',
      display: 'Profile',
    },
    {
      url: '/web/student/help',
      display: 'Help',
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
        this.isValidUser = res.user.isStudent;
      } else {
        window.location.href = `${this.backendUrl}${res.studentLoginUrl}`;
      }
    }, () => {
      // TODO
    });
  }

}
