import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { PanelChevronComponent } from './panel-chevron.component';

/**
 * Module for chevron icon used in panel headers.
 */
@NgModule({
  exports: [PanelChevronComponent],
  imports: [CommonModule, PanelChevronComponent],
})
export class PanelChevronModule { }
