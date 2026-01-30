import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { ReactiveFormsModule } from '@angular/forms';
import { RouterModule, Routes } from '@angular/router';
import { NgbAlertModule } from '@ng-bootstrap/ng-bootstrap';
import { NgxCaptchaModule } from 'ngx-captcha';
import { InstructorRequestFormComponent } from './instructor-request-form/instructor-request-form.component';
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
  exports: [
    RequestPageComponent,
  ],
  imports: [
    CommonModule,
    RouterModule.forChild(routes),
    ReactiveFormsModule,
    NgbAlertModule,
    NgxCaptchaModule,
    RequestPageComponent,
    InstructorRequestFormComponent,
],
})
export class RequestPageModule { }
