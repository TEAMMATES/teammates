import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { SessionResultPageComponent } from './session-result-page.component';

/**
 * Module for feedback session result page.
 */
@NgModule({
  imports: [
    CommonModule,
  ],
  declarations: [
    SessionResultPageComponent,
  ],
  exports: [
    SessionResultPageComponent,
  ],
})
export class SessionResultPageModule { }
