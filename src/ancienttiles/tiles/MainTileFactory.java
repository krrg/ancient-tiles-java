/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ancienttiles.tiles;

import ancienttiles.GlowGlyphTile;
import ancienttiles.LockTile;
import ancienttiles.MonsterBlockTile;
import ancienttiles.Tile;
import ancienttiles.ToggleButton;
import ancienttiles.ToggleTile;
import ancienttiles.WallTile;
import ancienttiles.tiles.ai.HoverTransitH;
import ancienttiles.tiles.ai.HoverTransitP;
import ancienttiles.tiles.ai.HoverTransitV;
import ancienttiles.tiles.ai.HowlerMonkey;
import ancienttiles.tiles.ai.JaguarX;
import ancienttiles.tiles.ai.JaguarY;
import ancienttiles.tiles.ai.MessageTileTimed;
import ancienttiles.tiles.ai.ParrotCCW;
import ancienttiles.tiles.ai.ParrotCW;
import ancienttiles.tiles.ai.Professor;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Random;
import java.util.Scanner;
import java.util.TreeMap;
import java.util.TreeSet;
import javax.imageio.ImageIO;

/**
 *
 * @author krr428
 */
public class MainTileFactory implements TileFactory
{

    private static MainTileFactory rootFactory = null;
    private Random rand = null; //Useful for factories.
    private Map<String, Object> resources = null;
    private Map<String, SpecialCreator> specialCreators = null;
    private Map<String, String> tiledImages = null;
    private Map<URL, BufferedImage> imageCache = null;

    public static MainTileFactory getInstance()
    {
        if (rootFactory == null)
        {
            rootFactory = new MainTileFactory();
        }

        return rootFactory;
    }

    private MainTileFactory()
    {
        rand = new SecureRandom();
        resources = new HashMap<String, Object>();
        specialCreators = new HashMap<String, SpecialCreator>();
        this.imageCache = new HashMap<URL, BufferedImage>();
        
        initResourceList();
        initSpecialCreators();
    }

    public Tile getTile(String name)
    {
        //Cannot be a special creator.
        URL imgloc;
        try
        {
            imgloc = getImageURL(name);
        }
        catch (NoSuchElementException ne)
        {
            System.out.println(ne.toString());
            imgloc = getImageURL("transparent.png");
        }


        Tile generated = getTile(getImage(imgloc));
        generated.getMetadata().put("%imgloc", imgloc.toString());

        return generated;
    }

    public Tile getTile(String name, String... attr)
    {
        URL imgloc;
        try
        {
            imgloc = getImageURL(name);
        }
        catch (NoSuchElementException ne)
        {
            System.out.println(ne.toString());
            imgloc = getImageURL("transparent.png");
        }

        Tile generated = getTile(getImage(imgloc), attr);
        generated.getMetadata().put("%imgloc", imgloc.toString());
        return generated;
    }

    public Tile getTile(Image img, String... attr)
    {
        Tile t = getCreator(attr).create(img);
        addAttributes(t, attr);
        return t;
    }

    public Tile getTile(String name, int x, int y, String... attr)
    {
        if (TiledTileFactory.getInstance().containsImageFor(name))
        {
            Image generated = TiledTileFactory.getInstance().getImageFor(name, x, y);
            Tile t = getTile(generated, attr);
            return t;
        }
        else
        {
            return getTile(name, attr);
        }
    }

    public URL getImageURL(String id)
    {
        if (resources.containsKey(id))
        {
            Object obj = resources.get(id);
            if (obj instanceof List)
            {
                List randomList = ((List) obj);
                id = randomList.get(rand.nextInt(randomList.size())).toString();
            }
            else
            {
                id = obj.toString();
            }
        }        
        URL url = getClass().getResource(id);        
        if (url == null)
        {
            throw new NoSuchElementException("Not able to find resource: " + id);
        }
        return url;
    }

    public BufferedImage getImage(URL imgloc)
    {
        try
        {
            if (imageCache.containsKey(imgloc))
            {
                return imageCache.get(imgloc);
            }
            else
            {
                BufferedImage bi = ImageIO.read(imgloc);
                imageCache.put(imgloc, bi);
                return bi;
            }
        }
        catch (IOException e)
        {
            System.out.println("Could not load '" + imgloc + "'");
            return null;
        }
    }

    public Image getImage(String id)
    {
        return getImage(getImageURL(id));
    }

    protected void addAttributes(Tile t, String... attr)
    {
        for (String attribute : attr)
        {
            if (attribute.startsWith("%"))
            {
                Scanner sc = new Scanner(attribute);
                sc.useDelimiter("=");
                String key = sc.next();
                if (!sc.hasNext())
                {
                    // System.out.println("Error in formatt on 163.");
                    continue;
                }
                String value = sc.next();
                t.addMetadata(key, value);
            }
            else
            {
                t.addAttribute(attribute);
            }
        }
    }

    protected SpecialCreator getCreator(Collection<String> attributes)
    {
        for (String attribute : attributes)
        {
            if (specialCreators.containsKey(attribute))
            {
                return specialCreators.get(attribute);
            }
        }
        return new TileCreator();
    }

    protected SpecialCreator getCreator(String... attributes)
    {
        return getCreator(Arrays.asList(attributes));
    }

    private void initSpecialCreators()
    {
        //There are a few objects that have special constructors, etc
        // that need a specific method to create a special Tile object.
        // The key corresponds to an attribute.
        specialCreators.put("wall", new WallTileCreator());
        specialCreators.put("redlock", getLockCreator("redkey"));
        specialCreators.put("greenlock", getLockCreator("greenkey"));
        specialCreators.put("yellowlock", getLockCreator("yellowkey"));
        specialCreators.put("bluelock", getLockCreator("bluekey"));
        //specialCreators.put("ai sglowtile", new SimpleGlowTileCreator());
        specialCreators.put("ai jaguarX", new JaguarXCreator());
        specialCreators.put("ai jaguarY", new JaguarYCreator());
        specialCreators.put("ParrotCW", new ParrotCWCreator());
        specialCreators.put("ParrotCCW", new ParrotCCWCreator());
        specialCreators.put("HI ExitSquare", new TileExitSquareCreator());
        specialCreators.put("HI JumpSquare", new TileJumpSquareCreator());
        specialCreators.put("toggle wall", new ToggleSquareCreator());
        specialCreators.put("toggle btn", new ToggleButtonCreator());
        specialCreators.put("ai hoverH", new HoverTransitHCreator());
        specialCreators.put("ai hoverV", new HoverTransitVCreator());
        specialCreators.put("ai hoverP", new HoverTransitPCreator());
        specialCreators.put("ai monkey", new HowlerMonkeyCreator());
        specialCreators.put("helptile", new MessageTileTimedCreator());
        specialCreators.put("HI EnterChildSquare", new TileEnterChildCreator());
        specialCreators.put("glowglyph", new GlowGlyphCreator());
        specialCreators.put("monsterblock", new MonsterBlockCreator());
        specialCreators.put("professor", new ProfessorCreator());
        //specialCreators.put("lock", new LockTileCreator());
    }

    private void initResourceList()
    {
        resources.put("grass", "grass1_m.png");
        resources.put("sand", Arrays.asList("sand1.png", "sand2.png"));
        resources.put("human", "unknownAgent.PNG");
        resources.put("wall", Arrays.asList("walls/wall1.PNG", "walls/wall2.PNG", "walls/wall3.PNG"));
        resources.put("mwall", Arrays.asList("glyphs/glyph1.png", "glyphs/glyph2.png", "glyphs/glyph3.png",
                "glyphs/glyph4.png", "glyphs/glyph5.png", "glyphs/glyph6.png", "glyphs/glyph7.png",
                "glyphs/glyph8.png", "glyphs/glyph9.png", "glyphs/glyph10.png"));
        resources.put("mwall d", Arrays.asList("glyphs/dglyph1.png", "glyphs/dglyph2.png", "glyphs/dglyph3.png",
                "glyphs/dglyph4.png", "glyphs/dglyph5.png", "glyphs/dglyph6.png", "glyphs/dglyph7.png",
                "glyphs/dglyph8.png", "glyphs/dglyph9.png", "glyphs/dglyph10.png", "glyphs/dglyph11.png",
                "glyphs/dglyph12.png", "glyphs/dglyph13.png", "glyphs/dglyph14.png", "glyphs/dglyph15.png",
                "glyphs/dglyph16.png", "glyphs/dglyph17.png", "glyphs/dglyph18.png", "glyphs/dglyph19.png"));
        resources.put("key y", "keys/yellowkey1.png");
        resources.put("key g", "keys/greenkey1.png");
        resources.put("key r", "keys/redkey1.png");
        resources.put("key b", "keys/bluekey1.png");
        resources.put("lock y", "keys/lock3_yellow1.png");
        resources.put("lock r", "keys/lock3_red1.png");
        resources.put("lock g", "keys/lock3_green1.png");
        resources.put("lock b", "keys/lock3_blue1.png");
        resources.put("ai sglowtile", "transparent.png");
        resources.put("ai jaguar", "mayan/jag1_left.png");
        resources.put("ai parrot", "mayan/parrot1_HDL.png");
        resources.put("toggle btn", "mayan/toggleBtnUp.png");
        resources.put("ai hover", "orange_grate.png");
        resources.put("ai monkey", Arrays.asList("mayan/hmonk_r.png", "mayan/hmonk_l.png"));
        resources.put("helptile", "question_mark.png");
        resources.put("barkcodex", "mayan/barkscroll.png");
        resources.put("professor", "unknownAgent.PNG");

        initLavaFloor();
        initToggleBtnImages();

    }

    private void initLavaFloor()
    {
        List<String> lavaFloorImages = new ArrayList<String>();

        for (int i = 1; i <= 7; i++)
        {
            lavaFloorImages.add("mayan/lava/lv" + i + ".png");
        }

        resources.put("lvfloor", lavaFloorImages);
    }

    private void initToggleBtnImages()
    {
        List<String> togglesON = new ArrayList<String>();
        for (int i = 1; i <= 10; i++)
        {
            //resources.put("toggle wall on " + i, "glyphs/toggleON_" + i + ".png");
            togglesON.add("glyphs/toggleON_" + i + ".png");
        }
        resources.put("toggle wall on", togglesON);
        List<String> togglesOFF = new ArrayList<String>();
        for (int i = 1; i <= 10; i++)
        {
            //resources.put("toggle wall off " + i, "toggleON_" + i, rand)
            togglesOFF.add("glyphs/toggleOFF_" + i + ".png");
        }
        resources.put("toggle wall off", togglesOFF);
    }

    @Deprecated
    @Override
    public Tile constructTile(String name, String... attributes)
    {
        Tile t = constructTile(name);
        Tile old = t;
        if (t == null)
        {
            return null;
        }

        //Check if one of the attributes is associated with a special creator.
        // It prioritizes based on order in the attribute list.
        for (String attribute : attributes)
        {
            if (specialCreators.containsKey(attribute))
            {
                t = specialCreators.get(attribute).create(t.getImage());
                break;
            }
        }

        for (String key : old.getMetadata().keySet())
        {
            t.addMetadata(key, old.getMetadata().get(key));
        }

        for (String attribute : attributes)
        {
            if (attribute.startsWith("%"))
            {
                Scanner sc = new Scanner(attribute);
                sc.useDelimiter("=");
                String key = sc.next();
                if (!sc.hasNext())
                {
                    // System.out.println("Error in formatt on 163.");
                    continue;
                }
                String value = sc.next();
                t.addMetadata(key, value);
            }
            else
            {
                t.addAttribute(attribute);
            }

        }



        return t;
    }

    @Deprecated
    @Override
    public Tile constructTile(String name)
    {
        Object resource = resources.get(name.toLowerCase());
        if (resource == null)
        {
            resource = name.toString(); //If it is not in the lookup, try the literal string.
        }
        String location;
        if (resource instanceof List)
        {
            List<String> randList = (List) resource;
            int index = rand.nextInt(randList.size());
            location = randList.get(index);
        }
        else
        {
            location = resource.toString();
        }

        URL url = getClass().getResource(location);
        if (url == null)
        {
            return new Tile(new BufferedImage(32, 32, BufferedImage.TYPE_INT_ARGB));
        }

        try
        {
            BufferedImage ii;

            if (imageCache.containsKey(url))
            {
                ii = new BufferedImage(32, 32, BufferedImage.TYPE_INT_ARGB);
                ii.getGraphics().drawImage(imageCache.get(url), 0, 0, null);
            }
            else
            {
                ii = ImageIO.read(url);
                imageCache.put(url, ii);
            }

            Tile t = new Tile(ii);
//            t.addMetadata("%imgloc", location);
            t.getMetadata().put("%imgloc", location); //Bypass the addMetadata, we don't want this to be the primary attribute.
            return t;
        }
        catch (IOException i)
        {
            return new Tile(new BufferedImage(32, 32, BufferedImage.TYPE_INT_ARGB));
        }


    }

    private SpecialCreator getLockCreator(final String... validKeys)
    {
        //Creates a special machine to create a lock that fits the given keys.
        SpecialCreator sc = new SpecialCreator()
        {
            @Override
            public Tile create(Image ii)
            {
                LockTile lt = new LockTile(ii, new TreeSet<String>(Arrays.asList(validKeys)));
                return lt;
            }
        };

        return sc;


    }

    protected interface SpecialCreator
    {

        public Tile create(Image ii);
    }

    private class TileCreator implements SpecialCreator
    {

        @Override
        public Tile create(Image ii)
        {
            return new Tile(ii);
        }
    }

    private class WallTileCreator implements SpecialCreator
    {

        @Override
        public Tile create(Image ii)
        {
            return new WallTile(ii);
        }
    }

    private class ParrotCWCreator implements SpecialCreator
    {

        @Override
        public Tile create(Image ii)
        {
            return new ParrotCW(ii);
        }
    }

    private class ParrotCCWCreator implements SpecialCreator
    {

        @Override
        public Tile create(Image ii)
        {
            return new ParrotCCW(ii);
        }
    }

    private class JaguarXCreator implements SpecialCreator
    {

        @Override
        public Tile create(Image ii)
        {
            return new JaguarX(ii);
        }
    }

    private class JaguarYCreator implements SpecialCreator
    {

        @Override
        public Tile create(Image ii)
        {
            return new JaguarY(ii);
        }
    }

    private class TileJumpSquareCreator implements SpecialCreator
    {

        @Override
        public Tile create(Image ii)
        {
            return new TileJumpSquare(ii);
        }
    }

    private class TileExitSquareCreator implements SpecialCreator
    {

        @Override
        public Tile create(Image ii)
        {
            return new TileExitSquare(ii);
        }
    }

    private class ToggleSquareCreator implements SpecialCreator
    {

        @Override
        public Tile create(Image ii)
        {
            return new ToggleTile(ii);
        }
    }

    private class ToggleButtonCreator implements SpecialCreator
    {

        @Override
        public Tile create(Image ii)
        {
            return new ToggleButton(ii);
        }
    }

    private class HoverTransitHCreator implements SpecialCreator
    {

        @Override
        public Tile create(Image ii)
        {
            return new HoverTransitH(ii);
        }
    }

    private class HoverTransitVCreator implements SpecialCreator
    {

        @Override
        public Tile create(Image ii)
        {
            return new HoverTransitV(ii);
        }
    }

    private class HoverTransitPCreator implements SpecialCreator
    {

        @Override
        public Tile create(Image ii)
        {
            return new HoverTransitP(ii);
        }
    }

    private class HowlerMonkeyCreator implements SpecialCreator
    {

        @Override
        public Tile create(Image ii)
        {
            return new HowlerMonkey(ii);
        }
    }

    private class MessageTileTimedCreator implements SpecialCreator
    {

        @Override
        public Tile create(Image ii)
        {
            return new MessageTileTimed(ii);
        }
    }

    private class TileEnterChildCreator implements SpecialCreator
    {

        @Override
        public Tile create(Image ii)
        {
            return new TileEnterChildSquare(ii);
        }
    }

    private class GlowGlyphCreator implements SpecialCreator
    {

        @Override
        public Tile create(Image ii)
        {
            return new GlowGlyphTile(ii);
        }
    }
    
    private class MonsterBlockCreator implements SpecialCreator
    {

        @Override
        public Tile create(Image ii)
        {
            return new MonsterBlockTile(ii);
        }
        
    }
    
    
    private class ProfessorCreator implements SpecialCreator
    {

        @Override
        public Tile create(Image ii)
        {
            return new Professor(ii);
        }
        
    }
//    private class LockTileCreator implements SpecialCreator
//    {
//
//    }
}
