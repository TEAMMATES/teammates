import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { GroupedResponsesModule } from '../grouped-responses/grouped-responses.module';
import { GrqRgqViewResponsesComponent } from './grq-rgq-view-responses.component';

/**
 * Module for component to display list of responses in GRQ/RGQ view.
 */
@NgModule({
  declarations: [GrqRgqViewResponsesComponent],
  exports: [GrqRgqViewResponsesComponent],
  imports: [
    CommonModule,
    GroupedResponsesModule,
  ],
})
export class GrqRgqViewResponsesModule { }
