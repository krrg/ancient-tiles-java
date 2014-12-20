/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ancienttiles.tiles;

import ancienttiles.HumanIntelligence;
import ancienttiles.LayerStack;
import ancienttiles.OnMovementTile;
import ancienttiles.RestrictedMovementTile;
import ancienttiles.Tile;
import ancienttiles.tiles.ai.ArtificialIntelligence;
import java.awt.Image;
import java.awt.Point;
import java.net.URL;
import java.util.Set;

/**
 *
 * @author krr428
 */
public class TileEnterChildSquare extends Tile implements OnMovementTile, RestrictedMovementTile
{

    protected LayerStack parent = null;
    protected Set<Tile> parentInventory = null;
    protected Point previousHILoc = null;

    public TileEnterChildSquare(Image i)
    {
        super(i);
    }

    @Override
    public boolean allowMoveFrom(HumanIntelligence hi)
    {
        if (parent == null)
        {
            previousHILoc = new Point(hi.getX(), hi.getY());
        }        
        return true;
    }

    @Override
    public boolean allowMoveFrom(ArtificialIntelligence ai)
    {
        return false;
    }

    
    
    @Override
    public void movedOn(ArtificialIntelligence ai)
    {
    }

    @Override
    public void movedOn(HumanIntelligence hi)
    {
        //Figure out if they just moved back on top of us.
        if (parent == null)
        {
            handleSteppedOn(hi);
        }
        else
        {
            handleTraveledBack(hi);
        }

        //Whenever the map is changed, if it is a change back to the parent map,
        // add back the old inventory.
//        hi.getManager().getMapManager().addMapChangedListener(new MapChangedListener() {
//
//            @Override
//            public void mapChanged()
//            {
//                if (hi.getManager().getCurrentMap() == parent)
//                {
//                    //IE, if they changed the map back to the parent.
//                    System.out.println("The map was changed back to the parent.");                    
//                }
//            }
//        });

    }

    public void handleTraveledBack(HumanIntelligence hi)
    {
        if (hi.getDeathState())
        {
            hi.getMovementMgr().requestMove(previousHILoc.x, previousHILoc.y);
            hi.resetDeathState();
        }
        
        hi.getInventoryMgr().clearInventory();

        for (Tile item : parentInventory)
        {
            hi.getInventoryMgr().addItem(item);
        }

        hi.getManager().getAIManager().initTimedObjects();

        this.parent = null;
        this.parentInventory = null;
        this.getAttributes().remove("HI MainStart");

        System.out.println("Welcome back.");

    }

    public void handleSteppedOn(HumanIntelligence hi)
    {
        if (!this.getMetadata().containsKey("%exit"))
        {
            System.out.println("This tile has no map specified to it.");
            return;
        }

        String filename = this.getMetadata().get("%exit");

        URL mapURL = null;
        try
        {
            mapURL = MainTileFactory.class.getResource(filename);
        }
        catch (NullPointerException ne)
        {
            System.out.println("Could not load map: '" + filename + "'.");
            return;
        }




        parent = hi.getManager().getCurrentMap();

        Point start = parent.getPreferredHIStart();

        //Swap the start tiles.
        //Tile startTile = parent.getLayer("HumInt").getTileAt(start.x, start.y);
        parent.getLayer("HumInt").setTileAt(start.x, start.y, null);
        //parent.getLayer("HumInt").setTileAt(hi.getX(), hi.getY(), startTile);
        this.addAttribute("HI MainStart");
        //Save the inventory.
        parentInventory = hi.getInventoryMgr().getInventory();

        //Load the new map, set the parent to the old map.
        hi.getManager().getMapManager().loadMap(mapURL);
        hi.getManager().getCurrentMap().setParent(parent);
    }
}
