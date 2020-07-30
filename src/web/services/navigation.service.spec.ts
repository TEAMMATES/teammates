import { TestBed } from '@angular/core/testing';

import { NavigationService } from './navigation.service';

describe('NavigationService', () => {
  it('should be created', () => {
    const service: NavigationService = TestBed.inject(NavigationService);
    expect(service).toBeTruthy();
  });

  it('should return an encoded param string upon calling encodeParams', () => {
    const service: NavigationService = TestBed.inject(NavigationService);
    expect(service.encodeParams({ courseId: '#123?123' })).toEqual('?courseId=%23123%3F123');
  });
});
