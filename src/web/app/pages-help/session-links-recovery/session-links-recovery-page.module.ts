import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { RouterModule, Routes } from '@angular/router';
import { NgxCaptchaModule } from 'ngx-captcha';
import { AjaxLoadingModule } from '../../components/ajax-loading/ajax-loading.module';
import { SessionLinksRecoveryPageComponent } from './session-links-recovery-page.component';

const routes: Routes = [
  {
    path: '',
    component: SessionLinksRecoveryPageComponent,
  },
];

/**
 * Module for student recover session links page.
 */
@NgModule({
  imports: [
    CommonModule,
    FormsModule,
    ReactiveFormsModule,
    NgxCaptchaModule,
    RouterModule.forChild(routes),
    AjaxLoadingModule,
  ],
  declarations: [
    SessionLinksRecoveryPageComponent,
  ],
  exports: [
    SessionLinksRecoveryPageComponent,
  ],
})
export class SessionLinksRecoveryPageModule { }
