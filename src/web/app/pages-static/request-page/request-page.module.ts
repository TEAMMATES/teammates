import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { ReactiveFormsModule } from '@angular/forms';
import { RouterModule, Routes } from '@angular/router';
import { NgxCaptchaModule } from 'ngx-captcha';
import { RequestPageComponent } from './request-page.component';

const routes: Routes = [
  {
    path: '',
    component: RequestPageComponent,
  },
];

/**
 * Module for request page.
 */
@NgModule({
  declarations: [
    RequestPageComponent,
  ],
  exports: [
    RequestPageComponent,
  ],
  imports: [
    CommonModule,
    ReactiveFormsModule,
    RouterModule.forChild(routes),
    NgxCaptchaModule,
  ],
})
export class RequestPageModule { }
