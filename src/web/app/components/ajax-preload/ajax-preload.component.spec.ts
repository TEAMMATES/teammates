import { ComponentFixture, TestBed, waitForAsync } from '@angular/core/testing';

import { AjaxPreloadComponent } from './ajax-preload.component';

describe('AjaxPreloadComponent', () => {
  let component: AjaxPreloadComponent;
  let fixture: ComponentFixture<AjaxPreloadComponent>;

  beforeEach(waitForAsync(() => {
    TestBed.configureTestingModule({
      declarations: [
        AjaxPreloadComponent,
      ],
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(AjaxPreloadComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
