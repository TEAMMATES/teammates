import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { TeammatesCommonModule } from '../teammates-common/teammates-common.module';
import { NotificationBannerComponent } from './notification-banner.component';

/**
 * Module for banner used to display notifications to the user.
 */
@NgModule({
  declarations: [NotificationBannerComponent],
  exports: [NotificationBannerComponent],
  imports: [CommonModule, TeammatesCommonModule],
})
export class NotificationBannerModule { }
