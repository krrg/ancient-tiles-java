/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ancienttiles;

import java.util.Map;

/**
 *
 * @author krr428
 */
public class GameStartState
{

    private LayerStack startMap = null;
    private Map<String, LayerStack> availableMaps = null;
    private HumanIntelligence human = null;

    public GameStartState(LayerStack start, Map<String, LayerStack> mapstore, HumanIntelligence hi)
    {
        this.startMap = start;
        this.availableMaps = mapstore;
        this.human = hi;
    }

    public LayerStack getStartMap()
    {
        return startMap;
    }

    public Map<String, LayerStack> getAvailableMaps()
    {
        return availableMaps;
    }

    public HumanIntelligence getHuman()
    {
        return human;
    }
    
    
}
