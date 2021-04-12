import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';

import { TeammatesRouterModule } from '../../../components/teammates-router/teammates-router.module';
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
    TeammatesRouterModule,
  ],
  exports: [
    ResponseModerationButtonComponent,
  ],
})
export class ResponseModerationButtonModule { }
