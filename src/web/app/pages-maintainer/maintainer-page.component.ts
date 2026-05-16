import { Component, OnInit, inject } from '@angular/core';
import { environment } from '../../environments/environment';
import { AuthService } from '../../services/auth.service';
import { AuthInfo } from '../../types/api-output';
import { PageComponent } from '../page.component';

/**
 * Base skeleton for maintainer pages.
 */
@Component({
  selector: 'tm-maintainer-page',
  templateUrl: './maintainer-page.component.html',
  imports: [PageComponent],
})
export class MaintainerPageComponent implements OnInit {
  private authService = inject(AuthService);

  user = '';
  isInstructor = false;
  isStudent = false;
  isAdmin = false;
  isMaintainer = false;
  navItems: any[] = [
    {
      url: '/web/maintainer',
      display: 'Home',
    },
    {
      url: '/web/maintainer/timezone',
      display: 'Timezone Listing',
    },
    {
      url: '/web/maintainer/stats',
      display: 'Usage Statistics',
    },
  ];
  isFetchingAuthDetails = false;

  private backendUrl: string = environment.backendUrl;

  ngOnInit(): void {
    this.isFetchingAuthDetails = true;
    this.authService.getAuthUser('/web/maintainer/home').subscribe({
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
          window.location.href = `${this.backendUrl}${res.loginUrl}`;
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
