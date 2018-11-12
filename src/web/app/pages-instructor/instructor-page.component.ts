import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { environment } from '../../environments/environment';
import { AuthService } from '../../services/auth.service';
import { AuthInfo } from '../auth-info';

/**
 * Base skeleton for instructor pages.
 */
@Component({
  selector: 'tm-instructor-page',
  template: '<tm-page [navItems]="navItems" [logoutUrl]="logoutUrl" [isValidUser]="isValidUser"></tm-page>',
})
export class InstructorPageComponent implements OnInit {

  logoutUrl: string = '';
  isValidUser: boolean = false;
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
      url: '/web/instructor/help',
      display: 'Help',
    },
  ];

  private backendUrl: string = environment.backendUrl;

  constructor(private route: ActivatedRoute, private authService: AuthService) {}

  ngOnInit(): void {
    this.route.queryParams.subscribe((queryParams: any) => {
      this.authService.getAuthUser(queryParams.user).subscribe((res: AuthInfo) => {
        if (res.logoutUrl) {
          this.logoutUrl = `${this.backendUrl}${res.logoutUrl}`;
        }
        if (res.user) {
          this.isValidUser = res.user.isInstructor;
        } else {
          window.location.href = `${this.backendUrl}${res.instructorLoginUrl}`;
        }
      }, () => {
        // TODO
      });
    });
  }

}
