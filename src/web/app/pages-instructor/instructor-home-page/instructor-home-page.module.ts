import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { RouterModule } from '@angular/router';
import { NgbModule } from '@ng-bootstrap/ng-bootstrap';
import { LoadingSpinnerModule } from '../../components/loading-spinner/loading-spinner.module';
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
    LoadingSpinnerModule,
    CommonModule,
    SessionsTableModule,
    FormsModule,
    RouterModule,
    NgbModule,
  ],
  exports: [
    InstructorHomePageComponent,
  ],
})
export class InstructorHomePageModule { }
