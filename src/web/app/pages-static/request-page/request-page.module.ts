import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { ReactiveFormsModule } from '@angular/forms';
import { RouterModule, Routes } from '@angular/router';
import { NgxCaptchaModule } from 'ngx-captcha';
import { AjaxLoadingModule } from '../../components/ajax-loading/ajax-loading.module';
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
    AjaxLoadingModule,
  ],
})
export class RequestPageModule { }
