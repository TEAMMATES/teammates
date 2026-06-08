import { Pipe, PipeTransform } from '@angular/core';

/**
 * Pipe to handle the transformation of an enum to an array of all types it has.
 *
 * <p>Assumes the enum is string in runtime.
 */
@Pipe({ name: 'enumToArray' })
export class EnumToArrayPipe implements PipeTransform {
  /**
   * Transforms enum to an array of all types it has.
   * Uses 'any' because Angular pipes don't support generic type parameters,
   * and this pipe needs to work with any enum type while preserving the enum's value types.
   * Each template using this pipe has proper type checking at the point of use.
   */
  transform(enumObj: Record<string, unknown>): unknown[] {
    return Object.keys(enumObj).map((s: string) => enumObj[s] as unknown);
  }
}
