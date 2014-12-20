/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ancienttiles;

import ancienttiles.griddisplay.TileDisplay;
import ancienttiles.tiles.MainTileFactory;
import ancienttiles.tiles.mapgen.XMLMapGenerator;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.HashMap;
import java.util.Map;
import javax.swing.AbstractAction;
import javax.swing.ActionMap;
import javax.swing.InputMap;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

/**
 *
 * @author krr428
 */
public class Main
{
    private static GameManager engine = null;
    
    public static LayerStack initTestMap1()
    {
//        ZTFloatMap3 zfloat = new ZTFloatMap3();
//        LayerStack map = zfloat.generateMap(MainTileFactory.class.getResource("maps/cv.zip"));
//        //javax.swing.JOptionPane.showMessageDialog(null, map.getOrigin().toString());
        XMLMapGenerator xmlmap = new XMLMapGenerator();
        LayerStack map = xmlmap.generateMap(MainTileFactory.class.getResource("maps/Master.zip"));
//        LayerStack map = xmlmap.generateMap(MainTileFactory.class.getResource("maps/Combinatoric.zip"));
        return map;
    }

    public static GameManager initGameManager()
    {
        Map<String, LayerStack> maps = new HashMap<String, LayerStack>();
        maps.put("Level 001", initTestMap1());
        Point histart = maps.get("Level 001").getPreferredHIStart();
        HumanIntelligence hi = new HumanIntelligence(histart.x, histart.y);
        GameManager gm = new GameManager(hi, maps, null);
        return gm;
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args)
    {
        JFrame jf = new JFrame("Ancient Tiles, alpha version");
        jf.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        jf.setLocationByPlatform(true);

//        MapFactory smf = new TFloatMap2(40, 70);
//        LayerStack ls = smf.generateMap();
//        HumanIntelligence hi = new HumanIntelligence(5, 5);
//        Map<String, LayerStack> maps = new TreeMap<>();
//        maps.put("Level00", ls);
        engine = initGameManager();

        TileDisplay tileDisplayer = new TileDisplay(engine);
        engine.getViewManager().setDisplay(tileDisplayer);
        //engine.initStartState();

        //Default
        

        JScrollPane jsp = new JScrollPane(tileDisplayer);

        JPanel mainPanel = new JPanel();
        mainPanel.add(jsp);
        mainPanel.add(new GameInfoPanel(engine));
        jf.getContentPane().add(mainPanel);

        InputMap arrowKeyInputBinding = tileDisplayer.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
        ActionMap arrowKeyActionBinding = tileDisplayer.getActionMap();

        engine.getInputManager().setActionMap(arrowKeyActionBinding);
        engine.getInputManager().setInputMap(arrowKeyInputBinding);
        engine.getInputManager().init();
        
        //jf.setJMenuBar(getJMenuBar());

        jf.pack();

        tileDisplayer.repaint();
        jf.setVisible(true);
    }
    
    private static JMenuBar getJMenuBar()
    {
        JMenuBar rootMenuBar = new JMenuBar();
        
        JMenu file = new JMenu("File");
        file.add(new AbstractAction("Save")
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                System.out.println("Saving...");
                
                JFileChooser jfc = new JFileChooser();
                
                int returnValue = jfc.showSaveDialog(null);
                if (returnValue == JFileChooser.APPROVE_OPTION)
                {
                    //Then serialize.
                    System.out.println("Serializing internal engine...");
                    try
                    {
                        String fileName = jfc.getSelectedFile().getAbsolutePath();
                        saveGame(fileName);
                        System.out.println("Finished");
                    }
                    catch (IOException ioexc)
                    {
                        System.out.println(ioexc.toString());
                        System.out.println("Could not save!");
                    }
                }
                else
                {
                    System.out.println("Cancelling...");
                }
                
                
            }
        });
        
        file.add(new AbstractAction("Load") {

            @Override
            public void actionPerformed(ActionEvent e)
            {
                System.out.println("Loading...");
                JFileChooser jfc = new JFileChooser();
                
                int returnValue = jfc.showOpenDialog(null);
                if (returnValue == JFileChooser.APPROVE_OPTION)
                {
                    //Then serialize.
                    System.out.println("Deserializing internal engine...");
                    try
                    {
                        openGame(jfc.getSelectedFile().getAbsolutePath());
                    }
                    catch (IOException exc)
                    {
                        System.out.println(exc.toString());
                        System.out.println("Unable to deserialize!");
                    }
                            
                }
                else
                {
                    System.out.println("Cancelling...");
                }
            }
        });
        
        rootMenuBar.add(file);        
        return rootMenuBar;
    }
    
    private static void saveGame(String location) throws IOException
    {
        FileOutputStream fos = new FileOutputStream(location);
        BufferedOutputStream bos = new BufferedOutputStream(fos);
        
        ObjectOutputStream serializer = new ObjectOutputStream(bos);
        serializer.writeObject(engine.getMapManager().getCurrent());
        serializer.writeObject(engine.getHIManager().getHI());
        serializer.flush();
        serializer.close();
    }
    
    private static void openGame(String location) throws IOException
    {
        FileInputStream fis = new FileInputStream(location);
        BufferedInputStream bis = new BufferedInputStream(fis);
        ObjectInputStream deserializer = new ObjectInputStream(bis);
        
        try
        {
            LayerStack currentMap = (LayerStack)deserializer.readObject();
            HumanIntelligence hi = (HumanIntelligence)deserializer.readObject();
            
            engine.getHIManager().setHI(hi);
            engine.getMapManager().setCurrentMap(currentMap);
            engine.getMapManager().resetCurrent();
                        
            System.out.println("Finished.");
        }
        catch (ClassNotFoundException e)
        {
            System.out.println("Could not deserialize!");            
        }
        
        deserializer.close();
                
    }
}
