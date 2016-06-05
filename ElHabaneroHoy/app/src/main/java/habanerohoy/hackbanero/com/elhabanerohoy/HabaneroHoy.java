package habanerohoy.hackbanero.com.elhabanerohoy;

import android.app.Application;
import android.util.Log;

import com.parse.Parse;

/**
 * Created by jaime on 04/06/16.
 */
public class HabaneroHoy extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        Parse.initialize(new Parse.Configuration.Builder(this)
                .applicationId("habanero123$")
                .clientKey("habaneroclient123$")
                .server("http://elhabanerohoy.herokuapp.com/parse/")
                .enableLocalDataStore()

        .build()
        );

    }
}
