import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { AjaxLoadingComponent } from './ajax-loading.component';

/**
 * Ajax loading module.
 */
@NgModule({
  imports: [
    CommonModule,
  ],
  declarations: [
    AjaxLoadingComponent,
  ],
  exports: [
    AjaxLoadingComponent,
  ],
})
export class AjaxLoadingModule { }
