import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { LoaderBarComponent } from './loader-bar.component';

@NgModule({
  declarations: [LoaderBarComponent],
  imports: [
    CommonModule
  ],
  exports: [
    LoaderBarComponent,
  ]
})
export class LoaderBarModule { }
