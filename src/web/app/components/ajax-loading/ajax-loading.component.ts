import { Component, Input } from '@angular/core';

/**
 * Displaying the ajax loader.
 */
@Component({
  selector: 'tm-ajax-loading',
  templateUrl: './ajax-loading.component.html',
  styleUrls: ['./ajax-loading.component.scss'],
})
export class AjaxLoadingComponent {

  @Input()
  useBlueSpinner: boolean = false;

  @Input()
  color: string = 'white';

}
