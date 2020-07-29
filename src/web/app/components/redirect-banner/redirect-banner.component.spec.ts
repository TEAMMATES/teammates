import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { RedirectBannerComponent } from './redirect-banner.component';

describe('RedirectBannerComponent', () => {
  let component: RedirectBannerComponent;
  let fixture: ComponentFixture<RedirectBannerComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [RedirectBannerComponent],
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(RedirectBannerComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
