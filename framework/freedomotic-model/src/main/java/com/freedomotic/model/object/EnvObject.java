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

import com.freedomotic.model.environment.Environment;
import com.freedomotic.model.geometry.FreedomShape;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author nicoletti
 */
@SuppressWarnings("serial")
@javax.persistence.Entity
@Table(name = "object")
@Transactional
public class EnvObject implements Serializable {

    private static final long serialVersionUID = -7253889516478184321L;

    public static long getSerialVersionUID() {
        return serialVersionUID;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Column
    private String name;
    @Column
    private String description;
    @Column
    private String actAs;
    @Column
    private String type;
    @Column
    private String uuid;
    @Column
    private String hierarchy;
    @Column
    private String protocol;
    @Column
    private String phisicalAddress;
    @OneToMany(cascade = {CascadeType.ALL})
    //@JoinColumn(name = "behavior_id")
    private List<Behavior> behaviors;
    @OneToMany(cascade = {CascadeType.ALL})
    @JoinColumn(name = "representation_id")
    private List<Representation> representation;
    @ElementCollection
    private Set<String> tags;
    @Column
    private Properties actions;
    @Column
    private Properties triggers;
    @Column
    private int currentRepresentation;
    //@OneToOne
    //private Environment environment;
    @Column
    private String envUUID;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public List<Representation> getRepresentation() {
        return representation;
    }

    public void setRepresentation(List<Representation> representation) {
        this.representation = representation;
    }

    public Set<String> getTags() {
        return tags;
    }

    public void setTags(Set<String> tags) {
        this.tags = tags;
    }

    public void setBehaviors(List<Behavior> behaviors) {
        this.behaviors = behaviors;
    }

    public void setActions(Properties actions) {
        this.actions = actions;
    }

    public void setTriggers(Properties triggers) {
        this.triggers = triggers;
    }

    /**
     *
     * @return
     */
    @RequiresPermissions("objects:read")
    public Properties getActions() {
        return actions;
    }

    /**
     *
     * @return
     */
    @RequiresPermissions("objects:read")
    public Properties getTriggers() {
        if (triggers == null) {
            triggers = new Properties();
        }

        return triggers;
    }

    /**
     *
     * @param name
     */
    @RequiresPermissions("objects:update")
    public void setName(String name) {
        this.name = name;
    }

    /**
     *
     * @return
     */
    @RequiresPermissions("objects:read")
    public String getName() {
        return this.name;
    }

    /**
     *
     * @return
     */
    @RequiresPermissions("objects:read")
    public String getUUID() {
        return uuid;
    }

    /**
     *
     * @param uuid
     */
    @RequiresPermissions("objects:update")
    public void setUUID(String uuid) {
        this.uuid = uuid;
    }

    /**
     *
     * @return
     */
    @RequiresPermissions("objects:read")
    public String getHierarchy() {
        return hierarchy;
    }

    /**
     *
     * @param hierarchy
     */
    @RequiresPermissions("objects:update")
    public void setHierarchy(String hierarchy) {
        this.hierarchy = hierarchy;
    }

    /**
     *
     * @param index
     */
    @RequiresPermissions("objects:update")
    public void setCurrentRepresentation(int index) {
        if (representation.get(index) != null) {
            currentRepresentation = index;
        }
    }

    /**
     *
     * @return
     */
    @RequiresPermissions("objects:read")
    public Representation getCurrentRepresentation() {
        return representation.get(currentRepresentation);
    }

    /**
     *
     * @return
     */
    @RequiresPermissions("objects:read")
    public int getCurrentRepresentationIndex() {
        return currentRepresentation;
    }

    /**
     *
     * @return
     */
    @RequiresPermissions("objects:read")
    public List<Representation> getRepresentations() {
        return representation;
    }

    /**
     *
     * @return
     */
    @RequiresPermissions("objects:read")
    public String getProtocol() {
        if ((protocol == null) || (protocol.isEmpty())) {
            protocol = "unknown";
        }

        return protocol;
    }

    /**
     *
     * @param protocol
     */
    @RequiresPermissions("objects:update")
    public void setProtocol(String protocol) {
        this.protocol = protocol;
    }

    /**
     *
     * @return
     */
    @RequiresPermissions("objects:read")
    public ArrayList<Behavior> getActiveBehaviors() {
        ArrayList<Behavior> activeBehaviors = new ArrayList<Behavior>();
        for (Behavior behavior : behaviors) {
            if (behavior.isActive()) {
                activeBehaviors.add(behavior);
            }
        }

        return activeBehaviors;
    }

    /**
     *
     * @return
     */
    @RequiresPermissions("objects:read")
    public List<Behavior> getBehaviors() {
        return behaviors;
    }

    /**
     *
     * @param behavior
     * @return
     */
    @RequiresPermissions("objects:read")
    public Behavior getBehavior(String behavior) {
        for (Behavior b : behaviors) {
            if (b.getName().equalsIgnoreCase(behavior)) {
                return b;
            }
        }

        //Freedomotic.logger.warning("Searching for behavior named '" + behavior + "' but it doesen't exists for object '" + getName() + "'.");
        return null; //this behaviors doesn't exists for this object
    }

    /**
     *
     * @param actAs
     */
    @RequiresPermissions("objects:update")
    public void setActAs(String actAs) {
        this.actAs = actAs;
    }

    /**
     *
     * @return
     */
    @RequiresPermissions("objects:read")
    public String getActAs() {
        return this.actAs;
    }

    /**
     *
     * @return
     */
    @RequiresPermissions("objects:read")
    public String getDescription() {
        return description;
    }

    /**
     *
     * @param desc
     */
    @RequiresPermissions("objects:update")
    public void setDescription(String desc) {
        this.description = desc;
    }

    /**
     *
     * @param type
     */
    @RequiresPermissions("objects:update")
    public void setType(String type) {
        this.type = type;
    }

    /**
     *
     * @return
     */
    @RequiresPermissions("objects:read")
    public String getType() {
        return this.type;
    }

    /**
     *
     * @return
     */
    @RequiresPermissions("objects:read")
    public String getPhisicalAddress() {
        if ((phisicalAddress == null) || (phisicalAddress.isEmpty())) {
            phisicalAddress = "unknown";
        }

        return phisicalAddress.trim();
    }

    /**
     *
     * @param address
     */
    @RequiresPermissions("objects:update")
    public void setPhisicalAddress(String address) {
        phisicalAddress = address;
    }

    /**
     *
     * @return
     */
    @RequiresPermissions("objects:read")
    public FreedomShape getShape() {
        return getCurrentRepresentation().getShape();
    }

    /**
     * Create an HashMap with all object properties useful in an event. In
     * EnvObjectLogic this method is used to get basic exposed data on which are
     * added behaviors related data.
     *
     * @return a set of key/values of object properties
     */
    @RequiresPermissions("objects:read")
    public HashMap<String, String> getExposedProperties() {
        HashMap<String, String> result = new HashMap<String, String>();
        result.put("object.name", getName());
        result.put("object.address", getPhisicalAddress());
        result.put("object.protocol", getProtocol());
        result.put("object.type", getType());
        result.put("object.tags", getTagsString());
        result.put("object.uuid", getUUID());
        return result;
    }

    /**
     * Returns only the last part of the type
     *
     * @return
     */
    @RequiresPermissions("objects:read")
    public String getSimpleType() {
        //get the part of the string after the last dot characher
        //eg: 'EnvObject.ElectricDevice.Light' -> returns 'light'
        return getType().substring(getType().lastIndexOf(".") + 1).trim().toLowerCase();
    }

    /**
     *
     * @param obj
     * @return
     */
    @Override
    @RequiresPermissions("objects:read")
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }

        if (getClass() != obj.getClass()) {
            return false;
        }

        final EnvObject other = (EnvObject) obj;

        if (!this.name.equalsIgnoreCase(other.name)) {
            //if they have different names they cannot have same address/protocol
            //otherwise are the same object despite of the different name
            if ((this.getPhisicalAddress().equalsIgnoreCase(other.getPhisicalAddress()))
                    && (this.getProtocol().equalsIgnoreCase(other.getProtocol()))) {
                if ((this.getPhisicalAddress().equalsIgnoreCase("unknown"))
                        || (this.getProtocol().equalsIgnoreCase("unknown"))) {
                    return false;
                } else {
                    return true;
                }
            } else {
                return false;
            }
        }

        return true;
    }

    /**
     *
     * @return
     */
    @Override
    @RequiresPermissions("objects:read")
    public int hashCode() {
        int hash = 5;
        hash = (89 * hash) + ((this.name != null) ? this.name.hashCode() : 0);

        return hash;
    }

    /**
     *
     * @return
     */
    @Override
    @RequiresPermissions("objects:read")
    public String toString() {
        return getName();
    }

    /**
     *
     * @return
     */
    @RequiresPermissions("objects:read")
    public Set<String> getTagsList() {
        return this.tags;
    }

    /**
     *
     * @return
     */
    @RequiresPermissions("objects:read")
    public String getTagsString() {
        StringBuilder tagString = new StringBuilder();
        Boolean morethanone = false;
        for (String tag : getTagsList()) {
            if (tag.trim() != "") {
                if (morethanone) {
                    tagString.append(",");
                }
                tagString.append(tag.trim());
                morethanone = true;
            }
        }
        return tagString.toString();
    }

    /**
     *
     */
    public void initTags() {
        if (this.tags == null) {
            this.tags = new HashSet();
        }
    }

    @Deprecated
    public String getEnvironmentID() {
        return this.getEnvUUID();
    }

    @Deprecated
    public void setEnvironmentID(String uuid) {
        this.setEnvUUID(uuid);
    }

    public String getEnvUUID() {
        return envUUID;
    }

    public void setEnvUUID(String envUUID) {
        this.envUUID = envUUID;
    }

}
