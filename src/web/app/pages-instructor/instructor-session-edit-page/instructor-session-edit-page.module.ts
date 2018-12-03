import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { SessionEditFormModule } from '../../components/session-edit-form/session-edit-form.module';
import { InstructorSessionEditPageComponent } from './instructor-session-edit-page.component';

/**
 * Module for instructor session edit page.
 */
@NgModule({
  imports: [
    CommonModule,
    SessionEditFormModule,
  ],
  declarations: [
    InstructorSessionEditPageComponent,
  ],
  exports: [
    InstructorSessionEditPageComponent,
  ],
})
export class InstructorSessionEditPageModule { }
