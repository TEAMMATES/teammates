import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';

import { ResponseModerationButtonComponent } from './response-moderation-button.component';
import { TeammatesRouterModule } from '../../../components/teammates-router/teammates-router.module';

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
