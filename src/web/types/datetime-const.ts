export enum Hours {
    ZERO = 0,
    TWELVE = 12,
    IN_ONE_DAY = 24,
    IN_THREE_DAYS = 72,
    IN_ONE_WEEK = 168,
}

export enum Seconds {
    IN_ONE_MINUTE = 60,
}

export enum Minutes {
    IN_ONE_HOUR = 60,
}

export enum Milliseconds {
    IN_ONE_SECOND = 1000,
    IN_TEN_SECONDS = 10000,
    IN_ONE_MINUTE = 60000,
    IN_TEN_MINUTES = 600000,
    IN_FIFTEEN_MINUTES = 900000,
    IN_ONE_HOUR = 3600000,
    IN_ONE_DAY = 86400000,
    IN_ONE_WEEK = 604800000,
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

export interface DateFormat {
    year: number;
    month: number;
    day: number;
}

export interface TimeFormat {
    hour: number;
    minute: number;
}
