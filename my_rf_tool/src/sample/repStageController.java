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

    public TableView itemReport = new TableView();
    private Parent parent;
    private Controller report = new Controller();
    TableColumn pidd = new TableColumn("Product ID");
    TableColumn piddescc = new TableColumn("Product Description");



    @Override
    public void initialize(URL location, ResourceBundle resources) {
        itemReport.getItems().addAll(pidd,piddescc);
         /***************************************************************
         * retrieve values from main scene and display them onto console*
         * will later make this be put into the tableview for this scene*
         ***************************************************************/

         /** Allows to access values from other controller **/
        FXMLLoader newScene= new FXMLLoader();
        newScene.setLocation(getClass().getResource("Main.fxml"));
        try {parent = newScene.load();}catch(IOException e){}
        if(parent != null){
            report = newScene.getController();
            System.out.println(report.txSelection);
            System.out.println(report.SWPID);
            System.out.println(report.selectedSWDescription);
            /** create oberservable arraylist for table insertion **/
            ObservableList data = FXCollections.observableArrayList(
                    new Report(report.txSelection,report.txSelection),
                    new Report(report.SWPID,report.selectedSWDescription)
            );

            pidd.setCellValueFactory(new PropertyValueFactory<Report, String>("pid"));
            piddescc.setCellValueFactory(new PropertyValueFactory<Report,String>("piddesc"));
           itemReport.setItems(data);
        }
    }
}