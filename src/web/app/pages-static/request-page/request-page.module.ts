import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { RequestPageComponent } from './request-page.component';

/**
 * Module for request page.
 */
@NgModule({
  declarations: [
    RequestPageComponent,
  ],
  exports: [
    RequestPageComponent,
  ],
  imports: [
    CommonModule,
  ],
})
export class RequestPageModule { }
