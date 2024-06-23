import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { RouterModule, Routes } from '@angular/router';
import { NgbTooltipModule } from '@ng-bootstrap/ng-bootstrap';
import { AdminSearchPageComponent } from './admin-search-page.component';
import {
  AccountRequestTableModule,
} from '../../components/account-requests-table/account-request-table.module';
import { AjaxLoadingModule } from '../../components/ajax-loading/ajax-loading.module';
import { Pipes } from '../../pipes/pipes.module';

const routes: Routes = [
  {
    path: '',
    component: AdminSearchPageComponent,
  },
];

/**
 * Module for admin search page.
 */
@NgModule({
  declarations: [
    AdminSearchPageComponent,
  ],
  exports: [
    AdminSearchPageComponent,
  ],
  imports: [
    CommonModule,
    FormsModule,
    NgbTooltipModule,
    AccountRequestTableModule,
    RouterModule.forChild(routes),
    Pipes,
    AjaxLoadingModule,
  ],
})
export class AdminSearchPageModule { }
