import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { SessionEditFormModule } from '../../components/session-edit-form/session-edit-form.module';
import { InstructorSessionsPageComponent } from './instructor-sessions-page.component';

/**
 * Module for instructor sessions page.
 */
@NgModule({
  imports: [
    CommonModule,
    SessionEditFormModule,
  ],
  declarations: [
    InstructorSessionsPageComponent,
  ],
  exports: [
    InstructorSessionsPageComponent,
  ],
})
export class InstructorSessionsPageModule { }
