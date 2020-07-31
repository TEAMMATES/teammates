import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { PanelChevronComponent } from './panel-chevron.component';

/**
 * Module for chevron icon used in panel headers.
 */
@NgModule({
  declarations: [PanelChevronComponent],
  exports: [PanelChevronComponent],
  imports: [CommonModule],
})
export class PanelChevronModule { }
