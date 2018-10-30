package sample;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class Controller implements Initializable {

    //FXML elements
    public Label TX_Text;
    public Button genRep;
    public ComboBox tx_cb;

    //var used to get Transmitter ID
    private String ID;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        //print out that RF Tool is loading
        System.out.println("Loading RF Tool...");

    }

    public void getTXInfo() {
        System.out.println(" ");    //just adding some space for clarity on console print out
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = null;
        try {
            builder = factory.newDocumentBuilder();
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        }

        // Load the input XML document, parse it and return an instance of the document class. will need to make this work on any computer somehow.
        Document document = null;
        try {
            document = builder.parse(new File("C:\\Users\\Tyler\\IdeaProjects\\my_rf_tool\\src\\sample\\test.xml"));
        } catch (SAXException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        //refere to Transmitter.java
        List<sample.Transmitter> transmitters = new ArrayList<sample.Transmitter>();
        NodeList nodeList = document.getDocumentElement().getChildNodes();
        for (int i = 0; i < nodeList.getLength(); i++) {        //loop through to get every item and its attributes from the test.xml
            Node node = nodeList.item(i);

            if (node.getNodeType() == Node.ELEMENT_NODE) {
                Element elem = (Element) node;

                // Get the value of the ID attribute.
               ID = node.getAttributes().getNamedItem("ID").getNodeValue();

                // Get the value of all sub-elements.
                int pa = Integer.parseInt(elem.getElementsByTagName("PA").item(0).getChildNodes().item(0).getNodeValue());

                int cabinets = Integer.parseInt(elem.getElementsByTagName("Cabinets").item(0).getChildNodes().item(0).getNodeValue());

                int powerblocks = Integer.parseInt(elem.getElementsByTagName("Powerblocks").item(0).getChildNodes().item(0).getNodeValue());

                String linesize = elem.getElementsByTagName("Linesize").item(0).getChildNodes().item(0).getNodeValue();

                transmitters.add(new sample.Transmitter(ID, pa, cabinets, powerblocks, linesize));

                //breaks for loop when ID == tx_sb value. need ID for populating label element.
                if(ID.equals(tx_cb.getValue()))
                        break;
            }
        }

        for (sample.Transmitter tx : transmitters) {
                if(ID.equals(tx_cb.getValue()))
                     TX_Text.setText(tx.toString());
            }

        }

    }


