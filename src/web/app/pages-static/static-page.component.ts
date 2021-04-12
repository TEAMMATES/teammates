import { Component, OnInit } from '@angular/core';
import { environment } from '../../environments/environment';
import { AuthService } from '../../services/auth.service';
import { AuthInfo } from '../../types/api-output';

/**
 * Base skeleton for static pages.
 */
@Component({
  selector: 'tm-static-page',
  templateUrl: './static-page.component.html',
})
export class StaticPageComponent implements OnInit {

  studentLoginUrl: string = '';
  instructorLoginUrl: string = '';
  user: string = '';
  institute?: string = '';
  isInstructor: boolean = false;
  isStudent: boolean = false;
  isAdmin: boolean = false;
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
          display: 'Student Help',
        },
        {
          url: '/web/front/help/instructor',
          display: 'Instructor Help',
        },
        {
          url: '/web/front/help/session-links-recovery',
          display: 'Recover Session Links',
        },
      ],
    },
  ];
  isFetchingAuthDetails: boolean = false;

  private backendUrl: string = environment.backendUrl;

  constructor(private authService: AuthService) {}

  ngOnInit(): void {
    this.isFetchingAuthDetails = true;
    this.authService.getAuthUser().subscribe((res: AuthInfo) => {
      if (res.user) {
        this.user = res.user.id;
        if (res.masquerade) {
          this.user += ' (M)';
        }
        this.institute = res.institute;
        this.isInstructor = res.user.isInstructor;
        this.isStudent = res.user.isStudent;
        this.isAdmin = res.user.isAdmin;
      } else {
        this.studentLoginUrl = `${this.backendUrl}${res.studentLoginUrl}`;
        this.instructorLoginUrl = `${this.backendUrl}${res.instructorLoginUrl}`;
      }
      this.isFetchingAuthDetails = false;
    }, () => {
      this.isInstructor = false;
      this.isStudent = false;
      this.isAdmin = false;
      this.isFetchingAuthDetails = false;
    });
  }

}
