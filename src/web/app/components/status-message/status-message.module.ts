import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { StatusMessageComponent } from './status-message.component';

/**
 * Module for status messages.
 */
@NgModule({
  imports: [
    CommonModule,
    StatusMessageComponent,
  ],
  exports: [
    StatusMessageComponent,
  ],
})
export class StatusMessageModule { }
