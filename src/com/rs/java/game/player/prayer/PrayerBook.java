package com.rs.java.game.player.prayer;

import java.io.Serial;
import java.io.Serializable;
import java.util.*;
import java.util.function.Function;

import com.rs.java.game.Animation;
import com.rs.java.game.Graphics;
import com.rs.java.game.minigames.clanwars.ClanWars;
import com.rs.java.game.minigames.clanwars.ClanWars.Rules;
import com.rs.java.game.player.CombatDefinitions;
import com.rs.java.game.player.Player;
import com.rs.java.game.player.Skills;
import com.rs.java.game.player.controlers.WildernessControler;
import com.rs.java.utils.Utils;

public class PrayerBook implements Serializable {
    @Serial
    private static final long serialVersionUID = -2082861520556582824L;

    // Constants
    private static final int PRAYER_POINTS_PER_LEVEL = 10;
    private static final int MAX_LEECH_BONUS = 15;
    private static final int MIN_LEECH_BONUS = -15;
    private static final int DEFENCE_REQUIREMENT_NORMAL = 70;
    private static final int DEFENCE_REQUIREMENT_ANCIENT = 30;
    private static final int[] STAT_VARBIT_IDS = {6857, 6858, 6859, 6860, 6861}; // Attack, Strength, Defence, Range, Magic

    // Configuration
    private transient Player player;
    private PrayerBookType activeBook = PrayerBookType.NORMAL;
    private boolean usingQuickPrayer = false;

    // State
    private int prayerPoints;
    private transient final EnumSet<NormalPrayer> activeNormalPrayers = EnumSet.noneOf(NormalPrayer.class);
    private transient final EnumSet<AncientPrayer> activeAncientPrayers = EnumSet.noneOf(AncientPrayer.class);
    private transient final boolean[] quickNormalPrayers = new boolean[NormalPrayer.values().length];
    private transient final boolean[] quickAncientPrayers = new boolean[AncientPrayer.values().length];
    private transient final int[] leechBonuses = new int[5]; // 0=Attack, 1=Strength, 2=Defence, 3=Range, 4=Magic
    private transient boolean boostedLeech = false;
    private transient final Map<Prayer, Long> nextDrainTimes = new HashMap<>();

    // Initialization
    public PrayerBook() {
        this.prayerPoints = 10;
    }

    public void onLogin() {
        initializePrayerBook();
    }

    public void initializePrayerBook() {
        if (player != null && player.isActive()) {
            refresh();
            resetLeechBonuses();
        }
        debug("Initialized prayer book");
    }


    public void setPlayer(Player player) {
        this.player = player;
    }

    private static final boolean DEBUG = true;

    private void debug(String message) {
        if (DEBUG) {
            player.message("[Prayer Debug] " + message);
            System.out.println("[Prayer Debug] " + message);
        }
    }

    public boolean hasFullPrayerPoints() {
        return prayerPoints >= getMaxPrayerPoints();
    }

    public boolean isActive(Prayer prayer) {
        if (prayer == null) {
            return false;
        }
        return getActivePrayerSet().contains(prayer);
    }

    public boolean handlePrayerClick(int componentId, int slotId) {
        debug("Handling prayer click - componentId: " + componentId + ", slotId: " + slotId);
        if (componentId == 8 || componentId == 42) { // Main prayer book components
            Prayer prayer = isAncientCurses()
                    ? AncientPrayer.forId(slotId)
                    : NormalPrayer.forId(slotId);
            debug("Found prayer: " + (prayer != null ? prayer.getName() : "null"));
            if (prayer != null) {
                boolean result = switchPrayer(prayer);
                debug("Switch result: " + result);
                return result;
            }
        } else if (componentId == 43) {
            debug("Toggling quick prayers");// Quick prayers toggle
            switchQuickPrayerSettings();
            return true;
        }
        debug("No valid prayer action found");
        return false;
    }

    public boolean switchPrayer(Prayer prayer) {
        boolean currentlyActive = isActive(prayer);
        if (currentlyActive) {
            debug("Prayer is currently active - toggling off");
            return deactivatePrayer(prayer);
        }
        debug("Attempting to activate prayer: " + prayer.getName());
        if (!canActivatePrayer(prayer)) {
            debug("Cannot activate prayer due to failed checks");
            return false;
        }
        debug("Closing conflicting prayers for: " + prayer.getName());
        closeConflictingPrayers(prayer);

        if (isQuickPrayerMode()) {
            debug("Setting quick prayer: " + prayer.getName() + " (ID: " + prayer.getId() + ")");
            getQuickPrayerArray()[prayer.getId()] = true;
        } else {
            debug("Activating prayer normally: " + prayer.getName());
            // Safe casting based on prayer book type
            if (isAncientCurses()) {
                activeAncientPrayers.add((AncientPrayer) prayer);
                debug("Added to ancient prayers set");
            } else {
                activeNormalPrayers.add((NormalPrayer) prayer);
                debug("Added to normal prayers set");
            }
            resetDrainTimer(prayer);
            debug("Reset drain timer for prayer");
            updateAppearanceIfNeeded(prayer);
            playActivationEffects(prayer);
        }

        player.getPackets().sendSound(2662, 0, 1);
        recalculatePrayer();
        debug("Prayer activated successfully: " + prayer.getName());
        return true;
    }

    private boolean deactivatePrayer(Prayer prayer) {
        debug("Deactivating prayer: " + prayer.getName());

        if (isQuickPrayerMode()) {
            debug("Removing from quick prayers");
            getQuickPrayerArray()[prayer.getId()] = false;
        } else {
            debug("Removing from active prayers");
            if (isAncientCurses()) {
                activeAncientPrayers.remove((AncientPrayer) prayer);
            } else {
                activeNormalPrayers.remove((NormalPrayer) prayer);
            }
            nextDrainTimes.remove(prayer);
            updateAppearanceIfNeeded(prayer);
        }

        player.getPackets().sendSound(2662, 0, 1);
        recalculatePrayer();
        debug("Prayer deactivated successfully");
        return true;
    }

    private boolean canActivatePrayer(Prayer prayer) {
        if (player.isDead()) {
            debug("Cannot activate - player is dead");
            return false;
        }
        if (!hasPrayerPoints()) {
            debug("Cannot activate - no prayer points");
            player.getPackets().sendGameMessage("Please recharge your prayer.");
            return false;
        }
        if (!meetsLevelRequirement(prayer)) {
            debug("Cannot activate - level requirement not met");
            return false;
        }
        if (!meetsSpecialRequirements(prayer)) {
            debug("Cannot activate - special requirements not met");
            return false;
        }
        if (isDisabledInClanWars()) {
            debug("Cannot activate - disabled in clan wars");
            return false;
        }
        if (isPrayerDelayActive()) {
            debug("Cannot activate - prayer delay active");
            return false;
        }
        debug("All activation checks passed");
        return true;
    }

    private boolean isPrayerDelayActive() {
        if (player.getPrayerDelay() >= Utils.currentTimeMillis()) {
            player.getPackets().sendGameMessage("You are currently injured and cannot use protection prayers!");
            return true;
        }
        return false;
    }

    private boolean meetsLevelRequirement(Prayer prayer) {
        int prayerLevel = player.getSkills().getLevelForXp(Skills.PRAYER);
        if (prayerLevel < prayer.getRequiredLevel()) {
            debug("Prayer level too low (has: " + prayerLevel + ", needed: " + prayer.getRequiredLevel() + ")");
            player.getPackets().sendGameMessage("You need a Prayer level of at least " + prayer.getRequiredLevel() + " to use this prayer.");
            return false;
        }

        // Special defence requirements
        if (prayer.getBook() == PrayerBookType.NORMAL &&
                (prayer == NormalPrayer.PIETY || prayer == NormalPrayer.CHIVALRY || prayer == NormalPrayer.RIGOUR || prayer == NormalPrayer.AUGURY)) {
            int defenceLevel = player.getSkills().getLevelForXp(Skills.DEFENCE);
            if (defenceLevel < DEFENCE_REQUIREMENT_NORMAL) {
                debug("Defence level too low for normal prayer (has: " + defenceLevel + ", needed: " + DEFENCE_REQUIREMENT_NORMAL + ")");
                player.getPackets().sendGameMessage("You need a Defence level of at least " + DEFENCE_REQUIREMENT_NORMAL + " to use this prayer.");
                return false;
            }
        }

        if (prayer.getBook() == PrayerBookType.ANCIENT_CURSES) {
            int defenceLevel = player.getSkills().getLevelForXp(Skills.DEFENCE);
            if (defenceLevel < DEFENCE_REQUIREMENT_ANCIENT) {
                debug("Defence level too low for ancient prayer (has: " + defenceLevel + ", needed: " + DEFENCE_REQUIREMENT_ANCIENT + ")");
                player.getPackets().sendGameMessage("You need a Defence level of at least " + DEFENCE_REQUIREMENT_ANCIENT + " to use ancient curses.");
                return false;
            }
        }

        debug("Level requirements met");
        return true;
    }

    private boolean meetsSpecialRequirements(Prayer prayer) {
        if (!prayer.hasSpecialRequirements(player)) {
            player.getPackets().sendGameMessage("You don't have access to this prayer.");
            return false;
        }
        return true;
    }

    private boolean isDisabledInClanWars() {
        if (player.getCurrentFriendChat() != null) {
            ClanWars war = player.getCurrentFriendChat().getClanWars();
            if (war != null && war.get(Rules.NO_PRAYER) &&
                    (war.getFirstPlayers().contains(player) || war.getSecondPlayers().contains(player))) {
                player.getPackets().sendGameMessage("Prayer has been disabled during this war.");
                return true;
            }
        }
        return false;
    }

    /**
     * Closes all prayers in the specified conflict groups
     * @param groups The conflict groups to close prayers from
     */
    private void closePrayers(PrayerConflictGroup... groups) {
        for (PrayerConflictGroup group : groups) {
            closePrayersInGroup(group);
        }
    }

    /**
     * Closes all prayers belonging to a specific conflict group
     * @param group The conflict group to close
     */
    private void closePrayersInGroup(PrayerConflictGroup group) {
        // Get the prayers that belong to this conflict group
        Prayer[] prayersInGroup = getPrayersInConflictGroup(group);

        // Close each prayer in the group
        for (Prayer prayer : prayersInGroup) {
            if (isQuickPrayerMode()) {
                getQuickPrayerArray()[prayer.getId()] = false;
            } else {
                getActivePrayerSet().remove(prayer);
                nextDrainTimes.remove(prayer);
            }
        }

        // Update appearance if needed
        if (group == PrayerConflictGroup.STANDARD_PROTECTION ||
                group == PrayerConflictGroup.RETRIBUTION ||
                group == PrayerConflictGroup.SPECIAL_PROTECTION) {
            player.getAppearence().generateAppearenceData();
        }
    }

    /**
     * Gets all prayers belonging to a conflict group
     */
    private Prayer[] getPrayersInConflictGroup(PrayerConflictGroup group) {
        if (isAncientCurses()) {
            return getAncientPrayersInGroup(group);
        } else {
            return getNormalPrayersInGroup(group);
        }
    }

    private Prayer[] getNormalPrayersInGroup(PrayerConflictGroup group) {
        switch (group) {
            case DEFENSIVE_SKINS:
                return new Prayer[]{NormalPrayer.THICK_SKIN, NormalPrayer.ROCK_SKIN, NormalPrayer.STEEL_SKIN};
            case STRENGTH:
                return new Prayer[]{NormalPrayer.BURST_OF_STRENGTH, NormalPrayer.SUPERHUMAN_STRENGTH,
                        NormalPrayer.ULTIMATE_STRENGTH};
            case ATTACK:
                return new Prayer[]{NormalPrayer.CLARITY_OF_THOUGHT, NormalPrayer.IMPROVED_REFLEXES, NormalPrayer.INCREDIBLE_REFLEXES};
            case PROTECT_ITEM:
                return new Prayer[]{NormalPrayer.PROTECT_ITEM, AncientPrayer.PROTECT_ITEM_CURSE};
            default:
                return new Prayer[0];
        }
    }

    private Prayer[] getAncientPrayersInGroup(PrayerConflictGroup group) {
        switch (group) {
            case STANDARD_PROTECTION:
                return new Prayer[]{AncientPrayer.DEFLECT_MAGIC, AncientPrayer.DEFLECT_MISSILES,
                        AncientPrayer.DEFLECT_MELEE};
            // ... other groups
            default:
                return new Prayer[0];
        }
    }

    private void closeConflictingPrayers(Prayer prayer) {
        if (prayer.getBook() == PrayerBookType.NORMAL) {
            NormalPrayer normalPrayer = (NormalPrayer) prayer;
            switch (normalPrayer) {
                case THICK_SKIN:
                case ROCK_SKIN:
                case STEEL_SKIN:
                    debug("Closing defensive skin prayers");
                    closePrayers(PrayerConflictGroup.DEFENSIVE_SKINS, PrayerConflictGroup.OTHER);
                    break;
                case BURST_OF_STRENGTH:
                case SUPERHUMAN_STRENGTH:
                case ULTIMATE_STRENGTH:
                    debug("Closing strength prayers");
                    closePrayers(PrayerConflictGroup.STRENGTH, PrayerConflictGroup.RANGED,
                            PrayerConflictGroup.MAGIC, PrayerConflictGroup.OTHER);
                    break;
                // ... other cases
            }
        } else {
            AncientPrayer ancientPrayer = (AncientPrayer) prayer;
            switch (ancientPrayer) {
                case SAP_WARRIOR:
                case SAP_RANGER:
                case SAP_MAGE:
                case SAP_SPIRIT:
                    closePrayers(PrayerConflictGroup.STRENGTH, PrayerConflictGroup.OTHER);
                    break;
                // ... other cases
            }
        }
    }

    private void playActivationEffects(Prayer prayer) {
        if (isQuickPrayerMode()) return;

        Animation animation = prayer.getActivationAnimation();
        Graphics graphics = prayer.getActivationGraphics();

        if (animation != null) {
            player.animate(animation);
        }
        if (graphics != null) {
            player.gfx(graphics);
        }
    }

    private void updateAppearanceIfNeeded(Prayer prayer) {
        if (prayer.isProtectionPrayer() ||
                prayer == NormalPrayer.RETRIBUTION ||
                prayer == NormalPrayer.REDEMPTION ||
                prayer == NormalPrayer.SMITE ||
                prayer == AncientPrayer.DEFLECT_MAGIC ||
                prayer == AncientPrayer.DEFLECT_MISSILES ||
                prayer == AncientPrayer.DEFLECT_MELEE ||
                prayer == AncientPrayer.WRATH ||
                prayer == AncientPrayer.SOUL_SPLIT ||
                prayer == AncientPrayer.TURMOIL) {

            player.getAppearence().generateAppearenceData();
        }
    }

    private void recalculatePrayer() {
        int configValue = 0;

        if (isQuickPrayerMode()) {
            boolean[] quickPrayers = getQuickPrayerArray();
            for (int i = 0; i < quickPrayers.length; i++) {
                if (quickPrayers[i]) {
                    Prayer prayer = getPrayerForId(i);
                    if (prayer != null) {
                        configValue += getPrayerConfigValue(prayer);
                    }
                }
            }
        } else {
            for (Prayer prayer : getActivePrayerSet()) {
                configValue += getPrayerConfigValue(prayer);
            }
        }

        int configId = isAncientCurses() ?
                (isQuickPrayerMode() ? 1587 : 1582) :
                (isQuickPrayerMode() ? 1397 : 1395);

        player.getPackets().sendConfig(configId, configValue);
    }

    private int getPrayerConfigValue(Prayer prayer) {
        return isAncientCurses() ? (int) Math.pow(2, prayer.getId()) : prayer.getConfigValue();
    }

    private double calculateBoost(Function<Prayer, Double> boostGetter) {
        if (!hasActivePrayers()) return 1.0;

        double bonus = 1.0;
        for (Prayer prayer : getActivePrayers()) {
            Double boost = boostGetter.apply(prayer);
            if (boost != null) {
                // Handle leech bonuses separately
                if (prayer instanceof AncientPrayer && ((AncientPrayer)prayer).getLeechBonusIndex() >= 0) {
                    int index = ((AncientPrayer)prayer).getLeechBonusIndex();
                    bonus += (boost + leechBonuses[index]) / 100.0;
                } else {
                    bonus += boost;
                }
            }
        }
        return bonus;
    }

    // Prayer Effects
    public double getMagicMultiplier() {
        return calculateBoost(Prayer::getMagicBoost);
    }

    // Ranged
    public double getRangedMultiplier() {
        return calculateBoost(Prayer::getRangedBoost);
    }

    // Attack
    public double getAttackMultiplier() {
        return calculateBoost(Prayer::getAttackBoost);
    }

    // Strength
    public double getStrengthMultiplier() {
        return calculateBoost(Prayer::getStrengthBoost);
    }

    // Defence
    public double getDefenceMultiplier() {
        return calculateBoost(Prayer::getDefenceBoost);
    }

    // ... similar methods for other combat bonuses

    // Prayer Drain System
    public void processPrayerDrain() {
        if (!hasActivePrayers()) {
            return;
        }

        long currentTime = Utils.currentTimeMillis();
        int totalDrain = 0;

        for (Prayer prayer : getActivePrayers()) {
            Long nextDrain = nextDrainTimes.get(prayer);
            if (nextDrain != null && nextDrain <= currentTime) {
                totalDrain += calculateDrainAmount(prayer, currentTime);
            }
        }

        if (totalDrain > 0) {
            drainPrayer(totalDrain, true);
            if (!hasPrayerPoints()) {
                closeAllPrayers();
            }
        }
    }

    private int calculateDrainAmount(Prayer prayer, long currentTime) {
        int drain = 0;
        double rate = prayer.getDrainRate() * 1000;
        rate += player.getCombatDefinitions().getBonuses()[CombatDefinitions.PRAYER_BONUS] * 50;

        long drainTimer = nextDrainTimes.get(prayer);
        int passedTime = (int) (currentTime - drainTimer);

        drain++;
        int count = 0;
        while (passedTime >= rate && count++ < 10) {
            drain++;
            passedTime -= rate;
        }

        nextDrainTimes.put(prayer, (currentTime + (long) rate) - passedTime);
        return drain;
    }

    public void switchQuickPrayerSettings() {
        // Toggle quick prayer state
        usingQuickPrayer = !usingQuickPrayer;

        // Update client configuration
        player.getPackets().sendGlobalConfig(181, usingQuickPrayer ? 1 : 0);
        player.getPackets().sendConfig(1584, isAncientCurses() ? 1 : 0);

        // Unlock prayer book buttons based on current mode
        unlockPrayerBookButtons();

        // Update appearance if needed (for protection prayer visuals)
        player.getAppearence().generateAppearenceData();

        // Recalculate active prayer effects
        recalculatePrayer();

        // Special case for quick prayer activation
        if (usingQuickPrayer) {
            player.getPackets().sendGlobalConfig(168, 6); // Visual effect for quick prayer activation
        }

        // Debug logging
        debug("Quick prayers " + (usingQuickPrayer ? "enabled" : "disabled"));

        // Play sound effect
        player.getPackets().sendSound(2662, 0, 1);
    }

    private void unlockPrayerBookButtons() {
        // Unlock either quick prayer selection or normal prayer buttons
        int componentId = usingQuickPrayer ? 42 : 8; // 42 = quick prayers, 8 = normal prayers
        player.getPackets().sendUnlockIComponentOptionSlots(271, componentId, 0, 29, 0);
    }

    // Quick Prayers
    public void switchQuickPrayers() {
        if (player.isDead() || !hasPrayerPoints()) return;

        if (hasActivePrayers()) {
            closeAllPrayers();
        } else {
            boolean activated = false;
            boolean[] quickPrayers = getQuickPrayerArray();

            for (int i = 0; i < quickPrayers.length; i++) {
                if (quickPrayers[i]) {
                    Prayer prayer = getPrayerForId(i);
                    if (prayer != null && switchPrayer(prayer)) {
                        activated = true;
                    }
                }
            }
            if (activated) {
                player.getPackets().sendGlobalConfig(182, 1);
                recalculatePrayer();
                return;
            }
            player.message("You don't have any quick prayers configured.");
        }
    }

    public int getPrayerHeadIcon() {
        if (!hasActivePrayers()) {
            return -1;
        }

        if (isAncientCurses()) {
            return getAncientCursesHeadIcon();
        } else {
            return getNormalPrayersHeadIcon();
        }
    }

    private int getAncientCursesHeadIcon() {
        if (isActive(AncientPrayer.DEFLECT_SUMMONING)) {
            int value = -1;
            if (isActive(AncientPrayer.DEFLECT_MAGIC)) value += 3;
            if (isActive(AncientPrayer.DEFLECT_MISSILES)) value += 2;
            if (isActive(AncientPrayer.DEFLECT_MELEE)) value += 1;
            return value;
        } else {
            if (isActive(AncientPrayer.DEFLECT_MAGIC)) return 14;
            if (isActive(AncientPrayer.DEFLECT_MISSILES)) return 15;
            if (isActive(AncientPrayer.DEFLECT_MELEE)) return 13;
            if (isActive(AncientPrayer.WRATH)) return 16;
            if (isActive(AncientPrayer.SOUL_SPLIT)) return 17;
            if (isActive(AncientPrayer.TURMOIL)) return 18;
        }
        return -1;
    }

    private int getNormalPrayersHeadIcon() {
        if (isActive(NormalPrayer.PROTECT_FROM_SUMMONING)) return 8;
        if (isActive(NormalPrayer.PROTECT_FROM_MAGIC)) return 3;
        if (isActive(NormalPrayer.PROTECT_FROM_MISSILES)) return 2;
        if (isActive(NormalPrayer.PROTECT_FROM_MELEE)) return 1;
        if (isActive(NormalPrayer.RETRIBUTION)) return 8;
        if (isActive(NormalPrayer.REDEMPTION)) return 4;
        if (isActive(NormalPrayer.SMITE)) return 5;

        return -1;
    }

    // Utility Methods
    public void reset() {
        closeAllPrayers();
        prayerPoints = player.getSkills().getLevelForXp(Skills.PRAYER) * PRAYER_POINTS_PER_LEVEL;
        refreshPrayerPoints();
    }

    public void closeAllPrayers() {
        activeNormalPrayers.clear();
        activeAncientPrayers.clear();
        nextDrainTimes.clear();
        resetLeechBonuses();
        recalculatePrayer();
        player.getPackets().sendGlobalConfig(182, 0);
        player.getAppearence().generateAppearenceData();
    }

    public void closeProtectionPrayers() {
        if (isAncientCurses()) {
            activeAncientPrayers.remove(AncientPrayer.DEFLECT_MAGIC);
            activeAncientPrayers.remove(AncientPrayer.DEFLECT_MISSILES);
            activeAncientPrayers.remove(AncientPrayer.DEFLECT_MELEE);
        } else {
            activeNormalPrayers.remove(NormalPrayer.PROTECT_FROM_MAGIC);
            activeNormalPrayers.remove(NormalPrayer.PROTECT_FROM_MISSILES);
            activeNormalPrayers.remove(NormalPrayer.PROTECT_FROM_MELEE);
            activeNormalPrayers.remove(NormalPrayer.RETRIBUTION);
        }
        player.getAppearence().generateAppearenceData();
    }

    // Prayer Points Management
    public boolean drainPrayer(int amount) {
        if (prayerPoints <= 0) return false;
        prayerPoints = Math.max(0, prayerPoints - amount);
        refreshPrayerPoints();
        return true;
    }

    public boolean drainPrayer(int amount, boolean updateCheck) {
        if (prayerPoints <= 0) return false;
        int newPrayer = Math.max(0, prayerPoints - amount);
        boolean changed = (newPrayer != prayerPoints);
        prayerPoints = newPrayer;
        if (changed && (!updateCheck || prayerPoints % 10 == 0)) {
            refreshPrayerPoints();
        }
        return changed;
    }

    public boolean drainPrayer() {
        if (prayerPoints <= 0) return false;
        prayerPoints = 0;
        refreshPrayerPoints();
        return true;
    }

    public boolean drainPrayerOnHalf() {
        if (prayerPoints <= 0) return false;
        int newAmount = prayerPoints / 2;
        if (newAmount == prayerPoints) return false;
        prayerPoints = newAmount;
        refreshPrayerPoints();
        return true;
    }

    public boolean restorePrayer(int amount) {
        int max = getMaxPrayerPoints();
        if (prayerPoints >= max) return false;
        prayerPoints = Math.min(max, prayerPoints + amount);
        refreshPrayerPoints();
        return true;
    }

    public boolean restorePrayer(int amount, boolean updateCheck) {
        int max = getMaxPrayerPoints();
        if (prayerPoints >= max) return false;
        int newPrayer = Math.min(max, prayerPoints + amount);
        boolean changed = (newPrayer != prayerPoints);
        prayerPoints = newPrayer;
        if (changed && (!updateCheck || prayerPoints % 10 == 0)) {
            refreshPrayerPoints();
        }
        return changed;
    }

    // Active Prayers Management
    public Set<Prayer> getActivePrayers() {
        return Collections.unmodifiableSet(getActivePrayerSet());
    }

    public int[] getActivePrayerIds() {
        Set<? extends Prayer> activeSet = getActivePrayerSet();
        int[] ids = new int[activeSet.size()];
        int index = 0;
        for (Prayer prayer : activeSet) {
            ids[index++] = prayer.getId();
        }
        return ids;
    }

    public boolean isPrayerActive(Prayer prayer) {
        return getActivePrayerSet().contains(prayer);
    }

    public int getActivePrayerCount() {
        return getActivePrayerSet().size();
    }

    public boolean hasProtectionPrayerActive() {
        for (Prayer prayer : getActivePrayers()) {
            if (prayer.isProtectionPrayer()) {
                return true;
            }
        }
        return false;
    }

    public boolean hasProtectItemPrayerActive() {
        for (Prayer prayer : getActivePrayers()) {
            return prayer.isProtectItemPrayer();
        }
        return false;
    }

    public boolean isMeleeProtecting() {
        return isActive(NormalPrayer.PROTECT_FROM_MELEE) || isActive(AncientPrayer.DEFLECT_MELEE);
    }

    public boolean isMageProtecting() {
        return isActive(NormalPrayer.PROTECT_FROM_MAGIC) || isActive(AncientPrayer.DEFLECT_MAGIC);
    }

    public boolean isRangeProtecting() {
        return isActive(NormalPrayer.PROTECT_FROM_MISSILES) || isActive(AncientPrayer.DEFLECT_MISSILES);
    }

    // Leech and Stat Adjustment System
    public void adjustStat(int stat, int percentage) {
        if (stat < 0 || stat >= STAT_VARBIT_IDS.length) return;
        int clientValue = percentage + 30;
        player.getVarsManager().sendVarBit(STAT_VARBIT_IDS[stat], clientValue);
        if (stat <= 4) updateCombatBonuses();
    }

    public void increaseLeechBonus(int bonusIndex) {
        if (leechBonuses[bonusIndex] < MAX_LEECH_BONUS) {
            leechBonuses[bonusIndex]++;
            adjustStat(bonusIndex, leechBonuses[bonusIndex]);
        }
    }

    public void decreaseLeechBonus(int bonusIndex) {
        if (leechBonuses[bonusIndex] > MIN_LEECH_BONUS) {
            leechBonuses[bonusIndex]--;
            adjustStat(bonusIndex, leechBonuses[bonusIndex]);
        }
    }

    public void updateLeechBonuses() {
        for (int i = 0; i < 5; i++) {
            adjustStat(i, leechBonuses[i]);
        }
    }

    public void resetLeechBonuses() {
        Arrays.fill(leechBonuses, 0);
        for (int i = 0; i < 5; i++) {
            adjustStat(i, 0);
        }
    }

    public boolean reachedMax(int bonusIndex) {
        return leechBonuses[bonusIndex] >= MAX_LEECH_BONUS;
    }

    public boolean reachedMin(int bonusIndex) {
        return leechBonuses[bonusIndex] <= MIN_LEECH_BONUS;
    }

    // Helper Methods
    private int getMaxPrayerPoints() {
        return player.getSkills().getLevelForXp(Skills.PRAYER) * PRAYER_POINTS_PER_LEVEL;
    }

    private void refreshPrayerPoints() {
        if (player.toggles("ONEXHITS", false)) {
            player.getPackets().sendConfig(2382, prayerPoints);
            player.getPackets().sendIComponentText(749, 6, prayerPoints / 10 + "");
        } else {
            player.getPackets().sendConfig(2382, prayerPoints);
        }
    }

    private void updateCombatBonuses() {
        //player.getCombatDefinitions().calculateLevels();
        if (WildernessControler.isAtWild(player)) {
            player.getPackets().sendPlayerOption("Attack", 1, false);
        }
    }

    private EnumSet<? extends Prayer> getActivePrayerSet() {
        return isAncientCurses() ? activeAncientPrayers : activeNormalPrayers;
    }

    private boolean[] getQuickPrayerArray() {
        return isAncientCurses() ? quickAncientPrayers : quickNormalPrayers;
    }

    private Prayer getPrayerForId(int id) {
        if (isAncientCurses()) {
            for (AncientPrayer prayer : AncientPrayer.values()) {
                if (prayer.getId() == id) return prayer;
            }
        } else {
            for (NormalPrayer prayer : NormalPrayer.values()) {
                if (prayer.getId() == id) return prayer;
            }
        }
        return null;
    }

    private void resetDrainTimer(Prayer prayer) {
        double rate = prayer.getDrainRate() * 1000;
        rate += player.getCombatDefinitions().getBonuses()[CombatDefinitions.PRAYER_BONUS] * 50;
        nextDrainTimes.put(prayer, Utils.currentTimeMillis() + (long) rate);
    }

    /**
     * Refreshes all prayer-related interfaces and visual elements
     */
    public void refresh() {
        // Refresh prayer points display
        refreshPrayerPoints();

        // Update prayer book interface
        player.getInterfaceManager().sendPrayerBook();

        // Recalculate active prayers
        recalculatePrayer();

        // Update appearance if needed
        player.getAppearence().generateAppearenceData();

        // Refresh quick prayers setting
        player.getPackets().sendGlobalConfig(181, usingQuickPrayer ? 1 : 0);
        player.getPackets().sendConfig(1584, isAncientCurses() ? 1 : 0);
        player.getPackets().sendUnlockIComponentOptionSlots(271, usingQuickPrayer ? 42 : 8, 0, 29, 0);

    }

    // Getters and Setters
    public boolean isAncientCurses() { return activeBook == PrayerBookType.ANCIENT_CURSES; }
    public int getPrayerPoints() { return prayerPoints; }
    public boolean hasPrayerPoints() { return prayerPoints > 0; }
    public boolean hasActivePrayers() { return !getActivePrayerSet().isEmpty(); }
    public boolean isQuickPrayerMode() { return usingQuickPrayer; }
    public boolean isBoostedLeech() { return boostedLeech; }
    public void setBoostedLeech(boolean boostedLeech) { this.boostedLeech = boostedLeech; }
    public void setPrayerBook(boolean ancientcurses) {
        if (player == null || player.isDead()) {
            debug("Cannot switch prayer book - player not ready");
            return;
        }

        // Get the new book type (corrected logic)
        PrayerBookType newBookType = ancientcurses ? PrayerBookType.ANCIENT_CURSES : PrayerBookType.NORMAL;

        // Don't switch if already in the requested book
        if (this.activeBook == newBookType) {
            debug("Already in requested prayer book");
            return;
        }

        closeAllPrayers();
        this.activeBook = newBookType;
        this.usingQuickPrayer = false;

        // Reset both quick prayer arrays to be safe
        Arrays.fill(quickNormalPrayers, false);
        Arrays.fill(quickAncientPrayers, false);
        // Update client interface
        refresh();

        debug("Successfully switched to " + (isAncientCurses() ? "Ancient Curses" : "Normal Prayers"));
        player.getPackets().sendGameMessage("You have switched to " +
                (isAncientCurses() ? "Ancient Curses" : "Normal Prayers") + ".");
    }
}