/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package appAFND.view;

import appAFND.controller.AlphabetController;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ScrollPane;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import appAFND.model.Node;
import appAFND.controller.NodeController;
import appAFND.controller.StateController;
import appAFND.model.Alphabet;
import appAFND.model.Automaton;
import appAFND.model.NFA;
import appAFND.model.State;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Optional;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import java.io.File;
import java.io.IOException;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.EventHandler;
import javafx.scene.Cursor;
import javafx.scene.Group;
import javafx.scene.SnapshotParameters;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.scene.Cursor;
import javafx.scene.Group;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.SplitPane;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputDialog;
import javafx.scene.control.TableView;
import javafx.scene.image.WritableImage;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.MouseButton;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Polygon;
import javafx.scene.shape.Rectangle;
import javafx.scene.transform.Rotate;
import org.controlsfx.control.spreadsheet.GridBase;
import org.controlsfx.control.spreadsheet.SpreadsheetCell;
import org.controlsfx.control.spreadsheet.SpreadsheetCellType;
import org.controlsfx.control.spreadsheet.SpreadsheetView;
import javax.imageio.ImageIO;
import javax.swing.JFrame;

import org.bytedeco.javacpp.opencv_core;
import org.bytedeco.javacpp.opencv_core.KeyPointVector;
import org.bytedeco.javacpp.opencv_core.Mat;
import org.bytedeco.javacpp.opencv_core.Scalar;
import org.bytedeco.javacpp.opencv_features2d;
import org.bytedeco.javacpp.opencv_features2d.DrawMatchesFlags;
import org.bytedeco.javacpp.opencv_features2d.FastFeatureDetector;
import static org.bytedeco.javacpp.opencv_features2d.drawKeypoints;
import org.bytedeco.javacpp.opencv_imgcodecs;
import static org.bytedeco.javacpp.opencv_imgcodecs.IMREAD_COLOR;
import static org.bytedeco.javacpp.opencv_imgcodecs.imread;
import org.bytedeco.javacv.CanvasFrame;
import org.bytedeco.javacv.OpenCVFrameConverter;
import org.controlsfx.control.spreadsheet.SpreadsheetColumn;


/**
 * FXML Controller class
 *
 * @author kirit
 */
public class AFNDController implements Initializable
{

    @FXML
    private ScrollPane scrollPane;

    private int radius;
    private ArrayList<NodeController> nodes;
    private ArrayList<TransitionView> transitions;
    @FXML
    private MenuBar menuBar;
    @FXML
    private Menu menuFile;
    @FXML
    private Menu menuEdit;
    @FXML
    private Menu menuOptions;
    @FXML
    private Menu menuHelp;
    @FXML
    private Button buttonMove;
    @FXML
    private Button buttonState;
    @FXML
    private Button buttonTransition;
    @FXML
    private Button buttonUndo;
    @FXML
    private Button buttonRedo;
    @FXML
    private ComboBox<?> comboZoom;
    @FXML
    private SplitPane splitPane;
    
    private SpreadsheetView spreadSheet;
    
    private Group group = new Group();
    private Rectangle canvas = new Rectangle(0, 0, 0, 0);
    private String buttonPressed;
    private int canvasWidth;
    private int canvasHeight;
    @FXML
    private TabPane tabPane;
    
    private Automaton automaton;
    @FXML
    private TextField wordField;
    @FXML
    private Button buttonExecute;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        this.askForAlphabet();
        this.canvasWidth = 1200;
        this.canvasHeight = 800;
        
        this.canvas.setHeight(canvasHeight);
        this.canvas.setWidth(this.canvasWidth);
        this.canvas.setFill(Color.WHITE);
        this.group.getChildren().add(this.canvas);
        this.scrollPane.setContent(this.group);
        
        //this.automaton = new NFA();
        
        this.canvas.setOnMouseClicked(new EventHandler<MouseEvent>(){
            @Override
            public void handle(MouseEvent event) {
                canvasClick(event);
            }
        });
        this.canvas.setOnMouseDragged(new EventHandler<MouseEvent>(){
            @Override
            public void handle(MouseEvent event) {
                canvasDrag(event);
            }
        });
        
        this.radius = 25;
        this.nodes = new ArrayList<>();
        this.transitions = new ArrayList<>();
        
        this.updateTable();
        
        
        this.splitPane.getItems().addAll(this.spreadSheet);
        this.splitPane.setDividerPositions(0.99);
        
    }

    @FXML
    private void zoom(ActionEvent event) {
    }

    @FXML
    private void selectEdit(ActionEvent event) {
        buttonMove.setCursor(Cursor.OPEN_HAND);
        buttonPressed = "Edit";
        event.consume();
    }

    @FXML
    private void selectState(ActionEvent event) {
        buttonPressed = "State";
        event.consume();
    }
    
    @FXML
    private void selectTransition(ActionEvent event) {
        buttonPressed = "Transition";
        event.consume();
    }

    @FXML
    private void undo(ActionEvent event) {
    }

    @FXML
    private void redo(ActionEvent event) {
    }

  
    private void canvasDrag(MouseEvent event) {
    }

    private void canvasClick(MouseEvent event) {
        double x = event.getX();                
        if(x < 25){
            x = 25;
        }
        else if (x > this.canvasWidth-25){
            x = this.canvasWidth-25;
        }

        double y = event.getY();
        if(y < 25){
            y = 25;
        }
        else if (y > this.canvasHeight-25){
            y = this.canvasHeight-25;
        }
        
        switch(buttonPressed){
            case "Edit":
                //Right click -> Context menu (change label, set final)
                //Drag node with transitions
                break;
                
            case "State":
                Node node = new Node(x, y, this.radius, this.nodes.size());
                NodeView nodeView = new NodeView(x, y, this.radius, this.nodes.size());
                NodeController nodeController = new NodeController(node, nodeView);

                boolean overlapped = false;

                for (NodeController item : this.nodes)
                {
                    if (item.compareTo(nodeController) == 0)
                    {
                        overlapped = true;
                    }
                }

                if (!overlapped)
                {
                    nodeController.drawNode(this.group);

                    
                    if(this.nodes.size() == 0)
                    {
                    // ADD INITIAL NODE
                    }
                    //ADD NODES TO NFA
                    
                    this.nodes.add(nodeController);
                }
                WritableImage image = group.snapshot(new SnapshotParameters(), null);
                // TODO: probably use a file chooser here
                File file = new File("canvas.png");
                try {
                    ImageIO.write(SwingFXUtils.fromFXImage(image, null), "png", file);
                } 
                catch (IOException e) {
                    // TODO: handle exception here
                }

                Mat image2 = imread(file.getAbsolutePath(), IMREAD_COLOR);
                FastFeatureDetector ffd = FastFeatureDetector.create(
                    25/* threshold for detection */ ,
                    true /* non-max suppression */ ,
                    FastFeatureDetector.TYPE_7_12);
                KeyPointVector keyPoints = new KeyPointVector();
                ffd.detect(image2, keyPoints);
                //Muestra una imagen en una ventana nueva con los vertices detectados
                /*Mat c = new Mat();
                drawKeypoints(image2, keyPoints, c, new Scalar(0, 0, 255, 0), DrawMatchesFlags.DEFAULT);
                OpenCVFrameConverter.ToMat converter = new OpenCVFrameConverter.ToMat();
                CanvasFrame canvas = new CanvasFrame("hola", 1);
                canvas.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
                canvas.showImage(converter.convert(c));*/

                //Cantidad de vertices detectados
                System.out.println(keyPoints.size());
                break;
            case "Transition":
                TransitionView transition = new TransitionView(x, y, x, y, this.transitions.size());
                group.getChildren().add(transition.getTransition());
                transitions.add(transition);
                break;
            default:
                break;
                
        
        }  
    }

    public void setAutomaton(Automaton automaton)
    {
        this.automaton = automaton;
    }
    
    private void updateTable()
    {
        
        this.automaton.addState(new StateController(new State(true, "Q1", true), new StateView()));
        
        int rowCount = this.automaton.getStates().size() + 1;
        int colCount = this.automaton.getAlphabet().alphabetSize() + 1;
        
        GridBase grid = new GridBase(rowCount, colCount);
        
        ArrayList<ObservableList<SpreadsheetCell>> rows = new ArrayList<>(grid.getRowCount());
        
        HashMap<StateController, HashMap<String, ArrayList<StateController>>> transitions = this.automaton.getF();
        
        ObservableList<SpreadsheetCell> list = FXCollections.observableArrayList();
        SpreadsheetCell cell00 = SpreadsheetCellType.STRING.createCell(0, 0, 1, 1, "Q/\u03A3");
        cell00.setStyle("-fx-background-color: #8FE6F3");
        list.add(cell00);
        
        for(int j = 0; j < this.automaton.getAlphabet().alphabetSize(); j++)
        {
            SpreadsheetCell alphCell = SpreadsheetCellType.STRING.createCell(0, j + 1, 1, 1, this.automaton.getAlphabet().getCharacter(j).toString());
            list.add(alphCell);
        }
        
        rows.add(list);
        
        
        for (int i = 0; i < this.automaton.getStates().size(); i++)
        {
            list = FXCollections.observableArrayList();

            StateController state = this.automaton.getStates().get(i);
            SpreadsheetCell stateCell = SpreadsheetCellType.STRING.createCell(i + 1, 0, 1, 1, state.getStateLabel());
            list.add(stateCell);
            HashMap<String, ArrayList<StateController>> stateTransitions = transitions.get(state);
            for (int j = 0; j < this.automaton.getAlphabet().alphabetSize(); j++)
            {
                ArrayList<StateController> to = stateTransitions.get(this.automaton.getAlphabet().getCharacter(j).toString());
                String label = "";
                if(to != null)
                {
                    for (int k = 0; k < to.size(); k++)
                    {
                        label += to.get(k).getStateLabel();
                        if(!(k < to.size() - 1))
                            label += ", ";
                    }
                }
                else {
                    label += " ";
                }
                SpreadsheetCell transCell = SpreadsheetCellType.STRING.createCell(i+ 1, j + 1, 0, 0, label);
                list.add(transCell);
            }
            rows.add(list);

        }
        
        grid.setRows(rows);
        this.spreadSheet = new SpreadsheetView(grid);
        this.spreadSheet.setEditable(false);
        this.spreadSheet.setShowColumnHeader(false);
        this.spreadSheet.setShowRowHeader(false);
        
        ObservableList<SpreadsheetColumn> columns = this.spreadSheet.getColumns();
        if(columns.get(0).isColumnFixable())
            this.spreadSheet.getFixedColumns().addAll(columns.get(0));
        if(this.spreadSheet.isRowFixable(0))
            this.spreadSheet.getFixedRows().add(new Integer(0));
    }
    
    private void askForAlphabet()
    {
        TextInputDialog dialog = new TextInputDialog("");
        dialog.setTitle("Input alphabet");
        dialog.setHeaderText("Please, insert symbols for alphabet (separated by semicolon ;)");
        dialog.setContentText("Symbols:");
        
        Optional<String> result = dialog.showAndWait();
        
        if (result.isPresent())
        {
            String[] alphabet = result.get().split(";");
            char[] chars = new char[alphabet.length];
            Alphabet alph = new Alphabet();
            AlphabetView alphView = new AlphabetView();
            AlphabetController alphController = new AlphabetController(alph, alphView);
            try
            {
                for(String str : alphabet)
                {
                    alphController.addCharacter(str.charAt(0));
                }
                this.automaton = new NFA(new ArrayList<>(), alphController, new ArrayList<>(), null);
            }
            catch(Exception ex)
            {
                System.exit(0);
            }
        }
        else
        {
            System.exit(0);
        }
    }
}