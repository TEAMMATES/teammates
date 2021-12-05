import { ComponentFixture, TestBed, waitForAsync } from '@angular/core/testing';

import { AjaxLoadingComponent } from './ajax-loading.component';

describe('AjaxLoadingComponent', () => {
  let component: AjaxLoadingComponent;
  let fixture: ComponentFixture<AjaxLoadingComponent>;

  beforeEach(waitForAsync(() => {
    TestBed.configureTestingModule({
      declarations: [AjaxLoadingComponent],
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(AjaxLoadingComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
