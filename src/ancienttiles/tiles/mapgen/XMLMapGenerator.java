/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ancienttiles.tiles.mapgen;

import ancienttiles.Layer;
import ancienttiles.LayerStack;
import ancienttiles.tiles.MainTileFactory;
import ancienttiles.tiles.mapgen.laygen.LargePictureGen;
import ancienttiles.tiles.mapgen.laygen.RasterToLayer;
import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.StringReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 * Based on the original ideas in the ZTFloatMap3, the XML Map Generator reads
 * in an xml configuration file, then parses the included images accordingly.
 * Thus is it much more flexible than the original ZTFloatMap3
 *
 * @author krr428
 */
public class XMLMapGenerator extends ZipFloatMap
{

    private URL origin;

    public XMLMapGenerator()
    {
        super();
    }

    public XMLMapGenerator(int width_, int height_)
    {
        super(width_, height_);
    }

    private Schema loadMapGenSchema() throws SAXException
    {
        URL xsdloc = getClass().getResource("mapgen.xsd");
        SchemaFactory schemaFactory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
        Schema loadedSchema = schemaFactory.newSchema(xsdloc);
        return loadedSchema;
    }

    protected final void autodetectSize()
    {
        if (width == -1)
        {
            //Then look at the floor, and use that as the width and height.
            if (getLayerImages().containsKey("floor"))
            {
                width = getLayerImages().get("floor").getWidth();
            }
            else
            {
                width = getLayerImages().get(getLayerImages().keySet().iterator().next()).getWidth();
            }
        }
        if (height == -1)
        {
            if (getLayerImages().containsKey("floor"))
            {
                height = getLayerImages().get("floor").getHeight();
            }
            else
            {
                height = getLayerImages().get(getLayerImages().keySet().iterator().next()).getHeight();
            }
        }
    }

    private String getLayerGenType(Node layerGen)
    {
        return layerGen.getAttributes().getNamedItem("type").getNodeValue();
    }

    private Layer getColorLayer(Node layerGen, Node layer)
    {
        String strCol = layerGen.getAttributes().getNamedItem("color").getNodeValue();
        //Now attempt to parse it.
        int rgb = Integer.parseInt(strCol, 16); //Hex
        Color color = new Color(rgb);

        LargePictureGen lpg = new LargePictureGen(width, height);
        return lpg.generateLayer(color);
    }

    private Layer getRGBLayer(Node layerGen, Node layer, String layerID)
    {
        //This is where the big time work comes in.
        System.out.println("LayerID: " + layerID);
        RasterToLayer rtl = new RasterToLayer(width, height, layerID);
        BufferedImage correspondingImage = getReferencedImage(layerGen);

        //Now look for the following pattern:
        /*
         * <maprgb rgb='FFFFFF' image="floor1.png">
         <attribute>floor</attribute>                
         </maprgb>
         */

        for (int i = 0; i < layerGen.getChildNodes().getLength(); i++)
        {
            Node n = layerGen.getChildNodes().item(i);

            if (n.getNodeName().equals("maprgb"))
            {
                //First get the rgb value.
                int rgb = maprgb_getRGBAttribute(n);
                String imageLoc = maprgb_getTileImage(n);
                String[] attributes = maprgb_getAttributes(n);

                rtl.maprgb(rgb, imageLoc, attributes);
            }
        }


        for (int i = 0; layer != null && i < layer.getChildNodes().getLength(); i++)
        {
            /*
             * <tile x="1" y="26">
             <attribute>helptile</attribute>
             <attribute>additionalAttributes</attribute>
             <attribute>something</attribute>
             </tile>
             */

            Node n = layer.getChildNodes().item(i);

            if (n.getNodeName().equals("tile"))
            {
                int x = getNumericalAttribute(n, "x");
                int y = getNumericalAttribute(n, "y");

                String[] attributes = maprgb_getAttributes(n);

                for (String attr : attributes)
                {
                    rtl.mapattribute(x, y, attr);
                }
            }
        }

        return rtl.generateLayer(correspondingImage);
    }

    private int getNumericalAttribute(Node tile, String key)
    {
        String strint = tile.getAttributes().getNamedItem(key).getNodeValue();
        return Integer.parseInt(strint);
    }

    private String[] maprgb_getAttributes(Node maprgb)
    {
        List<String> attributes = new ArrayList<String>();
        for (int i = 0; i < maprgb.getChildNodes().getLength(); i++)
        {
            Node child = maprgb.getChildNodes().item(i);
            if (child.getNodeName().equals("attribute") && child.getChildNodes().getLength() > 0)
            {
                String attr = child.getChildNodes().item(0).getNodeValue();
                attributes.add(attr);
            }
        }

        String[] attrs = attributes.toArray(new String[0]);
        return attrs;
    }

    private String maprgb_getTileImage(Node maprgb)
    {
        return maprgb.getAttributes().getNamedItem("image").getNodeValue();
    }

    private int maprgb_getRGBAttribute(Node maprgb)
    {
        String value = maprgb.getAttributes().getNamedItem("rgb").getNodeValue();
        String strAlpha = "FF";
        String strRed = null;
        String strGreen = null;
        String strBlue = null;

        if (value.length() == 8)
        {
            strAlpha = value.substring(0, 2);
            strRed = value.substring(2, 4);
            strGreen = value.substring(4, 6);
            strBlue = value.substring(6, 8);
        }
        else if (value.length() == 6)
        {
            strAlpha = "FF";
            strRed = value.substring(0, 2);
            strGreen = value.substring(2, 4);
            strBlue = value.substring(4, 6);
        }
        else
        {
            System.out.println("Malformed rgb attribute specified.");

        }

        int alpha = Integer.parseInt(strAlpha, 16);
        int red = Integer.parseInt(strRed, 16);
        int green = Integer.parseInt(strGreen, 16);
        int blue = Integer.parseInt(strBlue, 16);

        Color c = new Color(red, green, blue, alpha);

        return c.getRGB();
    }

    private BufferedImage getReferencedImage(Node layerGen)
    {
        String strImgLoc = layerGen.getAttributes().getNamedItem("imgloc").getNodeValue();
        //Now figure out if it is in our collection.
        BufferedImage layerImg;
        if (this.getLayerImages().containsKey(strImgLoc))
        {
            layerImg = getLayerImages().get(strImgLoc);
        }
        else
        {
            URL layerLoc;
            try
            {
                layerLoc = MainTileFactory.getInstance().getImageURL(strImgLoc);
            }
            catch (NoSuchElementException e)
            {
                layerLoc = MainTileFactory.getInstance().getImageURL("transparent.png");
            }

            //If we can find the picture
            if (layerLoc != null)
            {
                layerImg = MainTileFactory.getInstance().getImage(layerLoc);
            }
            else
            {
                System.out.println("Could not find image specified: '" + strImgLoc + "' in zip file or in internal store.");
                return new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
            }
        }

        return layerImg;
    }

    private Layer getPictureLayer(Node layerGen, Node layer)
    {
        BufferedImage layerImg = getReferencedImage(layerGen);
        LargePictureGen lpg = new LargePictureGen(width, height);
        return lpg.generateLayer(layerImg, true);
    }

    private Layer getLayer(Node layerGen, Node layer, String layerID)
    {
        //The layerGen defines what generator to use and parameters for it.
        //The layer defines attributes for specific tiles within the layer.
        String layerType = getLayerGenType(layerGen);

        if (layerType.equals("color"))
        {
            return getColorLayer(layerGen, layer);
        }
        if (layerType.equals("rgbmap"))
        {
            return getRGBLayer(layerGen, layer, layerID);
        }
        if (layerType.equals("picture"))
        {
            return getPictureLayer(layerGen, layer);
        }
        else
        {
            return new Layer(width, height);
        }
    }

    private LayerStack getLayers(Document xmldoc)
    {
        LayerStack result = new LayerStack(width, height);
        //There ought to be two sections: The XML mapgen part and the map part.
        //If the mapgen part is missing, then we should default to something...maybe?

        //First, read in the map gen part.

        Element ae = xmldoc.getDocumentElement();

        Node mapgen = ae.getElementsByTagName("mapgen").item(0); //Get the first one, ignore all others.
        Node map = ae.getElementsByTagName("map").item(0);

        Map<String, Node> mapgenLayers = new HashMap<String, Node>();
        Map<String, Node> mapLayers = new HashMap<String, Node>();

        MapGenForm phase1mgf = super.initMapGenForm();
        phase1mgf.loadProgress.setString("Reading tile attribute data...");
        for (int i = 0; i < mapgen.getChildNodes().getLength(); i++)
        {
            Node nd = mapgen.getChildNodes().item(i);
            if (nd.getNodeType() == Node.ELEMENT_NODE && nd.getNodeName().equals("layer"))
            {
                String id = nd.getAttributes().getNamedItem("id").getNodeValue();
                mapgenLayers.put(id, nd);
            }
            phase1mgf.loadProgress.setValue(phase1mgf.loadProgress.getValue() + 100 / mapgen.getChildNodes().getLength());
        }
        phase1mgf.setVisible(false);

        MapGenForm phase2mgf = super.initMapGenForm();
        phase2mgf.loadProgress.setString("Reading layer archives...");
        for (int i = 0; i < map.getChildNodes().getLength(); i++)
        {
            Node nd = map.getChildNodes().item(i);
            if (nd.getNodeType() == Node.ELEMENT_NODE && nd.getNodeName().equals("layer"))
            {
                String id = nd.getAttributes().getNamedItem("id").getNodeValue();
                mapLayers.put(id, nd);
            }
            phase2mgf.loadProgress.setValue(phase1mgf.loadProgress.getValue() + 100 / map.getChildNodes().getLength());
        }


        for (String layerID : mapgenLayers.keySet())
        {
            Layer layer = getLayer(mapgenLayers.get(layerID), mapLayers.get(layerID), layerID);
            result.addLayer(layerID, layer);
            System.out.println("Added layer: " + layerID);
        }

        System.out.println(mapgen.getNodeName());
        System.out.println(map.getNodeName());

        phase1mgf.dispose();
        phase2mgf.dispose();
        return result;
    }

    private Document getMapXML() throws IOException, ParserConfigurationException, SAXException
    {
        if (super.getMetaData().containsKey("map.xml") == false)
        {
            throw new IOException("Could not locate map.xml");
        }
        String xmlmap = super.getMetaData().get("map.xml").toString();

        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setSchema(loadMapGenSchema());
        DocumentBuilder builder = factory.newDocumentBuilder();
        InputSource is = new InputSource(new StringReader(xmlmap));

        Document mapdoc = builder.parse(is);

        System.out.println(mapdoc.getDocumentElement().getNodeName());


        return mapdoc;
    }

    @Override
    public LayerStack generateMap()
    {
        autodetectSize();
        try
        {
            LayerStack ls = getLayers(getMapXML());
            ls.setOrigin(getOrigin());
            return ls;
        }
        catch (IOException e)
        {
            handleExceptionClause(e);
        }
        catch (ParserConfigurationException ex)
        {
            Logger.getLogger(XMLMapGenerator.class.getName()).log(Level.SEVERE, null, ex);
        }
        catch (SAXException ex)
        {
            Logger.getLogger(XMLMapGenerator.class.getName()).log(Level.SEVERE, null, ex);
        }

        return null;
    }
    
    private void handleExceptionClause(Exception ex)
    {
        System.out.println(ex.toString());
            Logger
                    .getLogger(XMLMapGenerator.class
                    .getName()).log(Level.SEVERE, null, ex);
    }
}
