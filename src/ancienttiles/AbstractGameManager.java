/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ancienttiles;

import ancienttiles.griddisplay.TileDisplay;
import ancienttiles.tiles.ai.TimedObject;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.URL;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import javax.swing.ActionMap;
import javax.swing.InputMap;
import javax.swing.Timer;
import javax.swing.event.MouseInputAdapter;

/**
 *
 * @author krr428
 */
public abstract class AbstractGameManager
{
    private AbstractMapManager mapManager = null;
    private HumIntManager humanIntelligenceManager = null;
    private AbstractAIManager aiManager = null;
    private ViewManager viewManager = null;
    private AbstractTimingManager timingManager = null;
    private AbstractInputManager inputManager = null;   
        
    //<editor-fold defaultstate="collapsed" desc="Field Gets/Sets">
    public AbstractMapManager getMapManager()
    {
        return mapManager;
    }
    
    protected void setMapManager(AbstractMapManager mapManager)
    {
        this.mapManager = mapManager;
    }
    
    public HumIntManager getHIManager()
    {
        return humanIntelligenceManager;
    }
    
    protected void setHIManager(HumIntManager humanIntelligenceManager)
    {
        this.humanIntelligenceManager = humanIntelligenceManager;
    }
    
    public AbstractAIManager getAIManager()
    {
        return aiManager;
    }
    
    protected void setAIManager(AbstractAIManager aiManager)
    {
        this.aiManager = aiManager;
    }
    
    public ViewManager getViewManager()
    {
        return viewManager;
    }
    
    protected void setViewManager(ViewManager viewManager)
    {
        this.viewManager = viewManager;
    }
    
    public AbstractTimingManager getTimingManager()
    {
        return timingManager;
    }
    
    protected void setTimingManager(AbstractTimingManager timingManager)
    {
        this.timingManager = timingManager;
    }
    
    public AbstractInputManager getInputManager()
    {
        return inputManager;
    }

    protected void setInputManager(AbstractInputManager inputManager)
    {
        this.inputManager = inputManager;
    }
    
    
//</editor-fold>

    // This method provided for convenience and compatibility.
    public LayerStack getCurrentMap()
    {
        return getMapManager().getCurrent();
    }
    
    public interface MapChangedListener
    {
        public void mapChanged();
    }

    public abstract class AbstractMapManager
    {
        
        //<editor-fold defaultstate="collapsed" desc="MapChangedEvent">
        private Set<MapChangedListener> mapChangedListeners = null;
                
        public AbstractMapManager()
        {
            mapChangedListeners = new HashSet<MapChangedListener>();
        }
        
        public void addMapChangedListener(MapChangedListener mcl)
        {
            mapChangedListeners.add(mcl);
        }
        
        public void removeMapChangedListener(MapChangedListener mcl)
        {
            mapChangedListeners.remove(mcl);
        }
        
        public void fireMapChanged()
        {
            for (MapChangedListener mcl: mapChangedListeners)
            {
                mcl.mapChanged();
            }
            getViewManager().setUpdateOn();
        }
//</editor-fold>               
        
        public abstract LayerStack getCurrent();

        public abstract LayerStack getMap(String name);

        public abstract void addMap(String name, LayerStack map);

        public abstract void setCurrentMap(String name);

        public abstract void setCurrentMap(LayerStack ls);

        public abstract Collection<Tile> getCurTiles(int x, int y);
        
        public abstract void resetCurrent();
        public abstract void loadMap(URL location, int width, int height);
        public abstract void loadMap(URL location);
        public abstract void loadParent();
        
//        @Deprecated
//        public void initTimedObjects()
//        {
//            getAIManager().initTimedObjects();
//        }

    }

    public class HumIntManager
    {
        private HumanIntelligence humanIntelligence = null;
        
        public HumanIntelligence getHI()
        {
            return humanIntelligence;
        }

        void setHI(HumanIntelligence hi)
        {
            this.humanIntelligence = hi;
            this.humanIntelligence.setManager(AbstractGameManager.this);
        }
    }

    public abstract class AbstractAIManager
    {    
        public abstract void killTimedObjects();
        public abstract void initTimedObjects();
    }
    
    

    public abstract class AbstractInputManager
    {
        protected InputMap inputMap = null;
        protected ActionMap actionMap = null;

        public AbstractInputManager()
        {
            
        }
        
        public AbstractInputManager(InputMap im, ActionMap am)
        {
            setActionMap(am);
            setInputMap(im);            
        }
        
        public AbstractInputManager(AbstractInputManager formerManager)
        {
            this(formerManager.getInputMap(), formerManager.getActionMap());
        }
        
        public InputMap getInputMap()
        {
            return inputMap;
        }

        public final void setInputMap(InputMap inputMap)
        {
            this.inputMap = inputMap;
        }

        public ActionMap getActionMap()
        {
            return actionMap;
        }

        public final void setActionMap(ActionMap actionMap)
        {
            this.actionMap = actionMap;
        }
        
        public abstract void init(); //Called when the
        //public abstract void bindKeys();
        public abstract MouseInputAdapter getMouseInputAdapter();
        
                
    }
    
    public class ViewManager implements ActionListener
    {
        private TileDisplay view = null;
        private Timer paintTimer = null;
        private boolean updateViewFlag = true;
        
        public ViewManager(TileDisplay td)
        {
            this.setDisplay(td);
            //Set to 1000ms / 40 frames per second.
            paintTimer = new Timer(1000/60, this);
            paintTimer.start();
        }
        
        public void setUpdateOn()
        {
            updateViewFlag = true;
        }
        
        public TileDisplay getView()
        {
            return view;
        }

        public final void setDisplay(TileDisplay td)
        {
            view = td;
        }

        @Override
        public void actionPerformed(ActionEvent e)
        {
            if (view != null && updateViewFlag)
            {
                view.repaint();
                updateViewFlag = false;
            }
        }
        
        
    }

    public abstract class AbstractTimingManager
    {        
        public abstract void addListener(String timer, TimedObject t);
        public abstract void removeListener(String timer, TimedObject t);        
        public abstract void createTimer(String name, int interval);
        public abstract void pauseTimer(String name);
        public abstract void removeTimer(String name);
        public abstract boolean containsTimer(String name);
        
    }
}
