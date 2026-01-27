import { TestBed } from '@angular/core/testing';

import { RouterModule } from '@angular/router';
import { NavigationService } from './navigation.service';

describe('NavigationService', () => {
  let service: NavigationService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [RouterModule.forRoot([])],
    });
    service = TestBed.inject(NavigationService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  it('should return an encoded param string upon calling encodeParams', () => {
    expect(service.encodeParams({ courseId: '#123?123' })).toEqual('?courseId=%23123%3F123');
  });
});
