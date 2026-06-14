import { Component } from '@angular/core';
import { LoginMethodButtonBaseComponent } from '../login-method-button-base/login-method-button-base.component';
import { LoginMethod } from '../../../../types/api-output';

@Component({
  selector: 'tm-dev-server-login-button',
  templateUrl: './dev-server-login-button.component.html',
  imports: [LoginMethodButtonBaseComponent],
})
export class DevServerLoginButtonComponent {
  protected readonly method = LoginMethod.DEV_SERVER;
}
