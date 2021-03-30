import { async, ComponentFixture, TestBed } from '@angular/core/testing';
import { FormsModule } from '@angular/forms';
import { NgbDatepickerModule } from '@ng-bootstrap/ng-bootstrap';
import { DatepickerTodayComponent } from './datepicker-today.component';

describe('DatepickerTodayComponent', () => {
  let component: DatepickerTodayComponent;
  let fixture: ComponentFixture<DatepickerTodayComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [DatepickerTodayComponent],
      imports: [
        FormsModule,
        NgbDatepickerModule,
      ],
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(DatepickerTodayComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
