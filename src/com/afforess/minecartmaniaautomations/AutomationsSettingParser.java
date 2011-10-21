package com.afforess.minecartmaniaautomations;

import java.io.File;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Comment;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.afforess.minecartmaniacore.config.MinecartManiaConfigurationParser;
import com.afforess.minecartmaniacore.config.SettingParser;
import com.afforess.minecartmaniacore.debug.MinecartManiaLogger;
import com.afforess.minecartmaniacore.utils.ItemUtils;
import com.afforess.minecartmaniacore.world.AbstractItem;

public class AutomationsSettingParser implements SettingParser {
    
    private static final double version = 1.0;
    private static MinecartManiaLogger log = MinecartManiaLogger.getInstance();
    
    public boolean isUpToDate(Document document) {
        try {
            NodeList list = document.getElementsByTagName("version");
            Double version = MinecartManiaConfigurationParser.toDouble(list.item(0).getChildNodes().item(0).getNodeValue(), 0);
            log.debug("Automations Config read: version: " + list.item(0).getTextContent());
            if (version == 1.3) {
                //Place the code to update to the next version here
                //version = 1.4;    //This needs to be updated to the next version of the document.
                //list.item(0).setTextContent(version.toString());
            }
            if (MinecartManiaAutomations.unrestrictedBlocks.isEmpty()) {
                setDefaultConfiguration();
            }
            return version == AutomationsSettingParser.version;
        } catch (Exception e) {
            return false;
        }
    }
    
    public boolean read(Document document) {
        NodeList list;
        try {
            list = document.getElementsByTagName("MinecartManiaConfiguration").item(0).getChildNodes(); //get the root nodes of the ConfigurationTree
            String elementChildName = ""; //holds the name of the node
            String elementChildValue = ""; //holds the value of the node
            //loop through each of the child nodes of the document
            for (int idx = 0; idx < list.getLength(); idx++) {
                Node elementChild = list.item(idx); //extract the node
                elementChildName = ""; //reset the child name
                elementChildValue = null; //reset the child value
                //do we have a valid element node
                if (elementChild.getNodeType() == Node.ELEMENT_NODE) {
                    elementChildName = elementChild.getNodeName(); //get the node name
                    elementChildValue = elementChild.getTextContent(); //get the node value
                    if (elementChildValue != null && elementChildValue != "") {
                        //Handle the possible nodes we have at this level.
                        if (elementChildName == "version") {
                            if (elementChildValue != String.valueOf(version)) { /* documentUpgrade(document); */
                            }
                        } else if (elementChildName == "UnrestrictedBlock") {
                            MinecartManiaAutomations.unrestrictedBlocks.add(ItemUtils.getFirstItemStringToMaterial(elementChildValue));
                            log.debug("Automations Config added unrestricted block: " + elementChildValue);
                        } else {
                            log.info("Chest Control Config read unknown node: " + elementChildName);
                        }
                    }
                }
            }
        } catch (Exception e) {
            return false;
        }
        return true;
    }
    
    private void setDefaultConfiguration() {
        MinecartManiaAutomations.unrestrictedBlocks.add(ItemUtils.getFirstItemStringToMaterial("1"));
        MinecartManiaAutomations.unrestrictedBlocks.add(ItemUtils.getFirstItemStringToMaterial("2"));
        MinecartManiaAutomations.unrestrictedBlocks.add(ItemUtils.getFirstItemStringToMaterial("3"));
        MinecartManiaAutomations.unrestrictedBlocks.add(ItemUtils.getFirstItemStringToMaterial("4"));
        MinecartManiaAutomations.unrestrictedBlocks.add(ItemUtils.getFirstItemStringToMaterial("12"));
        MinecartManiaAutomations.unrestrictedBlocks.add(ItemUtils.getFirstItemStringToMaterial("13"));
        MinecartManiaAutomations.unrestrictedBlocks.add(ItemUtils.getFirstItemStringToMaterial("14"));
        MinecartManiaAutomations.unrestrictedBlocks.add(ItemUtils.getFirstItemStringToMaterial("15"));
        MinecartManiaAutomations.unrestrictedBlocks.add(ItemUtils.getFirstItemStringToMaterial("16"));
        MinecartManiaAutomations.unrestrictedBlocks.add(ItemUtils.getFirstItemStringToMaterial("21"));
    }
    
    public boolean write(File configuration, Document document) {
        try {
            if (document == null) {
                DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
                DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
                //root elements
                document = docBuilder.newDocument();
                document.setXmlStandalone(true);
                Element rootElement = document.createElement("MinecartManiaConfiguration");
                document.appendChild(rootElement);
                
                Element setting = document.createElement("version");
                setting.appendChild(document.createTextNode(String.valueOf(version)));
                rootElement.appendChild(setting);
                
                setting = document.createElement("UnrestrictedBlocks");
                Comment comment = document.createComment("Blocks that all users can AutoMine");
                for (AbstractItem item : MinecartManiaAutomations.unrestrictedBlocks) {
                    Element child = document.createElement("UnrestrictedBlock");
                    child.appendChild(document.createTextNode(item.toMaterial().name()));
                    setting.appendChild(child);
                }
                rootElement.appendChild(setting);
                rootElement.insertBefore(comment, setting);
            }
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");
            DOMSource source = new DOMSource(document);
            StreamResult result = new StreamResult(configuration);
            transformer.transform(source, result);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }
}
