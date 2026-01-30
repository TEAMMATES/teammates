import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { ReactiveFormsModule } from '@angular/forms';
import { RouterModule, Routes } from '@angular/router';
import { NgxCaptchaModule } from 'ngx-captcha';


import { LoginPageComponent } from './login-page.component';

const routes: Routes = [
  {
    path: '',
    component: LoginPageComponent,
  },
];

/**
 * Module for login page.
 */
@NgModule({
  exports: [
    LoginPageComponent,
  ],
  imports: [
    CommonModule,
    RouterModule.forChild(routes),
    NgxCaptchaModule,
    ReactiveFormsModule,
    LoginPageComponent,
],
})
export class LoginPageModule {}
