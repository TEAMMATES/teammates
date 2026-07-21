import { ChangeDetectionStrategy, Component, OnInit, computed, inject, signal } from '@angular/core';
import { RouterLink } from '@angular/router';
import { map, finalize } from 'rxjs/operators';
import { AuthService } from '../../services/auth.service';
import { AuthInfo } from '../../types/api-output';
import { LoadingSpinnerDirective } from '../components/loading-spinner/loading-spinner.directive';
import { ConfigService } from '../../services/config.service';
import { toSignal } from '@angular/core/rxjs-interop';

interface RolePage {
  role: string;
  url: string;
}

@Component({
  selector: 'tm-role-selection-page',
  templateUrl: './role-selection-page.component.html',
  imports: [RouterLink, LoadingSpinnerDirective],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class RoleSelectionPageComponent implements OnInit {
  private readonly authService = inject(AuthService);
  private readonly configService = inject(ConfigService);

  readonly isLoadingRoles = signal(true);
  readonly rolePages = signal<RolePage[]>([]);
  readonly hasRolePages = computed(() => this.rolePages().length > 0);
  readonly supportEmail = toSignal(this.configService.getConfig().pipe(map((config) => config.supportEmail)), {
    initialValue: '',
  });

  ngOnInit(): void {
    this.isLoadingRoles.set(true);
    this.authService
      .getAuthUser()
      .pipe(
        finalize(() => {
          this.isLoadingRoles.set(false);
        }),
      )
      .subscribe({
        next: (authInfo: AuthInfo) => {
          const user = authInfo.user;
          const rolePages: RolePage[] = [];

          if (user?.isStudent) {
            rolePages.push({
              role: 'Student',
              url: '/web/student',
            });
          }

          if (user?.isInstructor) {
            rolePages.push({
              role: 'Instructor',
              url: '/web/instructor',
            });
          }

          if (user?.isAdmin) {
            rolePages.push({
              role: 'Admin',
              url: '/web/admin',
            });
          }

          if (user?.isMaintainer) {
            rolePages.push({
              role: 'Maintainer',
              url: '/web/maintainer',
            });
          }

          this.rolePages.set(rolePages);
        },
        error: () => {
          this.rolePages.set([]);
        },
      });
  }
}
