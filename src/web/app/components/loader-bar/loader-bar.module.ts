import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MatProgressBarModule } from '@angular/material';
import { LoaderBarComponent } from './loader-bar.component';

@NgModule({
  declarations: [LoaderBarComponent],
  imports: [
    MatProgressBarModule,
    CommonModule
  ],
  exports: [
    LoaderBarComponent,
  ]
})
export class LoaderBarModule { }
