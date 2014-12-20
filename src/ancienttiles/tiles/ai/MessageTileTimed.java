/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ancienttiles.tiles.ai;

import java.awt.Image;

/**
 *
 * @author krr428
 */
@Deprecated
public class MessageTileTimed extends MessageTile
{
//    private int countdownS = DEFAULT_EXPIRATION;
//    protected Timer msgExpireTimer = null;
//    protected static final int DEFAULT_EXPIRATION = 5;
//    protected static final int DEFAULT_COUNTDOWN = 1000; //1 second.
//    
    public MessageTileTimed(Image i)
    {
        super(i);
    }

    public MessageTileTimed(Image i, int x, int y, String layer)
    {
        super(i, x, y, layer);
    }
//
//    @Override
//    public void movedOn(HumanIntelligence hi)
//    {
//        kill();
//        super.movedOn(hi); 
//        msgExpireTimer = new Timer(DEFAULT_COUNTDOWN, this);
//        msgExpireTimer.start();
//    }
//
//    @Override
//    public void init(AbstractGameManager gm)
//    {
//        super.init(gm); 
//    }
//
//    @Override
//    public void kill()
//    {
//        super.kill(); 
//        
//        if (msgExpireTimer != null)
//        {
//            msgExpireTimer.stop();
//            msgExpireTimer.removeActionListener(this);
//            msgExpireTimer = null;
//        }        
//        countdownS = DEFAULT_EXPIRATION;
//    }
//
//    @Override
//    public void actionPerformed(ActionEvent e)
//    {
//        countdownS--;             
//        if (countdownS == 0)
//        {
//            this.kill();
//        }
//    }
    
    
}
