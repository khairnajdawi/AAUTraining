package jo.edu.aau.aautraining.shared;

import android.app.TimePickerDialog;
import android.content.Context;
import android.widget.EditText;
import android.widget.TimePicker;

import java.util.Locale;

public class MyTimePicker {
    private Context context;

    public static MyTimePicker getInstance(Context context){
        return new MyTimePicker(context);
    }

    public MyTimePicker(Context context) {
        this.context = context;
    }
    public void pickTimeFor(EditText editText){
        TimePickerDialog tpd = new TimePickerDialog(
                context,
                new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int i, int i1) {
                        editText.setText(String.format(Locale.US,"%02d:%02d",i,i1));
                    }
                },
                12,
                0,
                true
        );
        tpd.show();
    }
}
