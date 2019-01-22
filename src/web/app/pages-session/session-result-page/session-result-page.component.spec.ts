import { async, ComponentFixture, TestBed } from '@angular/core/testing';
import { SessionResultPageComponent } from './session-result-page.component';

describe('SessionResultPageComponent', () => {
  let component: SessionResultPageComponent;
  let fixture: ComponentFixture<SessionResultPageComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [SessionResultPageComponent],
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(SessionResultPageComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
