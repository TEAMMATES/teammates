import { ChangeDetectionStrategy, Component, output } from '@angular/core';

@Component({
  selector: 'tm-google-login-button',
  templateUrl: './google-login-button.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class GoogleLoginButtonComponent {
  readonly login = output<void>();
}
