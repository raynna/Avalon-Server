package raynna.game.item.meta;

public class RuneEssencePouchMetaData implements ItemMetadata {

    private int essenceAmount;
    private int maxCapacity;
    private boolean degraded;

    public RuneEssencePouchMetaData(int maxCapacity) {
        this.maxCapacity = maxCapacity;
    }

    @Override
    public Integer getType() {
        return MetaDataType.RUNE_ESSENCE_POUCH.getId();
    }

    @Override
    public Object getValue() {
        return essenceAmount;
    }

    @Override
    public int getMaxValue() {
        return maxCapacity;
    }

    @Override
    public int getMaxEntries() {
        return 1;
    }

    @Override
    public void setValue(Object value) {
        if (value instanceof Integer v) {
            essenceAmount = Math.min(v, maxCapacity);
        }
    }

    @Override
    public Integer getLastDisplayedPercentage() {
        return 0;
    }

    @Override
    public void setLastDisplayedPercentage(Object value) {

    }

    public int getEssenceAmount() {
        return essenceAmount;
    }

    public void addEssence(int amount) {
        essenceAmount = Math.min(maxCapacity, essenceAmount + amount);
    }

    public void removeEssence(int amount) {
        essenceAmount = Math.max(0, essenceAmount - amount);
    }

    public boolean isDegraded() {
        return degraded;
    }

    public void repair() {
        degraded = false;
    }

    public void degrade() {
        degraded = true;
    }

    @Override
    public boolean isStackableWith(ItemMetadata other) {
        return false;
    }

    @Override
    public ItemMetadata deepCopy() {
        RuneEssencePouchMetaData copy = new RuneEssencePouchMetaData(maxCapacity);
        copy.essenceAmount = essenceAmount;
        copy.degraded = degraded;
        return copy;
    }

    @Override
    public String getDisplaySuffix() {
        return "(Essence: " + essenceAmount + "/" + maxCapacity + ")";
    }

    @Override
    public int getBonusOverride(CombatBonusType type, int baseBonus) {
        return baseBonus;
    }
}