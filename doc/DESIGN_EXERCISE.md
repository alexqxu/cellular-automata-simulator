Netids: mc608, axo, aqx

Refactored Alex Xu's powerUp class into an abstract superclass.

```
public abstract class PowerUp{
    public static final int SPEED = 100;
    public static final int SIZE = 25;
    private String filename;
    private ImageView image;

    public PowerUp(int x, int y);

    public void move(double elapsedTime);
    public void delete();
    public void startDrop();

    /*
    getters and setters
    */

    public void activate();
}
```

The GamePlay class would need to be refactored by removing the if
statement currently used for the powerup type, and replaced with
powerUp.activate(). Each subclass of PowerUp would have its own
activate() method that would have its own effect (e.g. the health
powerup would increase the number of lives by 1.)

The levels do not need to be refactored with inheritance to allow
for a new level to be added without modifying the existing 
GamePlay code, as all that needs to be added is a new text file.

Right now, the only bricks in game are bricks with different
amounts of health, which does not require inheritance. However,
if more bricks were to be added, this Brick superclass would require
the following:

```
private double xVelocity;
private double yVelocity;

public void update(double elapsedTime);
```