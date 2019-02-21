import { HttpClientTestingModule } from '@angular/common/http/testing';
import { async, ComponentFixture, TestBed } from '@angular/core/testing';
import { ReactiveFormsModule } from '@angular/forms';
import { NgxCaptchaModule } from 'ngx-captcha';
import { LinkRecoveryPageComponent } from './link-recovery-page.component';

describe('LinkRecoveryPageComponent', () => {
  let component: LinkRecoveryPageComponent;
  let fixture: ComponentFixture<LinkRecoveryPageComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [LinkRecoveryPageComponent],
      imports: [
        ReactiveFormsModule,
        HttpClientTestingModule,
        NgxCaptchaModule,
      ],
    })
        .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(LinkRecoveryPageComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
