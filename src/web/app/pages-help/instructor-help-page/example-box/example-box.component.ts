import { AfterViewInit, Component, ElementRef, OnInit, ViewChild } from '@angular/core';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';

/**
 * Mock NgbModal to disable modal dialogs
 */
export class DisabledNgbModal {
  /**
   * Mock open
   */
  open(_content: any): void {}
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
export class ExampleBoxComponent implements OnInit, AfterViewInit {
  @ViewChild('exampleContent') exampleContent?: ElementRef;

  constructor() { }

  ngOnInit(): void {
  }

  ngAfterViewInit(): void {
    if (this.exampleContent) {
      const elems: any = this.exampleContent.nativeElement.querySelectorAll('a, select, input, .btn, button');
      for (const elem of elems) {
        elem.setAttribute('tabindex', '-1');
      }
    }
  }

}
