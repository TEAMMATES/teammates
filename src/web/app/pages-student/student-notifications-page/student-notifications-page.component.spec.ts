import { provideHttpClient } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideRouter } from '@angular/router';
import { StudentNotificationsPageComponent } from './student-notifications-page.component';

describe('StudentNotificationsPageComponent', () => {
  let component: StudentNotificationsPageComponent;
  let fixture: ComponentFixture<StudentNotificationsPageComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      providers: [provideRouter([]), provideHttpClient(), provideHttpClientTesting()],
    }).compileComponents();

    fixture = TestBed.createComponent(StudentNotificationsPageComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should snap with default fields', () => {
    component.timezone = 'UTC';
    fixture.detectChanges();
    expect(fixture).toMatchSnapshot();
  });
});
