/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ancienttiles;

import ancienttiles.GameManager.InputManager;
import ancienttiles.tiles.MainTileFactory;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.io.Serializable;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.event.MouseInputAdapter;

/**
 *
 * @author krr428
 */
public class HumanIntelligence implements Actor, Serializable
{
    
    private Tile repr = null;
    private transient AbstractGameManager manager = null;
    private MovementManager movement = null;
    private InventoryManager inventory = null;
    private ExternalInputManager externInput = null;
    private boolean deathState = false;
    
    public void resetDeathState()
    {
        deathState = false;
    }
    
    public boolean getDeathState()
    {
        return deathState;
    }
    
    public void deathSequence(Tile cause, String reason)
    {        
        deathState = true;   
        getManager().getAIManager().killTimedObjects();
        getManager().getMapManager().fireMapChanged();
        javax.swing.JOptionPane.showMessageDialog(getManager().getViewManager().getView(), reason);
        
        ((InputManager)(getManager().getInputManager())).enterEndState(true);         
    }
    
    public InventoryManager getInventoryMgr()
    {
        return inventory;
    }
    
    public MovementManager getMovementMgr()
    {
        return movement;
    }
    
    public ExternalInputManager getExternalInputMgr()
    {
        return externInput;
    }
    
    public HumanIntelligence(int x, int y)
    {
        this.repr = MainTileFactory.getInstance().getTile("human");
        
        this.inventory = new DefaultInventoryManager();
        this.movement = new DefaultMovementManager(x, y);
        this.externInput = new DefaultExternalInputManager();
    }
    
    public void setManager(AbstractGameManager mgr)
    {
        this.manager = mgr;
    }
    
    public AbstractGameManager getManager()
    {
        if (manager == null)
        {
            throw new UnsupportedOperationException("Game manager is null, make sure to set the game manager!");
        }
        return manager;
    }

    //<editor-fold defaultstate="collapsed" desc="Innerclass interfaces">
    public interface MovementManager
    {
        
        boolean requestMove(int x, int y);
        
        boolean requestMove(int x, int y, LayerStack map);
        
        boolean requestMove(int x, int y, String mapName);
        
        int getX();
        
        int getY();
    }
    
    public interface InventoryManager
    {
        
        Map<String, Object> getStats();
        
        Set<Tile> getInventory();
        
        boolean contains(String type);
        
        Tile getInstanceOf(String type);
        
        int getNumberOf(String type);
        
        Set<String> getContainedTypes();
        
        void addItem(Tile item);
        
        void removeItem(Tile item);
        
        void clearInventory();
    }
    
    public abstract class ExternalInputManager extends MouseInputAdapter
    {
        
        public abstract Map<Integer, Action> getKeyPressHandlers();
    }
    //</editor-fold>    

    //<editor-fold defaultstate="collapsed" desc="Inner class Implementations">
    public class DefaultInventoryManager implements InventoryManager, Serializable
    {
        
        private Map<String, Set<Tile>> inventory = null;
        private Map<String, Object> statistics = null;
        
        public DefaultInventoryManager()
        {
            inventory = new HashMap<String, Set<Tile>>();
            statistics = new TreeMap<String, Object>();
        }
        
        @Override
        public Set<String> getContainedTypes()
        {
            return inventory.keySet();
        }

        @Override
        public void clearInventory()
        {
            inventory = new HashMap<String, Set<Tile>>();
        }
        
        @Override
        public int getNumberOf(String type)
        {
            if (this.contains(type))
            {
                return inventory.get(type).size();
            } else
            {
                return 0;
            }
        }
        
        @Override
        public Map<String, Object> getStats()
        {
            return statistics;
        }
        
        @Override
        public boolean contains(String type)
        {
            if (inventory.containsKey(type))
            {
                return !inventory.get(type).isEmpty();
            } else
            {
                return false;
            }
        }
        
        @Override
        public Tile getInstanceOf(String type)
        {
            if (this.contains(type))
            {
                return inventory.get(type).iterator().next();
            } else
            {
                return null;
            }
        }
        
        @Override
        public Set<Tile> getInventory()
        {
            //Unions all sets of tiles in the tree.
            Collection<Set<Tile>> coll = inventory.values();
            Set<Tile> union_inventory = new HashSet<Tile>();
            for (Set<Tile> subset : coll)
            {
                for (Tile t: subset)
                {
                    union_inventory.add(t);
                }
            }
            
            return union_inventory;
        }
        
        @Override
        public void addItem(Tile item)
        {
            if (inventory.containsKey(item.getPrimaryAttribute()))
            {
                inventory.get(item.getPrimaryAttribute()).add(item);
            } else
            {
                inventory.put(item.getPrimaryAttribute(), new HashSet<Tile>());
                addItem(item); //Recurse around and add it.
            }
        }
        
        @Override
        public void removeItem(Tile item)
        {
            if (inventory.containsKey(item.getPrimaryAttribute()))
            {
                inventory.get(item.getPrimaryAttribute()).remove(item);
                if (inventory.get(item.getPrimaryAttribute()).isEmpty())
                {
                    //If this was the last item of this type, just remove the
                    // entire set that was holding items of this type.
                    inventory.remove(item.getPrimaryAttribute());
                }
            }
        }
    }
    
    public class DefaultMovementManager implements MovementManager, Serializable
    {
        
        private int loc_x;
        private int loc_y;
        
        public DefaultMovementManager(int loc_x, int loc_y)
        {
            this.loc_x = loc_x;
            this.loc_y = loc_y;
        }
        
        @Override
        public boolean requestMove(int x, int y)
        {
            return requestMove(x, y, getManager().getMapManager().getCurrent());
        }
        
        @Override
        public boolean requestMove(int x, int y, String mapName)
        {
            return requestMove(x, y, getManager().getMapManager().getMap(mapName)); // Look up the map.
        }
        
        protected boolean queryMoveTo(int x, int y, LayerStack map)
        {
            if (x >= map.getWidth() || y >= map.getHeight() || x < 0 || y < 0)
            {
                return false;
            }
            
            for (String layerName : map.getLayers())
            {
                Layer layer = map.getLayer(layerName);
                
                if (layer.getTileAt(x, y) != null
                        && layer.getTileAt(x, y) instanceof RestrictedMovementTile)
                {
                    
                    RestrictedMovementTile rmt = (RestrictedMovementTile) layer.getTileAt(x, y);
                    if (!rmt.allowMoveFrom(HumanIntelligence.this))
                    {
                        return false;
                    }
                    
                }
            }
            
            return true;
        }      
        
        
        private void notifyOnMovementTiles()
        {
            //for (String layerName : getManager().getCurrentMap().getLayers())
            //{
                //Tile t = getManager().getCurrentMap().getLayer(layerName).getTileAt(getX(), getY());
            
            boolean hasFloor = false;
            for (Tile t: getManager().getMapManager().getCurTiles(getX(), getY()))
            {
                if (t != null)
                {
                    if (t instanceof OnMovementTile)
                    {
                        OnMovementTile mt = (OnMovementTile) t;
                        mt.movedOn(HumanIntelligence.this);
                    }
                    
                    if (t.hasAttribute("floor"))
                    {
                        hasFloor = true;
                    }
                    
                    if (t.hasAttribute("killtile"))
                    {
                        
                    }
                }
            }
            
            if (!hasFloor)
            {
                HumanIntelligence.this.deathSequence(null, "Oh no! You've fallen off the edge to the infinite abyss!");
            }
        }
        
        private void getUAObjs()
        {
            for (String layerName : getManager().getMapManager().getCurrent().getLayers())
            {
                Tile t = getManager().getMapManager().getCurrent().getLayer(layerName).getTileAt(getX(), getY());
                if (t != null)
                {
                    if (t.hasAttribute("uaobj"))
                    {
                        getInventoryMgr().addItem(t);
                        getManager().getMapManager().getCurrent().getLayer(layerName).setTileAt(getX(), getY(), null);
                    }
                }
            }
        }
        
        @Override
        public boolean requestMove(int x, int y, LayerStack map)
        {
            boolean allowed = queryMoveTo(x, y, map);
            if (allowed)
            {
                setX(x);
                setY(y);
                if (map != getManager().getMapManager().getCurrent())
                {
                    getManager().getMapManager().setCurrentMap(map);
                }
                getUAObjs();
               
            }     
            
            notifyOnMovementTiles();
            getManager().getMapManager().fireMapChanged();
            
            
            return allowed;
        }
        
        private void setX(int x)
        {
            this.loc_x = x;
        }
        
        private void setY(int y)
        {
            this.loc_y = y;
        }
        
        @Override
        public int getX()
        {
            return this.loc_x;
        }
        
        @Override
        public int getY()
        {
            return this.loc_y;
        }
    }
    
    public class DefaultExternalInputManager extends ExternalInputManager implements Serializable
    {

        //<editor-fold defaultstate="collapsed" desc="Arrow key action handlers">
        private class LeftArrowAction extends AbstractAction
        {
            
            @Override
            public void actionPerformed(ActionEvent e)
            {
                MovementManager mm = getMovementMgr();
                mm.requestMove(mm.getX() - 1, mm.getY());
            }
        }
        
        private class RightArrowAction extends AbstractAction
        {
            
            @Override
            public void actionPerformed(ActionEvent e)
            {
                MovementManager mm = getMovementMgr();
                mm.requestMove(mm.getX() + 1, mm.getY());
            }
        }
        
        private class UpArrowAction extends AbstractAction
        {
            
            @Override
            public void actionPerformed(ActionEvent e)
            {
                MovementManager mm = getMovementMgr();
                mm.requestMove(mm.getX(), mm.getY() - 1);
            }
        }
        
        private class DownArrowAction extends AbstractAction
        {
            
            @Override
            public void actionPerformed(ActionEvent e)
            {
                MovementManager mm = getMovementMgr();
                mm.requestMove(mm.getX(), mm.getY() + 1);
            }
        }
        
        private class SpaceAction extends AbstractAction
        {

            @Override
            public void actionPerformed(ActionEvent e)
            {
                MovementManager mm = getMovementMgr();
                mm.requestMove(mm.getX(), mm.getY());
            }
            
        }
        //</editor-fold>

        @Override
        public Map<Integer, Action> getKeyPressHandlers()
        {
            Map<Integer, Action> handlers = new HashMap<Integer, Action>();
            handlers.put(KeyEvent.VK_LEFT, new LeftArrowAction());
            handlers.put(KeyEvent.VK_RIGHT, new RightArrowAction());
            handlers.put(KeyEvent.VK_UP, new UpArrowAction());
            handlers.put(KeyEvent.VK_DOWN, new DownArrowAction());
            handlers.put(KeyEvent.VK_SPACE, new SpaceAction());
            
            return handlers;
        }
    }
    //</editor-fold>

    @Override
    public int getX()
    {
        return getMovementMgr().getX();
    }
    
    @Override
    public int getY()
    {
        return getMovementMgr().getY();
    }
    
    @Override
    public Tile getTileRepr()
    {
        return repr;
    }
}
