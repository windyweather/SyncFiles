module net.windyweather.syncfiles {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.desktop;
    requires java.prefs;


    opens net.windyweather.syncfiles to javafx.fxml;
    exports net.windyweather.syncfiles;
}