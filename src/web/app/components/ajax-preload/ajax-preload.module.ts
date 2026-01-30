import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { AjaxPreloadComponent } from './ajax-preload.component';

/**
 * Ajax preload module.
 */
@NgModule({
  imports: [
    CommonModule,
    AjaxPreloadComponent,
  ],
  exports: [
    AjaxPreloadComponent,
  ],
})
export class AjaxPreloadModule { }
