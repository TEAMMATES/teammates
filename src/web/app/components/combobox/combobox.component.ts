import { NgFor, NgIf } from '@angular/common';
import { Component, ElementRef, EventEmitter, HostListener, Input, OnChanges, Output } from '@angular/core';
import { FormsModule } from '@angular/forms';

export interface ComboboxOption {
  label: string;
  value: string;
}

/**
 * Reusable combobox: a searchable dropdown that filters options as the user types.
 */
@Component({
  selector: 'tm-combobox',
  templateUrl: './combobox.component.html',
  styleUrls: ['./combobox.component.scss'],
  imports: [NgFor, NgIf, FormsModule],
})
export class ComboboxComponent implements OnChanges {

  @Input() options: ComboboxOption[] = [];
  @Input() selectedValue: string = '';
  @Input() placeholder: string = 'Select an option';
  @Input() disabled: boolean = false;

  @Output() selectionChange = new EventEmitter<string>();

  inputText: string = '';
  isOpen: boolean = false;
  filteredOptions: ComboboxOption[] = [];
  activeIndex: number = -1;

  constructor(private elementRef: ElementRef) {}

  ngOnChanges(): void {
    this.filteredOptions = this.options;
    if (!this.isOpen) {
      this.syncInputToSelection();
    }
  }

  // Restores the input to show the currently selected option's label.
  private syncInputToSelection(): void {
    const selected = this.options.find((o) => o.value === this.selectedValue);
    this.inputText = selected ? selected.label : '';
  }

  openDropdown(): void {
    if (this.disabled) return;
    this.isOpen = true;
    this.inputText = '';
    this.filteredOptions = this.options;
    this.activeIndex = -1;
  }

  onToggleClick(event: MouseEvent): void {
    event.stopPropagation();
    if (this.isOpen) {
      this.closeDropdown();
    } else {
      this.openDropdown();
    }
  }

  onInputChange(): void {
    this.filteredOptions = this.options.filter((o) =>
      o.label.toLowerCase().includes(this.inputText.toLowerCase()),
    );
    this.activeIndex = -1;
    if (!this.isOpen) {
      this.isOpen = true;
    }
  }

  selectOption(option: ComboboxOption): void {
    this.selectedValue = option.value;
    this.inputText = option.label;
    this.isOpen = false;
    this.activeIndex = -1;
    this.selectionChange.emit(option.value);
  }

  onKeydown(event: KeyboardEvent): void {
    if (!this.isOpen) {
      if (event.key === 'ArrowDown' || event.key === 'Enter') {
        this.openDropdown();
      }
      return;
    }
    switch (event.key) {
      case 'ArrowDown':
        event.preventDefault();
        this.activeIndex = Math.min(this.activeIndex + 1, this.filteredOptions.length - 1);
        break;
      case 'ArrowUp':
        event.preventDefault();
        this.activeIndex = Math.max(this.activeIndex - 1, 0);
        break;
      case 'Enter':
        event.preventDefault();
        if (this.activeIndex >= 0 && this.activeIndex < this.filteredOptions.length) {
          this.selectOption(this.filteredOptions[this.activeIndex]);
        }
        break;
      case 'Escape':
        this.closeDropdown();
        break;
      default:
        break;
    }
  }

  closeDropdown(): void {
    this.isOpen = false;
    this.syncInputToSelection();
    this.activeIndex = -1;
  }

  @HostListener('document:click', ['$event'])
  onDocumentClick(event: MouseEvent): void {
    if (!this.elementRef.nativeElement.contains(event.target) && this.isOpen) {
      this.closeDropdown();
    }
  }

}
