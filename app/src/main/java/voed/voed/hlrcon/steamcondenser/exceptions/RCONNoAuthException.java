/**
 * This code is free software; you can redistribute it and/or modify it under
 * the terms of the new BSD License.
 *
 * Copyright (c) 2008-2011, Sebastian Staudt
 */

package voed.voed.hlrcon.steamcondenser.exceptions;

/**
 * This exception class indicates that you have not authenticated yet with the
 * game server you're trying to send commands via RCON
 *
 * @author Sebastian Staudt
 * @see voed.voed.hlrcon.steamcondenser.steam.servers.GameServer#rconAuth
 * @see voed.voed.hlrcon.steamcondenser.steam.servers.GameServer#rconExec
 */
public class RCONNoAuthException extends SteamCondenserException {

    /**
     * Creates a new <code>RCONNoAuthException</code> instance
     */
    public RCONNoAuthException() {
        super("Not authenticated yet.");
    }
}
