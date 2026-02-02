import { ComponentFixture, TestBed } from '@angular/core/testing';
import { By } from '@angular/platform-browser';
import { NgbInputDatepicker } from '@ng-bootstrap/ng-bootstrap';
import { DateFormat } from 'src/web/types/datetime-const';
import { DatepickerComponent } from './datepicker.component';

describe('DatepickerComponent', () => {
  let component: DatepickerComponent;
  let fixture: ComponentFixture<DatepickerComponent>;

  beforeEach(() => {
    fixture = TestBed.createComponent(DatepickerComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should emit the date for changeDate', () => {
    const changeDateSpy = jest.spyOn(component.dateChangeCallback, 'emit');
    const date : DateFormat = { year: 2023, month: 10, day: 12 };
    component.changeDate(date);
    expect(changeDateSpy).toHaveBeenCalledWith(date);
  });

  it('the datepicker should navigate to today\'s date for selectTodayDate', () => {
    const datepicker = fixture.debugElement.query(By.directive(NgbInputDatepicker)).injector.get(NgbInputDatepicker);
    const todayDate = component.calendar.getToday();
    const selectTodayDateSpy = jest.spyOn(component.dateChangeCallback, 'emit');
    const datePickerNavigateSpy = jest.spyOn(datepicker, 'navigateTo');
    component.selectTodayDate(datepicker);
    expect(selectTodayDateSpy).toHaveBeenCalledWith(todayDate);
    expect(datePickerNavigateSpy).toHaveBeenCalledWith(todayDate);
  });
});
