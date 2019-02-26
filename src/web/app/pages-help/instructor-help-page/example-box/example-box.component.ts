import { Component, OnInit } from '@angular/core';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';

export class DisabledNgbModal {
  open(_content: any): void {}
}

@Component({
  selector: 'tm-example-box',
  templateUrl: './example-box.component.html',
  styleUrls: ['./example-box.component.scss'],
  providers: [{ provide: NgbModal, useClass: DisabledNgbModal }]
})
export class ExampleBoxComponent implements OnInit {

  constructor() { }

  ngOnInit() {
  }

}
