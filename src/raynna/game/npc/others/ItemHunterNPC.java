package raynna.game.npc.others;

import java.util.List;

import raynna.game.Animation;
import raynna.game.World;
import raynna.game.WorldObject;
import raynna.game.WorldTile;
import raynna.game.npc.NPC;
import raynna.game.player.OwnedObjectManager;
import raynna.game.player.Player;
import raynna.game.player.RouteEvent;
import raynna.game.player.Skills;
import raynna.game.player.actions.BoxAction;
import raynna.game.player.actions.BoxAction.HunterNPC;
import raynna.util.Utils;

@SuppressWarnings("serial")
public class ItemHunterNPC extends NPC {

    private BoxAction.HunterEquipment trap;
    private HunterNPC hNPC;

    private WorldObject o;
    private int captureTicks;

    //id, tile, mapAreaNameHash, canBeAttackFromOutOfArea, spawned
    public ItemHunterNPC(HunterNPC hNPC, int id, WorldTile tile,
                         int mapAreaNameHash, boolean canBeAttackFromOutOfArea,
                         boolean spawned) {
        super(id, tile, mapAreaNameHash, canBeAttackFromOutOfArea, spawned);
        this.hNPC = hNPC;
        this.trap = hNPC.getEquipment();
    }

    boolean success;

    @Override
    public void processNPC() {
        super.processNPC();
        if (captureTicks > 0) {
            captureTicks++;
            if (captureTicks == 2) {
                if (hNPC.equals(HunterNPC.CRIMSON_SWIFT)
                        || hNPC.equals(HunterNPC.GOLDEN_WARBLER)
                        || hNPC.equals(HunterNPC.COPPER_LONGTAIL)
                        || hNPC.equals(HunterNPC.CERULEAN_TWITCH)
                        || hNPC.equals(HunterNPC.TROPICAL_WAGTAIL)
                        || hNPC.equals(HunterNPC.WIMPY_BIRD))
                    addWalkSteps(o.getX(), o.getY(), 1, false);
            } else if (captureTicks == 4) {
                if (hNPC.equals(HunterNPC.CRIMSON_SWIFT)
                        || hNPC.equals(HunterNPC.GOLDEN_WARBLER)
                        || hNPC.equals(HunterNPC.COPPER_LONGTAIL)
                        || hNPC.equals(HunterNPC.CERULEAN_TWITCH)
                        || hNPC.equals(HunterNPC.TROPICAL_WAGTAIL)
                        || hNPC.equals(HunterNPC.WIMPY_BIRD)) {
                    this.setNextFaceWorldTile(new WorldTile(this.getX(), this.getY() - 1, this.getPlane()));
                } else
                    faceObject(o);

                Animation anim = hNPC.getCatchAnim();
                if (anim != null)
                    animate(anim);
            } else if (captureTicks == 6) {// up to five
                if (!OwnedObjectManager
                        .convertIntoObject(
                                o,
                                new WorldObject(hNPC.getSuccessfulTransformObjectId(), o.getType(),
                                        o.getRotation(), new WorldTile(o
                                        .getTileHash())),
                                player -> {
                                    if (player == null || isDead())
                                        return false;
                                    return isSuccessfulCatch(player);
                                })) {
                    Animation anim = hNPC.getFailCatchAnim();
                    if (anim != null)
                        animate(anim);
                    OwnedObjectManager.convertIntoObject(o, new WorldObject(
                            hNPC.getFailedTransformObjectId(), o.getType(), o.getRotation(),
                            new WorldTile(o.getTileHash())), null);
                } else
                    setRespawnTask();
            } else if (captureTicks == 8) {
                setCantInteract(false);
                setRandomWalk(getDefinitions().walkMask);
                System.out.println("reset walk");
            } else if (captureTicks == 10) {
                o = null;
                captureTicks = 0;
            }
            return;
        }

        if (o != null || hasFinished())
            return;
        List<WorldObject> objects = World.getRegion(getRegionId())
                .getSpawnedObjects();
        if (objects == null)
            return;
        for (final WorldObject o : objects) {
            if (o.getId() != trap.getObjectId() || !withinDistance(o, 4)
                    || Utils.random(50) != 0)
                continue;
            this.o = o;
            setCantInteract(true);
            setRandomWalk(0);
            resetWalkSteps();
            setNextFaceWorldTile(o);
            this.setRouteEvent(new RouteEvent(o, new Runnable() {


                @Override
                public void run() {
                    captureTicks = 1;
                }
            }, true));
            break;
        }
    }

    public boolean isSuccessfulCatch(Player player) {
        int currentLevel = player.getSkills()
                .getLevel(Skills.HUNTER), lureLevel = hNPC
                .getLevel();
        double ratio = ((double) (trap
                .getBaseLevel() + 20) / lureLevel)
                * currentLevel;
        return !(currentLevel < lureLevel || ratio < Utils
                .random(100));
    }
}