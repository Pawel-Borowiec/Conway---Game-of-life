package game;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import static game.Status.Untapped;

public class Main extends Application {
    public static MyButton[][] tiles = new MyButton[20][20];
    private Background playerColor ;
    private static Label countLabel;
    private static Color inactiveCellColor = Color.DARKGRAY;
    static int cellCount=0;
    static Slider slider;
    Thread animationThread;
    private boolean isSimulationGoing = false;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        primaryStage.setTitle("Conway - Game of Life");
        primaryStage.setWidth(1020);
        primaryStage.setHeight(840);
        addButtons();
        StackPane root = new StackPane();
        HBox vBox = new HBox();

        GridPane field = new GridPane();

        addToGrid(field);
        vBox.getChildren().addAll(field,getCommandPanel());
        root.getChildren().add(vBox);
        primaryStage.setScene(new Scene(root));
        primaryStage.show();
    }

    public void addButtons()
    {
        for(int i=0;i<20;i++) {
            for (int j = 0; j < 20; j++)
            {
                tiles[i][j]=createButton();
            }
        }
    }
    public void addToGrid(GridPane gridPane)
    {
        for(int i=0;i<20;i++)
        {
            for (int j=0;j<20;j++)
            {
                gridPane.add(tiles[i][j],i,j);
            }
        }
    }
    public MyButton createButton()
    {
        MyButton button = new MyButton();
        button.setPrefSize(40,40);
        button.setStyle("-fx-border-color: #000; -fx-border-width: 1px;");
        button.status= Untapped;
        button.setBackground(new Background(new BackgroundFill(inactiveCellColor,CornerRadii.EMPTY,Insets.EMPTY)));
        button.setOnAction(event -> {
            if(button.status== Untapped)
            {
                button.setBackground(playerColor);
                button.status=Status.Tapped;
                cellCount++;
            }else{
                button.setBackground(new Background(new BackgroundFill(inactiveCellColor,CornerRadii.EMPTY,Insets.EMPTY)));
                button.status= Untapped;
                cellCount--;
            }
            updateCellsCounter(cellCount);
        });
        return button;
    }

    private Button getNewTurnButton(){
        Button button = new Button("Next Turn");
        button.setPrefSize(200,60);
        button.setOnAction(event -> {
            invokeNextTurn();
        });
        return button;
    }

    private void invokeNextTurn(){
        for(int i=0;i<20;i++)
        {
            for (int j=0;j<20;j++) {
                checkSurroundingCells(j,i);
            }
        }
        for(int i=0;i<20;i++)
        {
            for (int j=0;j<20;j++) {
                updateCells(j,i);
            }
        }
        updateCellsCounter(cellCount);
    }
    private ColorPicker getPlayerColorPicker(){
        ColorPicker colorPicker = new ColorPicker();
        colorPicker.setPrefSize(200,60);
        colorPicker.setOnAction(event -> {
            playerColor = new Background(new BackgroundFill(colorPicker.getValue(),CornerRadii.EMPTY, Insets.EMPTY));
            for(int i=0;i<20;i++)
            {
                for (int j=0;j<20;j++) {
                    if(tiles[i][j].status==Status.Tapped)
                    {
                        tiles[i][j].setBackground(playerColor);
                    }
                }
            }
        });
        return colorPicker;
    }

    private ColorPicker getInactiveColorPicker(){
        ColorPicker colorPicker = new ColorPicker();
        colorPicker.setPrefSize(200,60);
        colorPicker.setValue(inactiveCellColor);
        colorPicker.setOnAction(event -> {
            inactiveCellColor = colorPicker.getValue();
            for(int i=0;i<20;i++)
            {
                for (int j=0;j<20;j++) {
                    if(tiles[i][j].status== Untapped)
                    {
                        tiles[i][j].setBackground(new Background(new BackgroundFill(colorPicker.getValue(),CornerRadii.EMPTY, Insets.EMPTY)));
                    }
                }
            }
        });
        return colorPicker;
    }
    private Label getCommandPanelLabel(String text){
        Label label = new Label(text);
        label.setPrefSize(200,60);
        label.setAlignment(Pos.CENTER);
        label.setStyle("-fx-background-color: lightgrey");
        return label;
    }

    public VBox getCommandPanel()
    {
        VBox hBox = new VBox();
        slider=getSlider();
        Button button = getNewTurnButton();
        countLabel = getCommandPanelLabel("Active Cells: 0");
        hBox.getChildren().addAll(
                button,
                getCommandPanelLabel(" % of alive cells for Randomize"),
                slider,
                getRandomButton(),
                countLabel,
                getCommandPanelLabel("\"Alive\" cell's color"),
                getPlayerColorPicker(),
                getCommandPanelLabel("\"Dead\" cell's color"),
                getInactiveColorPicker(),
                getStartSimulationButton(),
                getStopSimulationButton());
        return hBox;
    }
    public static void checkSurroundingCells(int x, int y)
    {
        int livingCellsCount =0;
        if(x!=0){
            if(tiles[x-1][y].status==Status.Tapped){
                livingCellsCount++;
            }
        }
        if( y!=0 ){
            if(tiles[x][y-1].status==Status.Tapped){
                livingCellsCount++;
            }

        }
        if(x!=tiles.length-1){
            if(tiles[x+1][y].status==Status.Tapped){
                livingCellsCount++;
            }
        }
        if(y!=tiles.length-1){
            if(tiles[x][y+1].status==Status.Tapped){
                livingCellsCount++;
            }
        }
        if(!(x==0 || y==0))
        {
            if(tiles[x-1][y-1].status==Status.Tapped){
                livingCellsCount++;
            }
        }
        if(!(x==tiles.length-1 || y==0)){
            if(tiles[x+1][y-1].status==Status.Tapped){
                livingCellsCount++;
            }
        }
        if(!(x==tiles.length-1 || y==tiles.length-1)){
            if(tiles[x+1][y+1].status==Status.Tapped){
                livingCellsCount++;
            }
        }
        if(!(x==0 || y==tiles.length-1)){
            if(tiles[x-1][y+1].status==Status.Tapped){
                livingCellsCount++;
            }
        }

        tiles[x][y].livingCellsCount= livingCellsCount;
        updateCellsCounter(cellCount);
    }
    private void updateCells(int x, int y)
    {
        if(tiles[x][y].livingCellsCount==3 && tiles[x][y].status== Untapped) {
            tiles[x][y].status=Status.Tapped;
            cellCount++;
            tiles[x][y].setBackground(playerColor);
        }else if(!(tiles[x][y].livingCellsCount==3 || tiles[x][y].livingCellsCount==2) &&tiles[x][y].status==Status.Tapped)
        {
            tiles[x][y].status= Untapped;
            BackgroundFill backgroundFill =new BackgroundFill(inactiveCellColor,CornerRadii.EMPTY, Insets.EMPTY);
            Background background =new Background(backgroundFill);
            tiles[x][y].setBackground(background);
            cellCount--;

        }
        tiles[x][y].livingCellsCount=0;
    }
    private Button getStartSimulationButton(){
        Button button = new Button("Start simulation");
        button.setPrefSize(200,60);
        button.setOnAction(event -> {
            isSimulationGoing = true;
            Runnable simulationRunnable = () -> {
                while (isSimulationGoing) {

                    Platform.runLater(() -> invokeNextTurn());
                    try {
                        Thread.sleep((1000));
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            };
            animationThread = new Thread(simulationRunnable);
            animationThread.start();
        });
        return button;
    }

    private Button getStopSimulationButton(){
        Button button = new Button("Stop simulation");
        button.setPrefSize(200,60);
        button.setOnAction(event -> {
            try{
                isSimulationGoing = false;
                animationThread.interrupt();
            }catch (Exception e){

            }
        });
        return button;
    }
    private Button getRandomButton()
    {
        Button button = new Button("Randomize");
        button.setPrefSize(200,60);
        button.setOnAction(event -> {
            int counter=0;
            int random;
            for(int i=0;i<20;i++)
            {
                for (int j=0;j<20;j++) {
                    random= (int)(Math.random()*100);
                    if(random<slider.getValue())
                    {
                        tiles[i][j].status=Status.Tapped;
                        tiles[i][j].setBackground(playerColor);
                        counter++;
                    }else
                    {
                        tiles[i][j].status= Untapped;
                        tiles[i][j].setBackground(new Background(new BackgroundFill(inactiveCellColor, CornerRadii.EMPTY, Insets.EMPTY)));
                    }
                }
            }
            updateCellsCounter(counter);
            cellCount=counter;
        });
        return button;
    }
    private static void updateCellsCounter(int x){
        countLabel.setText("Active Cells: "+x);
    }
    public Slider getSlider()
    {
        Slider slider = new Slider(0,100,0);
        slider.setMajorTickUnit(20.0);
        slider.setShowTickMarks(true);
        slider.setMinorTickCount(3);
        slider.setShowTickLabels(true);

        return slider;
    }
}
