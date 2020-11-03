package net.digitalphantasia.bulletheck.entity;

import net.digitalphantasia.bulletheck.BulletHeck;
import net.digitalphantasia.bulletheck.BulletHeckDifficulty;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityFireball;
import net.minecraft.entity.projectile.ProjectileHelper;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.player.PlayerInteractEvent.RightClickItem;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@EventBusSubscriber
public class EntityHeckOrb extends EntityFireball
{
    @SubscribeEvent
    public static void rightclickStickToSpawnForTesting(RightClickItem event)
    {
        World world = event.getWorld();

        if(!world.isRemote && event.getItemStack().getItem() == Items.STICK)
        {
            EntityPlayer player = event.getEntityPlayer();
            int cubeSize = 10;

            //spawns a cube of heck orbs of size cubeSize
            for(int x = 0; x < cubeSize; x++)
            {
                for(int y = 0; y < cubeSize; y++)
                {
                    for(int z = 0; z < cubeSize; z++)
                    {
                        EntityHeckOrb orb = new EntityHeckOrb(world, player, BulletHeckDifficulty.EASY);

                        orb.setPosition(player.posX + 3 + x, player.posY + y, player.posZ + z);
                        world.spawnEntity(orb);
                    }
                }

            }
        }
    }

    public static final DamageSource DAMAGE_SOURCE = new DamageSource(BulletHeck.MOD_ID + ":heck_orb").setDamageBypassesArmor();
    private static final DataParameter<Integer> DIFFICULTY = EntityDataManager.<Integer>createKey(EntityHeckOrb.class, DataSerializers.VARINT);
    private static final int TICKS_TO_LIVE = 200;
    private int ticksInAir = 0;
    private int ticksExisting = 0;

    public EntityHeckOrb(World world)
    {
        super(world);
        setSize(0.4F, 0.4F);
    }

    public EntityHeckOrb(World world, EntityLivingBase shooter, BulletHeckDifficulty difficulty)
    {
        super(world, shooter, 0.0D, 0.0D, 0.0D);

        accelerationX = 0.0D;
        accelerationY = 0.0D;
        accelerationZ = 0.0D;
        setSize(0.4F, 0.4F);
        dataManager.set(DIFFICULTY, difficulty.ordinal());
    }

    @Override
    protected void entityInit()
    {
        super.entityInit();
        dataManager.register(DIFFICULTY, BulletHeckDifficulty.NORMAL.ordinal());
    }

    @Override
    public void onUpdate()
    {
        if(++ticksExisting > TICKS_TO_LIVE)
            setDead();
        else if((shootingEntity == null || !shootingEntity.isDead) && world.isBlockLoaded(new BlockPos(this)))
        {
            RayTraceResult result = ProjectileHelper.forwardsRaycast(this, true, ++ticksInAir >= 25, shootingEntity);

            if(result != null)
                onImpact(result);

            posX += motionX;
            posY += motionY;
            posZ += motionZ;
            ProjectileHelper.rotateTowardsMovement(this, 0.2F);
            setPosition(posX, posY, posZ);
        }

        onEntityUpdate();
    }

    @Override
    protected float getMotionFactor()
    {
        return 1.0F;
    }

    @Override
    protected void onImpact(RayTraceResult result)
    {
        if(result.entityHit instanceof EntityPlayer && !result.entityHit.world.isRemote)
            result.entityHit.attackEntityFrom(DAMAGE_SOURCE, getDifficulty().getDamage());

        setDead();
    }

    @Override
    public boolean attackEntityFrom(DamageSource source, float amount)
    {
        if(source.getTrueSource() instanceof EntityPlayer)
        {
            EntityPlayer player = (EntityPlayer)source.getTrueSource();

            ItemStack stack = player.getHeldItemMainhand();

            if(stack.getItem() instanceof ItemSword)
            {
                double reachDist = player.getEntityAttribute(EntityPlayer.REACH_DISTANCE).getAttributeValue();
                Vec3d origin = new Vec3d(player.getPosition());
                AxisAlignedBB areaOfEffect;

                origin.add(0, player.getEyeHeight(), 0);
                areaOfEffect = new AxisAlignedBB(origin, origin);
                areaOfEffect = areaOfEffect.grow(reachDist);
                player.world.getEntitiesWithinAABB(EntityHeckOrb.class, areaOfEffect, e -> {
                    Vec3d ePos = e.getPositionVector();
                    Vec3d originToE = new Vec3d(origin.x - ePos.x, origin.y - ePos.y, origin.z - ePos.z);
                    boolean isInFrontOfPlayer = originToE.dotProduct(player.getLookVec()) <= 0;
                    boolean isInReach = MathHelper.sqrt(originToE.x * originToE.x + originToE.y * originToE.y + originToE.z * originToE.z) <= reachDist;

                    return isInFrontOfPlayer && isInReach;
                }).forEach(e -> e.setDead());
                return true;
            }
        }

        return false;
    }

    @Override
    protected boolean isFireballFiery()
    {
        return false;
    }

    @Override
    public boolean isInWater() //disables slowing down in water
    {
        return false;
    }

    @Override
    public boolean isInLava()
    {
        return false;
    }

    public void setDifficulty(BulletHeckDifficulty difficulty)
    {
        dataManager.set(DIFFICULTY, difficulty.ordinal());
    }

    public BulletHeckDifficulty getDifficulty()
    {
        return BulletHeckDifficulty.values()[dataManager.get(DIFFICULTY)];
    }
}
