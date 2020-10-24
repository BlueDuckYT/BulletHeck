package net.digitalphantasia.bulletheck.event;

import net.minecraft.entity.EntityList;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.world.EnumDifficulty;
import net.minecraft.world.World;
import net.minecraftforge.event.DifficultyChangeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

import java.util.Random;
import java.util.concurrent.ExecutorService;

import static java.util.concurrent.Executors.newCachedThreadPool;

@Mod.EventBusSubscriber
public class HeckEngine  {

    //attributes
    private static float firingChance;




    //methods
    @SubscribeEvent
    public static float difficultlyChanged (DifficultyChangeEvent event)
    {

        if (event.getDifficulty() == EnumDifficulty.NORMAL || event.getDifficulty() == EnumDifficulty.HARD) {

            return firingChance = 0.5F;

        }else {

            return firingChance = 0.25F;
        }
    }



    public static void tickingEvent (TickEvent.PlayerTickEvent event) {
        final ExecutorService bulletShooters = newCachedThreadPool();

        World world = event.player.getEntityWorld();


        EntityList nearbyEntities = world.getEntitiesWithinAABB(EntityMob.class, new AxisAlignedBB(pos).grow(40, 40, 40), e -> e.getAttackTarget() instanceof EntityPlayer);

        assocs.entrySet().forEach(nearbyEntities -> {
            bulletShooters.execute(() -> {
                Random rand = new Random();
                if (rand.nextFloat() >= firingChance) {
                    switch (nearbyEntities.ID)
                    {
                        case "minecraft:zombie":

                            break; //this area is where we will put the firing algorithms.  GameDEV50 will handle the pseudocode for these.
                        case "minecraft:pig_zombie":

                            break;
                        case "minecraft:husk":

                            break;
                        case "minecraft:skeleton":

                            break;
                        case "minecraft:stray":

                            break;
                        case "minecraft:spider":

                            break;
                        case "minecraft:cave_spider":

                            break;
                        case "minecraft:enderman":

                            break;
                    }
                }
            });
        });
    }
}