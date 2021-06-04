import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { TeammatesRouterModule } from '../../components/teammates-router/teammates-router.module';
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
  declarations: [
    FeaturesPageComponent,
  ],
  exports: [
    FeaturesPageComponent,
  ],
  imports: [
    CommonModule,
    RouterModule.forChild(routes),
    TeammatesRouterModule,
  ],
})
export class FeaturesPageModule { }
