import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ComboboxOption, SearchableComboboxComponent } from './searchable-combobox.component';

interface Student {
  id: string;
  name: string;
  email: string;
}

describe('SearchableComboboxComponent', () => {
  let component: SearchableComboboxComponent<string, Student>;
  let fixture: ComponentFixture<SearchableComboboxComponent<string, Student>>;
  let inputEl: HTMLInputElement;
  let comboboxEl: HTMLElement;

  const studentAlice: Student = {
    id: 'student-1',
    name: 'Alice Tan',
    email: 'alice@example.com',
  };
  const studentBob: Student = {
    id: 'student-2',
    name: 'Bob Ng',
    email: 'bob@example.com',
  };

  const aliceOption: ComboboxOption<string, Student> = {
    value: studentAlice.id,
    label: studentAlice.name,
    keywords: [studentAlice.email],
    data: studentAlice,
  };
  const bobOption: ComboboxOption<string, Student> = {
    value: studentBob.id,
    label: studentBob.name,
    keywords: [studentBob.email],
    data: studentBob,
  };

  beforeEach(() => {
    fixture = TestBed.createComponent<SearchableComboboxComponent<string, Student>>(SearchableComboboxComponent);
    component = fixture.componentInstance;
    component.options = [aliceOption, bobOption];
    fixture.detectChanges();
    inputEl = fixture.nativeElement.querySelector('input');
    comboboxEl = fixture.nativeElement.querySelector('.searchable-combobox');
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('writeValue: should display the selected option label', () => {
    component.writeValue(studentAlice.id);
    fixture.detectChanges();

    expect(inputEl.value).toBe(studentAlice.name);
  });

  it('filteredOptions: should show all options when input is empty', () => {
    expect(component.filteredOptions()).toEqual([aliceOption, bobOption]);
  });

  it('filteredOptions: should filter by label', () => {
    component.onInputValueChange('bob');

    expect(component.filteredOptions()).toEqual([bobOption]);
  });

  it('filteredOptions: should filter by keywords', () => {
    component.onInputValueChange('alice@example.com');

    expect(component.filteredOptions()).toEqual([aliceOption]);
  });

  it('onInputClick: should bypass active filter and show all options', () => {
    component.writeValue(studentAlice.id);
    component.onInputValueChange('alice');

    component.onInputClick();

    expect(component.filteredOptions()).toEqual([aliceOption, bobOption]);
  });

  it('onValuesChange: should update input and emit selected option', () => {
    const optionSelectedSpy = vi.spyOn(component.optionSelected, 'emit');
    const onChangeSpy = vi.fn();
    component.registerOnChange(onChangeSpy);

    component.onValuesChange([studentBob.id]);
    fixture.detectChanges();

    expect(inputEl.value).toBe(studentBob.name);
    expect(onChangeSpy).toHaveBeenCalledWith(studentBob.id);
    expect(optionSelectedSpy).toHaveBeenCalledWith(bobOption);
  });

  it('onValuesChange: should not select a value that has no matching option', () => {
    const onChangeSpy = vi.fn();
    component.registerOnChange(onChangeSpy);

    component.onValuesChange(['unmatched-id']);
    fixture.detectChanges();

    expect(inputEl.value).toBe('');
    expect(onChangeSpy).toHaveBeenCalledWith('unmatched-id');
  });

  it('onValuesChange: should not clear a selected value when the listbox emits no values', () => {
    component.writeValue(studentAlice.id);

    const onChangeSpy = vi.fn();
    component.registerOnChange(onChangeSpy);
    component.onValuesChange([]);
    fixture.detectChanges();

    expect(inputEl.value).toBe(studentAlice.name);
    expect(onChangeSpy).not.toHaveBeenCalled();
  });

  it('clearSelection: should clear the input and emit the configured clearValue', () => {
    const onChangeSpy = vi.fn();
    const onTouchedSpy = vi.fn();
    component.clearValue = '';
    component.registerOnChange(onChangeSpy);
    component.registerOnTouched(onTouchedSpy);
    component.writeValue(studentAlice.id);

    component.clearSelection(new MouseEvent('click'));
    fixture.detectChanges();

    expect(inputEl.value).toBe('');
    expect(onChangeSpy).toHaveBeenCalledWith('');
    expect(onTouchedSpy).toHaveBeenCalled();
  });

  it('onComboboxFocusOut: should restore selected label when focus leaves the combobox', () => {
    component.writeValue(studentAlice.id);
    component.onInputValueChange('unmatched text');

    comboboxEl.dispatchEvent(new FocusEvent('focusout', { relatedTarget: null }));
    fixture.detectChanges();

    expect(inputEl.value).toBe(studentAlice.name);
  });

  it('onComboboxFocusOut: should not restore label when focus moves to a child element', () => {
    component.writeValue(studentAlice.id);
    component.onInputValueChange('partial text');

    comboboxEl.dispatchEvent(new FocusEvent('focusout', { relatedTarget: inputEl }));
    fixture.detectChanges();

    expect(inputEl.value).toBe('partial text');
  });

  it('should disable the input when disabled', () => {
    component.setDisabledState(true);
    fixture.detectChanges();

    expect(inputEl.disabled).toBeTruthy();
  });

  it('isSelected: should use compareWith for custom equality', () => {
    component.compareWith = (a, b) => a?.toLowerCase() === b?.toLowerCase();
    component.writeValue('STUDENT-1');

    expect(component.isSelected({ value: 'student-1', label: studentAlice.name })).toBe(true);
    expect(component.isSelected({ value: 'student-2', label: studentBob.name })).toBe(false);
  });
});
