import { ComponentFixture, TestBed } from '@angular/core/testing';
import { By } from '@angular/platform-browser';
import { DatetimepickerComponent } from './datetimepicker.component';
import { DatepickerComponent } from '../datepicker/datepicker.component';
import { TimepickerComponent } from '../timepicker/timepicker.component';

describe('DatetimepickerComponent', () => {
  let component: DatetimepickerComponent;
  let fixture: ComponentFixture<DatetimepickerComponent>;

  beforeEach(() => {
    fixture = TestBed.createComponent(DatetimepickerComponent);
    component = fixture.componentInstance;
    component.dateTime = new Date(2026, 4, 26, 14, 30);
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('onDateChange: should emit updated Date when date changes', () => {
    const dateTimeChangeSpy = vi.spyOn(component.dateTimeChange, 'emit');

    component.onDateChange({ year: 2026, month: 6, day: 1 });

    expect(dateTimeChangeSpy).toHaveBeenCalledWith(new Date(2026, 5, 1, 14, 30));
    expect(component.dateTime).toEqual(new Date(2026, 5, 1, 14, 30));
  });

  it('onTimeChange: should emit updated Date when time changes', () => {
    const dateTimeChangeSpy = vi.spyOn(component.dateTimeChange, 'emit');

    component.onTimeChange({ hour: 23, minute: 59 });

    expect(dateTimeChangeSpy).toHaveBeenCalledWith(new Date(2026, 4, 26, 23, 59));
    expect(component.dateTime).toEqual(new Date(2026, 4, 26, 23, 59));
  });

  it('should reuse date and time object references while inputs are unchanged', () => {
    const initialDate = component.date;
    const initialTime = component.time;

    fixture.detectChanges();

    expect(component.date).toBe(initialDate);
    expect(component.time).toBe(initialTime);
  });

  it('should reuse date and time object references when Date input has the same timestamp', () => {
    const initialDate = component.date;
    const initialTime = component.time;

    component.dateTime = new Date(2026, 4, 26, 14, 30);

    expect(component.date).toBe(initialDate);
    expect(component.time).toBe(initialTime);
  });

  it('should reuse bounds object references when Date bounds have the same timestamps', () => {
    component.minDateTime = new Date(2026, 0, 2, 3, 45);
    component.maxDateTime = new Date(2026, 11, 31, 23, 59);

    const initialMinDate = component.minDate;
    const initialMinTime = component.minTime;
    const initialMaxDate = component.maxDate;
    const initialMaxTime = component.maxTime;

    component.minDateTime = new Date(2026, 0, 2, 3, 45);
    component.maxDateTime = new Date(2026, 11, 31, 23, 59);

    expect(component.minDate).toBe(initialMinDate);
    expect(component.minTime).toBe(initialMinTime);
    expect(component.maxDate).toBe(initialMaxDate);
    expect(component.maxTime).toBe(initialMaxTime);
  });

  it('should not emit when date change keeps the same Date value', () => {
    const dateTimeChangeSpy = vi.spyOn(component.dateTimeChange, 'emit');

    component.onDateChange({ year: 2026, month: 5, day: 26 });

    expect(dateTimeChangeSpy).not.toHaveBeenCalled();
    expect(component.dateTime).toEqual(new Date(2026, 4, 26, 14, 30));
  });

  it('should derive date and time bounds from Date inputs', () => {
    component.minDateTime = new Date(2026, 0, 2, 3, 45);
    component.maxDateTime = new Date(2026, 11, 31, 23, 59);

    expect(component.minDate).toEqual({ year: 2026, month: 1, day: 2 });
    expect(component.minTime).toEqual({ hour: 3, minute: 45 });
    expect(component.maxDate).toEqual({ year: 2026, month: 12, day: 31 });
    expect(component.maxTime).toEqual({ hour: 23, minute: 59 });
  });

  it('should pass disabled state to inner pickers', () => {
    component.isDisabled = true;
    fixture.detectChanges();

    const datepicker = fixture.debugElement.query(By.directive(DatepickerComponent)).componentInstance;
    const timepicker = fixture.debugElement.query(By.directive(TimepickerComponent)).componentInstance;

    expect(datepicker.isDisabled).toBe(true);
    expect(timepicker.isDisabled).toBe(true);
  });
});
