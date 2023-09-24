import { HttpClientTestingModule } from '@angular/common/http/testing';
import { ComponentFixture, TestBed, waitForAsync } from '@angular/core/testing';
import { RouterTestingModule } from '@angular/router/testing';
import { TimeFormat } from 'src/web/types/datetime-const';
import { TeammatesRouterModule } from '../teammates-router/teammates-router.module';
import { SessionEditFormComponent } from './session-edit-form.component';
import { SessionEditFormModule } from './session-edit-form.module';

describe('SessionEditFormComponent', () => {
  let component: SessionEditFormComponent;
  let fixture: ComponentFixture<SessionEditFormComponent>;

  beforeEach(waitForAsync(() => {
    TestBed.configureTestingModule({
      imports: [
        SessionEditFormModule,
        HttpClientTestingModule,
        RouterTestingModule,
        TeammatesRouterModule,
      ],
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(SessionEditFormComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should configure the time to be 23:59 if the hour is 23 and minute is greater than 0', () => {
    const time : TimeFormat = { hour: 23, minute: 5 };
    component.configureSubmissionOpeningTime(time);
    expect(time.hour).toEqual(23);
    expect(time.minute).toEqual(59);
  });

  it('should configure the time correctly if the hour is less than 23 and minute is greater than 0', () => {
    const time : TimeFormat = { hour: 22, minute: 5 };
    component.configureSubmissionOpeningTime(time);
    expect(time.hour).toEqual(23);
    expect(time.minute).toEqual(0);
  });

  it('should configure the time correctly if the minute is 0', () => {
    const time : TimeFormat = { hour: 21, minute: 0 };
    component.configureSubmissionOpeningTime(time);
    expect(time.hour).toEqual(21);
    expect(time.minute).toEqual(0);
  });

});
