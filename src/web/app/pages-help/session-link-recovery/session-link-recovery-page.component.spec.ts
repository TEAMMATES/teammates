import { HttpClientTestingModule } from '@angular/common/http/testing';
import { async, ComponentFixture, TestBed } from '@angular/core/testing';
import { ReactiveFormsModule } from '@angular/forms';
import { NgxCaptchaModule } from 'ngx-captcha';
import { SessionLinkRecoveryPageComponent } from './session-link-recovery-page.component';

describe('SessionLinkRecoveryPageComponent', () => {
  let component: SessionLinkRecoveryPageComponent;
  let fixture: ComponentFixture<SessionLinkRecoveryPageComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [SessionLinkRecoveryPageComponent],
      imports: [
        ReactiveFormsModule,
        HttpClientTestingModule,
        NgxCaptchaModule,
      ],
    })
        .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(SessionLinkRecoveryPageComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
