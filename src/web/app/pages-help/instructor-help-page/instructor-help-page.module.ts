import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { InstructorHelpPageComponent } from './instructor-help-page.component';

/**
 * Module for instructor help page.
 */
@NgModule({
  imports: [
    CommonModule,
  ],
  declarations: [
    InstructorHelpPageComponent,
  ],
  exports: [
    InstructorHelpPageComponent,
  ],
})
export class InstructorHelpPageModule { }
