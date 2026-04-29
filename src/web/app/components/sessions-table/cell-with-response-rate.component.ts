import { Component, Input } from '@angular/core';

import { AjaxLoadingComponent } from '../ajax-loading/ajax-loading.component';

@Component({
    selector: 'tm-response-rate',
    templateUrl: './cell-with-response-rate.component.html',
    imports: [AjaxLoadingComponent],
})
export class ResponseRateComponent {
  @Input() responseRate = '';
  @Input() idx = 0;
  @Input() empty = false;
  @Input() isLoading = false;
  @Input() onClick: () => void = () => { };

}
