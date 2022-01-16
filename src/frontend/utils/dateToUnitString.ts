type GetTimeUnit = 'getUTCHours' | 'getUTCMinutes' | 'getUTCSeconds';

/**
 * Format a date object into one of its basic units (e.g., hours).
 *
 * @param date
 * @param getTimeUnit
 * @param padStart Minimum number of '0's to pad to the start of the string
 */
export const dateToUnitString = (date: Date, getTimeUnit: GetTimeUnit, padStart = 2): string => {
  return date[getTimeUnit]().toString().padStart(padStart, '0');
};

export const dateToHoursString = (date: Date, padStart = 0) =>
  dateToUnitString(date, 'getUTCHours', padStart);

export const dateToMinutesString = (date: Date, padStart = 2) =>
  dateToUnitString(date, 'getUTCMinutes', padStart);

export const dateToSecondsString = (date: Date, padStart = 2) =>
  dateToUnitString(date, 'getUTCSeconds', padStart);
