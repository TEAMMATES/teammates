import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';
import { IndexPageComponent } from './index-page.component';

/**
 * Module for index page.
 */
@NgModule({
  declarations: [
    IndexPageComponent,
  ],
  exports: [
    IndexPageComponent,
  ],
  imports: [
    CommonModule,
    RouterModule,
  ],
})
export class IndexPageModule { }
