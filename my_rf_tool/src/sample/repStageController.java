package sample;

import javafx.beans.binding.Bindings;
import javafx.beans.binding.ObjectBinding;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.Initializable;
import javafx.geometry.Rectangle2D;
import javafx.print.*;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Screen;
import java.net.URL;
import java.util.ResourceBundle;

public class repStageController implements Initializable {

    public TableView itemReport;
    public Canvas diagram;
    private GraphicsContext gc;

    /** increment diagram index **/
    private int diagramIndex;

    /********** access Controller from repStageController **********/
    //private Controller report = new Controller();

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        /** set gc equal to diagram.getGraphicsContenxt2D so we can draw diagram on canvas **/
        gc = diagram.getGraphicsContext2D();

        /** we need to add to table via the initialize function--adds info as soon as window is opened **/

        TableColumn pid = new TableColumn("Product ID"); /** product id column **/
        TableColumn piddesc = new TableColumn("Product Description"); /** product description column **/
        TableColumn numberCol = new TableColumn("No."); /** product description column **/
        TableColumn quantity = new TableColumn("Quantity"); /** product description column **/

        itemReport.getColumns().addAll(numberCol, pid, piddesc, quantity);
        /**
         * retrieve values from main scene and display them onto console*
         * will later make this be put into the table view for this scene*
         **/

        /** Allows to access values from other controller **/
        //FXMLLoader newScene = new FXMLLoader();
        //newScene.setLocation(getClass().getResource("Main.fxml"));

        //try { parent = newScene.load(); } catch (IOException e) { }/** test new scene, throw exception if it fails **/

        //if (parent != null) {/** executes if new scene loaded properly **/
        //    report = newScene.getController(); /** get main scene controller **/

            /** outputting received values to console **/
            System.out.println("\n--------------------------------------");
            System.out.println("Transmitter selection: "+Controller.txSelection);
            System.out.println("Description: "+Controller.selectedSWDescription+"     Product ID: "+Controller.SWPID);
            System.out.println("Description: "+Controller.selectedFilterDescription+"     Product ID: "+Controller.filterPID);
            System.out.println("Description: "+Controller.paModuleDescription+"     Product ID: "+Controller.paModulePID);
            System.out.println("Description: "+Controller.auxAntFeedSelection+"     Product ID: "+Controller.auxAntFeedSelection);

            /** create observable array list for table insertion **/
            ObservableList<Report> data = FXCollections.observableArrayList(/** add items to array list **/
                    new Report(Controller.txSelection, Controller.txSelection),//transmitter info
                    new Report(Controller.SWPID, Controller.selectedSWDescription),//exciter software
                    new Report(Controller.filterPID, Controller.selectedFilterDescription),//filter info
                    new Report(Controller.paModulePID, Controller.selectedPADescription),//pa module info
                    new Report(Controller.auxAntFeedSelection, "Aux Antenna Feed: "+Controller.auxAntFeedSelection)//aux antenna feed info
                    /** other info to be added here soon **/
            );

            pid.setCellValueFactory(new PropertyValueFactory<Report, String>("Pid")); /** ties columns and info together for pid **/
            piddesc.setCellValueFactory(new PropertyValueFactory<Report, String>("Piddesc")); /** ties columns and info together for piddesc **/

            /** auto increment row number-- calls call() function**/
            numberCol.setCellFactory(repStageController::call);

            itemReport.setItems(data); /** adds data to table **/

            /** call create diagram function to begin putting together RF Flow Diagram **/
            createDiagram();
    }

    /** function that utilizes lambda notation to auto increment table rows **/
    private static Object call(Object col) {
        TableCell<Report, Integer> indexCell = new TableCell<>();
        ReadOnlyObjectProperty<TableRow<Report>> rowProperty = indexCell.tableRowProperty();
        ObjectBinding<String> rowBinding = Bindings.createObjectBinding(() -> {
            TableRow<Report> row = rowProperty.get();
            if (row != null) { // can be null during CSS processing
                int rowIndex = row.getIndex();
                if (rowIndex < row.getTableView().getItems().size())
                    return Integer.toString(rowIndex + 1);
            }
            return null;
        }, rowProperty);
        indexCell.textProperty().bind(rowBinding);
        return indexCell;
    }

                                /*********************** DIAGRAM CREATION ***********************/

    /** function to create diagram-- utilizes createRejectLoad(),createCoupler() and createHybridCombiner() **/
    private void createDiagram(){

        Rectangle2D bounds = Screen.getPrimary().getVisualBounds();

        /** initial coordinates for diagram objects **/
        double vPos, hPos;// variables that correlate with diagram positions

        /** conditionals to adjust starting position based upon screen size/window size upon starting **/
        if(Controller.txSelectionCabinets == 1) {
            vPos = 850;
            hPos= (bounds.getWidth()/2)-25;

        }
        else if(Controller.txSelectionCabinets == 2) {
            vPos = 900;
            hPos= (bounds.getWidth()/2.5) - 93.5;
        }
        else if(Controller.txSelectionCabinets == 3) {
            vPos = 1050;
            hPos= (bounds.getWidth()/2.5) - 124.75;
        }
        else if(Controller.txSelectionCabinets == 4) {
            vPos = 1050;
            hPos= (bounds.getWidth()/2.5) - 156;
        }
        else if(Controller.txSelectionCabinets == 5) {
            vPos = 1100;
            hPos= (bounds.getWidth()/2.5) - 187.25;
        }
        else {
            vPos = 1100;
            hPos= (bounds.getWidth()/2.5) - 187.25;
        }
        for (diagramIndex = 0; diagramIndex < Controller.txSelectionCabinets; diagramIndex++) {

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
             **/

            /** create cabinet and place numeric value in cabinet(1=cabinet 1 and so on) **/
            gc.strokePolygon(new double[]{hPos - 25, hPos + 25, hPos + 75}, new double[]{vPos + 100, vPos, vPos + 100}, 3);
            gc.strokeText(Integer.toString(diagramIndex + 1), hPos + 20, vPos + 70);// cabinet labels(1,2,3, etc...)
            if(diagramIndex==0)
                gc.strokeText("CABINETS \nTX MODEL: "+Controller.txSelection,hPos - 135, vPos + 55); // TX model label
            /** call createLowPass() to create low pass filter visual **/
            createLowPass(hPos,vPos);

            /** call createCoupler() to create coupler visual **/
            createCoupler(hPos, vPos - 175);
            if(diagramIndex==0)
                gc.strokeText("PRE COUPLER",hPos - 110, vPos - 160);

            /** pre coupler to mask filter **/
            gc.strokeLine(hPos + 25, vPos - 125, hPos + 25, vPos - 200);

            /** call createMask() to create mask filter visual **/
            createMask(hPos,vPos);

            /** call createCoupler() to create coupler visual **/
            createCoupler(hPos, vPos - 350);
            if(diagramIndex==0)
                gc.strokeText("POST COUPLER",hPos - 110, vPos - 335);

            /** post coupler and onward **/
            gc.strokeLine(hPos + 25, vPos - 300, hPos + 25, vPos - 375);

            if(Controller.txSelectionCabinets == 1)
                createAntenna(hPos+25,vPos-375);

            /** hybrid combiners **/
            if (Controller.txSelectionCabinets != 1) {
                /** line that extends from post coupler to hybrid combiner **/
                gc.strokeLine(hPos + 25, vPos - 375, hPos + 25, vPos - 400);
            }

            if (Controller.txSelectionCabinets > diagramIndex + 1) {/** if cabinets > 1, cabinet one will be attached to combiner **/
                if ((diagramIndex + 1 == 1)) {

                    /** call createHybridCombiner() to create hybrid combiner visual **/
                    createHybridCombiner(hPos, vPos - 450);
                    gc.strokeText("HYBRID COMBINER",hPos-110, vPos - 412.5);

                    /** call createRejectLoad() to create reject load visual **/
                    createRejectLoad(hPos + 25, vPos - 475);
                    gc.strokeText("REJECT LOAD",hPos - 185, vPos - 475);
                }
            }

            if (diagramIndex + 1 == 2) {
                if (Controller.txSelectionCabinets == 2) {/**add coupler to left side of combiner when number of cabinets = 2 **/
                    /** call createCoupler() to create coupler visual **/
                    createCoupler(hPos, vPos - 500);
                    gc.strokeText("POST COUPLER",hPos - 110, vPos - 485);
                    gc.strokeLine(hPos+25, vPos-700, hPos+25, vPos-500);

                    createAntenna(hPos+25,vPos-700);
                }
            }

            if (Controller.txSelectionCabinets == 3) {
                if (diagramIndex + 1 == 2) {

                    /** call createHybridCombiner() to create hybrid combiner visual **/
                    createHybridCombiner(hPos, vPos - 550);

                    /** call createCoupler() to create coupler visual **/
                    createCoupler(hPos, vPos - 600);
                    gc.strokeText("POST COUPLER",hPos - 110, vPos - 585);

                    gc.strokeLine(hPos+25, vPos-800, hPos+25, vPos-600);
                    createAntenna(hPos+25,vPos-800);
                }
                if (diagramIndex + 1 == 3) {

                    /** draw line from cabinet 3 to combiner **/
                    gc.strokeLine(hPos + 25, vPos - 400, hPos + 25, vPos - 500);

                    /** call createRejectLoad() to create reject load visual **/
                    createRejectLoad(hPos + 25, vPos - 575);
                }
            }
            if (Controller.txSelectionCabinets == 4) {
                if (diagramIndex + 1 == 2) {

                    /** call createHybridCombiner() to create hybrid combiner visual **/
                    createHybridCombiner(hPos, vPos - 550);

                    /** call createCoupler() to create coupler visual **/
                    createCoupler(hPos, vPos - 600);
                    gc.strokeText("POST COUPLER",hPos - 110, vPos - 585);

                    gc.strokeLine(hPos+25, vPos-800, hPos+25, vPos-600);
                    createAntenna(hPos+25,vPos-800);

                }
                if (diagramIndex + 1 == 3) {

                    /** call createHybridCombiner() to create hybrid combiner visual **/
                    createHybridCombiner(hPos, vPos - 450);

                    /** call createRejectLoad() to create reject load visual **/
                    createRejectLoad(hPos + 25, vPos - 575);
                }
                if (diagramIndex + 1 == 4) {

                    /** call createRejectLoad() to create reject load visual **/
                    createRejectLoad(hPos + 25, vPos - 475);
                }
            }

            if (Controller.txSelectionCabinets == 5) {
                if (diagramIndex + 1 == 2) {

                    /** call createHybridCombiner() to create hybrid combiner visual **/
                    createHybridCombiner(hPos, vPos - 550);

                    /** call createRejectLoad() to create reject load visual **/
                    createRejectLoad(hPos + 25, vPos - 575);
                }
                if (diagramIndex + 1 == 3) {
                    gc.strokeLine(hPos + 25, vPos - 400, hPos + 25, vPos - 500);

                    /** call createHybridCombiner() to create hybrid combiner visual **/
                    createHybridCombiner(hPos, vPos - 650);

                    /** call createCoupler() to create coupler visual **/
                    createCoupler(hPos, vPos - 700);
                    gc.strokeText("POST COUPLER",hPos - 110, vPos - 685);
                    gc.strokeLine(hPos+25, vPos-900, hPos+25, vPos-700);
                    createAntenna(hPos+25,vPos-900);
                }
                if (diagramIndex + 1 == 4) {

                    /** call createHybridCombiner() to create hybrid combiner visual **/
                    createHybridCombiner(hPos, vPos - 450);
                    gc.strokeLine(hPos + 25, vPos - 500, hPos + 25, vPos - 600);

                    /** call createRejectLoad() to create reject load visual **/
                    createRejectLoad(hPos + 25, vPos - 675);
                }
                if (diagramIndex + 1 == 5) {

                    /** call createRejectLoad() to create reject load visual **/
                    createRejectLoad(hPos + 25, vPos - 475);
                }
            }

            /** increment horizontal position
             *  incrementing this var allows for other cabinets, couplers, combiners, etc to be
             *  placed in appropriate horizontal positions
             **/
            hPos += 125;// creates distance between cabinets
        }

        diagram.setWidth(bounds.getWidth());
        diagram.setHeight(vPos+150);
    }

    /********** function to create the reject load visual for the diagram **********/
    private void createRejectLoad(double hPos1, double vPos1) {

        double vPos2=vPos1, hPos2 = hPos1;

        if(diagramIndex == 1 || diagramIndex == 0)/** determines the way reject lead horizontally **/
            hPos2-=50;
        else
            hPos2+=50;

        /** line leading to reject load **/
        gc.strokeLine(hPos1, vPos1, hPos2, vPos2);

        /** reject load zigzags **/
        for (int i = 0; i < 6; i++) {
            if (i == 0) {
                if (hPos2 < hPos1) {
                    gc.strokeLine(hPos1 - 50, vPos1, hPos2 - 5, vPos2 - 20);
                    vPos1 -= 20;
                    vPos2 += 20;
                    hPos1 -= 55;
                    hPos2 -= 15;
                } else {
                    gc.strokeLine(hPos1 + 50, vPos1, hPos2 + 5, vPos2 - 20);
                    vPos1 -= 20;
                    vPos2 += 20;
                    hPos1 += 55;
                    hPos2 += 15;
                }
            }
            if (i != 0 || i != 6) {
                if (hPos2 < hPos1) {
                    gc.strokeLine(hPos1, vPos1, hPos2, vPos2);
                    hPos1 -= 10;
                    hPos2 -= 10;
                } else {
                    gc.strokeLine(hPos1, vPos1, hPos2, vPos2);
                    hPos1 += 10;
                    hPos2 += 10;
                }
                double temp = vPos1;
                vPos1 = vPos2;
                vPos2 = temp;
            }
            if (i + 1 == 6) {
                if (hPos2 < hPos1) {
                    gc.strokeLine(hPos1, vPos1, hPos2 + 5, vPos2 - 20);
                } else {
                    gc.strokeLine(hPos1, vPos1, hPos2 - 5, vPos2 - 20);
                }
            }
        }
    }

    /********** function to create coupler visual for diagram **********/
    private void createCoupler(double hPos1, double vPos1) {

        /** coupler square **/
        gc.strokeRect(hPos1, vPos1, 50, 50);
        /** post coupler details  bottom left**/
        gc.strokeLine(hPos1 - 10, vPos1 + 37.5, hPos1 + 12.5, vPos1 + 37.5);
        gc.strokeLine(hPos1 + 12.5, vPos1 + 37.5, hPos1 + 12.5, vPos1 + 32.5);
        gc.strokeLine(hPos1 + 12.5, vPos1 + 32.5, hPos1 + 7.5, vPos1 + 32.5);

        /** post coupler details  top left**/
        gc.strokeLine(hPos1 - 10, vPos1 + 19, hPos1 + 12.5, vPos1 + 19);
        gc.strokeLine(hPos1 + 12.5, vPos1 + 19, hPos1 + 12.5, vPos1 + 14);
        gc.strokeLine(hPos1 + 12.5, vPos1 + 14, hPos1 + 7.5, vPos1 + 14);

        /** post coupler details bottom right **/
        gc.strokeLine(hPos1 + 60, vPos1 + 32.5, hPos1 + 37.5, vPos1 + 32.5);
        gc.strokeLine(hPos1 + 37.5, vPos1 + 32.5, hPos1 + 37.5, vPos1 + 37.5);
        gc.strokeLine(hPos1 + 37.5, vPos1 + 37.5, hPos1 + 42.5, vPos1 + 37.5);

        /** post coupler details top right **/
        gc.strokeLine(hPos1 + 60, vPos1 + 19, hPos1 + 37.5, vPos1 + 19);
        gc.strokeLine(hPos1 + 37.5, vPos1 + 19, hPos1 + 37.5, vPos1 + 14);
        gc.strokeLine(hPos1 + 37.5, vPos1 + 14, hPos1 + 42.5, vPos1 + 14);
    }

    /********** function to create hybrid combiner visual for diagram **********/
    private void createHybridCombiner(double hPos1, double vPos1) {

        /** combiner rectangle **/
        gc.strokeRect(hPos1, vPos1, 175, 75);

        if (diagramIndex == 0 || diagramIndex == 1) {
            gc.strokeLine(hPos1 + 25, vPos1 + 50, hPos1 + 150, vPos1 + 25);    /** / **/
            if (diagramIndex == 0)
                gc.strokeLine(hPos1 + 150, vPos1 + 25, hPos1 + 150, vPos1 - 50);
            else if (Controller.txSelectionCabinets == 5)
                gc.strokeLine(hPos1 + 150, vPos1 + 25, hPos1 + 150, vPos1 - 50);
            else
                gc.strokeLine(hPos1 + 150, vPos1 + 25, hPos1 + 150, vPos1 - 25);

            gc.strokeLine(hPos1 + 150, vPos1 + 50, hPos1 + 25, vPos1 + 25); /** \ **/
            if ((Controller.txSelectionCabinets >= 3 && diagramIndex == 1) && Controller.txSelectionCabinets != 5)
                gc.strokeLine(hPos1 + 25, vPos1 + 25, hPos1 + 25, vPos1 - 50);
            else
                gc.strokeLine(hPos1 + 25, vPos1 + 25, hPos1 + 25, vPos1 - 25);
        } else if (diagramIndex > 1) {
            gc.strokeLine(hPos1 + 25, vPos1 + 50, hPos1 + 150, vPos1 + 25);    /** / **/
            gc.strokeLine(hPos1 + 150, vPos1 + 25, hPos1 + 150, vPos1 - 25);

            gc.strokeLine(hPos1 + 150, vPos1 + 50, hPos1 + 25, vPos1 + 25); /** \ **/
            gc.strokeLine(hPos1 + 25, vPos1 + 25, hPos1 + 25, vPos1 - 50);
        }
    }

    /********** function to create antenna visual for diagram **********/
    private void createAntenna(double hPos1, double vPos1){

        /** call createSwitch() to create switch visual **/
        createSwitch(hPos1,vPos1);

        /** call creatTestLoad() to create test load visual **/
        createTestLoad(hPos1+55,vPos1+90);
    }

    /********** function to create test load visual for diagram **********/
    private void createTestLoad(double hPos1, double vPos1){
        gc.strokeRect(hPos1, vPos1, 50, 100);
        gc.strokeLine(hPos1+25, vPos1, hPos1+25, vPos1+25);
        gc.strokeLine(hPos1+25, vPos1+25, hPos1+5, vPos1+30);
        gc.strokeLine(hPos1+5, vPos1+30, hPos1+45, vPos1+40);
        gc.strokeLine(hPos1+45, vPos1+40, hPos1+5, vPos1+50);
        gc.strokeLine(hPos1+5, vPos1+50, hPos1+45, vPos1+60);
        gc.strokeLine(hPos1+45, vPos1+60, hPos1+5, vPos1+70);
        gc.strokeLine(hPos1+5, vPos1+70, hPos1+45, vPos1+80);
        gc.strokeLine(hPos1+45, vPos1+80, hPos1+5, vPos1+90);
        gc.strokeLine(hPos1+5, vPos1+90, hPos1+25, vPos1+95);
        gc.strokeText("TEST LOAD",hPos1-5, vPos1 +115);
    }

    /********** function to create low pass filter visual for diagram **********/
    private void createLowPass(double hPos1, double vPos1){
        /** create line from cabinet to low pass filter **/
        gc.strokeLine(hPos1 + 25, vPos1, hPos1 + 25, vPos1 - 25);

        /** low pass + pre coupler **/
        gc.strokeRect(hPos1, vPos1 - 125, 50, 100);/** low pass rectangle **/
        if(diagramIndex==0)
            gc.strokeText("LOW PASS FILTER",hPos1 - 110, vPos1 - 75);

        /** low pass details **/
        gc.strokeLine(hPos1 + 20, vPos1 - 50, hPos1 + 20, vPos1 - 85);
        gc.strokeLine(hPos1 + 20, vPos1 - 85, hPos1 + 40, vPos1 - 100);

    }

    /********** function to create mask filter visual for diagram **********/
    private void createMask(double hPos1,double vPos1){
        gc.strokeRect(hPos1, vPos1 - 300, 50, 100);/** mask filter rectangle square **/
        if(diagramIndex==0)
            gc.strokeText("MASK FILTER",hPos1 - 110, vPos1 - 250);

        /** mask filter details **/
        gc.strokeLine(hPos1 + 20, vPos1 - 262.5, hPos1 + 40, vPos1 - 287.5);
        gc.strokeLine(hPos1 + 20, vPos1 - 237.5, hPos1 + 20, vPos1 - 262.5);
        gc.strokeLine(hPos1 + 20, vPos1 - 237.5, hPos1 + 40, vPos1 - 212.5);
    }

    private void createSwitch(double hPos1,double vPos1){
        gc.strokeLine(hPos1, vPos1, hPos1 + 50, vPos1);
        gc.strokeOval(hPos1+42.5, vPos1-7.5, 15,15);
        gc.strokeLine(hPos1+50, vPos1, hPos1 + 80, vPos1+40);
        gc.strokeOval(hPos1+72.5, vPos1+32.5, 15,15);
        gc.strokeLine(hPos1+80,vPos1+40,hPos1+80, vPos1+90);
        gc.strokeOval(hPos1+72.5, vPos1-47.5, 15,15);
        gc.strokeOval(hPos1+102.5, vPos1-7.5, 15,15);
    }

    /********** function to save/print diagram **********/
    public void printSaveDiagram() {
        System.out.println("printing/saving file!");
        // Create the Printer Job
        PrinterJob printerJob = PrinterJob.createPrinterJob();

        // Get The Printer Job Settings
        JobSettings jobSettings = printerJob.getJobSettings();

        PageLayout pageLayout;

        if(printerJob.showPrintDialog(Controller.stage) == false)
            printerJob.endJob();
        // Get The Printer
        Printer printer = printerJob.getPrinter();

        // Create the Page Layout of the Printer
        pageLayout = printer.createPageLayout(Paper.C, PageOrientation.LANDSCAPE,Printer.MarginType.EQUAL_OPPOSITES);
        jobSettings.setPageLayout(pageLayout);
        printerJob.printPage(diagram);
        printerJob.endJob();
    }

    /********** function to save/print item report **********/
    public void printSaveTable(){
        System.out.println("printing/saving file!");
        // Create the Printer Job
        PrinterJob printerJob = PrinterJob.createPrinterJob();

        // Get The Printer Job Settings
        JobSettings jobSettings = printerJob.getJobSettings();

        PageLayout pageLayout;
        if(printerJob.showPrintDialog(Controller.stage) == false)
            printerJob.endJob();

            // Get The Printer
            Printer printer = printerJob.getPrinter();
            // Create the Page Layout of the Printer
            pageLayout = printer.createPageLayout(Paper.A4, PageOrientation.LANDSCAPE,Printer.MarginType.DEFAULT);
            jobSettings.setPageLayout(pageLayout);
            printerJob.printPage(itemReport);
            printerJob.endJob();
    }
}