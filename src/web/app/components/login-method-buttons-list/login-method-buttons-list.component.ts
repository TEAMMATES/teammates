import { Component, forwardRef, Input } from '@angular/core';
import { LoginMethod } from '../../../types/api-output';
import { DevServerLoginButtonComponent } from '../login-method-buttons/dev-server-login-button/dev-server-login-button.component';
import { GoogleLoginButtonComponent } from '../login-method-buttons/google-login-button/google-login-button.component';
import { LOGIN_METHOD_BUTTON_CONTEXT, LoginMethodButtonContext } from '../login-method-buttons/login-method-button-context';

@Component({
  selector: 'tm-login-method-buttons-list',
  templateUrl: './login-method-buttons-list.component.html',
  styleUrls: ['./login-method-buttons-list.component.scss'],
  imports: [GoogleLoginButtonComponent, DevServerLoginButtonComponent],
  providers: [
    {
      provide: LOGIN_METHOD_BUTTON_CONTEXT,
      useExisting: forwardRef(() => LoginMethodButtonsListComponent),
    },
  ],
})
export class LoginMethodButtonsListComponent implements LoginMethodButtonContext {
  @Input() nextUrl = '/';
  @Input() supportedLoginMethods: ReadonlySet<LoginMethod> = new Set();

  protected readonly LoginMethod: typeof LoginMethod;

  constructor() {
    this.LoginMethod = LoginMethod;
  }

  isSupported(method: LoginMethod): boolean {
    return this.supportedLoginMethods.has(method);
  }
}
