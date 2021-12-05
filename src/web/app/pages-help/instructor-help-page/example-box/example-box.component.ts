import { Component, Injectable } from '@angular/core';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';

/**
 * Mock NgbModal to disable modal dialogs
 */
@Injectable()
export class DisabledNgbModal {
  /**
   * Mock open
   */
  open(): void {}
}

/**
 * Surround the component with an example box and disable some functionalities
 */
@Component({
  selector: 'tm-example-box',
  templateUrl: './example-box.component.html',
  styleUrls: ['./example-box.component.scss'],
  providers: [{ provide: NgbModal, useClass: DisabledNgbModal }],
})
export class ExampleBoxComponent {}
