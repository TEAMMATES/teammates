import { Component, Input } from '@angular/core';
import { LoginMethod } from '../../../../types/api-output';

@Component({
  selector: 'tm-login-method-button-base',
  styleUrls: ['./login-method-button-base.component.scss'],
  templateUrl: './login-method-button-base.component.html',
})
export class LoginMethodButtonBaseComponent {
  @Input() backendLoginUrl = '';
  @Input({ required: true }) loginMethod!: LoginMethod;

  login(): void {
    const loginUrl = this.getCompleteLoginUrl();
    globalThis.location.href = loginUrl;
  }

  private getCompleteLoginUrl(): string {
    const url = new URL(this.backendLoginUrl, globalThis.location.origin);
    url.searchParams.set('method', this.loginMethod);
    return url.toString();
  }
}
