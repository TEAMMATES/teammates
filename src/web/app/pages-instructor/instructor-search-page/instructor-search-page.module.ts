import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { InstructorSearchComponentsModule } from './instructor-search-components.module';
import { InstructorSearchPageComponent } from './instructor-search-page.component';



const routes: Routes = [
  {
    path: '',
    component: InstructorSearchPageComponent,
  },
];

/**
 * Module for instructor search page.
 */
@NgModule({
  exports: [
    InstructorSearchPageComponent,
  ],
  imports: [
    CommonModule,
    InstructorSearchComponentsModule,
    RouterModule.forChild(routes),
    InstructorSearchPageComponent,
],
})
export class InstructorSearchPageModule { }
