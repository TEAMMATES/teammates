import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { FormsModule } from '@angular/forms';
import { MatSnackBarModule } from '@angular/material';
import { RankOptionsFieldComponent } from './rank-options-field.component';

describe('RankOptionsFieldComponent', () => {
  let component: RankOptionsFieldComponent;
  let fixture: ComponentFixture<RankOptionsFieldComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [RankOptionsFieldComponent],
      imports: [
        FormsModule,
        MatSnackBarModule,
      ],
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(RankOptionsFieldComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
