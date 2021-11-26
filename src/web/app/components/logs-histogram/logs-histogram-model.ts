import { SourceLocation } from '../../../types/api-output';

/**
 * The model for a histogram data.
 */
export interface LogsHistogramDataModel {
  sourceLocation: SourceLocation;
  numberOfTimes: number;
}
