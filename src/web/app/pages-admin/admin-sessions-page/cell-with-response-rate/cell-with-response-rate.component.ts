import { CommonModule } from '@angular/common';
import { Component, Input } from '@angular/core';
import { OngoingSessionModel } from '../../../../types/api-output';
import { AjaxLoadingModule } from '../../../components/ajax-loading/ajax-loading.module';

@Component({
    selector: 'tm-response-rate',
    templateUrl: './cell-with-response-rate.component.html',
    imports: [AjaxLoadingModule, CommonModule],
    standalone: true,
})
export class ResponseRateComponent {
    @Input() session!: OngoingSessionModel;
    @Input() getResponseRate!: () => void;
    empty: boolean = true;
    isLoading: boolean = false;

    callGetResponseRate(): void {
      this.isLoading = true;
      this.getResponseRate();
      this.empty = false;
      this.isLoading = false;
    }
}
