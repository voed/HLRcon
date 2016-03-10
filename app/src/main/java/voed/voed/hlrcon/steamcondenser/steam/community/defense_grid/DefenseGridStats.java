/**
 * This code is free software; you can redistribute it and/or modify it under
 * the terms of the new BSD License.
 *
 * Copyright (c) 2009-2012, Sebastian Staudt
 */

package voed.voed.hlrcon.steamcondenser.steam.community.defense_grid;

import java.util.ArrayList;
import java.util.HashMap;

import voed.voed.hlrcon.steamcondenser.exceptions.SteamCondenserException;
import voed.voed.hlrcon.steamcondenser.steam.community.GameStats;
import voed.voed.hlrcon.steamcondenser.steam.community.XMLData;

/**
 * This class represents the game statistics for a single user in Defense Grid:
 * The Awakening
 *
 * @author Sebastian Staudt
 */
public class DefenseGridStats extends GameStats {

    private HashMap<String, int[]> alienStats;
    private int bronzeMedals;
    private int silverMedals;
    private int goldMedals;
    private int levelsPlayed;
    private int levelsPlayedCampaign;
    private int levelsPlayedChallenge;
    private int levelsWon;
    private int levelsWonCampaign;
    private int levelsWonChallenge;
    private int encountered;
    private int killed;
    private int killedCampaign;
    private int killedChallenge;
    private float resources;
    private float heatDamage;
    private float timePlayed;
    private float interest;
    private float damage;
    private float damageCampaign;
    private float damageChallenge;
    private int orbitalLaserFired;
    private float orbitalLaserDamage;
    private HashMap<String, ArrayList<float[]>> towerStats;

    /**
     * Creates a <code>DefenseGridStats</code> object by calling the super
     * constructor with the game name <code>"defensegrid:awakening"</code>
     *
     * @param steamId The custom URL or the 64bit Steam ID of the user
     * @throws SteamCondenserException if the stats cannot be parsed
     */
    public DefenseGridStats(Object steamId) throws SteamCondenserException {
        super(steamId, "defensegrid:awakening");

        if(this.isPublic()) {
            XMLData generalData = this.xmlData.getElement("stats", "general");
            this.bronzeMedals = generalData.getInteger("bronze_medals_won", "value");
            this.silverMedals = generalData.getInteger("silver_medals_won", "value");
            this.goldMedals = generalData.getInteger("gold_medals_won", "value");
            this.levelsPlayed = generalData.getInteger("levels_played_total", "value");
            this.levelsPlayedCampaign = generalData.getInteger("levels_played_campaign", "value");
            this.levelsPlayedChallenge = generalData.getInteger("levels_played_challenge", "value");
            this.levelsWon = generalData.getInteger("levels_won_total", "value");
            this.levelsWonCampaign = generalData.getInteger("levels_won_campaign", "value");
            this.levelsWonChallenge = generalData.getInteger("levels_won_challenge", "value");
            this.encountered = generalData.getInteger("total_aliens_encountered", "value");
            this.killed = generalData.getInteger("total_aliens_killed", "value");
            this.killedCampaign = generalData.getInteger("total_aliens_killed_campaign", "value");
            this.killedChallenge = generalData.getInteger("total_aliens_killed_challenge", "value");
            this.resources = generalData.getFloat("resources_recovered", "value");
            this.heatDamage = generalData.getFloat("heatdamage", "value");
            this.timePlayed = generalData.getFloat("time_played", "value");
            this.interest = generalData.getFloat("interest_gained", "value");
            this.damage = generalData.getFloat("tower_damage_total", "value");
            this.damageCampaign = generalData.getFloat("tower_damage_total_campaign", "value");
            this.damageChallenge = generalData.getFloat("tower_damage_total_challenge", "value");
            this.orbitalLaserFired = this.xmlData.getInteger("stats", "orbitallaser", "fired", "value");
            this.orbitalLaserDamage = this.xmlData.getFloat("stats", "orbitallaser", "damage", "value");
        }
    }

    /**
     * Returns stats about the aliens encountered by the player
     *
     * The map returned uses the names of the aliens as keys. Every value of
     * the map is an array containing the number of aliens encountered as the
     * first element and the number of aliens killed as the second element.
     *
     * @return array Stats about the aliens encountered
     * @throws SteamCondenserException if the stats cannot be parsed
     */
    public HashMap<String, int[]> getAlienStats()
            throws SteamCondenserException {
        if(!this.isPublic()) {
            return null;
        }

        if(this.alienStats != null) {
            XMLData aliensData = this.xmlData.getElement("stats", "aliens");
            this.alienStats = new HashMap<String, int[]>();
            String[] aliens = {"bulwark", "crasher", "dart", "decoy",
                "drone", "grunt", "juggernaut", "manta", "racer", "rumbler",
                "seeker", "spire", "stealth", "swarmer", "turtle", "walker"};

            for(String alien : aliens) {
                int[] alienData = new int[2];
                alienData[0] = aliensData.getInteger(alien, "encountered", "value");
                alienData[1] = aliensData.getInteger(alien, "killed", "value");
                this.alienStats.put(alien, alienData);
            }
        }

        return this.alienStats;
    }

    /**
     * Returns the bronze medals won by this player
     *
     * @return Bronze medals won
     */
    public int getBronzeMedals() {
        return this.bronzeMedals;
    }

    /**
     * Returns the damage done by this player
     *
     * @return Damage done
     */
    public float getDamage() {
        return this.damage;
    }

    /**
     * Returns the damage done during the campaign by this player
     *
     * @return Damage done during the campaign
     */
    public float getDamageCampaign() {
        return this.damageCampaign;
    }

    /**
     * Returns the damage done during challenges by this player
     *
     * @return Damage done during challenges
     */
    public float getDamageChallenge() {
        return this.damageChallenge;
    }

    /**
     * Returns the aliens encountered by this player
     *
     * @return Aliens encountered
     */
    public int getEncountered() {
        return this.encountered;
    }

    /**
     * Returns the gold medals won by this player
     *
     * @return Gold medals won
     */
    public int getGoldMedals() {
        return this.goldMedals;
    }

    /**
     * Returns the heat damage done by this player
     *
     * @return Heat damage done
     */
    public float getHeatDamage() {
        return this.heatDamage;
    }

    /**
     * Returns the interest gained by the player
     *
     * @return Interest gained
     */
    public float getInterest() {
        return this.interest;
    }

    /**
     * Returns the aliens killed by the player
     *
     * @return Aliens killed
     */
    public int getKilled() {
        return this.killed;
    }

    /**
     * Returns the aliens killed during the campaign by the player
     *
     * @return Aliens killed during the campaign
     */
    public int getKilledCampaign() {
        return this.killedCampaign;
    }

    /**
     * Returns the aliens killed during challenges by the player
     *
     * @return Aliens killed during challenges
     */
    public int getKilledChallenge() {
        return this.killedChallenge;
    }

    /**
     * Returns the number of levels played by the player
     *
     * @return Number of levels played
     */
    public int getLevelsPlayed() {
        return this.levelsPlayed;
    }

    /**
     * Returns the number of levels played during the campaign by the player
     *
     * @return Number of levels played during the campaign
     */
    public int getLevelsPlayedCampaign() {
        return this.levelsPlayedCampaign;
    }

    /**
     * Returns the number of levels played during challenges by the player
     *
     * @return Number of levels played during challenges
     */
    public int getLevelsPlayedChallenge() {
        return this.levelsPlayedChallenge;
    }

    /**
     * Returns the number of levels won by the player
     *
     * @return Number of levels won
     */
    public int getLevelsWon() {
        return this.levelsWon;
    }

    /**
     * Returns the number of levels won during the campaign by the player
     *
     * @return Number of levels during the campaign won
     */
    public int getLevelsWonCampaign() {
        return this.levelsWonCampaign;
    }

    /**
     * Returns the number of levels won during challenges by the player
     *
     * @return Number of levels during challenges won
     */
    public int getLevelsWonChallenge() {
        return this.levelsWonChallenge;
    }

    /**
     * Returns the damage dealt by the orbital laser
     *
     * @return Damage dealt by the orbital laser
     */
    public float getOrbitalLaserDamage() {
        return this.orbitalLaserDamage;
    }

    /**
     * Returns the number of times the orbital lasers has been fired by the player
     *
     * @return Number of times the orbital laser has been fired
     */
    public int getOrbitalLaserFired() {
        return this.orbitalLaserFired;
    }

    /**
     * Returns the amount of resources harvested by the player
     *
     * @return Resources harvested by the player
     */
    public float getResources() {
        return this.resources;
    }

    /**
     * Returns the silver medals won by this player
     *
     * @return Silver medals won
     */
    public int getSilverMedals() {
        return this.silverMedals;
    }

    /**
     * Returns the time played in seconds by the player
     *
     * @return Time played
     */
    public float getTimePlayed() {
        return this.timePlayed;
    }

    /**
     * Returns stats about the towers built by the player
     *
     * The map returned uses the names of the towers as keys. Every value of
     * the map is another map using the keys 1 to 3 for different tower levels.
     * The values of these maps are an array containing the number of towers
     * built as the first element and the damage dealt by this specific tower
     * type as the second element.
     *
     * The Command tower uses the resources gained as second element.
     * The Temporal tower doesn't have a second element.
     *
     * @return Stats about the towers built
     * @throws SteamCondenserException if the stats cannot be parsed
     */
    public HashMap<String, ArrayList<float[]>> getTowerStats()
            throws SteamCondenserException {
        if(!this.isPublic()) {
            return null;
        }

        if(this.towerStats != null) {
            XMLData towersData = this.xmlData.getElement("stats", "towers");
            this.towerStats = new HashMap<String, ArrayList<float[]>>();
            String[] towers = {"cannon", "flak", "gun", "inferno", "laser", "meteor", "missile", "tesla"};

            ArrayList<float[]> towerData;
            for(String tower : towers) {
                towerData = new ArrayList<float[]>();
                for(int i = 1; i <= 3; i++) {
                    float[] levelData = new float[2];
                    levelData[0] = towersData.getXPath(tower + "[@level=" + i + "]/built/value").getFloat();
                    levelData[1] = towersData.getXPath(tower + "[@level=" + i + "]/damage/value").getFloat();
                    towerData.add(i, levelData);
                }
                this.towerStats.put(tower, towerData);
            }

            towerData = new ArrayList<float[]>();
            for(int i = 1; i <= 3; i++) {
                float[] levelData = new float[2];
                levelData[0] = towersData.getXPath("command[@level=" + i + "]/built/value").getFloat();
                levelData[1] = towersData.getXPath("command[@level=" + i + "]/resource/value").getFloat();
                towerData.add(i, levelData);
            }
            this.towerStats.put("command", towerData);

            towerData = new ArrayList<float[]>();
            for(int i = 1; i <= 3; i++) {
                float[] levelData = new float[2];
                levelData[0] = towersData.getXPath("temporal[@level=" + i + "]/built/value").getFloat();
                towerData.add(i, levelData);
            }
            this.towerStats.put("temporal", towerData);
        }

        return this.towerStats;
    }

}
