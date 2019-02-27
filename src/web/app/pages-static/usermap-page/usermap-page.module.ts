import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { UsermapPageComponent } from './usermap-page.component';

/**
 * Module for usermap page.
 */
@NgModule({
  declarations: [
    UsermapPageComponent,
  ],
  exports: [
    UsermapPageComponent,
  ],
  imports: [
    CommonModule,
  ],
})
export class UsermapPageModule { }
