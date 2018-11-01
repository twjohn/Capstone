package sample;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
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
import java.util.logging.Level;
import java.util.logging.Logger;

public class Controller implements Initializable {

    //FXML elements
    public Label TX_Text,errCatcher;
    public Button genRep;
    public ComboBox tx_cb,paModules;
    public TextField channel, tpo;
    public ImageView repImage;

    //var used to get Transmitter ID
    private String ID;
    private int pa;

    //////////////////////////START AT RUNTIME initialize()//////////////////////////////////////////
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        //////////////////////////start non-numerical input checker//////////////////////////////////////////
        channel.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue,
                                String newValue) {
                if (!newValue.matches("\\d*"))
                    channel.setText(newValue.replaceAll("[^\\d]", ""));
            }
        });
        tpo.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue,
                                String newValue) {
                if (!newValue.matches("\\d*"))
                    tpo.setText(newValue.replaceAll("[^\\d]", ""));
            }
        });
        //////////////////////////////// end non-numerical input checker///////////////////////////////////

        //print out that RF Tool is loading
        System.out.println("Loading RF Tool...");
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
            document = builder.parse(new File("D:\\CapstoneComputingProject(CSC495,496)\\my_rf_tool\\src\\sample\\test.xml"));
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
                pa = Integer.parseInt(elem.getElementsByTagName("PA").item(0).getChildNodes().item(0).getNodeValue());

                int cabinets = Integer.parseInt(elem.getElementsByTagName("Cabinets").item(0).getChildNodes().item(0).getNodeValue());

                int powerblocks = Integer.parseInt(elem.getElementsByTagName("Powerblocks").item(0).getChildNodes().item(0).getNodeValue());

                String linesize = elem.getElementsByTagName("Linesize").item(0).getChildNodes().item(0).getNodeValue();

                transmitters.add(new sample.Transmitter(ID, pa, cabinets, powerblocks, linesize));
                //read transmitter ID's into tx_cb combo box
                tx_cb.getItems().add(ID);
            }
        }
    }
    ////////////////////////////////END initialize()///////////////////////////////////////////////////

    //////////////////////////////// start genNewRep() ////////////////////////////////////////////////
    //@FXML
     public  void genNewRep(){
        ////////////////////////////////Display image in image view////////////////////////////////////////
        if(tx_cb.getValue()==null){
            System.out.println("Not enough information available to generate proper diagram...");
            errCatcher.setTextFill(Color.web("#ff0000"));
            errCatcher.setText("Not enough information available to generate proper diagram...");

        }
        else {
            errCatcher.setTextFill(Color.web("#00bc4e"));
            errCatcher.setText("RF Diagram generated...");
            File file = new File("D:\\CapstoneComputingProject(CSC495,496)\\my_rf_tool\\src\\sample\\gates.png");
            Image image = new Image(file.toURI().toString());
            repImage.setImage(image);
            System.out.println("RF Diagram for specified input found \n displaying diagram now...");
        }
        ////////////////////////////////end Display image in image view////////////////////////////////////
        genRep.setOnMouseClicked((event) -> {
            try {
                FXMLLoader fxmlLoader = new FXMLLoader();
                fxmlLoader.setLocation(getClass().getResource("repStage.fxml"));
                Scene scene = new Scene(fxmlLoader.load(), 600, 400);
                Stage stage = new Stage();
                stage.setTitle("Generated report");
                stage.setScene(scene);
                stage.show();
            }

            catch (IOException e){
                Logger logger = Logger.getLogger(getClass().getName());
                logger.log(Level.SEVERE, "Failed to create generate report.", e);
            }
        });
        }
        ////////////////////////////End genNewRep()/////////////////////////////////////////

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
            document = builder.parse(new File("D:\\CapstoneComputingProject(CSC495,496)\\my_rf_tool\\src\\sample\\test.xml"));
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
                pa = Integer.parseInt(elem.getElementsByTagName("PA").item(0).getChildNodes().item(0).getNodeValue());

                int cabinets = Integer.parseInt(elem.getElementsByTagName("Cabinets").item(0).getChildNodes().item(0).getNodeValue());

                int powerblocks = Integer.parseInt(elem.getElementsByTagName("Powerblocks").item(0).getChildNodes().item(0).getNodeValue());

                String linesize = elem.getElementsByTagName("Linesize").item(0).getChildNodes().item(0).getNodeValue();

                transmitters.add(new sample.Transmitter(ID, pa, cabinets, powerblocks, linesize));

                //breaks for loop when ID == tx_sb value. need ID for populating label element. display info to console about selection
                if(ID.equals(tx_cb.getValue())) {
                    System.out.println(ID+" selected");
                    System.out.println(pa+" PA Modules for "+ID);
                    //delete vals when new option is selected. prevents someone from picking incorrect number of PA modules for transmitter
                    paModules.getItems().clear();
                    //paModules.getItems().add(pa);
                    break;
                }
                }

            }
            for (sample.Transmitter tx : transmitters) {
                if(ID.equals(tx_cb.getValue())) {
                    TX_Text.setText(tx.toString());
                   // paModules.setValue(pa);
                }
            }
        }
    }


