import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';

import { NgbCollapseModule } from '@ng-bootstrap/ng-bootstrap';
import { GrqRgqViewResponsesComponent } from './grq-rgq-view-responses.component';



import { GroupedResponsesModule } from '../grouped-responses/grouped-responses.module';

/**
 * Module for component to display list of responses in GRQ/RGQ view.
 */
@NgModule({
  exports: [GrqRgqViewResponsesComponent],
  imports: [
    CommonModule,
    GroupedResponsesModule,
    NgbCollapseModule,
    GrqRgqViewResponsesComponent,
],
})
export class GrqRgqViewResponsesModule { }
