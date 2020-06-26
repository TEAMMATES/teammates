import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
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
  ],
})
export class InstructorHelpGettingStartedPageModule { }
