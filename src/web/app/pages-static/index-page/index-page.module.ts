import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { IndexPageComponent } from './index-page.component';
import { TeammatesRouterModule } from '../../components/teammates-router/teammates-router.module';

const routes: Routes = [
  {
    path: '',
    component: IndexPageComponent,
  },
];

/**
 * Module for index page.
 */
@NgModule({
  exports: [
    IndexPageComponent,
  ],
  imports: [
    CommonModule,
    RouterModule.forChild(routes),
    TeammatesRouterModule,
    IndexPageComponent,
  ],
})
export class IndexPageModule { }
