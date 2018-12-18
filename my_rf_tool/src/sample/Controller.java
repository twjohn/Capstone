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
import javafx.scene.control.*;
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

import javax.swing.text.AbstractDocument;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;


public class Controller implements Initializable {

    private final int hardCodeVal = 14;/** hard coded value for getting pa module descriptions at addPAModules()----- change to find better wat later**/

    /** FXML elements **/
    public Label TX_Text, errCatcher;
    public Button genRep;
    public ComboBox tx_cb, paModules, mainExciterSW, filter, switchPatch, testLoad;
    public TextField channel, tpo;
    public ImageView repImage;
    public CheckBox dualExciter;

    /** var used to get Transmitter ID **/
    private static String ID, linesize;
    private static int pa, powerblocks;
    private static Double powerRating;
    protected static String txSelection;

    /** vars to hold various info about information needed **/
    public static Double TestPower, switchPower, channelNumber, channelLimit, filterPower;
    public static String filterSize, filterPIDDescription, filterPID;
    public static int cabinets;
    public static String SWPIDDESCRIPTION, SWPID;
    public static String paModuleDescription, paModulePID;

    /** selected values-- values passed to new scene-- report **/
    public static String selectedSWDescription, selectedFilterDescription, selectedPADescription;

    /** override function to run things needed at run time(combobox population, textbox input checker, etc.) **/
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        /********************** START non-numerical input checker **********************/

        tpo.setText("1");/** set default values for tpo textbox **/

        channel.setText("2");/** set default values for channel textbox **/

        addMainSWInfo();/** Loads Software info upon window initialization **/
        addPAModules();/** Loads PA Modules info uon window initialization **/

        mainExciterSW.getSelectionModel().selectFirst();/** Automatically picks first selection for mainExciterSW **/
        paModules.getSelectionModel().selectFirst(); /** Automatically picks first selection for paModules **/

        /***************** channel textbox input checker(allows integers only) *****************/
        channel.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                if (!newValue.matches("\\d*")) channel.setText(newValue.replaceAll("[^\\d]", ""));
            }
        });

        /***************** tpo textbox input checker(allows decimal numbers and integers) *****************/
        tpo.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                if (!newValue.matches("\\d*")) tpo.setText(newValue.replaceAll("[^\\d.]", ""));
            }
        });
        /********************** END non-numerical input checker **********************/

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

        /** Load the input XML document, parse it and return an instance of the document class. install on computers C drive to work on any computer **/

        Document document = null;
        try {
            document = builder.parse(new File("E:\\CapstoneComputingProject(CSC495,496)\\my_rf_tool\\src\\sample\\test.xml"));
        } catch (SAXException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        //refere to Transmitter.java
        List<sample.Transmitter> transmitters = new ArrayList<sample.Transmitter>();
        NodeList nodeList = document.getDocumentElement().getChildNodes();
        for (int i = 0; i < nodeList.getLength(); i++) {/** loop through to get every item and its attributes from the test.xml **/
            Node node = nodeList.item(i);
            if (node.getNodeType() == Node.ELEMENT_NODE) {
                Element elem = (Element) node;
                /** Get the value of the ID attribute **/
                ID = node.getAttributes().getNamedItem("ID").getNodeValue();// transmitters(i.e. ULXTE-2)
                /**  Get the value of all sub-elements **/
                pa = Integer.parseInt(elem.getElementsByTagName("PA").item(0).getChildNodes().item(0).getNodeValue());//pa modules
                int cabinets = Integer.parseInt(elem.getElementsByTagName("Cabinets").item(0).getChildNodes().item(0).getNodeValue());//cabinets
                int powerblocks = Integer.parseInt(elem.getElementsByTagName("Powerblocks").item(0).getChildNodes().item(0).getNodeValue());//power blocks
                String linesize = elem.getElementsByTagName("Linesize").item(0).getChildNodes().item(0).getNodeValue();//line size
                powerRating = Double.parseDouble(elem.getElementsByTagName("Power").item(0).getChildNodes().item(0).getNodeValue());

                transmitters.add(new sample.Transmitter(ID, pa, cabinets, powerblocks, linesize, powerRating));//call constructor in Transmitter.java to set values for variables
                /** read transmitter ID's into tx_cb combo box **/
                if(powerRating > Double.parseDouble(tpo.getText()))
                tx_cb.getItems().add(ID);/** add each ID(i.e. ULXTE-2) to tx_cb combo box **/

                System.out.println(ID + " added to Transmitter combo box");//print info to console
            }
        }
    }

    /** Method to generate a report based upon user input-- opens new window that displays item report and rf diagram **/
    public void genNewRep() {

        if (tx_cb.getValue() == null) {//conditional to tell if enough information was provide to construct a rf diagram
            System.out.println("Not enough information available to generate proper diagram...");
            errCatcher.setTextFill(Color.web("#ff0000"));
            errCatcher.setText("Not enough information available to generate proper diagram...");

        } else {
            genRep.setOnMouseClicked((event) -> {
                try {/** when genRep button is clicked, a event occurs and opens a new window, if unable to open, a error is caught **/

                    /** get needed product ID's (PID) **/
                    getSwPID();
                    getFilterPID();
                    getPaPID();

                    /** get selected values to pass to new window **/
                    selectedSWDescription = (String)mainExciterSW.getValue();
                    selectedFilterDescription = (String)filter.getValue();
                    selectedPADescription = (String)paModules.getValue();
                    txSelection = (String)tx_cb.getValue();

                    System.out.println("my troubleshot-------------" +selectedSWDescription);
                    FXMLLoader fxmlLoader = new FXMLLoader();
                    fxmlLoader.setLocation(getClass().getResource("repStage.fxml"));
                    Scene scene = new Scene(fxmlLoader.load(), 600, 400);
                    Stage stage = new Stage();
                    stage.setTitle("Generated report");
                    stage.setScene(scene);
                    stage.show();
                } catch (IOException e) {//catch error opening window
                    Logger logger = Logger.getLogger(getClass().getName());
                    logger.log(Level.SEVERE, "Failed to create generate report.", e);
                }
            });
            /** displays and opens new window when proper input is detected for all cases **/
            errCatcher.setTextFill(Color.web("#00bc4e"));
            errCatcher.setText("RF Diagram generated...");
        }
    }

     /** simple method to check if tpo textfield is empty and sets focus to it **/
    public void tpoCheck() {
        if (tpo.getText().isEmpty()) {
            tpo.selectAll();
            tpo.requestFocus();
        } else System.out.println(tpo.getText());
    }

    /** method to check if dual exciter check box is selected returns true if selected **/
    public boolean checkDualExciters() {
        if (dualExciter.isSelected())
            return true;
        else return false;
    }

    /** event for when a transmitter is picked from tx_cb combo box **/
    public void getTXInfo() {

        tpoCheck(); //check tpo
        channelNumber = Double.parseDouble(channel.getText());//set channelNumber
        System.out.println(channelNumber);

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
            document = builder.parse(new File("E:\\CapstoneComputingProject(CSC495,496)\\my_rf_tool\\src\\sample\\test.xml"));
        } catch (SAXException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        //refer to Transmitter.java
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
                cabinets = Integer.parseInt(elem.getElementsByTagName("Cabinets").item(0).getChildNodes().item(0).getNodeValue());// cabinets
                powerblocks = Integer.parseInt(elem.getElementsByTagName("Powerblocks").item(0).getChildNodes().item(0).getNodeValue());// power blocks
                linesize = elem.getElementsByTagName("Linesize").item(0).getChildNodes().item(0).getNodeValue();//line size
                powerRating = Double.parseDouble(elem.getElementsByTagName("Power").item(0).getChildNodes().item(0).getNodeValue());

                transmitters.add(new Transmitter(ID, pa, cabinets, powerblocks, linesize, powerRating));//call constructor in Transmitter.java to set values for variables

                /** breaks for loop when ID == tx_cb value. need ID for populating label element. display info to console about selection **/
                if (ID.equals(tx_cb.getValue())) {
                    //gets each individual type of filter for selected transmitter

                    NodeList power = elem.getElementsByTagName("Power");
                    Node node2 = power.item(0);

                    System.out.println(ID + " selected");
                    System.out.println(pa + " PA Modules for " + ID);
                    System.out.println(powerRating);
                    break;
                }
            }

        }
        for (Transmitter tx : transmitters) {
            if (ID.equals(tx_cb.getValue())) {
                TX_Text.setText(tx.toString());
            }
        }


        /***************************************************************
        //adding switch options after loading tx options
        ***************************************************************/
        switchPatch.getItems().clear(); //reset switch/patch
        switchPatch.getItems().add("Switch/Patch");
        switchPatch.setValue("Switch/Patch");

        DocumentBuilderFactory switchFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder switchBuilder = null;
        try {
            switchBuilder = switchFactory.newDocumentBuilder();
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        }

        // Load the input XML document, parse it and return an instance of the document class.
        Document switchDocument = null;
        try {
            switchDocument = switchBuilder.parse(new File("E:\\CapstoneComputingProject(CSC495,496)\\my_rf_tool\\src\\sample\\switches.xml"));
        } catch (SAXException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        //refer to Switches.java
        List<sample.Switches> theSwitches = new ArrayList<sample.Switches>();
        NodeList switchNodeList = switchDocument.getDocumentElement().getChildNodes();
        for (int i = 0; i < switchNodeList.getLength(); i++) {        //loop through to get every item and its attributes from the test.xml
            Node switchNode = switchNodeList.item(i);
            if (switchNode.getNodeType() == Node.ELEMENT_NODE) {
                Element switchElement = (Element) switchNode;

                ID = switchNode.getAttributes().getNamedItem("ID").getNodeValue();
                switchPower = Double.parseDouble(switchElement.getElementsByTagName("powerlimit").item(0).getChildNodes().item(0).getNodeValue());
                channelLimit = Double.parseDouble(switchElement.getElementsByTagName("channel").item(0).getChildNodes().item(0).getNodeValue());

                if (switchPower > powerRating) {

                    if (channelNumber < 18 && channelLimit < 18) {
                        theSwitches.add(new sample.Switches(ID, switchPower, channelLimit));
                        switchPatch.getItems().add(ID);
                    } else if (channelNumber > 17 && channelLimit > 17 || channelLimit == 0) {
                        theSwitches.add(new sample.Switches(ID, switchPower, channelLimit));
                        switchPatch.getItems().add(ID);
                    }
                }
            }
        }
        /***************************************************************
        * end adding switch options after loading tx options
        ***************************************************************/


        /***************************************************************
        * adding test load options after loading tx options
        ***************************************************************/
        testLoad.getItems().clear();
        testLoad.getItems().add("Test Load");
        testLoad.setValue("Test Load");

        DocumentBuilderFactory loadsFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder loadsBuilder = null;
        try {
            loadsBuilder = loadsFactory.newDocumentBuilder();
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        }

        Document loadsDocument = null;
        try {
            loadsDocument = loadsBuilder.parse(new File("E:\\CapstoneComputingProject(CSC495,496)\\my_rf_tool\\src\\sample\\loads.xml"));
        } catch (SAXException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        List<sample.Loads> theLoads = new ArrayList<sample.Loads>();
        NodeList loadsNodeList = loadsDocument.getDocumentElement().getChildNodes();
        for (int i = 0; i < loadsNodeList.getLength(); i++) {        //loop through to get every item and its attributes from the test.xml
            Node loadsNode = loadsNodeList.item(i);
            if (loadsNode.getNodeType() == Node.ELEMENT_NODE) {
                Element loadsElement = (Element) loadsNode;

                ID = loadsNode.getAttributes().getNamedItem("ID").getNodeValue();
                TestPower = Double.parseDouble(loadsElement.getElementsByTagName("Power").item(0).getChildNodes().item(0).getNodeValue());

                if (TestPower > powerRating) {
                    theLoads.add(new sample.Loads(ID, TestPower));
                    testLoad.getItems().add(ID);
                }
            }
        }
        /***************************************************************
        //end      adding test load options after loading tx options
        ***************************************************************/
        addFilters();  //Call method to add filters to filter combobox
    }

    /** adding main exciter software options and getting PID for selected software **/
    public void addMainSWInfo() {

        DocumentBuilderFactory mainExciterSWFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder mainExciterSWBuilder = null;

        try { mainExciterSWBuilder = mainExciterSWFactory.newDocumentBuilder(); }
        catch (ParserConfigurationException e) { e.printStackTrace(); }

        Document mainExciterSWDocument = null;
        try { mainExciterSWDocument = mainExciterSWBuilder.parse(new File("E:\\CapstoneComputingProject(CSC495,496)\\my_rf_tool\\src\\sample\\pa_exciter_control.xml")); }
        catch (SAXException e) { e.printStackTrace(); } catch (IOException e) { e.printStackTrace(); }

        mainExciterSW.getItems().clear();
        NodeList SWNodeList = mainExciterSWDocument.getDocumentElement().getChildNodes();

            for (int j = 0; j < SWNodeList.getLength(); j++) {
                Node SWNode = SWNodeList.item(j);
                if (SWNode.getNodeType() == SWNode.ELEMENT_NODE) {
                    Element swElement = (Element) SWNode;

                        SWPIDDESCRIPTION = swElement.getElementsByTagName("PIDDESCRIPTION").item(0).getChildNodes().item(0).getNodeValue();
                        System.out.println(SWPIDDESCRIPTION);
                        if(SWPIDDESCRIPTION.equals("MODULE PA WIDEBAND (TYPE D)(47-750MHz)"))
                            break;
                        mainExciterSW.getItems().add(SWPIDDESCRIPTION);

                }
            }
        }
    public void getSwPID(){

        DocumentBuilderFactory SWIPFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder SWIPBuilder = null;
        try {
            SWIPBuilder = SWIPFactory.newDocumentBuilder();
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        }

        Document loadsDocument = null;
        try {
            loadsDocument = SWIPBuilder.parse(new File("E:\\CapstoneComputingProject(CSC495,496)\\my_rf_tool\\src\\sample\\pa_exciter_control.xml"));
        } catch (SAXException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        List<sample.Loads> exciterSW = new ArrayList<sample.Loads>();
        NodeList loadsNodeList = loadsDocument.getDocumentElement().getChildNodes();
        for (int i = 0; i < loadsNodeList.getLength(); i++) {        //loop through to get every item and its attributes from the test.xml
            Node loadsNode = loadsNodeList.item(i);
            if (loadsNode.getNodeType() == Node.ELEMENT_NODE) {
                Element loadsElement = (Element) loadsNode;

                SWPID = (loadsElement.getElementsByTagName("PID").item(0).getChildNodes().item(0).getNodeValue());
                SWPIDDESCRIPTION = (loadsElement.getElementsByTagName("PIDDESCRIPTION").item(0).getChildNodes().item(0).getNodeValue());
            }
            if (SWPIDDESCRIPTION.equals(mainExciterSW.getValue()))
                break;
        }


    }

    /** adding filter options and getting PID for selected filter **/
    public void addFilters(){
        //Adds filters to filter combobox based on tx power rating and filter power rating
        filter.getItems().clear();
        filter.getItems().add("Filter");
        filter.setValue("Filter");

        DocumentBuilderFactory filteringFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder filterBuilder = null;
        try {
            filterBuilder = filteringFactory.newDocumentBuilder();
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        }

        Document filterDocument = null;
        try {
            filterDocument = filterBuilder.parse(new File("E:\\CapstoneComputingProject(CSC495,496)\\my_rf_tool\\src\\sample\\maskFiltersCouplers.xml"));
        } catch (SAXException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        List<sample.Filtering> theFilters = new ArrayList<sample.Filtering>();
        NodeList filtersNodeList = filterDocument.getDocumentElement().getChildNodes();
        for (int i = 0; i < filtersNodeList.getLength(); i++) {
            Node filtersNode = filtersNodeList.item(i);
            if (filtersNode.getNodeType() == Node.ELEMENT_NODE) {
                Element filtersElement = (Element) filtersNode;

                filterPower = Double.parseDouble((filtersElement.getElementsByTagName("Power").item(0).getChildNodes().item(0).getNodeValue()));
                filterPIDDescription = filtersElement.getElementsByTagName("PIDDESCRIPTION").item(0).getChildNodes().item(0).getNodeValue();
                filterSize = filtersElement.getElementsByTagName("Size").item(0).getChildNodes().item(0).getNodeValue();
                filterPID = filtersElement.getElementsByTagName("PID").item(0).getChildNodes().item(0).getNodeValue();

                if (filterPower > powerRating * 1000 || filterPower * cabinets > powerRating * 1000) { //powerRating is KW need to multiply by 1000 to get into watts
                    theFilters.add(new sample.Filtering(filterPower, filterPIDDescription, filterSize, filterPID));//filterPower is set in watts    (1,000 watts = 1KW)
                    filter.getItems().add(filterPIDDescription + " input size " + filterSize);
                }
            }
        }
    /***************************************************************
    //end adding filter options
    ***************************************************************/
}
    public void getFilterPID(){
    DocumentBuilderFactory filteringFactory = DocumentBuilderFactory.newInstance();
    DocumentBuilder filterBuilder = null;
    try {
        filterBuilder = filteringFactory.newDocumentBuilder();
    } catch (ParserConfigurationException e) {
        e.printStackTrace();
    }

    Document filterDocument = null;
    try {
        filterDocument = filterBuilder.parse(new File("E:\\CapstoneComputingProject(CSC495,496)\\my_rf_tool\\src\\sample\\maskFiltersCouplers.xml"));
    } catch (SAXException e) {
        e.printStackTrace();
    } catch (IOException e) {
        e.printStackTrace();
    }

    List<sample.Filtering> theFilters = new ArrayList<sample.Filtering>();
    NodeList filtersNodeList = filterDocument.getDocumentElement().getChildNodes();
    for (int i = 0; i < filtersNodeList.getLength(); i++) {
        Node filtersNode = filtersNodeList.item(i);
        if (filtersNode.getNodeType() == Node.ELEMENT_NODE) {
            Element filtersElement = (Element) filtersNode;

            filterPIDDescription = filtersElement.getElementsByTagName("PIDDESCRIPTION").item(0).getChildNodes().item(0).getNodeValue();
            filterSize = filtersElement.getElementsByTagName("Size").item(0).getChildNodes().item(0).getNodeValue();
            filterPID = filtersElement.getElementsByTagName("PID").item(0).getChildNodes().item(0).getNodeValue();
        }
        String filterConcatenation = filterPIDDescription + " input size " + filterSize;
        System.out.print("----------------------------- " + filterConcatenation);
        if(filterConcatenation.equals(filter.getValue()))
            break;
    }
}

    /** adding PA Module options and getting PID for selected Pa Module type **/
    public void getPaPID(){
    DocumentBuilderFactory PAModuleFactory = DocumentBuilderFactory.newInstance();
    DocumentBuilder PAModuleBuilder = null;

    try { PAModuleBuilder = PAModuleFactory.newDocumentBuilder(); }
    catch (ParserConfigurationException e) { e.printStackTrace(); }

    Document mainExciterSWDocument = null;
    try { mainExciterSWDocument = PAModuleBuilder.parse(new File("E:\\CapstoneComputingProject(CSC495,496)\\my_rf_tool\\src\\sample\\pa_exciter_control.xml")); }
    catch (SAXException e) { e.printStackTrace(); } catch (IOException e) { e.printStackTrace(); }

    NodeList SWNodeList = mainExciterSWDocument.getDocumentElement().getChildNodes();

    for (int j = 0; j < SWNodeList.getLength(); j++) {
        Node SWNode = SWNodeList.item(j);
        if (SWNode.getNodeType() == SWNode.ELEMENT_NODE) {
            Element swElement = (Element) SWNode;

            paModuleDescription = swElement.getElementsByTagName("PIDDESCRIPTION").item(0).getChildNodes().item(0).getNodeValue();
            paModulePID = swElement.getElementsByTagName("PID").item(0).getChildNodes().item(0).getNodeValue();
            if(paModuleDescription.equals(paModules.getValue()))
                break;

        }
    }
}
    public void addPAModules(){
        DocumentBuilderFactory PAModuleFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder PAModuleBuilder = null;

        try { PAModuleBuilder = PAModuleFactory.newDocumentBuilder(); }
        catch (ParserConfigurationException e) { e.printStackTrace(); }

        Document mainExciterSWDocument = null;
        try { mainExciterSWDocument = PAModuleBuilder.parse(new File("E:\\CapstoneComputingProject(CSC495,496)\\my_rf_tool\\src\\sample\\pa_exciter_control.xml")); }
        catch (SAXException e) { e.printStackTrace(); } catch (IOException e) { e.printStackTrace(); }

        NodeList PANodeList = mainExciterSWDocument.getDocumentElement().getChildNodes();

        for (int j = hardCodeVal; j < PANodeList.getLength(); j++) {
            Node SWNode = PANodeList.item(j);
            if (SWNode.getNodeType() == SWNode.ELEMENT_NODE) {
                Element swElement = (Element) SWNode;

                paModuleDescription = swElement.getElementsByTagName("PIDDESCRIPTION").item(0).getChildNodes().item(0).getNodeValue();

                if(paModuleDescription.equals("GPS ANTENNA HIGH GAIN AND 1FT CABLE"))break;
                paModules.getItems().add(paModuleDescription);

            }
        }

}

    /** event for when the tpo is changed-- checks for compatibility with transmitter **/
    public void tpoChange(){

        if(tpo.getText().equals("")){
            tx_cb.getItems().clear();
            tx_cb.getItems().add("Select Transmitter");
            tx_cb.setValue("Select Transmitter");
            System.out.println("No tpo value!");
            return;
        }
        tx_cb.getItems().clear();
        tx_cb.getItems().add("Select Transmitter");
        tx_cb.setValue("Select Transmitter");

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
            document = builder.parse(new File("E:\\CapstoneComputingProject(CSC495,496)\\my_rf_tool\\src\\sample\\test.xml"));
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
                powerRating = Double.parseDouble(elem.getElementsByTagName("Power").item(0).getChildNodes().item(0).getNodeValue());

                transmitters.add(new sample.Transmitter(ID, pa, cabinets, powerblocks, linesize, powerRating));//call constructor in Transmitter.java to set values for variables
                //read transmitter ID's into tx_cb combo box

                if(powerRating > Double.parseDouble(tpo.getText()))
                    tx_cb.getItems().add(ID);//add each ID(i.e. ULXTE-2) to tx_cb combo box


            }
        }
    }

}

