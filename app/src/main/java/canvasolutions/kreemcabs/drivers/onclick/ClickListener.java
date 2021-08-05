package canvasolutions.kreemcabs.drivers.onclick;

import android.view.View;

/**
 * Created by Woumtana on 24/02/2017.
 */

public interface ClickListener {
    void onClick(View view, int position);

    void onLongClick(View view, int position);
}
