import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';
import { AboutPageComponent } from './about-page.component';

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
    RouterModule,
  ],
})
export class AboutPageModule { }
