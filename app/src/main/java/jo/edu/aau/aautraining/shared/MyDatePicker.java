package jo.edu.aau.aautraining.shared;

import android.app.DatePickerDialog;
import android.content.Context;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;


public class MyDatePicker {

    private Context context;
    private int titleResId;
    private String dateFormat = "yyyy/MM/dd";

    public static MyDatePicker getInstance(Context context, int titleResId) {
        return new MyDatePicker(context, titleResId);
    }

    public MyDatePicker(Context context) {
        this.context = context;
    }

    public int getTitleResId() {
        return titleResId;
    }

    public MyDatePicker setTitleResId(int titleResId) {
        this.titleResId = titleResId;
        return this;
    }

    private MyDatePicker(Context context, int titleResId) {
        this.context = context;
        this.titleResId = titleResId;
    }

    public MyDatePicker setDateFormat(String dateFormat) {
        this.dateFormat = dateFormat;
        return this;
    }

    public void selectDateFor(EditText editText) {
        selectDate(Calendar.getInstance(), editText);
    }

    public void selectDate(final Calendar calendar, final EditText editText) {
        DatePickerDialog.OnDateSetListener odpl = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int monthOfYear, int dayOfMonth) {
                calendar.set(Calendar.YEAR, year);
                calendar.set(Calendar.MONTH, monthOfYear);
                calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                SimpleDateFormat sdf = new SimpleDateFormat(dateFormat, Locale.US);
                String dateStr = sdf.format(calendar.getTime());
                editText.setText(dateStr);
            }
        };
        DatePickerDialog dpd = new DatePickerDialog(context, odpl,
                calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH));
        dpd.setTitle(titleResId);
        dpd.show();
    }

    public void selectDate(String dateStr, final EditText editText) {
        SimpleDateFormat sdf = new SimpleDateFormat(dateFormat, Locale.US);
        Calendar calendar = Calendar.getInstance();
        try {
            calendar.setTime(sdf.parse(dateStr));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        DatePickerDialog.OnDateSetListener odpl = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int monthOfYear, int dayOfMonth) {
                calendar.set(Calendar.YEAR, year);
                calendar.set(Calendar.MONTH, monthOfYear);
                calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                String dateStr = sdf.format(calendar.getTime());
                editText.setText(dateStr);
            }
        };
        DatePickerDialog dpd = new DatePickerDialog(context, odpl,
                calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH));
        dpd.setTitle(titleResId);
        dpd.show();
    }

    public void selectDate(String dateStr, final TextView textView) {
        SimpleDateFormat sdf = new SimpleDateFormat(dateFormat, Locale.US);
        Calendar calendar = Calendar.getInstance();
        try {
            calendar.setTime(sdf.parse(dateStr));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        DatePickerDialog.OnDateSetListener odpl = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int monthOfYear, int dayOfMonth) {
                calendar.set(Calendar.YEAR, year);
                calendar.set(Calendar.MONTH, monthOfYear);
                calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                String dateStr = sdf.format(calendar.getTime());
                textView.setText(dateStr);
            }
        };
        DatePickerDialog dpd = new DatePickerDialog(context, odpl,
                calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH));
        dpd.setTitle(titleResId);
        dpd.show();
    }
}
