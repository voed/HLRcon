/**
 * This code is free software; you can redistribute it and/or modify it under
 * the terms of the new BSD License.
 *
 * Copyright (c) 2011, Sebastian Staudt
 */

package voed.voed.hlrcon.steamcondenser.steam.community.portal2;

import voed.voed.hlrcon.steamcondenser.exceptions.SteamCondenserException;
import voed.voed.hlrcon.steamcondenser.exceptions.WebApiException;
import voed.voed.hlrcon.steamcondenser.steam.community.GameInventory;
import voed.voed.hlrcon.steamcondenser.steam.community.GameStats;

/**
 * The Portal2Stats class represents the game statistics for a single user in
 * Portal 2
 *
 * @author Sebastian Staudt
 */
public class Portal2Stats extends GameStats {

    private Portal2Inventory inventory;

    /**
     * Creates a new object holding Portal 2 statistics for the given user
     *
     * @param steamId The custom URL or 64bit Steam ID of the user
     * @throws SteamCondenserException if an error occurs while fetching the
     *         stats data
     */
    public Portal2Stats(Object steamId) throws SteamCondenserException {
        super(steamId, "portal2");
    }

    /**
     * Returns the current Portal 2 inventory (a.k.a. Robot Enrichment) of this
     * player
     *
     * @return This player's Portal 2 inventory
     * @throws WebApiException if an error occurs while querying Steam's Web
     *         API
     */
    public Portal2Inventory getInventory() throws SteamCondenserException {
        if(!this.isPublic()) {
            return null;
        }

        if(this.inventory == null) {
            this.inventory = (Portal2Inventory) GameInventory.create(Portal2Inventory.APP_ID, this.user.getSteamId64());
        }

        return this.inventory;
    }

}
