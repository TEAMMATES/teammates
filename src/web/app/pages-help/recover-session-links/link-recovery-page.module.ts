import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { LinkRecoveryPageComponent } from './link-recovery-page.component';

/**
 * Module for student recover session links page.
 */
@NgModule({
  imports: [
    CommonModule,
    FormsModule,
  ],
  declarations: [
    LinkRecoveryPageComponent,
  ],
  exports: [
    LinkRecoveryPageComponent,
  ],
})
export class LinkRecoveryPageModule { }
