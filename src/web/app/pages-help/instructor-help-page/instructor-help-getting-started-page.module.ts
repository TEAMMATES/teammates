import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { TeammatesRouterModule } from '../../components/teammates-router/teammates-router.module';
import { AddCourseFormModule } from '../../pages-instructor/instructor-courses-page/add-course-form/add-course-form.module';
import { ExampleBoxModule } from './example-box/example-box.module';
import { InstructorHelpGettingStartedComponent } from './instructor-help-getting-started/instructor-help-getting-started.component';

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
    AddCourseFormModule,
    ExampleBoxModule,
    TeammatesRouterModule,
  ],
})
export class InstructorHelpGettingStartedPageModule { }
