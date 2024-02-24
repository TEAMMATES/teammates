import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { UserNotificationsListComponent } from './user-notifications-list.component';
import { LoadingRetryModule } from '../loading-retry/loading-retry.module';
import { LoadingSpinnerModule } from '../loading-spinner/loading-spinner.module';
import { PanelChevronModule } from '../panel-chevron/panel-chevron.module';
import { TeammatesCommonModule } from '../teammates-common/teammates-common.module';

/**
 * Module for user notifications list.
 */
@NgModule({
  imports: [
    CommonModule,
    TeammatesCommonModule,
    PanelChevronModule,
    LoadingSpinnerModule,
    LoadingRetryModule,
  ],
  declarations: [
    UserNotificationsListComponent,
  ],
  exports: [
    UserNotificationsListComponent,
  ],
})
export class UserNotificationsListModule { }
