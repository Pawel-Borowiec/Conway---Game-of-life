package game;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

public class Main extends Application {
    public static MyButton[][] przyciski= new MyButton[20][20];
    static Background playerColor ;
    static Label countLabel;
    static Color basicColor = Color.DARKGRAY;
    static int cellCount=0;
    static Slider slider;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        primaryStage.setTitle("Conway - Gra w życie");
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
                przyciski[i][j]=createButton();
            }
        }
    }
    public void addToGrid(GridPane gridPane)
    {
        for(int i=0;i<20;i++)
        {
            for (int j=0;j<20;j++)
            {
                gridPane.add(przyciski[i][j],i,j);
            }
        }
    }
    public MyButton createButton()
    {
        MyButton button = new MyButton();
        button.setPrefSize(40,40);
        button.setMinSize(40,40);
        button.setMaxSize(40,40);
        button.status=Status.Untapped;
        button.setBackground(new Background(new BackgroundFill(basicColor,CornerRadii.EMPTY,Insets.EMPTY)));
        button.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                if(button.status==Status.Untapped)
                {
                    button.setBackground(playerColor);
                    button.status=Status.Tapped;
                    cellCount++;
                }else{
                    button.setBackground(new Background(new BackgroundFill(basicColor,CornerRadii.EMPTY,Insets.EMPTY)));
                    button.status=Status.Untapped;
                    cellCount--;
                }
                countLabel.setText("Aktywne komorki: "+cellCount);
            }
        });
        return button;
    }

    public VBox getCommandPanel()
    {
        VBox hBox = new VBox();
        slider=getSlider();
        Button button = new Button("Kolejna Tura");
        button.setPrefSize(200,60);
        button.setMinSize(200,60);
        button.setMaxSize(200,60);
        countLabel = new Label("Aktywne Komórki: ");
        countLabel.setPrefSize(200,60);
        countLabel.setMinSize(200,60);
        countLabel.setMaxSize(200,60);
        countLabel.setStyle("-fx-background-color: lightgrey");
        button.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                for(int i=0;i<20;i++)
                {
                    for (int j=0;j<20;j++) {
                        sprawdz(j,i);
                    }
                }
                for(int i=0;i<20;i++)
                {
                    for (int j=0;j<20;j++) {
                        nastaw(j,i);
                    }
                }
                countLabel.setText("Aktywne komorki: "+cellCount);
            }
        });

        ColorPicker colorPicker = new ColorPicker();
        colorPicker.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                BackgroundFill backgroundFill=new BackgroundFill(colorPicker.getValue(),CornerRadii.EMPTY, Insets.EMPTY);
                Background background =new Background(backgroundFill);
                playerColor=background;
                for(int i=0;i<20;i++)
                {
                    for (int j=0;j<20;j++) {
                        if(przyciski[i][j].status==Status.Tapped)
                        {
                            przyciski[i][j].setBackground(playerColor);
                        }
                    }
                }
            }
        });

        // playerColor=new Background(backgroundFill);
        hBox.getChildren().addAll(button,slider,getRandomButton(),countLabel, colorPicker);
        return hBox;
    }
    public static void sprawdz(int x,int y)
    {
        int count =0;
        if(x!=0) {
            if(przyciski[x-1][y].status==Status.Tapped)
            {
                // System.out.println("lewo");
                count++;
            }
        }
        if( y!=0 ) {
            if(przyciski[x][y-1].status==Status.Tapped)
            {
                // System.out.println("dol");
                count++;
            }

        }
        if(x!=przyciski.length-1) {
            if(przyciski[x+1][y].status==Status.Tapped)
            {
                //System.out.println("prawo");
                count++;
            }
        }
        if(y!=przyciski.length-1) {
            if(przyciski[x][y+1].status==Status.Tapped)
            {
                // System.out.println("dol");
                count++;
            }
        }
        //if(!(x==0 || y==przyciski.length-1))
        if(!(x==0 || y==0))
        {
            //System.out.println("policzono w lewy gorny rog");
            if(przyciski[x-1][y-1].status==Status.Tapped)
            {

                count++;
            }
        }
        if(!(x==przyciski.length-1 || y==0))
        {
            //System.out.println("policzono w prawy gorny rog");
            if(przyciski[x+1][y-1].status==Status.Tapped)
            {
                count++;
            }
        }
        if(!(x==przyciski.length-1 || y==przyciski.length-1))
        {
            //System.out.println("policzono w prawy doł");
            if(przyciski[x+1][y+1].status==Status.Tapped)
            {

                count++;
            }
        }
        if(!(x==0 || y==przyciski.length-1))
        {
            //System.out.println("policzono w lewy doł");
            if(przyciski[x-1][y+1].status==Status.Tapped)
            {
                count++;
            }

        }

        przyciski[x][y].livingCellsCount=count;
        countLabel.setText("Aktywne komórki: "+cellCount);
    }
    public void nastaw (int x, int y)
    {
        if(przyciski[x][y].livingCellsCount==3 && przyciski[x][y].status==Status.Untapped) {
            przyciski[x][y].status=Status.Tapped;
            cellCount++;
            przyciski[x][y].setBackground(playerColor);
        }else if(!(przyciski[x][y].livingCellsCount==3 || przyciski[x][y].livingCellsCount==2) &&przyciski[x][y].status==Status.Tapped)
        {
            przyciski[x][y].status=Status.Untapped;
            BackgroundFill backgroundFill =new BackgroundFill(basicColor,CornerRadii.EMPTY, Insets.EMPTY);
            Background background =new Background(backgroundFill);
            przyciski[x][y].setBackground(background);
            cellCount--;

        }
        przyciski[x][y].livingCellsCount=0;
    }
    public Button getRandomButton()
    {
        Button button = new Button("Generuj losowo");
        button.setPrefSize(200,60);
        button.setMinSize(200,60);
        button.setMaxSize(200,60);
        button.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                int licznik=0;
                int random;
                for(int i=0;i<20;i++)
                {
                    for (int j=0;j<20;j++) {
                        random= (int)(Math.random()*100);
                        if(random<slider.getValue())
                        {
                            przyciski[i][j].status=Status.Tapped;
                            przyciski[i][j].setBackground(playerColor);
                            licznik++;
                        }else
                        {
                            przyciski[i][j].status=Status.Untapped;
                            przyciski[i][j].setBackground(new Background(new BackgroundFill(basicColor, CornerRadii.EMPTY, Insets.EMPTY)));
                        }
                    }
                }
                countLabel.setText("Aktywne komórki: "+licznik);
                cellCount=licznik;
            }
        });
        return button;
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
