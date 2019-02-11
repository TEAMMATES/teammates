import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { LinkRecoveryPageComponent } from './link-recovery-page.component';

/**
 * Module for student recover session links page.
 */
@NgModule({
  imports: [
    CommonModule,
  ],
  declarations: [
    LinkRecoveryPageComponent,
  ],
  exports: [
    LinkRecoveryPageComponent,
  ],
})
export class LinkRecoveryPageModule { }
