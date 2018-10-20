package sample;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.sound.midi.Receiver;
import javax.sound.midi.Transmitter;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class Main extends Application {


    @Override
    public void start(Stage primaryStage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("sample.fxml"));
        primaryStage.setTitle("RF Tool");
        primaryStage.setScene(new Scene(root, 300, 275));
        primaryStage.show();

        System.out.println(" ");    //just adding some space for clarity on console print out



            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();

            // Load the input XML document, parse it and return an instance of the document class. will need to make this work on any computer somehow.
            Document document = builder.parse(new File("C:\\Users\\vdasilva\\Desktop\\my_rf_tool\\Capstone\\my_rf_tool\\src\\sample\\test.xml"));

            //refere to Transmitter.java
            List<sample.Transmitter> transmitters = new ArrayList<sample.Transmitter>();
            NodeList nodeList = document.getDocumentElement().getChildNodes();
            for (int i = 0; i < nodeList.getLength(); i++) {        //loop through to get every item and its attributes from the test.xml
                Node node = nodeList.item(i);

                if (node.getNodeType() == Node.ELEMENT_NODE) {
                    Element elem = (Element) node;

                    // Get the value of the ID attribute.
                    String ID = node.getAttributes().getNamedItem("ID").getNodeValue();

                    // Get the value of all sub-elements.
                    int pa = Integer.parseInt(elem.getElementsByTagName("PA").item(0).getChildNodes().item(0).getNodeValue());

                    int cabinets = Integer.parseInt(elem.getElementsByTagName("Cabinets").item(0).getChildNodes().item(0).getNodeValue());

                    int powerblocks = Integer.parseInt(elem.getElementsByTagName("Powerblocks").item(0).getChildNodes().item(0).getNodeValue());

                    String linesize = elem.getElementsByTagName("Linesize").item(0).getChildNodes().item(0).getNodeValue();

                    transmitters.add(new sample.Transmitter(ID, pa, cabinets, powerblocks, linesize));
                }
            }

            // Print all employees.
            for (sample.Transmitter tx : transmitters)
                System.out.println(tx.toString());
        }

    }


