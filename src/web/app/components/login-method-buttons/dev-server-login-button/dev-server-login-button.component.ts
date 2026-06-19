import { Component, EventEmitter, Output } from '@angular/core';

@Component({
  selector: 'tm-dev-server-login-button',
  templateUrl: './dev-server-login-button.component.html',
})
export class DevServerLoginButtonComponent {
  @Output() login: EventEmitter<void> = new EventEmitter<void>();
}
