import { ComponentFixture, TestBed } from '@angular/core/testing';

import { LoadingRetryComponent } from './loading-retry.component';

describe('LoadingRetryComponent', () => {
  let component: LoadingRetryComponent;
  let fixture: ComponentFixture<LoadingRetryComponent>;

  beforeEach(() => {
    fixture = TestBed.createComponent(LoadingRetryComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
