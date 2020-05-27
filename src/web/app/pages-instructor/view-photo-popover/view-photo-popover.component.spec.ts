import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { ViewPhotoPopoverComponent } from './view-photo-popover.component';

describe('ViewPhotoPopoverComponent', () => {
  let component: ViewPhotoPopoverComponent;
  let fixture: ComponentFixture<ViewPhotoPopoverComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ ViewPhotoPopoverComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(ViewPhotoPopoverComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
