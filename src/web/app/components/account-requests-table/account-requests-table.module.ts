import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { NgbTooltipModule, NgbDropdownModule } from '@ng-bootstrap/ng-bootstrap';
import { Pipes } from '../../pipes/pipes.module';
import { AccountRequestsTableComponent } from './account-requests-table.component';
import { AdjustWidthDirective } from '../width-adjuster/adjust-width-directive';

/**
 * Module for admin search page.
 */
@NgModule({
  declarations: [
    AccountRequestsTableComponent,
    AdjustWidthDirective,
  ],
  exports: [
    AccountRequestsTableComponent,
  ],
  imports: [
    CommonModule,
    FormsModule,
    NgbTooltipModule,
    NgbDropdownModule,
    Pipes
  ],
})
export class AccountRequestsTableModule { }
