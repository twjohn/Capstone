package sample;

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
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
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

            /** initial coordinates for diagram objects **/
            double vPos=800, hPos=100.0;
            GraphicsContext gc = diagram.getGraphicsContext2D();
            for(int i=0; i<report.txSelectionCabinets; i++) {
                /** far right side of first cabinet = 150px **/

                /** double array argument clarification
                 * first arr in strokePolygon is for horizontal position
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
                gc.setLineWidth(1.0);
                gc.strokeLine(hPos+25,vPos,hPos+25,vPos-25);

                /** low pass + pre coupler **/
                gc.strokeRect(hPos,vPos-125,50,100);
                gc.strokeRect(hPos,vPos-175,50,50);
                gc.strokeLine(hPos+20,vPos-50,hPos+20,vPos-85);
                gc.strokeLine(hPos+20,vPos-85,hPos+40,vPos-100);
                //gc.strokeLine(hPos-10,vPos - 137.5);

                /** pre coupler to mask filter **/
                gc.strokeLine(hPos+25,vPos-125,hPos+25,vPos-200);

                /** mask filter + post coupler **/
                gc.strokeRect(hPos,vPos-300,50,100);
                gc.strokeRect(hPos,vPos-350,50,50);
                gc.strokeLine(hPos+20,vPos-262.5,hPos+40,vPos-287.5);
                gc.strokeLine(hPos+20,vPos-237.5,hPos+20,vPos-262.5);
                gc.strokeLine(hPos+20,vPos-237.5,hPos+40,vPos-212.5);

                /** pre coupler and onward **/
                gc.strokeLine(hPos+25,vPos-300,hPos+25,vPos-375);
                gc.stroke();


                /** hybrid combiners **/
                gc.strokeLine(hPos+25,vPos-375,hPos+25,vPos-400);
                if(report.txSelectionCabinets > i+1)
                    if(((i+1 == 1) && i+1 != 4)) {
                        gc.strokeRect(hPos, vPos - 450, 175, 75);
                        gc.strokeLine(hPos+25,vPos-400,hPos+150,vPos-425);
                        gc.strokeLine(hPos + 25, vPos - 425, hPos + 25, vPos -475);
                    }

                /** conditions for line crossing in hybrid combiners **/

                if(i+1 == 2) {
                    gc.strokeLine(hPos + 25, vPos - 400, hPos - 100, vPos - 425);
                    gc.strokeLine(hPos + 25, vPos - 425, hPos + 25, vPos -475);
                }

                if(report.txSelectionCabinets == 4) {
                    if (i+1==3) {
                        gc.strokeLine(hPos + 25, vPos - 400, hPos + 150, vPos - 425);
                        gc.strokeLine(hPos + 25, vPos - 425, hPos + 25, vPos -475);
                    }
                    if(i+1==4) {
                        gc.strokeLine(hPos + 25, vPos - 400, hPos - 100, vPos - 425);
                        gc.strokeLine(hPos + 25, vPos - 425, hPos + 25, vPos -475);
                    }
                }

                if(report.txSelectionCabinets == 5) {
                    if (i+1==4) {
                        gc.strokeLine(hPos + 25, vPos - 400, hPos + 150, vPos - 425);
                        gc.strokeLine(hPos + 25, vPos - 425, hPos + 25, vPos -475);
                    }
                    if(i+1==5) {
                        gc.strokeLine(hPos + 25, vPos - 400, hPos - 100, vPos - 425);
                        gc.strokeLine(hPos + 25, vPos - 425, hPos + 25, vPos -475);
                    }
                }
                /*******************************************************************************/


                /** condition to place hybrid combiners when cabinets = 4 or cabinets = 5 **/
                if((i+1 == 5) || (report.txSelectionCabinets == 4 && i+1 == 4)) {
                    gc.strokeRect(hPos - 125, vPos - 450, 175, 75);
                }

                /** increment horizontal position **/
                hPos += 125;
            }
        }
    }
}