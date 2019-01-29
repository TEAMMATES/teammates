import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { AjaxPreloadComponent } from './ajax-preload.component';

describe('AjaxPreloadComponent', () => {
  let component: AjaxPreloadComponent;
  let fixture: ComponentFixture<AjaxPreloadComponent>;

  beforeEach(async(() => {
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
