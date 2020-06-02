import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { RouterTestingModule } from '@angular/router/testing';
import { NgbModule } from '@ng-bootstrap/ng-bootstrap';
import { FormatPhotoUrlPipe } from '../format-photo-url.pipe';
import { ViewPhotoPopoverComponent } from '../view-photo-popover/view-photo-popover.component';
import { StudentNameComponent } from './student-name.component';

describe('StudentNameComponent', () => {
  let component: StudentNameComponent;
  let fixture: ComponentFixture<StudentNameComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [StudentNameComponent, FormatPhotoUrlPipe, ViewPhotoPopoverComponent],
      imports: [NgbModule, RouterTestingModule],
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(StudentNameComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
