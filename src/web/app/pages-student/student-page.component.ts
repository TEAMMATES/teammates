import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { environment } from '../../environments/environment';
import { AuthService } from '../../services/auth.service';
import { AuthInfo, NotificationTargetUser } from '../../types/api-output';

/**
 * Base skeleton for student pages.
 */
@Component({
  selector: 'tm-student-page',
  templateUrl: './student-page.component.html',
})
export class StudentPageComponent implements OnInit {

  user: string = '';
  isInstructor: boolean = false;
  isStudent: boolean = false;
  isAdmin: boolean = false;
  isMaintainer: boolean = false;
  navItems: any[] = [
    {
      url: '/web/student',
      display: 'Home',
    },
    {
      url: '/web/student/notifications',
      display: 'Notifications',
    },
    {
      url: '/web/student/help',
      display: 'Help',
    },
  ];
  isFetchingAuthDetails: boolean = false;
  notificationTargetUser: NotificationTargetUser = NotificationTargetUser.STUDENT;

  private backendUrl: string = environment.backendUrl;

  constructor(private route: ActivatedRoute, private authService: AuthService) {}

  ngOnInit(): void {
    this.isFetchingAuthDetails = true;
    this.route.queryParams.subscribe((queryParams: any) => {
      this.authService.getAuthUser(queryParams.user).subscribe({
        next: (res: AuthInfo) => {
          if (res.user) {
            this.user = res.user.id;
            if (res.masquerade) {
              this.user += ' (M)';
            }
            this.isInstructor = res.user.isInstructor;
            this.isStudent = res.user.isStudent;
            this.isAdmin = res.user.isAdmin;
            this.isMaintainer = res.user.isMaintainer;
          } else {
            window.location.href = `${this.backendUrl}${res.studentLoginUrl}`;
          }
          this.isFetchingAuthDetails = false;
        },
        error: () => {
          this.isInstructor = false;
          this.isStudent = false;
          this.isAdmin = false;
          this.isMaintainer = false;
          this.isFetchingAuthDetails = false;
        },
      });
    });
  }

}
