import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';
import { NgbTooltipModule } from '@ng-bootstrap/ng-bootstrap';
import { Pipes } from '../../pipes/pipes.module';

import { TeammatesCommonModule } from '../teammates-common/teammates-common.module';
import { TeammatesRouterModule } from '../teammates-router/teammates-router.module';
// import { JoinStatePipe } from './join-state.pipe';
import { SupportListComponent } from './support-ticket-list.component';

/**
 * Module for student list table component.
 */
@NgModule({
  declarations: [
    // JoinStatePipe,
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
    Pipes,
  ],
})
export class SupportTicketListModule { }
