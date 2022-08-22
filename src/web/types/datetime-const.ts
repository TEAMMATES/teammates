export enum HoursConst {
    ZERO_HOURS = 0,
    TWELVE_HOURS = 12,
    ONE_DAY_HOURS = 24,
    THREE_DAYS_HOURS = 72,
    ONE_WEEK_HOURS = 168,
}

export enum SecondsConst {
    ONE_MINUTE_SECONDS = 60,
}

export enum MinutesConst {
    ONE_HOURS_MINUTES = 60,
}

export enum MsConst {
    ONE_SECOND_MILLISECONDS = 1000,
    TEN_SECOND_MILLISECONDS = 10000,
    ONE_MINUTE_MILLISECONDS = 60000,
    TEN_MINUTE_MILLISECONDS = 600000,
    FIFTEEN_MINUTE_MILLISECONDS = 900000,
    ONE_HOUR_MILLISECONDS = 3600000,
    ONE_WEEK_MILLISECONDS = 604800000,
}

export const getDefaultDateFormat = (): DateFormat => {
    return { year: 0, month: 0, day: 0 };
};

export const getLatestTimeFormat = (): TimeFormat => {
    return { hour: 23, minute: 59 };
};

export const getDefaultTimeFormat = (): TimeFormat => {
    return { hour: 0, minute: 0 };
};

/**
 * The date format.
 */
export interface DateFormat {
    year: number;
    month: number;
    day: number;
}

/**
 * The output format of the time picker.
 */
export interface TimeFormat {
    hour: number;
    minute: number;
}
