/**
 *
 * Copyright (c) 2009-2013 Freedomotic team http://freedomotic.com
 *
 * This file is part of Freedomotic
 *
 * This Program is free software; you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation; either version 2, or (at your option) any later version.
 *
 * This Program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 *
 * You should have received a copy of the GNU General Public License along with
 * Freedomotic; see the file COPYING. If not, see
 * <http://www.gnu.org/licenses/>.
 */
package com.freedomotic.model.object;

import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

/**
 *
 * @author nicoletti
 */
@Entity
@DiscriminatorValue(value="BooleanBehavior")
public class BooleanBehavior extends Behavior {

    private static final long serialVersionUID = 8000833627513350346L;
    public final static String VALUE_TRUE = "true";
    public final static String VALUE_FALSE = "false";

    @Column(name="val")
    private boolean value;

    public boolean getValue() {
        return value;
    }

    public void setValue(boolean inputValue) {
        //activate this behavior if it was unactivated
        setActive(true);
        value = inputValue;
    }

    @Override
    public String toString() {
        return new Boolean(value).toString();
    }

}
