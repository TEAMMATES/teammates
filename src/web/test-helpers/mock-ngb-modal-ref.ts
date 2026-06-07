import { NgbModalRef } from '@ng-bootstrap/ng-bootstrap/modal';
import { NEVER, Observable } from 'rxjs';

/**
 * Creates a properly typed mock of NgbModalRef for testing.
 * T is the type of the component instance (defaults to an empty object).
 *
 * Implements the properties needed in tests: result, dismissed, and componentInstance.
 * Safe to use with vi.spyOn().mockReturnValue() for testing Angular components that use modals.
 *
 * Usage:
 *   // Default empty component instance
 *   createMockNgbModalRef()
 *
 *   // With specific component instance type
 *   createMockNgbModalRef<MyComponentType>({ prop: 'value' })
 *
 *   // With custom result promise
 *   createMockNgbModalRef({}, Promise.resolve(someValue))
 */
export const createMockNgbModalRef = <T extends object = Record<string, never>>(
  componentInstance: T = {} as T,
  result: Promise<unknown> = Promise.resolve(),
): NgbModalRef => {
  return {
    result,
    dismissed: NEVER as Observable<unknown>,
    get componentInstance(): T {
      return componentInstance;
    },
  } as NgbModalRef;
};
