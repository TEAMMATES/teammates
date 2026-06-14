import { Component, inject, Input } from '@angular/core';
import { LoginMethod } from '../../../../types/api-output';
import { environment } from '../../../../environments/environment';
import { LOGIN_METHOD_BUTTON_CONTEXT, LoginMethodButtonContext } from '../login-method-button-context';

/**
 * Base component for login method buttons that handles shared logic.
 */
@Component({
  selector: 'tm-login-method-button-base',
  styleUrls: ['./login-method-button-base.component.scss'],
  templateUrl: './login-method-button-base.component.html',
})
export class LoginMethodButtonBaseComponent {
  @Input({ required: true }) loginMethod!: LoginMethod;

  private readonly loginMethodButtonContext: LoginMethodButtonContext = inject(LOGIN_METHOD_BUTTON_CONTEXT);
  private readonly backendLoginUrl = environment.backendUrl + '/login';

  login(): void {
    const loginUrl = this.getCompleteLoginUrl();
    globalThis.location.href = loginUrl;
  }

  private getCompleteLoginUrl(): string {
    const url = new URL(this.backendLoginUrl, globalThis.location.origin);
    url.searchParams.set('nextUrl', this.loginMethodButtonContext.nextUrl || '/');
    url.searchParams.set('method', this.loginMethod);
    return url.toString();
  }
}
