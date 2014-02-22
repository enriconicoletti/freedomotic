/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.freedomotic.jfrontend;

import com.freedomotic.environment.EnvironmentLogic;
import com.freedomotic.environment.Room;
import com.freedomotic.environment.ZoneLogic;
import javax.swing.JPanel;

/**
 *
 * @author enrico
 */
public abstract class Drawer extends JPanel {

    /**
     *
     * @param callout1
     */
    public abstract void createCallout(Callout callout1);

    /**
     *
     * @param b
     */
    public abstract void setNeedRepaint(boolean b);

    void setObjectEditMode(boolean b) {
        //throw new UnsupportedOperationException("Not yet implemented");
    }

    void setRoomEditMode(boolean b) {
        //throw new UnsupportedOperationException("Not yet implemented");
    }

    boolean getRoomEditMode() {
        //throw new UnsupportedOperationException("Not yet implemented");
        return false;
    }

    ZoneLogic getSelectedZone() {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    void createHandles(Room room) {
        //throw new UnsupportedOperationException("Not yet implemented");
    }

    boolean getObjectEditMode() {
        //throw new UnsupportedOperationException("Not yet implemented");
        return false;
    }

    abstract EnvironmentLogic getCurrEnv();

    abstract void setCurrEnv(EnvironmentLogic env);
}
