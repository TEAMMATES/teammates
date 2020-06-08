import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { NgbModule } from '@ng-bootstrap/ng-bootstrap';
import { SortableTableComponent } from './sortable-table.component';

/**
 * Module for displaying data in a sortable table
 */
@NgModule({
  declarations: [SortableTableComponent],
  imports: [
    CommonModule,
    NgbModule,
  ],
  exports: [
    SortableTableComponent,
  ],
})
export class SortableTableModule { }
