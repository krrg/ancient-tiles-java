/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ancienttiles.griddisplay;

import ancienttiles.CustomRenderingTile;
import ancienttiles.GameManager;
import ancienttiles.Layer;
import ancienttiles.Tile;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.Timer;

/**
 *
 * @author krr428
 */
public class TileDisplay extends JPanel
{

    private int DISPLAY_WIDTH = 21;
    private int DISPLAY_HEIGHT = 21;
    private int FOCUS_OFFSET_X = DISPLAY_WIDTH / 2;
    private int FOCUS_OFFSET_Y = DISPLAY_WIDTH / 2;
    //private LayerStack game_map = null;
    //private HumanIntelligence human = null;
    private GameManager gameManager = null;
    private int focus_x = 0;
    private int focus_y = 0;
    private int real_upper_left_X = 0;
    private int real_upper_right_Y = 0;
    private boolean hiVisible = true;
    private boolean mapVisible = true;
    private Map<String, Image> post_overlays = null;
    private Map<String, Image> pre_underlays = null;
    private Map<Integer, Map<Integer, JComponent>> tooltips = null;

    public TileDisplay(GameManager gm)
    {
        this.setSize(DISPLAY_WIDTH * 32, DISPLAY_HEIGHT * 32);
        this.setMinimumSize(new Dimension(DISPLAY_WIDTH * 32, DISPLAY_HEIGHT * 32));
        this.setPreferredSize(new Dimension(DISPLAY_WIDTH * 32, DISPLAY_HEIGHT * 32));
        //this.setLayout(new FlowLayout());
        this.setLayout(null);
        this.gameManager = gm;

        this.setDoubleBuffered(true);
        this.setVisible(true);

        MouseAdapter mouseAd = new TileDisplayMouseListener();
        this.addMouseListener(mouseAd);
        this.addMouseMotionListener(mouseAd);

        post_overlays = new TreeMap<String, Image>();
        pre_underlays = new TreeMap<String, Image>();

        tooltips = new HashMap<Integer, Map<Integer, JComponent>>();

        //displayMessage("Press the arrow keys to begin moving.", MainTileFactory.getInstance().constructTile("wall").getImage());
    }

    public void setGameManager(GameManager gm)
    {
        this.gameManager = gm;
        this.repaint();
    }

    public void addPostOverlay(String name, Image image)
    {
        post_overlays.put(name, image);
        repaint();
    }

    public void removePostOverlay(String name)
    {
        post_overlays.remove(name);
        repaint();
    }

    public void addPreUnderlay(String name, Image image)
    {
        pre_underlays.put(name, image);
        repaint();
    }

    public void removePreUnderlay(String name)
    {
        pre_underlays.remove(name);
        repaint();
    }

    protected class TileDisplayMouseListener extends MouseAdapter
    {
        //TODO: This is where mouse code can be passed to the GameManager
    }

    public boolean isHiVisible()
    {
        return hiVisible;
    }

    public void setHiVisible(boolean hiVisible)
    {
        this.hiVisible = hiVisible;
    }

    public boolean isMapVisible()
    {
        return mapVisible;
    }

    public void setMapVisible(boolean mapVisible)
    {
        this.mapVisible = mapVisible;
    }

    protected int getFocusX()
    {
        return focus_x;
    }

    protected int getFocusY()
    {
        return focus_y;
    }

    protected void setFocus(int x, int y)
    {
        //System.out.println("Attempting to set focus to: " + x + " " + y);
        if (x < 0)
        {
            x = 0;
        }
        else if (x >= gameManager.getCurrentMap().getWidth())
        {
            x = gameManager.getCurrentMap().getWidth() - 1;
        }
        if (y < 0)
        {
            y = 0;
        }
        else if (y >= gameManager.getCurrentMap().getHeight())
        {
            y = gameManager.getCurrentMap().getHeight() - 1;
        }
        focus_x = x;
        focus_y = y;
        //System.out.println("Really set focus to: " + x + " " + y);
    }

    protected void drawUnderlays(Graphics g)
    {
        for (String imgName : pre_underlays.keySet())
        {
            Image i = pre_underlays.get(imgName);
            g.drawImage(i, 0, 0, DISPLAY_WIDTH * 32, DISPLAY_HEIGHT * 32, this);
        }
    }

    protected void drawOverlays(Graphics g)
    {
        for (String imgName : post_overlays.keySet())
        {
            Image i = post_overlays.get(imgName);
            g.drawImage(i, 0, 0, DISPLAY_WIDTH * 32, DISPLAY_HEIGHT * 32, this);
        }
    }

    @Override
    protected void paintComponent(Graphics g)
    {

        setFocus(gameManager.getHIManager().getHI().getX(), gameManager.getHIManager().getHI().getY());
        //To change body of generated methods, choose Tools | Templates.

        drawUnderlays(g);

        if (isMapVisible())
        {
            for (String layer_name : gameManager.getCurrentMap().getLayers())
            {
                Layer currentLayer = gameManager.getCurrentMap().getLayer(layer_name);
                if (currentLayer != null)
                {
                    paint_at_focus(focus_x, focus_y, g, new TileLayerFocusDrawer(currentLayer));
                }
            }

        }

        paint_at_focus(focus_x, focus_y, g, new ToolTipFocusDrawer(this.tooltips));

        //The below code ensures that the UA is always drawn on top, except for any overlays.
        if (isHiVisible())
        {
            Layer fakeHumanLayer = new Layer(gameManager.getCurrentMap().getWidth(),
                    gameManager.getCurrentMap().getHeight());
            fakeHumanLayer.setTileAt(gameManager.getHIManager().getHI().getX(),
                    gameManager.getHIManager().getHI().getY(), gameManager.getHIManager().getHI().getTileRepr());
            paint_at_focus(focus_x, focus_y, g, new TileLayerFocusDrawer(fakeHumanLayer));
        }

        drawOverlays(g);

        revalidate();
        paintChildren(g);
    }

    private interface FocusDrawer
    {

        public void paintAt(int i, int j, int u, int v, Graphics g);
    }

    private class TileLayerFocusDrawer implements FocusDrawer
    {

        private Layer currentLayer = null;

        public TileLayerFocusDrawer(Layer currentLayer)
        {
            this.currentLayer = currentLayer;
        }

        @Override
        public void paintAt(int i, int j, int u, int v, Graphics g)
        {
            if (currentLayer.getWidth() > i && currentLayer.getHeight() > j)
            {
                if (currentLayer.getTileAt(i, j) != null)
                {
                    Tile tile = currentLayer.getTileAt(i, j);
                    if (tile instanceof CustomRenderingTile)
                    {
                        ((CustomRenderingTile)tile).Render(TileDisplay.this, g, u * 32, v * 32);
                    }
                    else
                    {
                        g.drawImage(tile.getImage(), u * 32, v * 32, 32, 32, TileDisplay.this);
                    }
//                        //Icon icon = currentLayer.getTileAt(i, j).getImage();
//                        //icon.paintIcon(this, g, u * 32, v * 32);
//                        Icon icon = new ImageIcon(currentLayer.getTileAt(i, j).getImage());
//                        icon.paintIcon(this, g, u * 32, v * 32);s
                    
                }
            }
        }
    }

    private class ToolTipFocusDrawer implements FocusDrawer
    {

        public Map<Integer, Map<Integer, JComponent>> tooltips = null;

        public ToolTipFocusDrawer(Map<Integer, Map<Integer, JComponent>> tooltips)
        {
            this.tooltips = tooltips;

            for (int i : tooltips.keySet())
            {
                for (int j : tooltips.get(i).keySet())
                {
                    JComponent jc = tooltips.get(i).get(j);
                    if (jc != null)
                    {
                        TileDisplay.this.remove(tooltips.get(i).get(j));
                    }
                }
            }
        }

        @Override
        public void paintAt(int i, int j, int u, int v, Graphics g)
        {
            //Get the tooltip at i, j, paint it at u*32 v*32
            if (tooltips.get(i) != null && tooltips.get(i).get(j) != null)
            {
                JComponent jc = tooltips.get(i).get(j);
                jc.setLocation(u * 32, v * 32);
                jc.setSize(jc.getPreferredSize());
                TileDisplay.this.add(jc);
            }

        }
    }

    private void paint_at_focus(int x, int y, Graphics g, FocusDrawer fd)
    {
        if (x <= FOCUS_OFFSET_X)
        {
            x = FOCUS_OFFSET_X;
        }
        else if (x > gameManager.getCurrentMap().getWidth() - 1 - FOCUS_OFFSET_X)
        {
            x = gameManager.getCurrentMap().getWidth() - FOCUS_OFFSET_X - 1;
        }
        if (y <= FOCUS_OFFSET_Y)
        {
            y = FOCUS_OFFSET_Y;
        }
        else if (y > gameManager.getCurrentMap().getHeight() - 1 - FOCUS_OFFSET_Y)
        {
            y = gameManager.getCurrentMap().getHeight() - FOCUS_OFFSET_Y - 1;
        }

        int u = 0;
        for (int i = x - FOCUS_OFFSET_X; i <= x + FOCUS_OFFSET_X; i++)
        {
            int v = 0;
            for (int j = y - FOCUS_OFFSET_Y; j <= y + FOCUS_OFFSET_Y; j++)
            {
                if (u == 0 && v == 0)
                {
                    real_upper_left_X = i;
                    real_upper_right_Y = j;
                }

                fd.paintAt(i, j, u, v, g);

                v++;
            }
            u++;
        }

    }
    private Map<Timer, Point> tooltipTimeouts = null;

    public void displayMessageTooltip(final String message, final int tileX, final int tileY)
    {
        displayMessageTooltip(message, 3500, tileX, tileY);
    }

    public void displayMessageTooltip(final String message, int timeout, final int tileX, final int tileY)
    {
        if (tooltips.containsKey(tileX) && tooltips.get(tileX).containsKey(tileY))
        {
            TileDisplay.this.remove(tooltips.get(tileX).remove(tileY));
        }
        
        if (tooltipTimeouts == null)
        {
            tooltipTimeouts = new HashMap<Timer, Point>();
        }

        JLabel jtt = new JLabel()
        {
            @Override
            protected void paintComponent(Graphics g)
            {
                g.setColor(this.getBackground());
                g.fillRect(0, 0, this.getWidth(), this.getHeight());
                super.paintComponent(g); //To change body of generated methods, choose Tools | Templates.
            }
        };
        jtt.setText(message);
        jtt.setForeground(Color.BLACK);
        jtt.setBackground(new Color(0x66FFEEBB, true));
        jtt.setOpaque(false);


        final JPanel messagePanel = new JPanel();
        messagePanel.setLayout(new FlowLayout());
        messagePanel.setOpaque(false);
        messagePanel.add(jtt);
        messagePanel.setLocation(tileX * 32, tileY * 32);
        messagePanel.setSize(messagePanel.getPreferredSize());


        if (!tooltips.containsKey(tileX))
        {
            tooltips.put(tileX, new HashMap<Integer, JComponent>());
        }

        tooltips.get(tileX).put(tileY, messagePanel);


        final Timer expirationTimer = new javax.swing.Timer(timeout, null);
        expirationTimer.addActionListener(new ActionListener()
        {
            private JComponent myToolTip = messagePanel;
                    
            @Override
            public void actionPerformed(ActionEvent e)
            {       
                TileDisplay.this.remove(myToolTip);
                if (tooltips.get(tileX) != null && tooltips.get(tileX).get(tileY) != null)
                {
                    if (myToolTip == tooltips.get(tileX).get(tileY))
                    {
                        tooltips.get(tileX).remove(tileY);
                    }
                    
                }
                tooltipTimeouts.remove(expirationTimer);
                TileDisplay.this.repaint();
                
            }
        });

        expirationTimer.setRepeats(false);
        tooltipTimeouts.put(expirationTimer, new Point(tileX, tileY));
        expirationTimer.start();


    }

    public MessageCallback displayMessage(final String message, final Image background)
    {
        final JPanel messagePanel = new JPanel();
        messagePanel.setLayout(new BoxLayout(messagePanel, BoxLayout.LINE_AXIS));

        messagePanel.setOpaque(false);

        JLabel messageLabel = new JLabel(message)
        {
            @Override
            protected void paintComponent(Graphics g)
            {
                g.setColor(this.getBackground());
                g.fillRect(0, 0, this.getWidth(), this.getHeight());
                if (background != null)
                {
                    g.drawImage(background, 0, 0, this.getWidth(), this.getHeight(), null);
                }
                super.paintComponent(g); //To change body of generated methods, choose Tools | Templates.
            }
        };
        messageLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        messageLabel.setForeground(Color.WHITE);
        messageLabel.setBackground(new Color(0, 0, 0, 0x22));
        messageLabel.setOpaque(false);
        JButton okayButton = new JButton("Okay");
        okayButton.setFocusable(false);
        okayButton.addActionListener(new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                remove(messagePanel);
                revalidate();
                repaint();
            }
        });
        okayButton.setAlignmentX(Component.CENTER_ALIGNMENT);

        messagePanel.add(messageLabel);
        messagePanel.add(okayButton);

        add(messagePanel);

        return new MessageCallback(messagePanel, messageLabel, okayButton);
    }

    public class MessageCallback
    {

        private JComponent compReference = null;
        private JLabel messageLabel = null;
        private JButton okayButton = null;

        private MessageCallback(JComponent jc, JLabel messageLabel, JButton okButton)
        {
            this.compReference = jc;
            this.messageLabel = messageLabel;
            this.okayButton = okButton;
        }

        public void remove()
        {
            TileDisplay.this.remove(compReference);
            TileDisplay.this.repaint();
        }

        public void setMessage(String message)
        {
            messageLabel.setText(message);
        }

        public void setBtnMessage(String message)
        {
            okayButton.setText(message);
        }
    }
}
