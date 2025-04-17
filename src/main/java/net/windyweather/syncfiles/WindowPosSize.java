package net.windyweather.syncfiles;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;

/*
      Class for the Window Data
   */
public class WindowPosSize {


    public SimpleDoubleProperty dPosX;
    public SimpleDoubleProperty dPosY;
    public SimpleDoubleProperty dSizeX;
    public SimpleDoubleProperty dSizeY;


    public WindowPosSize() {
    }

    void setStuffToZero() {
        if (true) {
            dPosX = new SimpleDoubleProperty(0);
            dPosY = new SimpleDoubleProperty(0);
            dSizeX = new SimpleDoubleProperty(0);
            dSizeY = new SimpleDoubleProperty(0);
        }
    }
    public DoubleProperty dPosXProperty() { return dPosX; }
    public DoubleProperty dPosYProperty() { return dPosY; }
    public DoubleProperty dSizeXProperty() { return dSizeX; }
    public DoubleProperty dSizeYProperty() { return dSizeY; }

    public double getdPosX( ) {
        return dPosX.get();
    }

    public double getdPosY( ) {
        return dPosY.get();
    }

    public double getdSizeX( ) {
        return dSizeX.get();
    }

    public double getdSizeY( ) {
        return dSizeY.get();
    }

    public void setdPosX( SimpleDoubleProperty x ) {
        this.dPosX = x;
    }

    public void setdPosY( SimpleDoubleProperty y ) {
        this.dPosY = y;
    }

    public void setdSizeX( SimpleDoubleProperty width ) {
        this.dSizeX = width;
    }

    public void setdSizeY( SimpleDoubleProperty height ) {
        this.dSizeY = height;
    }

}
