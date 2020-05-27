import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ViewPhotoPopoverComponent } from "./view-photo-popover.component";
import { RouterModule } from "@angular/router";
import { NgbModule } from "@ng-bootstrap/ng-bootstrap";

@NgModule({
  declarations: [ViewPhotoPopoverComponent],
  imports: [
    CommonModule,
    RouterModule,
    NgbModule
  ],
  exports: [ViewPhotoPopoverComponent]
})
export class ViewPhotoPopoverModule { }
