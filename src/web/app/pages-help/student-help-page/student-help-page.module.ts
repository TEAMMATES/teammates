import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { StudentHelpPageComponent } from './student-help-page.component';

const routes: Routes = [
  {
    path: '',
    component: StudentHelpPageComponent,
  },
];

/**
 * Module for student help page.
 */
@NgModule({
  imports: [
    CommonModule,
    RouterModule.forChild(routes),
  ],
  declarations: [
    StudentHelpPageComponent,
  ],
  exports: [
    StudentHelpPageComponent,
  ],
})
export class StudentHelpPageModule { }
