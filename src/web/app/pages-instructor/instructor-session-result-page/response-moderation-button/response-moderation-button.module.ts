import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';

import { ResponseModerationButtonComponent } from './response-moderation-button.component';

/**
 * Module for moderating response button.
 */
@NgModule({
  declarations: [
    ResponseModerationButtonComponent,
  ],
  imports: [
    CommonModule,
    RouterModule,
  ],
  exports: [
    ResponseModerationButtonComponent,
  ],
})
export class ResponseModerationButtonModule { }
