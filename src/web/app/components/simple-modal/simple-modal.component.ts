import { Component, Input, OnInit, TemplateRef } from '@angular/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';
import { SimpleModalButton, SimpleModalType } from './simple-modal';

/**
 * A component to display contents of confirmation modals.
 */
@Component({
  selector: 'tm-simple-modal',
  templateUrl: './simple-modal.component.html',
  styleUrls: ['./simple-modal.component.scss'],
})
export class SimpleModalComponent implements OnInit {

  // enum
  SimpleModalType: typeof SimpleModalType = SimpleModalType;

  @Input() header: string | TemplateRef<any> = '';
  @Input() content: string | TemplateRef<any> = '';
  @Input() context: Record<string, any> = {};
  @Input() type: SimpleModalType = SimpleModalType.NEUTRAL;
  @Input() buttons: SimpleModalButton[] = [];

  get isHeaderTemplate(): boolean {
    return this.header instanceof TemplateRef;
  }

  get isTemplate(): boolean {
    return this.content instanceof TemplateRef;
  }

  constructor(public activeModal: NgbActiveModal) { }

  ngOnInit(): void {
  }

}
