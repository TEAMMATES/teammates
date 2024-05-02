import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { ReactiveFormsModule } from '@angular/forms';
import { RouterModule, Routes } from '@angular/router';
import { NgbAlertModule } from '@ng-bootstrap/ng-bootstrap';
import { NgxCaptchaModule } from 'ngx-captcha';
import { InstructorRequestFormComponent } from './instructor-request-form/instructor-request-form.component';
import { RequestPageComponent } from './request-page.component';
import { TeammatesRouterModule } from '../../components/teammates-router/teammates-router.module';

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
    InstructorRequestFormComponent,
  ],
  exports: [
    RequestPageComponent,
  ],
  imports: [
    CommonModule,
    RouterModule.forChild(routes),
    TeammatesRouterModule,
    ReactiveFormsModule,
    NgbAlertModule,
    NgxCaptchaModule,
  ],
})
export class RequestPageModule { }
