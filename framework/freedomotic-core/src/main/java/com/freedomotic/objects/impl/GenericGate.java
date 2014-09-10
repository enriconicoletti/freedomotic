/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.freedomotic.objects.impl;

import com.freedomotic.environment.Room;

/**
 *
 * @author nicoletti
 */
public interface GenericGate {

    /**
     *
     */
    void evaluateGate();

    /**
     *
     * @return
     */
    Room getFrom();

    /**
     *
     * @return
     */
    Room getTo();

    /**
     *
     * @return
     */
    boolean isOpen();
    
}
