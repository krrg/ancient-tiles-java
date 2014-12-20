/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ancienttiles;

import ancienttiles.griddisplay.TileDisplay;
import ancienttiles.tiles.MainTileFactory;
import ancienttiles.tiles.ai.TimedObject;
import ancienttiles.tiles.mapgen.XMLMapGenerator;
import java.awt.event.ActionEvent;
import java.io.Serializable;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ActionMap;
import javax.swing.InputMap;
import javax.swing.KeyStroke;
import javax.swing.event.MouseInputAdapter;

/**
 *
 * @author krr428
 */
public class GameManager extends AbstractGameManager implements Serializable
{
    //Finite states:
    //Pre-Start State
    //Game State
    //Post-Game State - Death
    //Post-Game State - Won

    protected GameManager(TileDisplay td)
    {
        this.setMapManager(new MapManager(new HashMap<String, LayerStack>()));
        this.setAIManager(new AIManager());
        this.setHIManager(new HumIntManager());        
        this.setInputManager(new InputManager());        
        this.setTimingManager(null);
        this.setViewManager(new ViewManager(td));
    }

    public GameManager(HumanIntelligence hi, TileDisplay td)
    {
        this(td);
        this.getHIManager().setHI(hi);
    }

    public GameManager(HumanIntelligence hi, Map<String, LayerStack> additionalMaps, TileDisplay td)
    {
        this(hi, td);
        this.setMapManager(new MapManager(additionalMaps));
    }

    public class AIManager extends AbstractAIManager
    {
        public AIManager()
        {
            
        }
        
        @Override
        public void killTimedObjects()
        {
            for (TimedObject to: getMapManager().getCurrent().getTimedObjects())
            {
                to.kill();
            }
        }
        
        @Override
        public void initTimedObjects()
        {
            for (TimedObject to: getMapManager().getCurrent().getTimedObjects())
            {
                to.init(GameManager.this);
            }
        }
    }
    
    public class InputManager extends AbstractInputManager
    {

        public InputManager()
        {            
//            getMapManager().initTimedObjects();
        }

        public InputManager(InputMap im, ActionMap am)
        {
            super(im, am);
        }

        public InputManager(AbstractInputManager formerManager)
        {
            super(formerManager);
        }

        @Override
        public void init()
        {
            enterStartState();
        }

        public void enterGameState()
        {
            bindKeys();
            getAIManager().initTimedObjects();
        }

        public void leaveGameState()
        {
            inputMap.clear();
            actionMap.clear();
        }

        public final void enterStartState()
        {
            getViewManager().getView().addPostOverlay("StartState",
                    MainTileFactory.getInstance().getImage("transparent_blue_overlay20p.png"));

            //Map each user control key.

            inputMap.clear();
            actionMap.clear();
            final Map<Integer, Action> humanActions =
                    getHIManager().getHI().getExternalInputMgr().getKeyPressHandlers();
            for (final int key : humanActions.keySet())
            {
                inputMap.put(KeyStroke.getKeyStroke(key, 0), "StartState" + key);
                actionMap.put("StartState" + key, new AbstractAction()
                {
                    @Override
                    public void actionPerformed(ActionEvent e)
                    {
                        exitStartState();
                        enterGameState();
                        humanActions.get(key).actionPerformed(e);
                    }
                });
            }



        }

        public void exitStartState()
        {
            getViewManager().getView().removePostOverlay("StartState");

        }

        public void enterEndState(boolean died)
        {
            //Reload the map.
            //Put the HI where it needs to go.            
            //getMapManager().resetCurrent();
            getMapManager().loadParent();
        }

        public void bindKeys()
        {
            Map<Integer, Action> humanActions =
                    getHIManager().getHI().getExternalInputMgr().getKeyPressHandlers();
            for (int key : humanActions.keySet())
            {
                inputMap.put(KeyStroke.getKeyStroke(key, 0), "HumInt" + key);
                actionMap.put("HumInt" + key, humanActions.get(key));
            }

        }

        @Override
        public MouseInputAdapter getMouseInputAdapter()
        {
            return getHIManager().getHI().getExternalInputMgr();
        }
    }

    private class MapManager extends AbstractMapManager
    {

        private LayerStack currentMap = null;
        private Map<String, LayerStack> mapStore = null;

        public MapManager(Map<String, LayerStack> maps)
        {
            mapStore = new HashMap<String, LayerStack>();
            mapStore.putAll(maps);
            if (mapStore.size() > 0)
            {
                setCurrentMap(mapStore.values().iterator().next());
            }
        }

        public MapManager(Map<String, LayerStack> maps, String defaultMap)
        {
            this(maps);
            setCurrentMap(mapStore.get(defaultMap));
        }

        public MapManager(String name, LayerStack singleMap)
        {
            this(new HashMap<String, LayerStack>());
            mapStore.put(name, singleMap);
            setCurrentMap(singleMap);
        }

        @Override
        public LayerStack getCurrent()
        {
            return currentMap;
        }

        @Override
        public LayerStack getMap(String name)
        {
            return mapStore.get(name);
        }

        @Override
        public void addMap(String name, LayerStack map)
        {
            mapStore.put(name, map);
        }

        @Override
        public void setCurrentMap(String name)
        {
            setCurrentMap(getMap(name));
        }

        @Override
        public final void setCurrentMap(LayerStack ls)
        {
            this.currentMap = ls;
        }

        @Override
        public void resetCurrent()
        {
            //We really just need to reload the map.
            loadMap(getCurrent().getOrigin(), getCurrent().getWidth(), getCurrent().getHeight());
            getHIManager().getHI().getInventoryMgr().clearInventory();
        }

        @Override
        public void loadMap(URL location, int width, int height)
        {
            System.out.println("Attempting to load: " + location.toString());
            //Does not reset the map, but rather loads it, then clears the inventory.
            for (TimedObject to : getCurrent().getTimedObjects())
            {
                to.kill();
            }
            //ZTFloatMap3 ztf = new ZTFloatMap3(width, height);
            XMLMapGenerator xmg = new XMLMapGenerator(width, height);
            setCurrentMap(xmg.generateMap(location));
            getHIManager().getHI().getMovementMgr().requestMove(currentMap.getPreferredHIStart().x,
                    currentMap.getPreferredHIStart().y);
            ((InputManager) getInputManager()).enterStartState();
        }

        @Override
        public void loadMap(URL location)
        {
            loadMap(location, -1, -1);
        }

        @Override
        public void loadParent()
        {
            if (currentMap.getParent() == null)
            {
                resetCurrent();
//                int response = JOptionPane.showConfirmDialog(null, "Your character has been killed in the world map.\nDo you want to pretend like nothing happend?"
//                     , "", JOptionPane.YES_NO_OPTION);
//                if (response == JOptionPane.YES_OPTION)
//                {
//                    System.out.println("We'll pretend nothing happened....but only because its the parent world.");
//                    
//                }
//                else
//                {
//                    resetCurrent();
//                }
            }
            else
            {
                for (TimedObject to : getCurrent().getTimedObjects())
                {
                    to.kill();
                }
                setCurrentMap(currentMap.getParent());
                getHIManager().getHI().getMovementMgr().requestMove(currentMap.getPreferredHIStart().x,
                        currentMap.getPreferredHIStart().y);
                //((InputManager)getInputManager()).enterStartState();
            }
        }

        @Override
        public Collection<Tile> getCurTiles(int x, int y)
        {
            if (x >= getCurrent().getWidth() || y >= getCurrent().getHeight())
            {
                return new HashSet<Tile>();
            }
            else
            {
                List<Tile> tiles = new ArrayList<Tile>();
                for (String layerName : getCurrent().getLayers())
                {
                    Tile tile = getCurrent().getLayer(layerName).getTileAt(x, y);
                    if (tile != null)
                    {
                        tiles.add(tile);
                    }
                }
                return tiles;
            }
        }
    }

    /*
     * private LayerStack currentMap = null;
     * private Map<String, LayerStack> availableMaps = null;
     * private HumanIntelligence human = null;
     * private TileDisplay model = null;
     * private MapChangedEvent changeEvent = null;
     * private MouseClickManager mouseClickManager = null;
     *
     * public GameManager()
     * {
     * final int test_width = 30;
     * final int test_height = 30;
     * MapFactory mf = new TFloatMap1(test_width, test_height);
     * currentMap = mf.generateMap();
     *
     * availableMaps = new HashMap<>();
     * availableMaps.put("SandyFlats", currentMap);
     * human = new HumanIntelligence(15, 15);
     *
     * init();
     * }
     *
     * public GameManager(GameStartState gss)
     * {
     * this.currentMap = gss.getStartMap();
     * this.availableMaps = new TreeMap<>(gss.getAvailableMaps());
     * this.human = gss.getHuman();
     * init();
     * }
     *
     * public final void init()
     * {
     * this.human.setManager(this);
     * changeEvent = new MapChangedEvent();
     * mouseClickManager = new MouseClickManager();
     * }
     *
     * public MouseClickManager getMouseClickManager()
     * {
     * return mouseClickManager;
     * }
     *
     * public class MouseClickManager extends MouseAdapter
     * {
     *
     * @Override
     * public void mouseClicked(MouseEvent e)
     * {
     * //Notice that inside this mouse event, the point is not AWT standard,
     * // but is standardized to which tile was clicked.
     *
     * for (String layer : currentMap.getLayers())
     * {
     * if (currentMap.getLayer(layer).getTileAt(e.getX(), e.getY()) instanceof ClickableTile)
     * {
     * ((ClickableTile) currentMap.getLayer(layer).getTileAt(e.getX(), e.getY())).mouseClicked(e);
     * }
     * }
     * currentMap.getLayer("02 Magic").setTileAt(e.getX(), e.getY(), MainTileFactory.getInstance().constructTile("water.gif", "water"));
     * notifyMapChanged();
     * }
     *
     * @Override
     * public void mousePressed(MouseEvent e)
     * {
     * for (String layer : currentMap.getLayers())
     * {
     * if (currentMap.getLayer(layer).getTileAt(e.getX(), e.getY()) instanceof ClickableTile)
     * {
     * ((ClickableTile) currentMap.getLayer(layer).getTileAt(e.getX(), e.getY())).mouseClicked(e);
     * }
     * }
     * currentMap.getLayer("02 Magic").setTileAt(e.getX(), e.getY(), MainTileFactory.getInstance().constructTile("water.gif", "water"));
     * notifyMapChanged();
     * }
     * }
     *
     * public void setModel(TileDisplay td)
     * {
     * this.model = td;
     * }
     *
     * public HumanIntelligence getHumanIntelligence()
     * {
     * return human;
     * }
     *
     * public void bindArrowKeys(InputMap inputMap, ActionMap actionMap)
     * {
     * Map<Integer, Action> humintActions =
     * getHumanIntelligence().getExternalInputMgr().getKeyPressHandlers();
     *
     * for (int key: humintActions.keySet())
     * {
     * inputMap.put(KeyStroke.getKeyStroke(key, 0), "HumInt" + key);
     * actionMap.put("HumInt" + key, humintActions.get(key));
     * }
     *
     * //        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_LEFT, 0), "LeftArrow");
     * //        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_RIGHT, 0), "RightArrow");
     * //        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_UP, 0), "UpArrow");
     * //        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_DOWN, 0), "DownArrow");
     * //
     * //        actionMap.put("LeftArrow", human.new LeftArrowKeyMover());
     * //        actionMap.put("RightArrow", human.new RightArrowKeyMover());
     * //        actionMap.put("UpArrow", human.new UpArrowKeyMover());
     * //        actionMap.put("DownArrow", human.new DownArrowKeyMover());
     * }
     *
     * public void setCurrentMap(String map)
     * {
     * if (getAvailableMaps().containsKey(map))
     * {
     * setCurrentMap(getAvailableMaps().get(map));
     * }
     * }
     *
     * public void setCurrentMap(LayerStack map)
     * {
     * currentMap = map;
     * notifyMapChanged();
     * }
     *
     * public LayerStack getCurrentMap()
     * {
     * return currentMap;
     * }
     *
     * public Map<String, LayerStack> getAvailableMaps()
     * {
     * return availableMaps;
     * }
     *
     * public void notifyMapChanged()
     * {
     * getMapChangedEvent().notifyHandlers();
     * model.repaint();
     * }
     *
     * public MapChangedEvent getMapChangedEvent()
     * {
     * return changeEvent;
     * }
     *
     * public class MapChangedEvent
     * {
     *
     * private List<MapChangedHandler> handlers = null;
     *
     * public MapChangedEvent()
     * {
     * handlers = new ArrayList<>();
     * }
     *
     * public void notifyHandlers()
     * {
     * for (MapChangedHandler chHandle : handlers)
     * {
     * chHandle.handleMapChanged();
     * }
     * }
     *
     * public void addHandler(MapChangedHandler mch)
     * {
     * handlers.add(mch);
     * }
     * }
     *
     * public interface MapChangedHandler
     * {
     *
     * public void handleMapChanged();
     * }*/
}
