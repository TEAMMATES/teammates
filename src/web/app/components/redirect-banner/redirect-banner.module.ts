import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { RedirectBannerComponent } from './redirect-banner.component';

/**
 * Module for redirect banner.
 */
@NgModule({
  declarations: [RedirectBannerComponent],
  exports: [
    RedirectBannerComponent,
  ],
  imports: [
    CommonModule,
  ],
})
export class RedirectBannerModule { }
