import { NgbModalRef } from '@ng-bootstrap/ng-bootstrap/modal';
import { NEVER } from 'rxjs';

/**
 * Mock NgbModalRef by implementing only the most important fields.
 */
export const createMockNgbModalRef = (
  componentInstance: any = {},
  result: Promise<any> = Promise.resolve(),
): NgbModalRef => {
  return {
    result,
    dismissed: NEVER,
    get componentInstance(): any {
      return componentInstance;
    },
  } as unknown as NgbModalRef;
};
