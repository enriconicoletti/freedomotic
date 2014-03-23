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
//Copyright 2009 Enrico Nicoletti
//eMail: enrico.nicoletti84@gmail.co m
//
//This file is part of EventEngine.
//
//EventEngine is free software; you can redistribute it and/or modify
//it under the terms of the GNU General Public License as published by
//the Free Software Foundation; either version 2 of the License, or
//any later version.
//
//EventEngine is distributed in the hope that it will be useful,
//but WITHOUT ANY WARRANTY; without even the implied warranty of
//MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
//GNU General Public License for more details.
//
//You should have received a copy of the GNU General Public License
//along with EventEngine; if not, write to the Free Software
//Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
package com.freedomotic.model.environment;

import com.freedomotic.model.geometry.FreedomPolygon;
import com.freedomotic.model.object.EnvObject;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Transient;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author enrico
 */
@SuppressWarnings("serial")
@javax.persistence.Entity
@Table(name = "zone")
@Transactional
public class Zone implements Serializable {

    private static final long serialVersionUID = 4668625650384850879L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Column
    private String name;
    @Column
    private String description;
    @Column
    private Boolean room;
    @OneToOne(cascade = {CascadeType.ALL})
    private FreedomPolygon shape;
    @Transient
    private List<EnvObject> objects;
    @Column
    private String texture;
    @ManyToOne
    @JoinColumn(name="environment_id")
    private Environment environment;

    /**
     *
     * @return
     */
    public String getName() {
        return this.name;
    }

    /**
     *
     * @return
     */
    public FreedomPolygon getShape() {
        return this.shape;
    }

    /**
     *
     * @return
     */
    public String getDescription() {
        if (description == null) {
            description = "";
        }

        return description;
    }

    /**
     *
     * @param description
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     *
     * @return
     */
    public String getTexture() {
        return texture;
    }

    /**
     *
     * @param name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     *
     * @param shape
     */
    public void setShape(FreedomPolygon shape) {
        this.shape = shape;
    }

    /**
     *
     * @return
     */
    @Override
    public String toString() {
        return this.getName();
    }

    /**
     *
     * @param file
     */
    public void setTexture(String file) {
        this.texture = file;
    }

    /**
     *
     * @return
     */
    public List<EnvObject> getObjects() {
        if (objects == null) {
            objects = new ArrayList<EnvObject>();
        }

        return objects;
    }

    /**
     *
     * @param objects
     */
    public void setObjects(ArrayList<EnvObject> objects) {
        this.objects = objects;
    }

    /**
     *
     */
    public void init() {
    }

    /**
     *
     */
    public void setChanged() {
    }

    /**
     *
     * @param obj
     * @return
     */
    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }

        if (getClass() != obj.getClass()) {
            return false;
        }

        final Zone other = (Zone) obj;

        if ((this.name == null) ? (other.name != null) : (!this.name.equalsIgnoreCase(other.name))) {
            return false;
        }

        return true;
    }

    /**
     *
     * @return
     */
    @Override
    public int hashCode() {
        int hash = 5;
        hash = (17 * hash) + ((this.name != null) ? this.name.hashCode() : 0);

        return hash;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Environment getEnvironment() {
        return environment;
    }

    public void setEnvironment(Environment environment) {
        this.environment = environment;
    }

    public Boolean isRoom() {
        return room;
    }

    public void setRoom(Boolean room) {
        this.room = room;
    }

    @Deprecated
    public void setAsRoom(boolean b) {
        setRoom(b);
    }

}
