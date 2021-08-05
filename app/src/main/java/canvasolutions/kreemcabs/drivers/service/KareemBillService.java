package canvasolutions.kreemcabs.drivers.service;

import android.app.Service;
import android.content.Intent;
import android.location.LocationManager;
import android.os.IBinder;

import androidx.annotation.Nullable;

public class KareemBillService extends Service {
    float travellingCost = 0;
    float startCost = 78;
    int waitingTime = 0;
    LocationManager locationManager;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        locationManager = (LocationManager) getApplicationContext().getSystemService(LOCATION_SERVICE);



        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
