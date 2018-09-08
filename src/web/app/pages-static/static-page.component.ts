import { Component, OnInit } from '@angular/core';
import { environment } from '../../environments/environment';
import { AuthService } from '../../services/auth.service';

/**
 * Base skeleton for static pages.
 */
@Component({
  selector: 'tm-static-page',
  templateUrl: './static-page.component.html',
  styleUrls: ['./static-page.component.scss'],
})
export class StaticPageComponent implements OnInit {

  studentLoginUrl: string = '';
  instructorLoginUrl: string = '';
  logoutUrl: string = '';
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
  ];

  private backendUrl: string = environment.backendUrl;

  constructor(private authService: AuthService) {}

  ngOnInit(): void {
    this.authService.getAuthUser().subscribe((res: any) => {
      if (res.user) {
        this.logoutUrl = `${this.backendUrl}${res.logoutUrl}`;
        this.isInstructor = res.user.isInstructor;
        this.isStudent = res.user.isStudent;
        this.isAdmin = res.user.isAdmin;
      } else {
        this.studentLoginUrl = `${this.backendUrl}${res.studentLoginUrl}`;
        this.instructorLoginUrl = `${this.backendUrl}${res.instructorLoginUrl}`;
      }
    }, () => {
      // TODO
    });
  }

}
