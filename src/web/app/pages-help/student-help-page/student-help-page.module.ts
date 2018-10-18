import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { StudentHelpPageComponent } from './student-help-page.component';

/**
 * Module for student help page.
 */
@NgModule({
  imports: [
    CommonModule,
  ],
  declarations: [
    StudentHelpPageComponent,
  ],
  exports: [
    StudentHelpPageComponent,
  ],
})
export class StudentHelpPageModule { }
