import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';
import { FeaturesPageComponent } from './features-page.component';

/**
 * Module for features page.
 */
@NgModule({
  declarations: [
    FeaturesPageComponent,
  ],
  exports: [
    FeaturesPageComponent,
  ],
  imports: [
    CommonModule,
    RouterModule,
  ],
})
export class FeaturesPageModule { }
