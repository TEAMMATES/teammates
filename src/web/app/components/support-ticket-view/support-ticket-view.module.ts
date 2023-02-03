import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';
import { Pipes } from '../../pipes/pipes.module';

import { TeammatesCommonModule } from '../teammates-common/teammates-common.module';
import { TeammatesRouterModule } from '../teammates-router/teammates-router.module';
import { SupportViewComponent } from './support-ticket-view.component';

/**
 * Module for student list table component.
 */
@NgModule({
  declarations: [
    SupportViewComponent,
  ],
  exports: [
    SupportViewComponent,
  ],
  imports: [
    CommonModule,
    RouterModule,
    TeammatesCommonModule,
    TeammatesRouterModule,
    Pipes,
  ],
})
export class SupportTicketViewModule { }
