/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.freedomotic.jfrontend;

import com.freedomotic.app.Freedomotic;
import com.freedomotic.core.ResourcesManager;
import com.freedomotic.environment.EnvironmentLogic;
import com.freedomotic.environment.ZoneLogic;
import com.freedomotic.events.ObjectReceiveClick;
import com.freedomotic.jfrontend.utils.SpringUtilities;
import com.freedomotic.model.object.EnvObject;
import com.freedomotic.objects.BehaviorLogic;
import com.freedomotic.objects.EnvObjectLogic;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SpringLayout;

/**
 *
 * @author enrico
 */
public class ListDrawer extends Renderer {

    private JComboBox cmbZone;
    private final JPanel panel = new JPanel();
    private final JavaDesktopFrontend master;
    private EnvironmentLogic currEnv;

    /**
     *
     * @param master
     */
    public ListDrawer(JavaDesktopFrontend master) {
        super(master);
        this.cmbZone = new JComboBox();
        this.master=master;
        currEnv = master.getApi().getDefaultEnvironments();
        cmbZone.removeAllItems();
        cmbZone.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                ZoneLogic zone = (ZoneLogic) cmbZone.getSelectedItem();
                enlistObjects(zone);
            }
        });
        this.setLayout(new BorderLayout());
        this.setBackground(Color.white);

        JScrollPane scroll = new JScrollPane(panel);
        add(scroll);
        enlistZones();
        //autoselect the first element
        enlistObjects((ZoneLogic) cmbZone.getItemAt(0));
        //JScrollPane scroll = new JScrollPane(this);
        setPreferredSize(new Dimension(400, 300));
        //add(scroll);
        validate();
    }

    /**
     *
     * @param env
     */
    public void setCurrEnv(EnvironmentLogic env) {
        this.currEnv = env;
    }

    /**
     *
     * @return
     */
    public EnvironmentLogic getCurrEnv() {
        return this.currEnv;
    }

    private void enlistZones() {
        for (ZoneLogic zone : getCurrEnv().getZones()) {
            cmbZone.addItem(zone);
        }
    }

    private void enlistObjects(ZoneLogic zone) {
        panel.removeAll();
        panel.setLayout(new SpringLayout());

        int row = 0;

        if (zone.getPojo().getObjects().isEmpty()) {
            panel.add(new JLabel("No objects in this zone"));
        }

        for (final EnvObject objPojo : zone.getPojo().getObjects()) {
            final EnvObjectLogic obj = master.getApi().getObjectByUUID(objPojo.getUUID());

            //a coloumn with object name
            JLabel icon = new JLabel(renderSingleObject(obj.getPojo()));
            icon.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    mouseClickObject(obj);
                }
            });
            panel.add(icon);

            JTextArea lblName = new JTextArea(objPojo.getName() + "\n\n" + getCompleteDescription(obj));
            lblName.setBackground(getBackground());
            panel.add(lblName);
            //a configuration button with a listener
            JButton btnConfig = new JButton("Configure");
            btnConfig.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    ObjectEditor objectEditor = new ObjectEditor(obj);
                    objectEditor.setVisible(true);
                }
            });
            panel.add(btnConfig);
            row++;
        }
        SpringUtilities.makeCompactGrid(panel,
                row, 3, //rows, cols
                5, 5, //initX, initY
                5, 5); //xPad, yPad
        validate();
    }

    private String getCompleteDescription(EnvObjectLogic obj) {
        StringBuilder description = new StringBuilder();
        description.append(obj.getPojo().getDescription()).append("\n");

        for (BehaviorLogic b : obj.getBehaviors()) {
            if (b.isActive()) {
                description.append(b.getName()).append(": ").append(b.getValueAsString()).append(" [Active]\n");
            } else {
                description.append(b.getName()).append(": ").append(b.getValueAsString())
                        .append(" [Inactive]\n");
            }
        }

        return description.toString();
    }

    private ImageIcon renderSingleObject(EnvObject obj) {
        if (obj != null) {
            if ((obj.getCurrentRepresentation().getIcon() != null)
                    && !obj.getCurrentRepresentation().getIcon().equalsIgnoreCase("")) {
                BufferedImage img = null;
                img = ResourcesManager.getResource(obj.getCurrentRepresentation().getIcon(),
                        48,
                        48); //-1 means no resizeing

                ImageIcon icon = new ImageIcon(img);

                return icon;
            }
        }

        return null;
    }

    /**
     *
     * @param obj
     */
    public void mouseClickObject(EnvObjectLogic obj) {
        ObjectReceiveClick event = new ObjectReceiveClick(this, obj, ObjectReceiveClick.SINGLE_CLICK);
        Freedomotic.sendEvent(event);
    }

    /**
     *
     * @param callout1
     */
    @Override
    public void createCallout(Callout callout1) {
        //throw new UnsupportedOperationException("Not supported yet.");
    }

    /**
     *
     * @param b
     */
    @Override
    public void setNeedRepaint(boolean b) {
        enlistObjects((ZoneLogic) cmbZone.getSelectedItem());
    }
}
