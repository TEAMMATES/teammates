import { Component, Input, OnInit, inject } from '@angular/core';
import { FormControl, ReactiveFormsModule } from '@angular/forms';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap/modal';
import { EditRequestModalComponentResult } from './admin-edit-request-modal-model';
import { CountryService } from '../../../../services/country.service';
import {
  ComboboxOption,
  SearchableComboboxComponent,
} from '../../../components/searchable-combobox/searchable-combobox.component';

/**
 * Modal to edit account requests.
 */
@Component({
  selector: 'tm-edit-request-modal',
  templateUrl: './admin-edit-request-modal.component.html',
  imports: [ReactiveFormsModule, SearchableComboboxComponent],
})
export class EditRequestModalComponent implements OnInit {
  activeModal = inject(NgbActiveModal);
  private readonly countryService = inject(CountryService);

  readonly countryOptions: ComboboxOption<string>[] = this.countryService.getCountryOptions().map((o) => ({
    value: o.code,
    label: o.name,
  }));

  country = new FormControl('');

  @Input()
  accountVerificationRequestName = '';
  @Input()
  accountVerificationRequestEmail = '';
  @Input()
  accountVerificationRequestInstitution = '';
  @Input()
  accountVerificationRequestCountry = '';
  @Input()
  accountVerificationRequestComments = '';

  ngOnInit(): void {
    this.country.setValue(this.accountVerificationRequestCountry);
  }

  /**
   * Fires the edit event.
   */
  edit(): void {
    const result: EditRequestModalComponentResult = {
      accountVerificationRequestName: this.accountVerificationRequestName,
      accountVerificationRequestEmail: this.accountVerificationRequestEmail,
      accountVerificationRequestInstitution: this.accountVerificationRequestInstitution,
      accountVerificationRequestCountry: this.country.value ?? '',
      accountVerificationRequestComment: this.accountVerificationRequestComments,
    };

    this.activeModal.close(result);
  }
}
