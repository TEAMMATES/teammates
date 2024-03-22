import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { NgbTooltipModule, NgbDropdownModule } from '@ng-bootstrap/ng-bootstrap';
import { AccountRequestsTableComponent } from './account-requests-table.component';
import { Pipes } from '../../pipes/pipes.module';

/**
 * Module for admin search page.
 */
@NgModule({
  declarations: [
    AccountRequestsTableComponent,
  ],
  exports: [
    AccountRequestsTableComponent,
  ],
  imports: [
    CommonModule,
    FormsModule,
    NgbTooltipModule,
    NgbDropdownModule,
    Pipes,
  ],
})
export class AccountRequestsTableModule { }