import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { ExampleBoxModule } from './example-box/example-box.module';
import {
  InstructorHelpGettingStartedComponent,
} from './instructor-help-getting-started/instructor-help-getting-started.component';
import { CourseEditFormModule } from '../../components/course-edit-form/course-edit-form.module';
import { TeammatesRouterModule } from '../../components/teammates-router/teammates-router.module';

const routes: Routes = [
  {
    path: '',
    component: InstructorHelpGettingStartedComponent,
  },
];

/**
 * Module for instructor help (getting started) page.
 */
@NgModule({
  declarations: [InstructorHelpGettingStartedComponent],
  exports: [InstructorHelpGettingStartedComponent],
  imports: [
    CommonModule,
    RouterModule.forChild(routes),
    ExampleBoxModule,
    TeammatesRouterModule,
    CourseEditFormModule,
  ],
})
export class InstructorHelpGettingStartedPageModule { }
