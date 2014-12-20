///*
// * To change this template, choose Tools | Templates
// * and open the template in the editor.
// */
//package ancienttiles.tiles;
//
//import ancienttiles.Tile;
//import java.awt.Image;
//import java.awt.image.BufferedImage;
//import java.util.Scanner;
//
///**
// *
// * @author krr428
// */
//public abstract class MTF2
//{
//
//    private static MTF2 instance = null;
//
//    public static MTF2 getInstance()
//    {
//        if (instance == null)
//        {
//            instance = new MTF2();
//        }
//        return instance;
//    }
//
//    public Tile getBlank()
//    {
//        return new Tile(new BufferedImage(32, 32, BufferedImage.TYPE_INT_ARGB));
//    }
//    
//    public abstract Tile getTile(String name);
//    public abstract Tile getTile(String name, String...attr);
//    public abstract Tile getTile(String name, int x, int y, String...attr);
//    
//    protected abstract Image getImage(String id);
//    
//    
//    
//    public abstract class SubFactory
//    {
//
//        public abstract Tile getInstance(Image ii);
//
//        public  Tile getInstance(Image ii, String... attributes)
//        {
//            Tile t = getInstance(ii);
//            
//            for (String attribute: attributes)
//            {
//                if (attribute.startsWith("%"))
//                {
//                    Scanner sc = new Scanner(attribute);
//                    sc.useDelimiter("=");
//                    String key = sc.next().trim();
//                    if (! sc.hasNext())
//                    {
//                        continue;
//                    }
//                    String value = sc.next().trim();
//                    
//                    t.addMetadata(key, value);
//                }
//                else
//                {
//                    t.addAttribute(attribute);
//                }
//            }
//            
//            return t;
//        }
//    }
//}
