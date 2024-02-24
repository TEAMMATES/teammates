import { NgbModalRef } from '@ng-bootstrap/ng-bootstrap';

/**
 * Mock NgbModalRef by implementing only the most important fields.
 */
export const createMockNgbModalRef =
    (componentInstance: any = {}, result: Promise<any> = Promise.resolve()): NgbModalRef => {
      return {
        result,
        get componentInstance(): any {
          return componentInstance;
        },
      } as NgbModalRef;
    };
