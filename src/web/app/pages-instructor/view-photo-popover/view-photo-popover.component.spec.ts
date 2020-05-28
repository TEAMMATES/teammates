import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { ViewPhotoPopoverComponent } from './view-photo-popover.component';
import { ViewPhotoPopoverModule } from './view-photo-popover.module';

describe('ViewPhotoPopoverComponent', () => {
  let component: ViewPhotoPopoverComponent;
  let fixture: ComponentFixture<ViewPhotoPopoverComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      imports: [ViewPhotoPopoverModule],
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
