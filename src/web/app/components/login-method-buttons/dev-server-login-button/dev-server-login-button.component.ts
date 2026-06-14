import { Component, Input } from '@angular/core';
import { LoginMethodButtonBaseComponent } from '../login-method-button-base/login-method-button-base.component';
import { LoginMethod } from '../../../../types/api-output';

@Component({
  selector: 'tm-dev-server-login-button',
  styleUrls: ['./dev-server-login-button.component.scss'],
  templateUrl: './dev-server-login-button.component.html',
  imports: [LoginMethodButtonBaseComponent],
})
export class DevServerLoginButtonComponent {
  @Input() backendLoginUrl = '';
  protected readonly method = LoginMethod.DEV_SERVER;
}
