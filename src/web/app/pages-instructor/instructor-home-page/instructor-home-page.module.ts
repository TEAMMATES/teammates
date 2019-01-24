import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { SessionsTableModule } from '../../components/sessions-table/sessions-table.module';
import { InstructorHomePageComponent } from './instructor-home-page.component';

/**
 * Module for instructor home page.
 */
@NgModule({
  declarations: [
    InstructorHomePageComponent,
  ],
  imports: [
    CommonModule,
    SessionsTableModule,
  ],
  exports: [
    InstructorHomePageComponent,
  ],
})
export class InstructorHomePageModule { }
