import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';
import { NgbTooltipModule } from '@ng-bootstrap/ng-bootstrap';
import { JoinStatePipe } from './join-state.pipe';
import { StudentListComponent } from './student-list.component';
import { Pipes } from '../../pipes/pipes.module';
import { SearchTermsHighlighterPipe } from '../../pipes/search-terms-highlighter.pipe';
import { SortableTableModule } from '../sortable-table/sortable-table.module';
import { TeammatesCommonModule } from '../teammates-common/teammates-common.module';
import { TeammatesRouterModule } from '../teammates-router/teammates-router.module';

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
    TeammatesCommonModule,
    TeammatesRouterModule,
    Pipes,
    SortableTableModule,
    JoinStatePipe,
    StudentListComponent,
  ],
  providers: [
    SearchTermsHighlighterPipe,
  ],
})
export class StudentListModule { }
