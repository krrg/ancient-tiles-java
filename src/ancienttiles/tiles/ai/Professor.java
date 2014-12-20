/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ancienttiles.tiles.ai;

import ancienttiles.AbstractGameManager;
import ancienttiles.HumanIntelligence;
import ancienttiles.OnMovementTile;
import ancienttiles.RestrictedMovementTile;
import ancienttiles.Tile;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import javax.swing.Timer;

/**
 *
 * @author krr428
 */
public class Professor extends MovingAI implements RestrictedMovementTile
{

    private static List<String> randomMessages = null;
    private static Random rand = null;
    private int moveX;
    private int moveY;

    public Professor(Image i)
    {
        super(i);
    }

    public Professor(Image i, int x, int y, String layer)
    {
        super(i, x, y, layer);
    }

    @Override
    public void init(AbstractGameManager gm)
    {
        initRandMessages();
        super.init(gm); //To change body of generated methods, choose Tools | Templates.        
    }

    private void initRandMessages()
    {
        if (randomMessages == null)
        {
            randomMessages = Arrays.asList("Hello, Professor!", "Fascinating, these Mayans..", "Professor, have seen these codices?",
                    "This will get published for sure!", "Dr. Smith, come look at these symbols...", "What will these Mayans think of next?",
                    "My goodness, look at the texture on this!", "Ah...an excellent specimen!", "Have you seen the local flora?",
                    "Professor, have you seen my lunch?", "I could spend all day here!", "Oh dear...Professor, have you seen my spectacles?",
                    "Just wait 'til I get this back to the lab!", "Dr. Jones, what do you make of this?", "But how interesting!", "An excellent example of Mesoamerica!",
                    "Take a look at this drawing!", "I think this proves the Heinman-Baird Theory!", "Ah, just the picture I needed for my next paper!",
                    "Oh dear, I seem to have misplaced my briefcase!", "Did Dr. Smith come on this outing?", "Ahem, I think I may have discovered something!",
                    "Judy, come look at these glyphs!", "Oh dear, I think my research assistant has wandered off again...", "I'll need pictures of this one...");
        }
        if (rand == null)
        {
            rand = new SecureRandom();
        }
    }

    protected static String getRandMessage()
    {
        int index = rand.nextInt(randomMessages.size());
        return randomMessages.get(index);
    }

    @Override
    protected int getMoveX()
    {
        return moveX;
    }

    @Override
    protected int getMoveY()
    {
        return moveY;
    }

    @Override
    protected void turn()
    {
        moveX = 0;
        moveY = 0;
        //Just choose a random direction.
        if (rand.nextBoolean())
        {
            moveX = getRandMove();
        }
        else
        {
            moveY = getRandMove();
        }

        if (rand.nextBoolean() && rand.nextBoolean())
        {
            showRandomMessage();
        }
    }

    protected int getRandMove()
    {
        int A = rand.nextInt(3);
        return A - 1;
    }

    @Override
    public void move()
    {
        if (rand.nextInt(100) > 70)
        {
            internalTimer.setDelay(getRandDelay(2500));
        }
        else
        {
            internalTimer.setDelay(getRandDelay(175));
            super.move();
        }

        double G = Math.abs(rand.nextGaussian());
        if (G > 0.75)
        {
            //Then change direction.
            turn();
            super.move();
        }
        
        for (Tile t : gameManager.getMapManager().getCurTiles(xloc, yloc))
        {
            if (t instanceof OnMovementTile)
            {
                ((OnMovementTile) t).movedOn(this);
            }
        }
    }

    protected int getRandDelay(int mean)
    {
        double G = rand.nextGaussian();
        G = (mean * G) + mean;
        return (int) Math.abs(G);
    }

    @Override
    protected boolean canMove(int x, int y)
    {
        if (x < 0 || y < 0 || x >= gameManager.getCurrentMap().getWidth()
                || y >= gameManager.getCurrentMap().getHeight())
        {
            return false;
        }

        boolean hasFloor = false;
        for (Tile t : gameManager.getMapManager().getCurTiles(x, y))
        {
            if (t.hasAttribute("wall"))
            {
                return false;
            }
            else if (t.hasAttribute("floor"))
            {
                hasFloor = true;
            }
            else if (t instanceof RestrictedMovementTile)
            {
                boolean allowed = ((RestrictedMovementTile) t).allowMoveFrom(this);
                if (!allowed)
                {
                    return false;
                }
            }
        }

        Tile t = gameManager.getCurrentMap().getLayer(getLayerName()).getTileAt(x, y);
        if (t != null && t.hasAttribute("ai"))
        {
            return false;
        }

        return hasFloor;
    }

    @Override
    public void movedOn(HumanIntelligence hi)
    {
        move();
    }
    private boolean messageShowing = false;

    protected void showRandomMessage()
    {
        if (!messageShowing)
        {
            gameManager.getViewManager().getView().displayMessageTooltip(getRandMessage(), 1150, xloc, yloc);
            messageShowing = true;
            Timer msgTimer = new Timer(1150, new ActionListener()
            {
                @Override
                public void actionPerformed(ActionEvent e)
                {
                    messageShowing = false;
                }
            });
            msgTimer.setRepeats(false);
            msgTimer.start();
        }


    }

    @Override
    public boolean allowMoveFrom(HumanIntelligence hi)
    {
        showRandomMessage();
        return false;
    }

    @Override
    public boolean allowMoveFrom(ArtificialIntelligence ai)
    {
        showRandomMessage();
        return false;
    }
}
