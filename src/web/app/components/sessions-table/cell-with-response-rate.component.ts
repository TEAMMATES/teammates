
import { Component, Input } from '@angular/core';

import { AjaxLoadingComponent } from '../ajax-loading/ajax-loading.component';

@Component({
    selector: 'tm-response-rate',
    templateUrl: './cell-with-response-rate.component.html',
    imports: [AjaxLoadingComponent],
})
export class ResponseRateComponent {
  @Input() responseRate: string = '';
  @Input() idx: number = 0;
  @Input() empty: boolean = false;
  @Input() isLoading: boolean = false;
  @Input() onClick: () => void = () => { };

}
