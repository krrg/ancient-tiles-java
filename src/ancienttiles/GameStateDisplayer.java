/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ancienttiles;

/**
 *
 * @author krr428
 */
public abstract class GameStateDisplayer
{
    protected AbstractGameManager model = null;
    
    public interface TextualInfoPrinter
    {
        public void output(String message);
    }
    
    public GameStateDisplayer(AbstractGameManager underlyingModel)
    {
        this.model = underlyingModel;
    }
    
    public abstract void updateDisplay();
}
