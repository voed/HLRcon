/**
 * This code is free software; you can redistribute it and/or modify it under
 * the terms of the new BSD License.
 *
 * Copyright (c) 2009-2012, Sebastian Staudt
 */

package voed.voed.hlrcon.steamcondenser.steam.community.l4d;

import voed.voed.hlrcon.steamcondenser.steam.community.XMLData;

/**
 * This class represents the statistics of a single weapon for a user in
 * Left4Dead
 *
 * @author Sebastian Staudt
 */
public class L4DWeapon extends AbtractL4DWeapon {

    /**
     * Creates a new instance of a weapon based on the given XML data
     *
     * @param weaponData The XML data for this weapon
     */
    public L4DWeapon(XMLData weaponData) {
        super(weaponData);

        this.killPercentage = Float.parseFloat(weaponData
            .getString("killpct").replace("%", "")) * 0.01f;
    }

}
