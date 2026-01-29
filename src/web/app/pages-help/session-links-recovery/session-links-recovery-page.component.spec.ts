import { provideHttpClient } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { ComponentFixture, TestBed, waitForAsync } from '@angular/core/testing';
import { ReactiveFormsModule } from '@angular/forms';
import { NgxCaptchaModule } from 'ngx-captcha';
import { SessionLinksRecoveryPageComponent } from './session-links-recovery-page.component';
import { AjaxLoadingModule } from '../../components/ajax-loading/ajax-loading.module';

describe('SessionLinksRecoveryPageComponent', () => {
  let component: SessionLinksRecoveryPageComponent;
  let fixture: ComponentFixture<SessionLinksRecoveryPageComponent>;

  beforeEach(waitForAsync(() => {
    TestBed.configureTestingModule({
      declarations: [SessionLinksRecoveryPageComponent],
      imports: [
        ReactiveFormsModule,
        NgxCaptchaModule,
        AjaxLoadingModule,
      ],
      providers: [
        provideHttpClient(),
        provideHttpClientTesting(),
      ],
    })
        .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(SessionLinksRecoveryPageComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
