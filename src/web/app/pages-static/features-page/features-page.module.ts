import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { FeaturesPageComponent } from './features-page.component';


const routes: Routes = [
  {
    path: '',
    component: FeaturesPageComponent,
  },
];

/**
 * Module for features page.
 */
@NgModule({
  exports: [
    FeaturesPageComponent,
  ],
  imports: [
    CommonModule,
    RouterModule.forChild(routes),
    FeaturesPageComponent,
],
})
export class FeaturesPageModule { }
