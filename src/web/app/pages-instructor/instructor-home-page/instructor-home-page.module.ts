import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { SessionsTableModule } from '../../components/sessions-table/sessions-table.module';
import { InstructorHomePageComponent } from './instructor-home-page.component';
import { RouterModule } from "@angular/router";

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
    FormsModule,
    RouterModule,
  ],
  exports: [
    InstructorHomePageComponent,
  ],
})
export class InstructorHomePageModule { }
