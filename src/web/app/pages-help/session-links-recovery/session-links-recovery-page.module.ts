import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { NgxCaptchaModule } from 'ngx-captcha';
import { SessionLinksRecoveryPageComponent } from './session-links-recovery-page.component';

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
    SessionLinksRecoveryPageComponent,
  ],
  exports: [
    SessionLinksRecoveryPageComponent,
  ],
})
export class SessionLinksRecoveryPageModule { }
