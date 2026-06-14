import { Component } from '@angular/core';
import { LoginMethodButtonBaseComponent } from '../login-method-button-base/login-method-button-base.component';
import { LoginMethod } from '../../../../types/api-output';

@Component({
  selector: 'tm-google-login-button',
  templateUrl: './google-login-button.component.html',
  imports: [LoginMethodButtonBaseComponent],
})
export class GoogleLoginButtonComponent {
  protected readonly method = LoginMethod.GOOGLE;
}
