import { ChangeDetectionStrategy, Component, input } from '@angular/core';
import { LoginMethod } from '../../../types/api-output';
import { DevServerLoginButtonComponent } from '../login-method-buttons/dev-server-login-button/dev-server-login-button.component';
import { GoogleLoginButtonComponent } from '../login-method-buttons/google-login-button/google-login-button.component';
import { environment } from '../../../environments/environment';

@Component({
  selector: 'tm-login-method-buttons-container',
  templateUrl: './login-method-buttons-container.component.html',
  imports: [GoogleLoginButtonComponent, DevServerLoginButtonComponent],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class LoginMethodButtonsContainerComponent {
  readonly nextUrl = input('/');
  readonly supportedLoginMethods = input<ReadonlySet<LoginMethod>>(new Set());

  protected readonly LoginMethod: typeof LoginMethod = LoginMethod;
  private readonly backendLoginUrl = `${environment.backendUrl}/login`;

  isSupported(method: LoginMethod): boolean {
    return this.supportedLoginMethods().has(method);
  }

  login(method: LoginMethod): void {
    globalThis.location.href = this.getCompleteLoginUrl(method);
  }

  private getCompleteLoginUrl(method: LoginMethod): string {
    const url = new URL(this.backendLoginUrl, globalThis.location.origin);
    url.searchParams.set('nextUrl', this.nextUrl() || '/');
    url.searchParams.set('method', method);
    return url.toString();
  }
}
