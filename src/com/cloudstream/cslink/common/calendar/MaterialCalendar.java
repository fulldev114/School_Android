package com.cloudstream.cslink.common.calendar;

import android.util.Log;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import java.text.DateFormatSymbols;
import java.util.Calendar;

/**
 * Created by Maximilian on 9/2/14.
 */
public class MaterialCalendar {
    // Variables
    protected static int mMonth = -1;
    protected static int mYear = -1;
    protected static int mCurrentDay = -1;
    protected static int mCurrentMonth = -1;
    protected static int mCurrentYear = -1;
    protected static int mFirstDay = -1;
    protected static int mNumDaysInMonth = -1;


    protected static void getInitialCalendarInfo() {
        Calendar cal = Calendar.getInstance();

        if (cal != null) {
            Log.d("MONTH_NUMBER", String.valueOf(cal.getActualMaximum(Calendar.DAY_OF_MONTH)));
            mNumDaysInMonth = cal.getActualMaximum(Calendar.DAY_OF_MONTH);

            mMonth = cal.get(Calendar.MONTH);
            mYear = cal.get(Calendar.YEAR);

            mCurrentDay = cal.get(Calendar.DAY_OF_MONTH);
            mCurrentMonth = mMonth;
            mCurrentYear = mYear;

            getFirstDay(mMonth, mYear);

            Log.d("CURRENT_DAY", String.valueOf(mCurrentDay));
            Log.d("CURRENT_MONTH_INFO", String.valueOf(getMonthName(mMonth) + " " + mYear + " has " + mNumDaysInMonth
                    + " days " +
                    "and starts on " + mFirstDay));
        }
    }

    private static void refreshCalendar(TextView monthTextView, GridView calendarGridView,
                                        MaterialCalendarAdapter materialCalendarAdapter, int month, int year) {

        checkCurrentDay(month, year);
        getNumDayInMonth(month, year);
        getFirstDay(month, year);

        if (monthTextView != null) {
            Log.d("REFRESH_MONTH", String.valueOf(month));
            monthTextView.setText(getMonthName(month) + " " + year);
        }

        // Clear Saved Events ListView count when changing calendars
//        if (MaterialCalendarFragment.mSavedEventsAdapter != null) {
//            MaterialCalendarFragment.mNumEventsOnDay = -1;
//            MaterialCalendarFragment.mSavedEventsAdapter.notifyDataSetChanged();
//            Log.d("EVENTS_ADAPTER", "refresh");
//        }

        MaterialCalendarFragment.getSavedEventsForCurrentMonth();

        if (materialCalendarAdapter != null) {
            if (calendarGridView != null) {
                calendarGridView.setItemChecked(calendarGridView.getCheckedItemPosition(), false);
            }

            materialCalendarAdapter.notifyDataSetChanged();
        }
    }

    private static String getMonthName(int month) {
        return new DateFormatSymbols().getMonths()[month];
    }

    private static void checkCurrentDay(int month, int year) {
        if (month == mCurrentMonth && year == mCurrentYear) {
            Calendar cal = java.util.Calendar.getInstance();
            mCurrentDay = cal.get(Calendar.DAY_OF_MONTH);
        } else {
            mCurrentDay = -1;
        }
    }

    private static void getNumDayInMonth(int month, int year) {
        Calendar cal = Calendar.getInstance();
        if (cal != null) {
            cal.set(Calendar.MONTH, month);
            cal.set(Calendar.YEAR, year);
            mNumDaysInMonth = cal.getActualMaximum(Calendar.DAY_OF_MONTH);

            Log.d("MONTH_NUMBER", String.valueOf(cal.getActualMaximum(Calendar.DAY_OF_MONTH)));
        }
    }

    private static void getFirstDay(int month, int year) {
        Calendar cal = Calendar.getInstance();
        if (cal != null) {
            cal.set(Calendar.MONTH, month);
            cal.set(Calendar.YEAR, year);
            cal.set(Calendar.DAY_OF_MONTH, 1);

            switch (cal.get(Calendar.DAY_OF_WEEK)) {
                case Calendar.SUNDAY:
                    Log.d("FIRST_DAY", "Sunday");
                    mFirstDay = 0;
                    break;

                case Calendar.MONDAY:
                    Log.d("FIRST_DAY", "Monday");
                    mFirstDay = 1;
                    break;

                case Calendar.TUESDAY:
                    Log.d("FIRST_DAY", "Tuesday");
                    mFirstDay = 2;
                    break;

                case Calendar.WEDNESDAY:
                    Log.d("FIRST_DAY", "Wednesday");
                    mFirstDay = 3;
                    break;

                case Calendar.THURSDAY:
                    Log.d("FIRST_DAY", "Thursday");
                    mFirstDay = 4;
                    break;

                case Calendar.FRIDAY:
                    Log.d("FIRST_DAY", "Friday");
                    mFirstDay = 5;
                    break;

                case Calendar.SATURDAY:
                    Log.d("FIRST_DAY", "Saturday");
                    mFirstDay = 6;
                    break;

                default:
                    break;
            }
        }
    }

    // Call in View.OnClickListener for Previous ImageView
    protected static void previousOnClick(ImageView previousImageView, TextView monthTextView,
                                          GridView calendarGridView, MaterialCalendarAdapter materialCalendarAdapter) {
        if (previousImageView != null && mMonth != -1 && mYear != -1) {
            previousMonth(monthTextView, calendarGridView, materialCalendarAdapter);
        }
    }

    // Call in View.OnClickListener for Next ImageView
    protected static void nextOnClick(ImageView nextImageView, TextView monthTextView,
                                      GridView calendarGridView,
                                      MaterialCalendarAdapter materialCalendarAdapter) {
        if (nextImageView != null && mMonth != -1 && mYear != -1) {
            nextMonth(monthTextView, calendarGridView, materialCalendarAdapter);
        }
    }

    private static void previousMonth(TextView monthTextView, GridView calendarGridView,
                                      MaterialCalendarAdapter materialCalendarAdapter) {
        if (mMonth == 0) {
            mMonth = 11;
            mYear = mYear - 1;
        } else {
            mMonth = mMonth - 1;
        }

        refreshCalendar(monthTextView, calendarGridView, materialCalendarAdapter, mMonth, mYear);
    }

    private static void nextMonth(TextView monthTextView, GridView calendarGridView,
                                  MaterialCalendarAdapter materialCalendarAdapter) {
        if (mMonth == 11) {
            mMonth = 0;
            mYear = mYear + 1;
        } else {
            mMonth = mMonth + 1;
        }

        refreshCalendar(monthTextView, calendarGridView, materialCalendarAdapter, mMonth, mYear);
    }

    // Call in GridView.OnItemClickListener for custom Calendar GirdView
    protected static void selectCalendarDay(MaterialCalendarAdapter materialCalendarAdapter, int position) {
        Log.d("SELECTED_POSITION", String.valueOf(position));
        int weekPositions = 6;
        int noneSelectablePositions = weekPositions + mFirstDay;

        if (position > noneSelectablePositions) {
            getSelectedDate(position, mMonth, mYear);

            if (materialCalendarAdapter != null) {
                materialCalendarAdapter.notifyDataSetChanged();
            }
        }
    }

    private static void getSelectedDate(int selectedPosition, int month, int year) {
        int weekPositions = 6;
        int dateNumber = selectedPosition - weekPositions - mFirstDay;
        Log.d("DATE_NUMBER", String.valueOf(dateNumber));
        Log.d("SELECTED_DATE", String.valueOf(month + "/" + dateNumber + "/" + year));
    }
}

