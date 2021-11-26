import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { RouterModule, Routes } from '@angular/router';
import { NgbDatepickerModule, NgbDropdownModule } from '@ng-bootstrap/ng-bootstrap';
import { LoadingSpinnerModule } from '../../components/loading-spinner/loading-spinner.module';
import { PanelChevronModule } from '../../components/panel-chevron/panel-chevron.module';
import { SortableTableModule } from '../../components/sortable-table/sortable-table.module';
import { TimepickerModule } from '../../components/timepicker/timepicker.module';
import { InstructorAuditLogsPageComponent } from './instructor-audit-logs-page.component';

const routes: Routes = [
  {
    path: '',
    component: InstructorAuditLogsPageComponent,
  },
];

/**
 * Module for instructor audit logs page
 */
@NgModule({
  declarations: [InstructorAuditLogsPageComponent],
  exports: [InstructorAuditLogsPageComponent],
  imports: [
    CommonModule,
    RouterModule.forChild(routes),
    NgbDatepickerModule,
    FormsModule,
    NgbDropdownModule,
    LoadingSpinnerModule,
    PanelChevronModule,
    SortableTableModule,
    TimepickerModule,
  ],
})
export class InstructorAuditLogsPageModule { }
