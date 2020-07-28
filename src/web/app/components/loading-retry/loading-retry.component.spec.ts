import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { LoadingRetryComponent } from './loading-retry.component';

describe('LoadingRetryComponent', () => {
  let component: LoadingRetryComponent;
  let fixture: ComponentFixture<LoadingRetryComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [LoadingRetryComponent],
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(LoadingRetryComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
