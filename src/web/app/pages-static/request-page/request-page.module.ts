import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { RequestPageComponent } from './request-page.component';
import { InstructorRequestFormComponent } from './instructor-request-form/instructor-request-form.component';
import { TeammatesRouterModule } from '../../components/teammates-router/teammates-router.module';
import { ReactiveFormsModule } from '@angular/forms';

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
    InstructorRequestFormComponent
  ],
  exports: [
    RequestPageComponent,
  ],
  imports: [
    CommonModule,
    RouterModule.forChild(routes),
    TeammatesRouterModule,
    ReactiveFormsModule
  ],
})
export class RequestPageModule { }
