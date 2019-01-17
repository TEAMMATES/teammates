import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { AjaxLoadingComponent } from './ajax-loading.component';

describe('AjaxLoadingComponent', () => {
  let component: AjaxLoadingComponent;
  let fixture: ComponentFixture<AjaxLoadingComponent>;

  beforeEach(async(() => {
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
