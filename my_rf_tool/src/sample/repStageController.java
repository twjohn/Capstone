package sample;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
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


public class repStageController implements Initializable {

    public TableView itemReport;
    private Parent parent;
    public static int count = 0;
    private Controller report = new Controller();
    TableColumn pid = new TableColumn("Product ID"); /** product id column **/
    TableColumn piddesc = new TableColumn("Product Description"); /** product description column **/
    TableColumn num = new TableColumn("No."); /** product description column **/
    TableColumn quantity = new TableColumn("Quantity"); /** product description column **/



    @Override
    public void initialize(URL location, ResourceBundle resources) {

        itemReport.getColumns().addAll(num,piddesc,quantity,pid);
         /***************************************************************
         * retrieve values from main scene and display them onto console*
         * will later make this be put into the tableview for this scene*
         ***************************************************************/

         /** Allows to access values from other controller **/
        FXMLLoader newScene= new FXMLLoader();
        newScene.setLocation(getClass().getResource("Main.fxml"));

        try {parent = newScene.load();}catch(IOException e){}/** test new scene, throw exception if it fails **/

        if(parent != null){/** executes if new scene loaded properly **/
            report = newScene.getController(); /** get main scene controller **/

            /** outputting received values to console **/
            System.out.println(report.txSelection);
            System.out.println(report.SWPID);
            System.out.println(report.selectedSWDescription);
            System.out.println(report.filterPID);
            System.out.println(report.selectedFilterDescription);
            System.out.println(report.paModulePID);
            System.out.println(report.selectedPADescription);

            /** create observable array list for table insertion **/
            ObservableList<Report> data = FXCollections.observableArrayList(/** add items to array list **/
                    //new Report();
                    new Report(count, report.txSelection,report.txSelection),//transmitter info
                    new Report(count, report.SWPID,report.selectedSWDescription),//exciter software
                    new Report(count, report.filterPID,report.selectedFilterDescription),//filter info
                    new Report(count, report.paModulePID,report.selectedPADescription)//pa module info
                    /** other info to be added here soon **/
            );

            pid.setCellValueFactory(new PropertyValueFactory<Report, String>("Pid")); /** ties columns and info together for pid **/
            piddesc.setCellValueFactory(new PropertyValueFactory<Report,String>("Piddesc")); /** ties columns and info together for piddesc **/
            num.setCellValueFactory(new PropertyValueFactory<Report,String>("Count")); /** ties columns and info together for piddesc **/

            itemReport.setItems(data); /** adds data to table **/

        }
    }
}