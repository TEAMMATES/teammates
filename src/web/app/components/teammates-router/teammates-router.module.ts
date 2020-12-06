import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { TeammatesRouterDirective } from './teammates-router.directive';

/**
 * Module for routing supporting masquerade mode
 */
@NgModule({
  declarations: [TeammatesRouterDirective],
  imports: [
    CommonModule,
  ],
  exports: [TeammatesRouterDirective],

})
export class TeammatesRouterModule { }
