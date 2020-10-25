package net.digitalphantasia.bulletheck;

public enum BulletHeckDifficulty
{
    EASY(5), NORMAL(5), HARD(10);

    private final int damage;

    private BulletHeckDifficulty(int damage)
    {
        this.damage = damage;
    }

    public int getDamage()
    {
        return damage;
    }
}
