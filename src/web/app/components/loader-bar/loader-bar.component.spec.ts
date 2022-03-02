import { ComponentFixture, TestBed, waitForAsync } from '@angular/core/testing';
import { NgbProgressbarModule } from '@ng-bootstrap/ng-bootstrap';

import { LoaderBarComponent } from './loader-bar.component';

describe('LoaderBarComponent', () => {
  let component: LoaderBarComponent;
  let fixture: ComponentFixture<LoaderBarComponent>;

  beforeEach(waitForAsync(() => {
    TestBed.configureTestingModule({
      declarations: [LoaderBarComponent],
      imports: [NgbProgressbarModule],
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(LoaderBarComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
