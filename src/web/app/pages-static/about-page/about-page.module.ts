import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { AboutPageComponent } from './about-page.component';

const routes: Routes = [
  {
    path: '',
    component: AboutPageComponent,
  },
];

/**
 * Module for about page.
 */
@NgModule({
  declarations: [
    AboutPageComponent,
  ],
  exports: [
    AboutPageComponent,
  ],
  imports: [
    CommonModule,
    RouterModule.forChild(routes),
  ],
})
export class AboutPageModule { }
