/**
 * This code is free software; you can redistribute it and/or modify it under
 * the terms of the new BSD License.
 *
 * Copyright (c) 2010-2012, Sebastian Staudt
 */

package voed.voed.hlrcon.steamcondenser.steam.community.alien_swarm;

import voed.voed.hlrcon.steamcondenser.steam.community.GameWeapon;
import voed.voed.hlrcon.steamcondenser.steam.community.XMLData;

/**
 * This class holds statistical information about weapons used by a player
 * in Alien Swarm
 *
 * @author Sebastian Staudt
 */
public class AlienSwarmWeapon extends GameWeapon {

    private float accuracy;

    private int damage;

    private int friendlyFire;

    private String name;

    /**
     * Creates a new weapon instance based on the assigned weapon XML data
     *
     * @param weaponData The data representing this weapon
     */
    public AlienSwarmWeapon(XMLData weaponData) {
        super(weaponData);

        this.accuracy     = weaponData.getFloat("accuracy");
        this.damage       = weaponData.getInteger("damage");
        this.friendlyFire = weaponData.getInteger("friendlyfire");
        this.name         = weaponData.getString("name");
        this.shots        = weaponData.getInteger("shotsfired");
    }

    /**
     * Returns the accuracy of the player with this weapon
     *
     * @return The accuracy of the player with this weapon
     */
    public float getAccuracy() {
        return this.accuracy;
    }

    /**
     * Returns the damage achieved with this weapon
     *
     * @return The damage achieved with this weapon
     */
    public int getDamage() {
        return this.damage;
    }

    /**
     * Returns the damage dealt to team mates with this weapon
     *
     * @return The damage dealt to team mates with this weapon
     */
    public int getFriendlyFire() {
        return this.friendlyFire;
    }

    /**
     * Returns the name of this weapon
     *
     * @return The name of this weapon
     */
    public String getName() {
        return this.name;
    }

}
