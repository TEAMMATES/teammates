import { Component, Input } from '@angular/core';
import { LoginMethod } from '../../../../types/api-output';
import { environment } from '../../../../environments/environment';

/**
 * Base component for login method buttons that handles shared logic.
 */
@Component({
  selector: 'tm-login-method-button-base',
  styleUrls: ['./login-method-button-base.component.scss'],
  templateUrl: './login-method-button-base.component.html',
})
export class LoginMethodButtonBaseComponent {
  @Input() nextUrl = '/';
  @Input({ required: true }) loginMethod!: LoginMethod;

  private readonly backendLoginUrl = environment.backendUrl + '/login';

  login(): void {
    const loginUrl = this.getCompleteLoginUrl();
    globalThis.location.href = loginUrl;
  }

  private getCompleteLoginUrl(): string {
    const url = new URL(this.backendLoginUrl, globalThis.location.origin);
    url.searchParams.set('nextUrl', this.nextUrl);
    url.searchParams.set('method', this.loginMethod);
    return url.toString();
  }
}
