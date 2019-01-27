import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { VisibilityCapabilityPipe } from './visibility-capability.pipe';
import { VisibilityEntityNamePipe } from './visibility-entity-name.pipe';

/**
 * Module to generate visibility messages.
 */
@NgModule({
  imports: [
    CommonModule,
  ],
  declarations: [
    VisibilityEntityNamePipe,
    VisibilityCapabilityPipe,
  ],
  exports: [
    VisibilityEntityNamePipe,
    VisibilityCapabilityPipe,
  ],
})
export class VisibilityMessagesModule { }
