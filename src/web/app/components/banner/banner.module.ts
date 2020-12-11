import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { BannerComponent } from './banner.component';

/**
 * Module for redirect banner.
 */
@NgModule({
    declarations: [BannerComponent],
    exports: [
        BannerComponent,
    ],
    imports: [
        CommonModule,
    ],
})
export class BannerModule { }