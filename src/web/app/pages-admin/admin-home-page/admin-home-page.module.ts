import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { RouterModule, Routes } from '@angular/router';
import { AdminHomePageComponent } from './admin-home-page.component';
import { NewInstructorDataRowComponent } from './new-instructor-data-row/new-instructor-data-row.component';
import { AccountRequestTableModule } from '../../components/account-requests-table/account-request-table.module';


import { FormatDateDetailPipe } from '../../components/teammates-common/format-date-detail.pipe';

const routes: Routes = [
  {
    path: '',
    component: AdminHomePageComponent,
  },
];

/**
 * Module for admin home page.
 */
@NgModule({
  exports: [
    AdminHomePageComponent,
  ],
  imports: [
    CommonModule,
    FormsModule,
    RouterModule.forChild(routes),
    AccountRequestTableModule,
    AdminHomePageComponent,
    NewInstructorDataRowComponent,
],
  providers: [
    FormatDateDetailPipe,
  ],
})
export class AdminHomePageModule { }
