import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { RouterModule } from '@angular/router';
import { NgbDropdownModule, NgbPaginationModule, NgbTooltipModule, NgbTypeaheadModule } from '@ng-bootstrap/ng-bootstrap';

import { TeammatesCommonModule } from '../teammates-common/teammates-common.module';
import { TeammatesRouterModule } from '../teammates-router/teammates-router.module';
import { SupportListComponent } from './support-ticket-list.component';

/**
 * Module for student list table component.
 */
@NgModule({
  declarations: [
    SupportListComponent,
  ],
  exports: [
    SupportListComponent,
  ],
  imports: [
    CommonModule,
    NgbTooltipModule,
    RouterModule,
    TeammatesCommonModule,
    TeammatesRouterModule,
    NgbPaginationModule,
    NgbTypeaheadModule,
    NgbDropdownModule,  
    ReactiveFormsModule, 
    FormsModule
  ]
})
export class SupportTicketListModule { }
