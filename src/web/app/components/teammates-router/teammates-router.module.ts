import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { TeammatesRouterDirective } from './teammates-router.directive';

/**
 * Module for routing supporting masquerade mode
 */
@NgModule({
  imports: [
    CommonModule,
    TeammatesRouterDirective,
  ],
  exports: [TeammatesRouterDirective],
})
export class TeammatesRouterModule { }
