import { Component, OnInit, inject } from '@angular/core';
import { AuthService } from '../../services/auth.service';
import { PageComponent } from '../page.component';

/**
 * Base skeleton for static pages.
 */
@Component({
  selector: 'tm-static-page',
  templateUrl: './static-page.component.html',
  imports: [PageComponent],
})
export class StaticPageComponent implements OnInit {
  private authService = inject(AuthService);

  user = '';
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

  ngOnInit(): void {
    this.isFetchingAuthDetails = true;
    this.authService.getAuthUser().subscribe({
      next: (res) => {
        if (res.user) {
          this.user = res.user.id;
          if (res.masquerade) {
            this.user += ' (M)';
          }
          this.isInstructor = res.user.isInstructor;
          this.isStudent = res.user.isStudent;
          this.isAdmin = res.user.isAdmin;
          this.isMaintainer = res.user.isMaintainer;
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
