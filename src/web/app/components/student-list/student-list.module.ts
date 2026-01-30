import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';
import { NgbTooltipModule } from '@ng-bootstrap/ng-bootstrap';
import { JoinStatePipe } from './join-state.pipe';
import { StudentListComponent } from './student-list.component';

import { SearchTermsHighlighterPipe } from '../../pipes/search-terms-highlighter.pipe';




/**
 * Module for student list table component.
 */
@NgModule({
  exports: [
    StudentListComponent,
  ],
  imports: [
    CommonModule,
    NgbTooltipModule,
    RouterModule,
    JoinStatePipe,
    StudentListComponent,
],
  providers: [
    SearchTermsHighlighterPipe,
  ],
})
export class StudentListModule { }
