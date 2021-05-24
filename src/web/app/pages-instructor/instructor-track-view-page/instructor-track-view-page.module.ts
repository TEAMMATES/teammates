import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule, Routes } from '@angular/router';
import { InstructorTrackViewPageComponent } from './instructor-track-view-page.component';
import { FormsModule } from '@angular/forms';
import { SessionEditFormModule } from '../../components/session-edit-form/session-edit-form.module';
import { NgbDropdownModule } from '@ng-bootstrap/ng-bootstrap';
import { LoadingSpinnerModule } from '../../components/loading-spinner/loading-spinner.module';
import { PanelChevronModule } from '../../components/panel-chevron/panel-chevron.module';
import { SortableTableModule } from '../../components/sortable-table/sortable-table.module';

const routes: Routes = [
  {
    path: '',
    component: InstructorTrackViewPageComponent,
  },
];

/**
 * Module for instructor audit logs page
 */
 @NgModule({
  declarations: [InstructorTrackViewPageComponent],
  exports: [InstructorTrackViewPageComponent],
  imports: [
    CommonModule,
    RouterModule.forChild(routes),
    FormsModule,
    SessionEditFormModule,
    NgbDropdownModule,
    LoadingSpinnerModule,
    PanelChevronModule,
    SortableTableModule,
  ],
})
export class InstructorTrackViewPageModule { }
