import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { LoadingSpinnerComponent } from './loading-spinner.component';

@NgModule({
  declarations: [
    LoadingSpinnerComponent,
  ],
  imports: [
    CommonModule,
  ],
  exports: [
    LoadingSpinnerComponent,
  ]
})
export class LoadingSpinnerModule { }
