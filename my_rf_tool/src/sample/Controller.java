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
    public ComboBox tx_cb,paModules,exciter,filter;
    public TextField channel, tpo;
    public ImageView repImage;

    //var used to get Transmitter ID
    private String ID;
    private int pa;
    private String Filter;

    //////////////////////////START AT RUNTIME initialize()//////////////////////////////////////////
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        //////////////////////////start non-numerical input checker//////////////////////////////////////////
        exciter.getSelectionModel().selectFirst();
        paModules.getSelectionModel().selectFirst();
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
            document = builder.parse(new File("H:\\CapstoneComputingProject(CSC495,496)\\my_rf_tool\\src\\sample\\test.xml"));
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
                ID = node.getAttributes().getNamedItem("ID").getNodeValue();// transmitters(i.e. ULXTE-2)
                // Get the value of all sub-elements.
                pa = Integer.parseInt(elem.getElementsByTagName("PA").item(0).getChildNodes().item(0).getNodeValue());//pa modules
                int cabinets = Integer.parseInt(elem.getElementsByTagName("Cabinets").item(0).getChildNodes().item(0).getNodeValue());//cabinets
                int powerblocks = Integer.parseInt(elem.getElementsByTagName("Powerblocks").item(0).getChildNodes().item(0).getNodeValue());//power blocks
                String linesize = elem.getElementsByTagName("Linesize").item(0).getChildNodes().item(0).getNodeValue();//line size
                transmitters.add(new sample.Transmitter(ID, pa, cabinets, powerblocks, linesize));//call constructor in Transmitter.java to set values for variables
                //read transmitter ID's into tx_cb combo box
                tx_cb.getItems().add(ID);//add each ID(i.e. ULXTE-2) to tx_cb combo box
                System.out.println(ID+ " added to Transmitter combo box");//print info to console
            }
        }
    }
    ////////////////////////////////END initialize()///////////////////////////////////////////////////

    //////////////////////////////// start genNewRep() ////////////////////////////////////////////////
    //@FXML
     public  void genNewRep(){
        ////////////////////////////////Open new window for report and rf diagram////////////////////////////////////////
        if(tx_cb.getValue()==null){//conditional to tell if enough information was provide to construct a rf diagram
            System.out.println("Not enough information available to generate proper diagram...");
            errCatcher.setTextFill(Color.web("#ff0000"));
            errCatcher.setText("Not enough information available to generate proper diagram...");

        }
        else {
            genRep.setOnMouseClicked((event) -> {
                try {//when genRep button is clicked, a event occurs and opens a new window, if unable to open, a error is caught
                    FXMLLoader fxmlLoader = new FXMLLoader();
                    fxmlLoader.setLocation(getClass().getResource("repStage.fxml"));
                    //FXMLLoader loader=new FXMLLoader(getClass().getResource("repStage.fxml"));
                    Scene scene = new Scene(fxmlLoader.load(), 600, 400);
                    Stage stage = new Stage();
                    stage.setTitle("Generated report");
                    stage.setScene(scene);
                    stage.show();


                  //  repStageController tx = new repStageController();
                   // tx.setVals(1);
                }

                catch (IOException e){
                    Logger logger = Logger.getLogger(getClass().getName());
                    logger.log(Level.SEVERE, "Failed to create generate report.", e);
                }
            });
            //can delete this section later based upon the route we take for generating rf diagram and item report
            errCatcher.setTextFill(Color.web("#00bc4e"));
            errCatcher.setText("RF Diagram generated...");
            File file = new File("H:\\CapstoneComputingProject(CSC495,496)\\my_rf_tool\\src\\sample\\gates.png");
            Image image = new Image(file.toURI().toString());
            repImage.setImage(image);
            System.out.println("RF Diagram for specified input found \n displaying diagram now...");
            ///////////////////////////////////////////////////////////////////////////////////////////////////////
        }
        ////////////////////////////////end open new window for report and rf diagram////////////////////////////////////

        }
        ////////////////////////////End genNewRep()/////////////////////////////////////////

    public void getTXInfo() {//event for when a transmitter is picked from tx_cb combo box
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
            document = builder.parse(new File("H:\\CapstoneComputingProject(CSC495,496)\\my_rf_tool\\src\\sample\\test.xml"));
        } catch (SAXException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        //refere to Transmitter.java
        List<Transmitter> transmitters = new ArrayList<Transmitter>();
        NodeList nodeList = document.getDocumentElement().getChildNodes();
        for (int i = 0; i < nodeList.getLength(); i++) {        //loop through to get every item and its attributes from the test.xml
            Node node = nodeList.item(i);

            if (node.getNodeType() == Node.ELEMENT_NODE) {
                Element elem = (Element) node;

                // Get the value of the ID attribute.
               ID = node.getAttributes().getNamedItem("ID").getNodeValue();//transmitters(i.e. ULXTE-2)
                // Get the value of all sub-elements.
                pa = Integer.parseInt(elem.getElementsByTagName("PA").item(0).getChildNodes().item(0).getNodeValue());//pa modules
                int cabinets = Integer.parseInt(elem.getElementsByTagName("Cabinets").item(0).getChildNodes().item(0).getNodeValue());// cabinets
                int powerblocks = Integer.parseInt(elem.getElementsByTagName("Powerblocks").item(0).getChildNodes().item(0).getNodeValue());// power blocks
                String linesize = elem.getElementsByTagName("Linesize").item(0).getChildNodes().item(0).getNodeValue();//line size
                transmitters.add(new Transmitter(ID, pa, cabinets, powerblocks, linesize));//call constructor in Transmitter.java to set values for variables

                //breaks for loop when ID == tx_cb value. need ID for populating label element. display info to console about selection.
                if(ID.equals(tx_cb.getValue())) {
                    //gets each individual type of filter for selected transmitter

                    //////////////////////////////testing passing vars to new scene///////////
                   // FXMLLoader loader=new FXMLLoader(getClass().getResource("repStage.fxml"));

                  //  repStageController tx = loader.getController();
                   //tx.setVals(1);
                    /////////////////////////////////////////////////////////////////////////
                    NodeList FilterNameList = elem.getElementsByTagName("Filter");
                    filter.getItems().clear();//clear combo box and repopulate with appropriate options for filter
                    for(int count = 0; count < FilterNameList.getLength(); count++) { //looping through filters to get all possible filters associated with selected transmitter
                        Node node1 = FilterNameList.item(count);
                        if(node1.getNodeType() == node1.ELEMENT_NODE){
                            Element Filt = (Element) node1;
                            Filter = Filt.getTextContent();// set variable Filter equal to the content read from Filter XML tag
                            System.out.println(Filter+" loaded into filter combo box");//print to console
                            filter.getItems().add(Filter);//add filters to filter combo box
                        }
                    }
                    System.out.println(ID+" selected");
                    System.out.println(pa+" PA Modules for "+ID);
                    break;
                    }
                }

            }
            for (Transmitter tx : transmitters) {
                if(ID.equals(tx_cb.getValue())) {
                    TX_Text.setText(tx.toString());
                }
            }
        }
    }


