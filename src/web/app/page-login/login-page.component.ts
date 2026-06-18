import { Component, inject, Input, OnInit } from '@angular/core';
import { ConfigService } from '../../services/config.service';
import { Config, LoginMethod } from '../../types/api-output';
import { finalize } from 'rxjs';
import { StatusMessageService } from '../../services/status-message.service';
import { LoadingSpinnerDirective } from '../components/loading-spinner/loading-spinner.directive';
import { LoginMethodButtonsContainerComponent } from '../components/login-method-buttons-container/login-method-buttons-container.component';

@Component({
  selector: 'tm-login-page',
  styleUrls: ['./login-page.component.scss'],
  templateUrl: './login-page.component.html',
  imports: [LoadingSpinnerDirective, LoginMethodButtonsContainerComponent],
})
export class LoginPageComponent implements OnInit {
  private readonly configService = inject(ConfigService);
  private readonly statusMessageService = inject(StatusMessageService);

  @Input() nextUrl = '/';

  isLoadingLoginMethods = true;
  loginMethods: Set<LoginMethod> = new Set();

  ngOnInit(): void {
    this.isLoadingLoginMethods = true;
    this.configService
      .getConfig()
      .pipe(
        finalize(() => {
          this.isLoadingLoginMethods = false;
        }),
      )
      .subscribe({
        next: (config: Config) => {
          this.loginMethods = new Set(config.loginMethods);
          if (this.loginMethods.size === 0) {
            // Should not happen as backend should have at least one login method configured.
            this.statusMessageService.showWarningToast(
              'No login methods are currently supported. Please contact the administrator.',
            );
          }
        },
        error: () => {
          this.statusMessageService.showErrorToast('Something went wrong. Please try again later.');
        },
      });
  }
}
