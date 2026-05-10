import { Component, OnInit } from '@angular/core';
import { AuthService } from '../../services/auth.service';
import { AuthInfo } from '../../types/api-output';
import { PageComponent } from '../page.component';
import { forkJoin } from 'rxjs';

/**
 * Base skeleton for static pages.
 */
@Component({
  selector: 'tm-static-page',
  templateUrl: './static-page.component.html',
  imports: [PageComponent],
})
export class StaticPageComponent implements OnInit {
  user = '';
  studentLoginUrl = '';
  instructorLoginUrl = '';
  isInstructor = false;
  isStudent = false;
  isAdmin = false;
  isMaintainer = false;
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
  isFetchingAuthDetails = false;

  constructor(private authService: AuthService) {}

  ngOnInit(): void {
    this.isFetchingAuthDetails = true;
    forkJoin({
      studentAuth: this.authService.getAuthUser(undefined, '/web/student/home'),
      instructorAuth: this.authService.getAuthUser(undefined, '/web/instructor/home'),
    }).subscribe({
      next: (responses) => {
        // both responses have the same UserInfo, so it doesn't matter which one we use
        const res: AuthInfo = responses.studentAuth;
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
          // If NOT logged in, save the generated URLs for the dropdown
          this.studentLoginUrl = responses.studentAuth.loginUrl;
          this.instructorLoginUrl = responses.instructorAuth.loginUrl;
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
  }
}
