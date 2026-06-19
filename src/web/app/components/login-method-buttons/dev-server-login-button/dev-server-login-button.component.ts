import { ChangeDetectionStrategy, Component, output } from '@angular/core';

@Component({
  selector: 'tm-dev-server-login-button',
  templateUrl: './dev-server-login-button.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class DevServerLoginButtonComponent {
  readonly login = output<void>();
}
