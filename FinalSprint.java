/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sprint;

import java.awt.Desktop;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Stack;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Alert;
import javafx.scene.control.CheckMenuItem;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.Slider;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCombination;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Ellipse;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;
import javafx.scene.text.Font;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Duration;
import javax.imageio.ImageIO;

/**
 *
 * @author Billy
 */
public class Sprint extends Application {

    public MenuBar menubar = new MenuBar();

    public Menu menuFile = new Menu("File");

    public Menu menuHelp = new Menu("Help");

    public Menu menuTools = new Menu("Tool");

    public Menu toolBar = new Menu("Redo/Undo");

    public Menu lineMenu = new Menu("Line Settings");

    public MenuItem save = new MenuItem("Save");

    public MenuItem saveAs = new MenuItem("Save As");

    public MenuItem open = new MenuItem("Open File");

    public MenuItem close = new MenuItem("Close Program");

    public MenuItem openNewTab = new MenuItem("Open New Tab");

    public MenuItem info = new MenuItem("Release Notes");

    public MenuItem redo;

    public MenuItem undo;

    public MenuItem clear;

    private int tabID = 0;
 
    public Stage primaryStage;
    
    public Canvas canvas;
    
    public GraphicsContext gc;
    
    public Stack<Shape> undohistory = new Stack();
    
    public Stack<Shape> redohistory = new Stack();
    
    public CheckMenuItem autoSave = new CheckMenuItem("ON/OFF");
    
    public CheckMenuItem pencil;
    
    public CheckMenuItem erase;
    
    public CheckMenuItem linebtn;
    
    public CheckMenuItem rectangle;
    
    public CheckMenuItem circle;
    
    public CheckMenuItem ellipse;
    
    public CheckMenuItem square;
    
    public CheckMenuItem textbtn;
    
    public Line line = new Line();
    
    public Rectangle rect = new Rectangle();
    
    public Circle circ = new Circle();
    
    public Ellipse elps = new Ellipse();
    
    public Thread timeThread;
    
    public File selectedFile;
    
    public ColorPicker cp;
    
    public ColorPicker cpfill;
    
    public TextField text;
    
    public TabPane tabPane = new TabPane();
    
    public Slider slider;
    
    final double W = 1200;
    
    final double H = 900;
    
    Label linewidth = new Label("Line Width");

    private static final Integer STARTTIME = 30;

    
    private Timeline timeline;

    
    private final Label timerLabel = new Label();

   
    private final IntegerProperty timeSeconds
            = new SimpleIntegerProperty(STARTTIME);

    public void start(Stage primaryStage) {
        timerLabel.setText(timeSeconds.toString());
        timerLabel.setTextFill(Color.BLACK);
        timerLabel.setStyle("-fx-font-size: 1em;");

        cp = new ColorPicker(Color.BLACK);// creating the colorpciker

        cpfill = new ColorPicker(Color.BLACK); // creating the colorpicker for the fill color

        text = new TextField();//creating the text field for drawing text

        canvas = new Canvas(W, H);//creating canvas

        gc = canvas.getGraphicsContext2D();//setting graphics context to the canvas
        gc.setLineWidth(1);

        /* setting the on actions in order to draw */
        canvas.setOnMousePressed(e -> mousePressed(e));
        canvas.setOnMouseDragged(e -> mouseDragged(e));
        canvas.setOnMouseReleased(e -> mouseReleased(e));

        /* setting the on action for the coloricker and changing the stroke color */
        cp.setOnAction((e) -> {
            gc.setStroke(cp.getValue());
        });
        /* setting the on action for the fill option on the color picker and changing the fill color*/
        cpfill.setOnAction((e) -> {
            gc.setStroke(cp.getValue());
        });

        StackPane stack = new StackPane(canvas);
        BorderPane pane = new BorderPane(stack);
        HBox hbox = new HBox(5);
        hbox.getChildren().addAll(makeMenu(), tabPane);//adding menu to hbox 
        Group root = new Group(pane, hbox);
        Scene scene = new Scene(root, W, H);
        hbox.setPrefHeight(scene.getHeight());
        hbox.setPrefWidth(scene.getWidth());
        
        timerLabel.textProperty().bind(timeSeconds.asString());

        primaryStage.setScene(scene);
        primaryStage.show();

    }

    /**
     *
     * @return
     */
    private MenuBar makeMenu() {
        /*creating all of the menus for the file, help, tools, redo/undo, and line settings  */
        menubar = new MenuBar();

        menuFile = new Menu("File");
        menuHelp = new Menu("Help");
        menuTools = new Menu("Tool");
        toolBar = new Menu("Redo/Undo");
        lineMenu = new Menu("Line Settings");

        menubar.getMenus().add(menuFile);
        menubar.getMenus().add(menuHelp);
        menubar.getMenus().add(menuTools);
        menubar.getMenus().add(toolBar);
        menubar.getMenus().add(lineMenu);

        save = new MenuItem("Save");
        saveAs = new MenuItem("Save As");
        open = new MenuItem("Open File");
        close = new MenuItem("Close Program");
        openNewTab = new MenuItem("Open New Tab");
        autoSave = new CheckMenuItem("ON/OFF", timerLabel);

        info = new MenuItem("Release Notes");

        MenuItem lineStroke = new MenuItem("Line Width", cp);
        MenuItem lineFill = new MenuItem("Line Fill", cpfill);
        MenuItem textItem = new MenuItem("Text Option", text);

        textbtn = new CheckMenuItem("Text");

        menuFile.getItems().addAll(open, openNewTab, save, saveAs, autoSave, close);
        menuHelp.getItems().add(info);
        lineMenu.getItems().addAll(lineStroke, lineFill, textItem);

        /*Creating Slider */
        slider = new Slider(1, 50, 3);
        slider.setShowTickLabels(true);
        slider.setShowTickMarks(true);
        MenuItem slidermenu = new MenuItem("Adjust Width", slider);
        lineMenu.getItems().add(slidermenu);

        /* creating the toggle buttons for each drawing tool the toggle buttons are
        allowing the tools to selected or deselected */
        pencil = new CheckMenuItem("Pencil");
        erase = new CheckMenuItem("Erase");
        linebtn = new CheckMenuItem("Line");
        rectangle = new CheckMenuItem("Rectangle");
        circle = new CheckMenuItem("Circle");
        ellipse = new CheckMenuItem("Ellipse");
        square = new CheckMenuItem("Square");

        menuTools.getItems().addAll(pencil, erase, linebtn, rectangle, circle, ellipse, square, textbtn);

        redo = new MenuItem("Redo");
        undo = new MenuItem("Undo");
        clear = new MenuItem("Reset Canvas");
        toolBar.getItems().addAll(undo, redo, clear);
        /* setting the on action for the clear function */
        clear.setOnAction((e) -> {
            clearCanvas();
        });
        /* Setting on action for the autosave timer and autosave feature */
        autoSave.setOnAction((e) -> {
            startTimer();
            Timer();
        });

        /*Setting the open on action to open a new file using filchooser and running a if statement to make sure the a file is selected */
        open.setOnAction((e) -> {
            doOpenImage();
        });

        /* Setting the on action for saving */
        save.setOnAction((e) -> {
            doSaveImage();
        });

        /* Setting the on action for the close feature*/
        close.setOnAction((e) -> {
            doCloseProgram();
        });

        /*Setting the on action for the info feature to open up the release notes */
        info.setOnAction((e) -> {
            doOpenReleaseNotes();
        });

        /* Setting the value property of the slider in order to change the width of the drawing tools.*/
        openNewTab.setOnAction((e) -> {
            openTab();
        });
        /* creating the listener to change the line width */
        slider.valueProperty().addListener(e -> {
            changeWidth();
        });

        /* Setting the shortcut keys for the open save and close features*/
        open.setAccelerator(KeyCombination.keyCombination("shortcut+O"));
        save.setAccelerator(KeyCombination.keyCombination("shortcut+S"));
        saveAs.setAccelerator(KeyCombination.keyCombination("shortcut+A"));
        close.setAccelerator(KeyCombination.keyCombination("shortcut+C"));

        /*Setting the on action for the undo */
        undo.setOnAction(e -> {
            doUndo();
        });
        /*Setting the on action for the redo */
        redo.setOnAction(e -> {
            doRedo();
        });
        /* Creating the tool tips for the timer label and the menubar */
        Tooltip tpTimer = new Tooltip("Turn autosave timer on and off");
        Tooltip.install(timerLabel, tpTimer);
        Tooltip tp = new Tooltip("This is your menu bar");
        Tooltip.install(menubar, tp);
        
        return menubar;
    }

    /**   
     * method to start the timer
     */
    private void startTimer() {

        if (timeline != null) {
            timeline.stop();
        }
        timeSeconds.set(STARTTIME);
        timeline = new Timeline();
        timeline.getKeyFrames().add(
                new KeyFrame(Duration.seconds(STARTTIME + 1),
                        new KeyValue(timeSeconds, 0)));
        timeline.playFromStart();

    }

    /**
     * method to clear the canvas
     */
    private void clearCanvas() {
        gc.setFill(Color.WHITE);
        gc.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());
    }

    /**
     * Method to change the width of the line when drawing
     */
    private void changeWidth() {
        double width = slider.getValue();
        if (textbtn.isSelected()) {
            gc.setLineWidth(1);
            gc.setFont(Font.font(slider.getValue()));
            linewidth.setText(String.format("%.1f", width));
            return;
        }
        linewidth.setText(String.format("%.1f", width));
        gc.setLineWidth(width);
    }

    /**
     * method to save the canvas to a file
     */
    private void doSaveImage() {
        FileChooser fc = new FileChooser();
        fc.setInitialFileName("imagefile.png");
        fc.setInitialDirectory(
                new File(System.getProperty("user.home")));
        fc.setTitle("Select file, must end in png");
        selectedFile = fc.showSaveDialog(primaryStage);
        if (selectedFile == null) {
            return;
        }
        try {
            Image canvasImage = canvas.snapshot(null, null);
            BufferedImage image = SwingFXUtils.fromFXImage(canvasImage, null);
            String filename = selectedFile.getName().toLowerCase();
            if (!filename.endsWith(".png")) {
                throw new Exception("The file name must en with \".png\".");
            }
            boolean hasFormat = ImageIO.write(image, "PNG", selectedFile);
            if (!hasFormat) {
                throw new Exception("PNG format not available");
            }
        } catch (Exception e) {
            Alert errorAlert = new Alert(Alert.AlertType.ERROR,
                    "Sorry,an error occured while \n trying to save the image: \n" + e.getMessage());
            errorAlert.showAndWait();
        }
    }

    /**
     * Auto save method
     */
    private void doSave() {
        if (selectedFile == null) {
            return;
        }
        try {
            Image canvasImage = canvas.snapshot(null, null);
            BufferedImage image = SwingFXUtils.fromFXImage(canvasImage, null);
            String filename = selectedFile.getName().toLowerCase();
            if (!filename.endsWith(".png")) {
                throw new Exception("The file name must en with \".png\".");
            }
            boolean hasFormat = ImageIO.write(image, "PNG", selectedFile);
            if (!hasFormat) {
                throw new Exception("PNG format not available");
            }
        } catch (Exception e) {
            Alert errorAlert = new Alert(Alert.AlertType.ERROR,
                    "Sorry,an error occured while \n trying to save the image: \n" + e.getMessage());
            errorAlert.showAndWait();
        }
    }

    /* Creating the open file method */
    /**
     *
     */
    private void doOpenImage() {
        FileChooser openfile = new FileChooser();
        openfile.setTitle("Open File");
        File file = openfile.showOpenDialog(primaryStage);
        if (file != null) {
            try {
                InputStream io = new FileInputStream(file);
                Image img = new Image(io);
                gc.drawImage(img, 0, 0);

            } catch (IOException ex) {
                System.out.println("Error!");
            }
        }
    }

    /*Creating the close program method */
    /**
     *
     */
    private void doCloseProgram() {
        Platform.exit();
    }

    /*Creating the release notes method */
    /**
     *
     */
    private void doOpenReleaseNotes() {
        File pdfFile = new File("C:\\Users\\Billy\\OneDrive\\Desktop\\Sprint 4");

        try {
            Desktop.getDesktop().open(pdfFile);
        } catch (IOException ex) {
            Logger.getLogger(Sprint.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * method to timer function in the auto save feature
     */
    private void Timer() {
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                doSave();
                System.out.println("Auto Saved");
            }

            private void doSave() {
                
        if (selectedFile == null) {
            return;
        }
        try {
            Image canvasImage = canvas.snapshot(null, null);
            BufferedImage image = SwingFXUtils.fromFXImage(canvasImage, null);
            String filename = selectedFile.getName().toLowerCase();
            if (!filename.endsWith(".png")) {
                throw new Exception("The file name must en with \".png\".");
            }
            boolean hasFormat = ImageIO.write(image, "PNG", selectedFile);
            if (!hasFormat) {
                throw new Exception("PNG format not available");
            }
        } catch (Exception e) {
            Alert errorAlert = new Alert(Alert.AlertType.ERROR,
                    "Sorry,an error occured while \n trying to save the image: \n" + e.getMessage());
            errorAlert.showAndWait();
        }
    }
            
        }, 30000);

    }

    /**
     * method to open a new tab
     */
    private void openTab() {
        Tab tab = new Tab("Tab:" + tabID++);
        tab.setContent(createContent());
        tabPane.getTabs().add(tab);
        tabPane.getSelectionModel().select(tab);
        Tooltip tpTab = new Tooltip("This is your tab pane");
        Tooltip.install(tabPane, tpTab);
    }

    /**
     * method to create the content that is going onto the new tab
     * @return
     */
    private Node createContent() {
        canvas = new Canvas(W, H);
        gc = canvas.getGraphicsContext2D();
        doOpenImage();
        canvas.setOnMousePressed(e -> mousePressed(e));
        canvas.setOnMouseDragged(e -> mouseDragged(e));
        canvas.setOnMouseReleased(e -> mouseReleased(e));
        Tooltip tpcanvas = new Tooltip("You can draw here");
        Tooltip.install(canvas, tpcanvas);

        return canvas;
    }

    /**
     * Drawing on actions
     * @param e
     */
    private void mousePressed(MouseEvent e) {
        if (circle.isSelected()) {
            gc.setStroke(cp.getValue());
            gc.setFill(cpfill.getValue());
            circ.setCenterX(e.getX());
            circ.setCenterY(e.getY());
        } else if (ellipse.isSelected()) {
            gc.setStroke(cp.getValue());
            gc.setFill(cpfill.getValue());
            elps.setCenterX(e.getX());
            elps.setCenterY(e.getY());
        } else if (rectangle.isSelected()) {
            gc.setStroke(cp.getValue());
            gc.setFill(cpfill.getValue());
            rect.setX(e.getX());
            rect.setY(e.getY());
        } else if (linebtn.isSelected()) {
            gc.setStroke(cpfill.getValue());
            line.setStartX(e.getX());
            line.setStartY(e.getY());
        } else if (pencil.isSelected()) {
            gc.setStroke(cpfill.getValue());
            gc.beginPath();
            gc.lineTo(e.getX(), e.getY());
        } else if (erase.isSelected()) {
            double lineWidth = gc.getLineWidth();
            gc.clearRect(e.getX() - lineWidth / 2, e.getY() - lineWidth / 2, lineWidth, lineWidth);
        } else if (textbtn.isSelected()) {
            gc.setLineWidth(1);
            gc.setFont(Font.font(slider.getValue()));
            gc.setStroke(cp.getValue());
            gc.setFill(cpfill.getValue());
            gc.fillText(text.getText(), e.getX(), e.getY());
            gc.strokeText(text.getText(), e.getX(), e.getY());
        }
    }

    /**
     *Drawing on actions
     * @param e
     */
    private void mouseDragged(MouseEvent e) {
        if (circle.isSelected()) {
            circ.setRadius((Math.abs(e.getX() - circ.getCenterX()) + Math.abs(e.getY() - circ.getCenterY()) / 2));
            if (circ.getCenterX() > e.getX()) {
                circ.setCenterX(e.getX());
            }
            if (circ.getCenterY() > e.getY()) {
                circ.setCenterY(e.getY());
            }

        } else if (ellipse.isSelected()) {

        } else if (rectangle.isSelected()) {

        } else if (linebtn.isSelected()) {

        } else if (pencil.isSelected()) {
            gc.lineTo(e.getX(), e.getY());
            gc.stroke();
        } else if (erase.isSelected()) {
            double lineWidth = gc.getLineWidth();
            gc.clearRect(e.getX() - lineWidth / 2, e.getY() - lineWidth / 2, lineWidth, lineWidth);
        }
    }

    /**
     *Drawing on actions
     * @param e
     */
    private void mouseReleased(MouseEvent e) {
        if (circle.isSelected()) {
            gc.fillOval(circ.getCenterX(), circ.getCenterY(), circ.getRadius(), circ.getRadius());
            gc.strokeOval(circ.getCenterX(), circ.getCenterY(), circ.getRadius(), circ.getRadius());
        } else if (ellipse.isSelected()) {
            elps.setRadiusX(Math.abs(e.getX() - elps.getCenterX()));
            elps.setRadiusY(Math.abs(e.getY() - elps.getCenterY()));

            if (elps.getCenterX() > e.getX()) {
                elps.setCenterX(e.getX());
            }
            if (elps.getCenterY() > e.getY()) {
                elps.setCenterY(e.getY());
            }

            gc.strokeOval(elps.getCenterX(), elps.getCenterY(), elps.getRadiusX(), elps.getRadiusY());
            gc.fillOval(elps.getCenterX(), elps.getCenterY(), elps.getRadiusX(), elps.getRadiusY());

            undohistory.push(new Ellipse(elps.getCenterX(), elps.getCenterY(), elps.getRadiusX(), elps.getRadiusY()));
        } else if (rectangle.isSelected()) {
            rect.setWidth(Math.abs((e.getX() - rect.getX())));
            rect.setHeight(Math.abs((e.getY() - rect.getY())));
            rect.setX((rect.getX() > e.getX()) ? e.getX() : rect.getX());
            if (rect.getX() > e.getX()) {
                rect.setX(e.getX());
            }
            rect.setY((rect.getY() > e.getY()) ? e.getY() : rect.getY());
            if (rect.getY() > e.getY()) {
                rect.setY(e.getY());
            }

            gc.fillRect(rect.getX(), rect.getY(), rect.getWidth(), rect.getHeight());
            gc.strokeRect(rect.getX(), rect.getY(), rect.getWidth(), rect.getHeight());

            undohistory.push(new Rectangle(rect.getX(), rect.getY(), rect.getWidth(), rect.getHeight()));
        } else if (linebtn.isSelected()) {
            line.setEndX(e.getX());
            line.setEndY(e.getY());
            gc.strokeLine(line.getStartX(), line.getStartY(), line.getEndX(), line.getEndY());

            undohistory.push(new Line(line.getStartX(), line.getStartY(), line.getEndX(), line.getEndY()));
        } else if (pencil.isSelected()) {
            gc.lineTo(e.getX(), e.getY());
            gc.stroke();
            gc.closePath();
        } else if (erase.isSelected()) {
            double lineWidth = gc.getLineWidth();
            gc.clearRect(e.getX() - lineWidth / 2, e.getY() - lineWidth / 2, lineWidth, lineWidth);
        }
        redohistory.clear();
        Shape lastUndo = undohistory.lastElement();
        lastUndo.setFill(gc.getFill());
        lastUndo.setStroke(gc.getStroke());
        lastUndo.setStrokeWidth(gc.getLineWidth());

    }

    /**
     * method to undo any previous action done onto the canvas
     */
    private void doUndo() {
        if (!undohistory.empty()) {
            gc.clearRect(0, 0, 1080, 700);
            Shape removedShape = undohistory.lastElement();
            if (removedShape.getClass() == Line.class) {
                Line tempLine = (Line) removedShape;
                tempLine.setFill(gc.getFill());
                tempLine.setStroke(gc.getStroke());
                tempLine.setStrokeWidth(gc.getLineWidth());
                redohistory.push(new Line(tempLine.getStartX(), tempLine.getStartY(), tempLine.getEndX(), tempLine.getEndY()));

            } else if (removedShape.getClass() == Rectangle.class) {
                Rectangle tempRect = (Rectangle) removedShape;
                tempRect.setFill(gc.getFill());
                tempRect.setStroke(gc.getStroke());
                tempRect.setStrokeWidth(gc.getLineWidth());
                redohistory.push(new Rectangle(tempRect.getX(), tempRect.getY(), tempRect.getWidth(), tempRect.getHeight()));
            } else if (removedShape.getClass() == Circle.class) {
                Circle tempCirc = (Circle) removedShape;
                tempCirc.setStrokeWidth(gc.getLineWidth());
                tempCirc.setFill(gc.getFill());
                tempCirc.setStroke(gc.getStroke());
                redohistory.push(new Circle(tempCirc.getCenterX(), tempCirc.getCenterY(), tempCirc.getRadius()));
            } else if (removedShape.getClass() == Ellipse.class) {
                Ellipse tempElps = (Ellipse) removedShape;
                tempElps.setFill(gc.getFill());
                tempElps.setStroke(gc.getStroke());
                tempElps.setStrokeWidth(gc.getLineWidth());
                redohistory.push(new Ellipse(tempElps.getCenterX(), tempElps.getCenterY(), tempElps.getRadiusX(), tempElps.getRadiusY()));
            }
            Shape lastRedo = redohistory.lastElement();
            lastRedo.setFill(removedShape.getFill());
            lastRedo.setStroke(removedShape.getStroke());
            lastRedo.setStrokeWidth(removedShape.getStrokeWidth());
            undohistory.pop();

            for (int i = 0; i < undohistory.size(); i++) {
                Shape shape = undohistory.elementAt(i);
                if (shape.getClass() == Line.class) {
                    Line temp = (Line) shape;
                    gc.setLineWidth(temp.getStrokeWidth());
                    gc.setStroke(temp.getStroke());
                    gc.setFill(temp.getFill());
                    gc.strokeLine(temp.getStartX(), temp.getStartY(), temp.getEndX(), temp.getEndY());
                } else if (shape.getClass() == Rectangle.class) {
                    Rectangle temp = (Rectangle) shape;
                    gc.setLineWidth(temp.getStrokeWidth());
                    gc.setStroke(temp.getStroke());
                    gc.setFill(temp.getFill());
                    gc.fillRect(temp.getX(), temp.getY(), temp.getWidth(), temp.getHeight());
                    gc.strokeRect(temp.getX(), temp.getY(), temp.getWidth(), temp.getHeight());
                } else if (shape.getClass() == Circle.class) {
                    Circle temp = (Circle) shape;
                    gc.setLineWidth(temp.getStrokeWidth());
                    gc.setStroke(temp.getStroke());
                    gc.setFill(temp.getFill());
                    gc.fillOval(temp.getCenterX(), temp.getCenterY(), temp.getRadius(), temp.getRadius());
                    gc.strokeOval(temp.getCenterX(), temp.getCenterY(), temp.getRadius(), temp.getRadius());
                } else if (shape.getClass() == Ellipse.class) {
                    Ellipse temp = (Ellipse) shape;
                    gc.setLineWidth(temp.getStrokeWidth());
                    gc.setStroke(temp.getStroke());
                    gc.setFill(temp.getFill());
                    gc.fillOval(temp.getCenterX(), temp.getCenterY(), temp.getRadiusX(), temp.getRadiusY());
                    gc.strokeOval(temp.getCenterX(), temp.getCenterY(), temp.getRadiusX(), temp.getRadiusY());
                }
            }

        } else {
            System.out.println("there is no action to undo");
        }
    }

    /**
     *method to redo any previous action done onto the canvas
     */
    private void doRedo() {
        if (!redohistory.empty()) {
            Shape shape = redohistory.lastElement();
            gc.setLineWidth(shape.getStrokeWidth());
            gc.setStroke(shape.getStroke());
            gc.setFill(shape.getFill());

            redohistory.pop();
            if (shape.getClass() == Line.class) {
                Line tempLine = (Line) shape;
                gc.strokeLine(tempLine.getStartX(), tempLine.getStartY(), tempLine.getEndX(), tempLine.getEndY());
                undohistory.push(new Line(tempLine.getStartX(), tempLine.getStartY(), tempLine.getEndX(), tempLine.getEndY()));
            } else if (shape.getClass() == Rectangle.class) {
                Rectangle tempRect = (Rectangle) shape;
                gc.fillRect(tempRect.getX(), tempRect.getY(), tempRect.getWidth(), tempRect.getHeight());
                gc.strokeRect(tempRect.getX(), tempRect.getY(), tempRect.getWidth(), tempRect.getHeight());

                undohistory.push(new Rectangle(tempRect.getX(), tempRect.getY(), tempRect.getWidth(), tempRect.getHeight()));
            } else if (shape.getClass() == Circle.class) {
                Circle tempCirc = (Circle) shape;
                gc.fillOval(tempCirc.getCenterX(), tempCirc.getCenterY(), tempCirc.getRadius(), tempCirc.getRadius());
                gc.strokeOval(tempCirc.getCenterX(), tempCirc.getCenterY(), tempCirc.getRadius(), tempCirc.getRadius());

                undohistory.push(new Circle(tempCirc.getCenterX(), tempCirc.getCenterY(), tempCirc.getRadius()));
            } else if (shape.getClass() == Ellipse.class) {
                Ellipse tempElps = (Ellipse) shape;
                gc.fillOval(tempElps.getCenterX(), tempElps.getCenterY(), tempElps.getRadiusX(), tempElps.getRadiusY());
                gc.strokeOval(tempElps.getCenterX(), tempElps.getCenterY(), tempElps.getRadiusX(), tempElps.getRadiusY());

                undohistory.push(new Ellipse(tempElps.getCenterX(), tempElps.getCenterY(), tempElps.getRadiusX(), tempElps.getRadiusY()));
            }
            Shape lastUndo = undohistory.lastElement();
            lastUndo.setFill(gc.getFill());
            lastUndo.setStroke(gc.getStroke());
            lastUndo.setStrokeWidth(gc.getLineWidth());
        } else {
            System.out.println("there is no action to redo");
        }
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }

}
