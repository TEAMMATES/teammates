import { CommonModule } from '@angular/common';
import { Component, Input } from '@angular/core';
import { AjaxLoadingModule } from '../ajax-loading/ajax-loading.module';

@Component({
  selector: 'tm-response-rate',
  templateUrl: './cell-with-response-rate.component.html',
  imports: [AjaxLoadingModule, CommonModule],
  standalone: true,
})
export class ResponseRateComponent {
  @Input() responseRate: string = '';
  @Input() idx: number = 0;
  @Input() empty: boolean = false;
  @Input() isLoading: boolean = false;
  @Input() onClick: () => void = () => { };

}
