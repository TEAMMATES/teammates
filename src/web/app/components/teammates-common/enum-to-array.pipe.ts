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
   */
  transform(enumObj: Record<string, string>): string[] {
    return Object.keys(enumObj).map((s: string) => enumObj[s]);
  }
}
