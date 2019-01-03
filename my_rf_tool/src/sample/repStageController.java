package sample;

import com.sun.javafx.geom.Rectangle;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.ObjectBinding;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Rectangle2D;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.util.Callback;
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

import static javafx.stage.Screen.getPrimary;

public class repStageController implements Initializable {

    public TableView itemReport;
    public Canvas diagram;
    private Parent parent;


    /** access Controller from repstageController **/
    private Controller report = new Controller();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        /** we need to add to table via the initialize function--adds info as soon as window is opened **/

        TableColumn pid = new TableColumn("Product ID"); /** product id column **/
        TableColumn piddesc = new TableColumn("Product Description"); /** product description column **/
        TableColumn numberCol = new TableColumn("No."); /** product description column **/
        TableColumn quantity = new TableColumn("Quantity"); /** product description column **/

        itemReport.getColumns().addAll(numberCol,pid,piddesc,quantity);
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
                    new Report(report.txSelection,report.txSelection),//transmitter info
                    new Report(report.SWPID,report.selectedSWDescription),//exciter software
                    new Report(report.filterPID,report.selectedFilterDescription),//filter info
                    new Report(report.paModulePID,report.selectedPADescription),//pa module info
                    new Report("arbitrary info","arbitrary info")
                    /** other info to be added here soon **/
            );

            pid.setCellValueFactory(new PropertyValueFactory<Report, String>("Pid")); /** ties columns and info together for pid **/
            piddesc.setCellValueFactory(new PropertyValueFactory<Report,String>("Piddesc")); /** ties columns and info together for piddesc **/

            /** auto increment row number **/
            numberCol.setCellFactory(col -> {
                TableCell<Report, Integer> indexCell = new TableCell<>();
                ReadOnlyObjectProperty<TableRow<Report>> rowProperty = indexCell.tableRowProperty();
                ObjectBinding<String> rowBinding = Bindings.createObjectBinding(() -> {
                    TableRow<Report> row = rowProperty.get();
                    if (row != null) { // can be null during CSS processing
                        int rowIndex = row.getIndex();
                        if (rowIndex < row.getTableView().getItems().size())
                            return Integer.toString(rowIndex+1);
                    }
                    return null;
                }, rowProperty);
                indexCell.textProperty().bind(rowBinding);
                return indexCell;
            });

            /**************************************************************/

            itemReport.setItems(data); /** adds data to table **/


            /****************************** DIAGRAMMING *******************************/

            /** initial coordinates for diagram objects **/
            double vPos=1100, hPos=500;

            GraphicsContext gc = diagram.getGraphicsContext2D();
            for(int i=0; i<report.txSelectionCabinets; i++) {

                /** argument clarification for strokePolygon();
                 * first arr in strokePolygon is for horizontal alignment
                 *                      index 0 = left
                 *                      index 1 = center
                 *                      index 2 = right
                 * second arr in strokePolygon is for vertical alignment
                 *                      index 0 = left
                 *                      index 1 = center
                 *                      index 2 = right
                 * last integer argument in for number of points, in this case, 3 for a triangle
                 */
                gc.strokePolygon(new double[]{hPos-25, hPos+25, hPos+75}, new double[]{vPos+100, vPos, vPos+100}, 3);
                gc.strokeText(Integer.toString(i+1),hPos+20,vPos+70);
                gc.strokeLine(hPos+25,vPos,hPos+25,vPos-25);

                /** low pass + pre coupler **/
                gc.strokeRect(hPos,vPos-125,50,100);/** low pass rectangle **/
                gc.strokeRect(hPos,vPos-175,50,50);/** coupler square **/

                    /** low pass details **/
                    gc.strokeLine(hPos+20,vPos-50,hPos+20,vPos-85);
                    gc.strokeLine(hPos+20,vPos-85,hPos+40,vPos-100);

                    /** pre coupler details  bottom left**/
                    gc.strokeLine(hPos-10,vPos-137.5, hPos+12.5,vPos-137.5);
                    gc.strokeLine(hPos+12.5,vPos-137.5,hPos+12.5,vPos-142.5);
                    gc.strokeLine(hPos+12.5,vPos-142.5,hPos+7.5,vPos-142.5);

                    /** pre coupler details  top left**/
                    gc.strokeLine(hPos-10,vPos-155, hPos+12.5,vPos-155);
                    gc.strokeLine(hPos+12.5,vPos-155,hPos+12.5,vPos-160);
                    gc.strokeLine(hPos+12.5,vPos-160,hPos+7.5,vPos-160);

                    /** pre coupler details bottom right **/
                    gc.strokeLine(hPos+60,vPos-142.5, hPos+37.5,vPos-142.5);
                    gc.strokeLine(hPos+37.5,vPos-142.5,hPos+37.5,vPos-137.5);
                    gc.strokeLine(hPos+37.5,vPos-137.5,hPos+42.5,vPos-137.5);

                    /** pre coupler details top right **/
                    gc.strokeLine(hPos+60,vPos-155, hPos+37.5,vPos-155);
                    gc.strokeLine(hPos+37.5,vPos-155,hPos+37.5,vPos-160);
                    gc.strokeLine(hPos+37.5,vPos-160,hPos+42.5,vPos-160);

                /** pre coupler to mask filter **/
                gc.strokeLine(hPos+25,vPos-125,hPos+25,vPos-200);

                /** mask filter + post coupler **/
                gc.strokeRect(hPos,vPos-300,50,100);/** mask filter rectangle square **/
                gc.strokeRect(hPos,vPos-350,50,50);/** coupler square **/

                    /** mask filter details **/
                    gc.strokeLine(hPos+20,vPos-262.5,hPos+40,vPos-287.5);
                    gc.strokeLine(hPos+20,vPos-237.5,hPos+20,vPos-262.5);
                    gc.strokeLine(hPos+20,vPos-237.5,hPos+40,vPos-212.5);

                    /** post coupler details bottom left **/
                    gc.strokeLine(hPos-10,vPos-312.5, hPos+12.5,vPos-312.5);
                    gc.strokeLine(hPos+12.5,vPos-312.5,hPos+12.5,vPos-317.5);
                    gc.strokeLine(hPos+12.5,vPos-317.5,hPos+7.5,vPos-317.5);

                    /** post coupler details top left **/
                    gc.strokeLine(hPos-10,vPos-330, hPos+12.5,vPos-330);
                    gc.strokeLine(hPos+12.5,vPos-330,hPos+12.5,vPos-335);
                    gc.strokeLine(hPos+12.5,vPos-335,hPos+7.5,vPos-335);

                    /** post coupler details bottom right **/
                    gc.strokeLine(hPos+60,vPos-317.5, hPos+37.5,vPos-317.5);
                    gc.strokeLine(hPos+37.5,vPos-317.5,hPos+37.5,vPos-312.5);
                    gc.strokeLine(hPos+37.5,vPos-312.5,hPos+42.5,vPos-312.5);

                    /** post coupler details top right **/
                    gc.strokeLine(hPos+60,vPos-330, hPos+37.5,vPos-330);
                    gc.strokeLine(hPos+37.5,vPos-330,hPos+37.5,vPos-335);
                    gc.strokeLine(hPos+37.5,vPos-335,hPos+42.5,vPos-335);

                /** pre coupler and onward **/
                gc.strokeLine(hPos+25,vPos-300,hPos+25,vPos-375);
                gc.stroke();


                /** hybrid combiners **/
                if(report.txSelectionCabinets!=1)
                    gc.strokeLine(hPos+25,vPos-375,hPos+25,vPos-400);
                
                if(report.txSelectionCabinets > i+1)/** correctly place combiners when there's more than 1 cabinet **/
                    if((i+1 == 1)) {
                        gc.strokeRect(hPos, vPos-450, 175, 75);
                        gc.strokeLine(hPos+25,vPos-400,hPos+150,vPos-425);
                        gc.strokeLine(hPos+25, vPos-425, hPos+25, vPos-475);
                        /** line to reject load **/
                        gc.strokeLine(hPos+25,vPos-475,hPos-25, vPos-475);
                        /** reject load **/
                        gc.strokeLine(hPos-25,vPos-475,hPos-30,vPos-495);
                        gc.strokeLine(hPos-30,vPos-495,hPos-40,vPos-455);
                        gc.strokeLine(hPos-40,vPos-455,hPos-50,vPos-495);
                        gc.strokeLine(hPos-50,vPos-495,hPos-60,vPos-455);
                        gc.strokeLine(hPos-60,vPos-455,hPos-70,vPos-495);
                        gc.strokeLine(hPos-70,vPos-495,hPos-80,vPos-455);
                        gc.strokeLine(hPos-80,vPos-455,hPos-90,vPos-495);
                        gc.strokeLine(hPos-90,vPos-495,hPos-95,vPos-475);
                    }

                /** conditions for line crossing in hybrid combiners **/

                if(i+1 == 2) {
                    gc.strokeLine(hPos+25, vPos-400, hPos-100, vPos-425);
                    gc.strokeLine(hPos+25, vPos-425, hPos+25, vPos-500);
                    if(report.txSelectionCabinets == 2){/**add coupler to left side of combiner when number of cabinets = 2 **/
                        gc.strokeRect(hPos,vPos-500,50,50);/** coupler square **/
                        /** post coupler details  bottom left**/
                        gc.strokeLine(hPos-10,vPos-462.5, hPos+12.5,vPos-462.5);
                        gc.strokeLine(hPos+12.5,vPos-462.5,hPos+12.5,vPos-467.5);
                        gc.strokeLine(hPos+12.5,vPos-467.5,hPos+7.5,vPos-467.5);

                        /** post coupler details  top left**/
                        gc.strokeLine(hPos-10,vPos-481, hPos+12.5,vPos-481);
                        gc.strokeLine(hPos+12.5,vPos-481,hPos+12.5,vPos-486);
                        gc.strokeLine(hPos+12.5,vPos-486,hPos+7.5,vPos-486);

                        /** post coupler details bottom right **/
                        gc.strokeLine(hPos+60,vPos-467.5, hPos+37.5,vPos-467.5);
                        gc.strokeLine(hPos+37.5,vPos-467.5,hPos+37.5,vPos-462.5);
                        gc.strokeLine(hPos+37.5,vPos-462.5,hPos+42.5,vPos-462.5);

                        /** post coupler details top right **/
                        gc.strokeLine(hPos+60,vPos-481, hPos+37.5,vPos-481);
                        gc.strokeLine(hPos+37.5,vPos-481,hPos+37.5,vPos-486);
                        gc.strokeLine(hPos+37.5,vPos-486,hPos+42.5,vPos-486);

                    }
                }

                if(report.txSelectionCabinets == 3){
                    if(i+1==2){
                        gc.strokeRect(hPos, vPos-550, 175, 75);/**combiner rectangle **/
                        gc.strokeLine(hPos+25, vPos-500, hPos+150, vPos-525);
                        gc.strokeLine(hPos+25, vPos-525, hPos+25, vPos-600);

                        gc.strokeRect(hPos,vPos-600,50,50);/** coupler square **/
                        /** post coupler details  bottom left**/
                        gc.strokeLine(hPos-10,vPos-562.5, hPos+12.5,vPos-562.5);
                        gc.strokeLine(hPos+12.5,vPos-562.5,hPos+12.5,vPos-567.5);
                        gc.strokeLine(hPos+12.5,vPos-567.5,hPos+7.5,vPos-567.5);

                        /** post coupler details  top left**/
                        gc.strokeLine(hPos-10,vPos-581, hPos+12.5,vPos-581);
                        gc.strokeLine(hPos+12.5,vPos-581,hPos+12.5,vPos-586);
                        gc.strokeLine(hPos+12.5,vPos-586,hPos+7.5,vPos-586);

                        /** post coupler details bottom right **/
                        gc.strokeLine(hPos+60,vPos-567.5, hPos+37.5,vPos-567.5);
                        gc.strokeLine(hPos+37.5,vPos-567.5,hPos+37.5,vPos-562.5);
                        gc.strokeLine(hPos+37.5,vPos-562.5,hPos+42.5,vPos-562.5);

                        /** post coupler details top right **/
                        gc.strokeLine(hPos+60,vPos-581, hPos+37.5,vPos-581);
                        gc.strokeLine(hPos+37.5,vPos-581,hPos+37.5,vPos-586);
                        gc.strokeLine(hPos+37.5,vPos-586,hPos+42.5,vPos-586);
                    }
                    if(i+1==3) {/** draw line from cabinet 3 to combiner **/
                        gc.strokeLine(hPos + 25, vPos - 400, hPos + 25, vPos - 500);
                        gc.strokeLine(hPos+25, vPos-500, hPos-100, vPos-525);
                        gc.strokeLine(hPos+25, vPos-525, hPos+25, vPos-575);

                        /** line to reject load **/
                        gc.strokeLine(hPos+25,vPos-575,hPos+75, vPos-575);
                        /** reject load **/
                        gc.strokeLine(hPos+75,vPos-575,hPos+80, vPos-595);
                        gc.strokeLine(hPos+80,vPos-595,hPos+85, vPos-555);
                        gc.strokeLine(hPos+85,vPos-555,hPos+95, vPos-595);
                        gc.strokeLine(hPos+95,vPos-595,hPos+105, vPos-555);
                        gc.strokeLine(hPos+105,vPos-555,hPos+115, vPos-595);
                        gc.strokeLine(hPos+115,vPos-595,hPos+125, vPos-555);
                        gc.strokeLine(hPos+125,vPos-555,hPos+135, vPos-595);
                        gc.strokeLine(hPos+135,vPos-595,hPos+140, vPos-575);
                    }
                }
                if(report.txSelectionCabinets == 4) {
                    if(i+1==2){
                        gc.strokeRect(hPos, vPos-550, 175, 75);/**combiner rectangle **/
                        gc.strokeLine(hPos+25, vPos-500, hPos+150, vPos-525);
                        gc.strokeLine(hPos+25, vPos-525, hPos+25, vPos-600);

                        gc.strokeRect(hPos,vPos-600,50,50);/** coupler square **/
                        /** post coupler details  bottom left**/
                        gc.strokeLine(hPos-10,vPos-562.5, hPos+12.5,vPos-562.5);
                        gc.strokeLine(hPos+12.5,vPos-562.5,hPos+12.5,vPos-567.5);
                        gc.strokeLine(hPos+12.5,vPos-567.5,hPos+7.5,vPos-567.5);

                        /** post coupler details  top left**/
                        gc.strokeLine(hPos-10,vPos-581, hPos+12.5,vPos-581);
                        gc.strokeLine(hPos+12.5,vPos-581,hPos+12.5,vPos-586);
                        gc.strokeLine(hPos+12.5,vPos-586,hPos+7.5,vPos-586);

                        /** post coupler details bottom right **/
                        gc.strokeLine(hPos+60,vPos-567.5, hPos+37.5,vPos-567.5);
                        gc.strokeLine(hPos+37.5,vPos-567.5,hPos+37.5,vPos-562.5);
                        gc.strokeLine(hPos+37.5,vPos-562.5,hPos+42.5,vPos-562.5);

                        /** post coupler details top right **/
                        gc.strokeLine(hPos+60,vPos-581, hPos+37.5,vPos-581);
                        gc.strokeLine(hPos+37.5,vPos-581,hPos+37.5,vPos-586);
                        gc.strokeLine(hPos+37.5,vPos-586,hPos+42.5,vPos-586);
                    }
                    if (i+1==3) {
                        gc.strokeLine(hPos+25, vPos-400, hPos+150, vPos-425);
                        gc.strokeLine(hPos+25, vPos-425, hPos+25, vPos-500);
                        gc.strokeLine(hPos+25, vPos-500, hPos-100, vPos-525);
                        gc.strokeLine(hPos+25, vPos-525, hPos+25, vPos-575);

                        /** line to reject load **/
                        gc.strokeLine(hPos+25,vPos-575,hPos+75, vPos-575);
                        /** reject load **/
                        gc.strokeLine(hPos+75,vPos-575,hPos+80, vPos-595);
                        gc.strokeLine(hPos+80,vPos-595,hPos+85, vPos-555);
                        gc.strokeLine(hPos+85,vPos-555,hPos+95, vPos-595);
                        gc.strokeLine(hPos+95,vPos-595,hPos+105, vPos-555);
                        gc.strokeLine(hPos+105,vPos-555,hPos+115, vPos-595);
                        gc.strokeLine(hPos+115,vPos-595,hPos+125, vPos-555);
                        gc.strokeLine(hPos+125,vPos-555,hPos+135, vPos-595);
                        gc.strokeLine(hPos+135,vPos-595,hPos+140, vPos-575);
                    }
                    if(i+1==4) {
                        gc.strokeLine(hPos+25, vPos-400, hPos-100, vPos-425);
                        gc.strokeLine(hPos+25, vPos-425, hPos+25, vPos-475);
                        /** line to reject load **/
                        gc.strokeLine(hPos+25,vPos-475,hPos+75, vPos-475);
                        /** reject load **/
                        gc.strokeLine(hPos+75,vPos-475,hPos+80, vPos-495);
                        gc.strokeLine(hPos+80,vPos-495,hPos+85, vPos-455);
                        gc.strokeLine(hPos+85,vPos-455,hPos+95, vPos-495);
                        gc.strokeLine(hPos+95,vPos-495,hPos+105, vPos-455);
                        gc.strokeLine(hPos+105,vPos-455,hPos+115, vPos-495);
                        gc.strokeLine(hPos+115,vPos-495,hPos+125, vPos-455);
                        gc.strokeLine(hPos+125,vPos-455,hPos+135, vPos-495);
                        gc.strokeLine(hPos+135,vPos-495,hPos+140, vPos-475);
                    }
                }

                if(report.txSelectionCabinets == 5) {
                    if(i+1==2){
                        gc.strokeRect(hPos, vPos-550, 175, 75);/**combiner rectangle **/
                        gc.strokeLine(hPos+25, vPos-500, hPos+150, vPos-525);
                        gc.strokeLine(hPos+25, vPos-525, hPos+25, vPos-575);

                        /** line to reject load **/
                        gc.strokeLine(hPos+25,vPos-575,hPos-25, vPos-575);
                        /** reject load **/
                        gc.strokeLine(hPos-25,vPos-575,hPos-30,vPos-595);
                        gc.strokeLine(hPos-30,vPos-595,hPos-40,vPos-555);
                        gc.strokeLine(hPos-40,vPos-555,hPos-50,vPos-595);
                        gc.strokeLine(hPos-50,vPos-595,hPos-60,vPos-555);
                        gc.strokeLine(hPos-60,vPos-555,hPos-70,vPos-595);
                        gc.strokeLine(hPos-70,vPos-595,hPos-80,vPos-555);
                        gc.strokeLine(hPos-80,vPos-555,hPos-90,vPos-595);
                        gc.strokeLine(hPos-90,vPos-595,hPos-95,vPos-575);
                    }
                    if(i+1==3){
                        gc.strokeLine(hPos + 25, vPos - 400, hPos + 25, vPos - 500);
                        gc.strokeLine(hPos+25, vPos-500, hPos-100, vPos-525);
                        gc.strokeLine(hPos+25, vPos-525, hPos+25, vPos-600);

                        gc.strokeRect(hPos, vPos-650, 175, 75);/**combiner rectangle **/
                        gc.strokeLine(hPos+25, vPos-600, hPos+150, vPos-625);
                        gc.strokeLine(hPos+25, vPos-625, hPos+25, vPos-700);

                        gc.strokeRect(hPos,vPos-700,50,50);/** coupler square **/
                        /** post coupler details  bottom left**/
                        gc.strokeLine(hPos-10,vPos-662.5, hPos+12.5,vPos-662.5);
                        gc.strokeLine(hPos+12.5,vPos-662.5,hPos+12.5,vPos-667.5);
                        gc.strokeLine(hPos+12.5,vPos-667.5,hPos+7.5,vPos-667.5);

                        /** post coupler details  top left**/
                        gc.strokeLine(hPos-10,vPos-681, hPos+12.5,vPos-681);
                        gc.strokeLine(hPos+12.5,vPos-681,hPos+12.5,vPos-686);
                        gc.strokeLine(hPos+12.5,vPos-686,hPos+7.5,vPos-686);

                        /** post coupler details bottom right **/
                        gc.strokeLine(hPos+60,vPos-667.5, hPos+37.5,vPos-667.5);
                        gc.strokeLine(hPos+37.5,vPos-667.5,hPos+37.5,vPos-662.5);
                        gc.strokeLine(hPos+37.5,vPos-662.5,hPos+42.5,vPos-662.5);

                        /** post coupler details top right **/
                        gc.strokeLine(hPos+60,vPos-681, hPos+37.5,vPos-681);
                        gc.strokeLine(hPos+37.5,vPos-681,hPos+37.5,vPos-686);
                        gc.strokeLine(hPos+37.5,vPos-686,hPos+42.5,vPos-686);
                    }
                    if (i+1==4) {
                        gc.strokeLine(hPos+25, vPos-400, hPos+150, vPos-425);
                        gc.strokeLine(hPos+25, vPos-425, hPos+25, vPos-500);
                        gc.strokeLine(hPos+25, vPos-500, hPos+25, vPos-600);

                        gc.strokeLine(hPos+25, vPos-600, hPos-100, vPos-625);
                        gc.strokeLine(hPos+25, vPos-625, hPos+25, vPos-675);

                        /** line to reject load **/
                        gc.strokeLine(hPos+25,vPos-675,hPos+75, vPos-675);
                        /** reject load **/
                        gc.strokeLine(hPos+75,vPos-675,hPos+80, vPos-695);
                        gc.strokeLine(hPos+80,vPos-695,hPos+90, vPos-655);
                        gc.strokeLine(hPos+90,vPos-655,hPos+100, vPos-695);
                        gc.strokeLine(hPos+100,vPos-695,hPos+110, vPos-655);
                        gc.strokeLine(hPos+110,vPos-655,hPos+120, vPos-695);
                        gc.strokeLine(hPos+120,vPos-695,hPos+130, vPos-655);
                        gc.strokeLine(hPos+130,vPos-655,hPos+140, vPos-695);
                        gc.strokeLine(hPos+140,vPos-695,hPos+145, vPos-675);
                    }
                    if(i+1==5) {
                        gc.strokeLine(hPos+25, vPos-400, hPos-100, vPos-425);
                        gc.strokeLine(hPos+25, vPos-425, hPos+25, vPos-475);
                        /** line to reject load **/
                        gc.strokeLine(hPos+25,vPos-475,hPos+75, vPos-475);
                        /** reject load **/
                        gc.strokeLine(hPos+75,vPos-475,hPos+80, vPos-495);
                        gc.strokeLine(hPos+80,vPos-495,hPos+90, vPos-455);
                        gc.strokeLine(hPos+90,vPos-455,hPos+100, vPos-495);
                        gc.strokeLine(hPos+100,vPos-495,hPos+110, vPos-455);
                        gc.strokeLine(hPos+110,vPos-455,hPos+120, vPos-495);
                        gc.strokeLine(hPos+120,vPos-495,hPos+130, vPos-455);
                        gc.strokeLine(hPos+130,vPos-455,hPos+140, vPos-495);
                        gc.strokeLine(hPos+140,vPos-495,hPos+145, vPos-475);
                    }
                }

                /** condition to place hybrid combiners when cabinets = 4 or cabinets = 5 **/
                if((i+1 == 5) || (report.txSelectionCabinets == 4 && i+1 == 4)) {
                    gc.strokeRect(hPos-125, vPos-450, 175, 75);
                }

                /** increment horizontal position
                 *  incrementing this var allows for other cabinets, couplers, combiners, etc to be
                 *  placed in appropriate horizontal positions
                 **/
                hPos += 125;
            }
        }
    }
}