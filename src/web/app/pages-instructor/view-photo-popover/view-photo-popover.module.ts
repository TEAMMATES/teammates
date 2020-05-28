import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';
import { NgbModule } from '@ng-bootstrap/ng-bootstrap';

import { ViewPhotoPopoverComponent } from './view-photo-popover.component';

/**
 * Module for component to display a photo popover
 */
@NgModule({
  declarations: [ViewPhotoPopoverComponent],
  imports: [
    CommonModule,
    RouterModule,
    NgbModule,
  ],
  exports: [ViewPhotoPopoverComponent],
})
export class ViewPhotoPopoverModule { }
