import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { NgbTooltipModule, NgbDropdownModule } from '@ng-bootstrap/ng-bootstrap';
import { AccountRequestTableComponent } from './account-request-table.component';
import { Pipes } from '../../pipes/pipes.module';

/**
 * Module for account requests table.
 */
@NgModule({
  declarations: [
    AccountRequestTableComponent,
  ],
  exports: [
    AccountRequestTableComponent,
  ],
  imports: [
    CommonModule,
    FormsModule,
    NgbTooltipModule,
    NgbDropdownModule,
    Pipes,
  ],
})
export class AccountRequestTableModule { }
