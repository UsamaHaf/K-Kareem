package canvasolutions.kreemcabs.drivers.datepicker;

import org.joda.time.DateTime;

public interface DatePickerListener {
    void onDateSelected(DateTime dateSelected);
}