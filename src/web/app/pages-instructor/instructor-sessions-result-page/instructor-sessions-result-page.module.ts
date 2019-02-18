import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { InstructorSessionsResultPageComponent } from './instructor-sessions-result-page.component';

/**
 * Module for instructor sessions result page.
 */
@NgModule({
  declarations: [
    InstructorSessionsResultPageComponent,
  ],
  exports: [
    InstructorSessionsResultPageComponent,
  ],
  imports: [
    CommonModule,
  ],
})
export class InstructorSessionsResultPageModule { }
