import { Component, inject, OnInit } from '@angular/core';
import { ConfigService } from '../../services/config.service';
import { Config, LoginMethod } from '../../types/api-output';
import { finalize } from 'rxjs';
import { StatusMessageService } from '../../services/status-message.service';
import { ActivatedRoute } from '@angular/router';
import { LoadingSpinnerDirective } from '../components/loading-spinner/loading-spinner.directive';
import { DevServerLoginButtonComponent } from '../components/login-method-buttons/dev-server-login-button/dev-server-login-button.component';
import { GoogleLoginButtonComponent } from '../components/login-method-buttons/google-login-button/google-login-button.component';

@Component({
  selector: 'tm-login-page',
  styleUrls: ['./login-page.component.scss'],
  templateUrl: './login-page.component.html',
  imports: [LoadingSpinnerDirective, GoogleLoginButtonComponent, DevServerLoginButtonComponent],
})
export class LoginPageComponent implements OnInit {
  private readonly configService = inject(ConfigService);
  private readonly statusMessageService = inject(StatusMessageService);
  private readonly route = inject(ActivatedRoute);

  protected readonly LoginMethod!: typeof LoginMethod;

  isLoadingLoginMethods = true;
  loginMethods: Set<LoginMethod> = new Set();
  backendLoginUrl = '';

  constructor() {
    this.LoginMethod = LoginMethod;
  }

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
          this.statusMessageService.showErrorToast('Failed to load login methods. Please try again later.');
        },
      });

    this.route.queryParams.subscribe((params) => {
      const redirectUrl = params['redirect'];
      if (redirectUrl) {
        this.backendLoginUrl = redirectUrl;
      }
    });
  }

  isSupported(loginMethod: LoginMethod): boolean {
    return this.loginMethods.has(loginMethod);
  }
}
