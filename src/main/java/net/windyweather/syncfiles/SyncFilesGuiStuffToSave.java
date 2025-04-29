package net.windyweather.syncfiles;

import javafx.beans.property.SimpleDoubleProperty;

/*
    Class to hold GUI stuff to save / restore in an XML file
 */
public class SyncFilesGuiStuffToSave {

    public SimpleDoubleProperty dSplitDivider;
    public SimpleDoubleProperty dPairTableNameWidth;
    public SimpleDoubleProperty dPairTableStatusWidth;
    public SimpleDoubleProperty dTreeTableWidth0;
    public SimpleDoubleProperty dTreeTableWidth1;
    public SimpleDoubleProperty dTreeTableWidth2;
    public SimpleDoubleProperty dTreeTableWidth3;


    public SyncFilesGuiStuffToSave() {

    }

    public double getDSplitDivider() { return dSplitDivider.get(); }
    public double getDPairTableNameWidth() { return dPairTableNameWidth.get(); }
    public double getDPairTableStatusWidth() { return dPairTableStatusWidth.get(); }
    public double getDTreeTableWidth0() { return dTreeTableWidth0.get(); }
    public double getDTreeTableWidth1() { return dTreeTableWidth1.get(); }
    public double getDTreeTableWidth2() { return dTreeTableWidth2.get(); }
    public double getDTreeTableWidth3() { return dTreeTableWidth3.get(); }

    public void setDSplitDivider( double div ) { this.dSplitDivider = new SimpleDoubleProperty( div ); }
    public void setDPairTableNameWidth( double div ) { this.dPairTableNameWidth = new SimpleDoubleProperty( div ); }
    public void setDPairTableStatusWidth( double div ) { this.dPairTableStatusWidth = new SimpleDoubleProperty( div ); }
    public void setDTreeTableWidth0( double div ) { this.dTreeTableWidth0 = new SimpleDoubleProperty( div ); }
    public void setDTreeTableWidth1( double div ) { this.dTreeTableWidth1 = new SimpleDoubleProperty( div ); }
    public void setDTreeTableWidth2( double div ) { this.dTreeTableWidth2 = new SimpleDoubleProperty( div ); }
    public void setDTreeTableWidth3( double div ) { this.dTreeTableWidth3 = new SimpleDoubleProperty( div ); }

}
