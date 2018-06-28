package edu.cnm.deepdive.apod.fragment;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.text.format.DateFormat;
import android.widget.DatePicker;
import android.widget.TimePicker;
import java.util.Calendar;

public class DateTimePickerFragment extends DialogFragment
    implements DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener {

  private Mode mode = Mode.DATE;
  private Calendar calendar = null;
  private boolean passthrough = false;
  private OnChangeListener onChangeListener = null;

  @NonNull
  @Override
  public Dialog onCreateDialog(Bundle savedInstanceState) {
    Calendar defaultValue = (calendar != null) ? calendar : Calendar.getInstance();
    Dialog dialog = null;
    if (mode == Mode.DATE) {
      dialog = new DatePickerDialog(getActivity(), this, defaultValue.get(Calendar.YEAR),
          defaultValue.get(Calendar.MONTH), defaultValue.get(Calendar.DAY_OF_MONTH));
    } else {
      dialog = new TimePickerDialog(getActivity(), this, defaultValue.get(Calendar.HOUR_OF_DAY),
          defaultValue.get(Calendar.MINUTE), DateFormat.is24HourFormat(getActivity()));
    }
    return dialog;
  }

  @Override
  public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
    Calendar updateValue = (passthrough && calendar != null) ? calendar : Calendar.getInstance();
    updateValue.set(Calendar.YEAR, year);
    updateValue.set(Calendar.MONTH, month);
    updateValue.set(Calendar.DAY_OF_MONTH, dayOfMonth);
    if (onChangeListener != null) {
      onChangeListener.onChange(updateValue);
    }
  }

  @Override
  public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
    Calendar updateValue = (passthrough && calendar != null) ? calendar : Calendar.getInstance();
    updateValue.set(Calendar.HOUR_OF_DAY, hourOfDay);
    updateValue.set(Calendar.MINUTE, minute);
    if (onChangeListener != null) {
      onChangeListener.onChange(updateValue);
    }
  }

  public void setMode(Mode mode) {
    this.mode = mode;
  }

  public void setCalendar(Calendar calendar) {
    this.calendar = calendar;
  }

  public void setPassthrough(boolean passthrough) {
    this.passthrough = passthrough;
  }

  public void setOnChangeListener(OnChangeListener onChangeListener) {
    this.onChangeListener = onChangeListener;
  }

  public enum Mode {
    DATE, TIME
  }

  public interface OnChangeListener {
    void onChange(Calendar calendar);
  }

}
