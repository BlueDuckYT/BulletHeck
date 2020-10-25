package net.digitalphantasia.bulletheck.entity;

import net.digitalphantasia.bulletheck.BulletHeck;
import net.digitalphantasia.bulletheck.BulletHeckDifficulty;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityFireball;
import net.minecraft.entity.projectile.ProjectileHelper;
import net.minecraft.init.Items;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
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
            EntityHeckOrb orb = new EntityHeckOrb(world, player, BulletHeckDifficulty.EASY);

            orb.setPosition(player.posX + 3, player.posY, player.posZ);
            world.spawnEntity(orb);
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
            setDead();
            return true;
        }
        else return false;
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
