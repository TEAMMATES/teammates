import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { environment } from '../../environments/environment';
import { AuthService } from '../../services/auth.service';
import { AuthInfo } from '../../types/api-output';

/**
 * Base skeleton for student pages.
 */
@Component({
  selector: 'tm-student-page',
  templateUrl: './student-page.component.html',
})
export class StudentPageComponent implements OnInit {

  logoutUrl: string = '';
  user: string = '';
  institute?: string = '';
  isInstructor: boolean = false;
  isStudent: boolean = false;
  isAdmin: boolean = false;
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

  constructor(private route: ActivatedRoute, private authService: AuthService) {}

  ngOnInit(): void {
    this.route.queryParams.subscribe((queryParams: any) => {
      this.authService.getAuthUser(queryParams.user).subscribe((res: AuthInfo) => {
        if (res.logoutUrl) {
          this.logoutUrl = `${this.backendUrl}${res.logoutUrl}`;
        }
        if (res.user) {
          this.user = res.user.id + (res.masquerade ? ' (M)' : '');
          this.institute = res.institute;
          this.isInstructor = res.user.isInstructor;
          this.isStudent = res.user.isStudent;
          this.isAdmin = res.user.isAdmin;
        } else {
          window.location.href = `${this.backendUrl}${res.studentLoginUrl}`;
        }
      }, () => {
        // TODO
      });
    });
  }

}
