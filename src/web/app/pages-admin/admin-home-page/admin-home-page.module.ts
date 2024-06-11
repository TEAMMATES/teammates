import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { RouterModule, Routes } from '@angular/router';
import { AdminHomePageComponent } from './admin-home-page.component';
import { NewInstructorDataRowComponent } from './new-instructor-data-row/new-instructor-data-row.component';
import { AccountRequestTableModule } from '../../components/account-requests-table/account-request-table.module';
import { AjaxLoadingModule } from '../../components/ajax-loading/ajax-loading.module';
import { LoadingSpinnerModule } from '../../components/loading-spinner/loading-spinner.module';
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
  declarations: [
    AdminHomePageComponent,
    NewInstructorDataRowComponent,
  ],
  exports: [
    AdminHomePageComponent,
  ],
  imports: [
    CommonModule,
    FormsModule,
    RouterModule.forChild(routes),
    AccountRequestTableModule,
    AjaxLoadingModule,
    LoadingSpinnerModule,
  ],
  providers: [
    FormatDateDetailPipe,
  ],
})
export class AdminHomePageModule { }
