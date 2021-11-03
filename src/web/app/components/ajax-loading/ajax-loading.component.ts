import { Component, Input, OnInit } from '@angular/core';

/**
 * Displaying the ajax loader.
 */
@Component({
  selector: 'tm-ajax-loading',
  templateUrl: './ajax-loading.component.html',
  styleUrls: ['./ajax-loading.component.scss'],
})
export class AjaxLoadingComponent implements OnInit {

  @Input()
  useBlueSpinner: boolean = false;

  @Input()
  color: string = 'white';

  constructor() { }

  ngOnInit(): void {
  }

}
