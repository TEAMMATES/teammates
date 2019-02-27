import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { NgxCaptchaModule } from 'ngx-captcha';
import { SessionLinkRecoveryPageComponent } from './session-link-recovery-page.component';

/**
 * Module for student recover session links page.
 */
@NgModule({
  imports: [
    CommonModule,
    FormsModule,
    ReactiveFormsModule,
    NgxCaptchaModule,
  ],
  declarations: [
    SessionLinkRecoveryPageComponent,
  ],
  exports: [
    SessionLinkRecoveryPageComponent,
  ],
})
export class SessionLinkRecoveryPageModule { }
