import { Component, Input, TemplateRef } from '@angular/core';
import { Router } from '@angular/router';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';
import { NavigationService } from '../../../services/navigation.service';
import { SimpleModalType } from './simple-modal-type';

/**
 * A component to display contents of confirmation modals.
 */
@Component({
  selector: 'tm-confirmation-modal',
  templateUrl: './simple-modal.component.html',
  styleUrls: ['./simple-modal.component.scss'],
})
export class SimpleModalComponent {

  // enum
  SimpleModalType: typeof SimpleModalType = SimpleModalType;

  @Input() header: string = '';
  @Input() content: string | TemplateRef<any> = '';
  @Input() type: SimpleModalType = SimpleModalType.NEUTRAL;
  @Input() isInformationOnly: boolean = false; // true will cause modal to only have 1 button
  @Input() confirmMessage: string = 'Yes'; // custom text message for confirm button
  @Input() cancelMessage: string = 'No, cancel the operation'; // custom text message for cancel button
  @Input() redirectionUrl: string = '';

  get isTemplate(): boolean {
    return this.content instanceof TemplateRef;
  }

  constructor(public activeModal: NgbActiveModal,
              private router: Router,
              private navigationService: NavigationService) { }

  redirect(): void {
    if (this.redirectionUrl) {
      this.navigationService.navigateByURL(this.router, this.redirectionUrl);
    }
  }

}
