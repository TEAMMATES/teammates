import { Component, EventEmitter, Output } from '@angular/core';

@Component({
  selector: 'tm-google-login-button',
  templateUrl: './google-login-button.component.html',
  styleUrls: ['./google-login-button.component.scss'],
})
export class GoogleLoginButtonComponent {
  @Output() login: EventEmitter<void> = new EventEmitter<void>();
}
