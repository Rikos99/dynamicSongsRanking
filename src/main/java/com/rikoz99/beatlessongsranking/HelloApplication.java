package com.rikoz99.beatlessongsranking;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.*;
import java.util.*;

public class HelloApplication extends Application {

    static File fr;
    static File propsFile = new File("settings.properties");

    static Properties props = new Properties();
    static ArrayList<HBox> HBoxList = new ArrayList<>();
    static ArrayList<Label> songsList = new ArrayList<>();
    static ArrayList<TextField> orderList = new ArrayList<>();

    @Override
    public void start(Stage stage) throws IOException {
        VBox content = new VBox();
        VBox list = new VBox();
        Label fileError_LBL = new Label();

        Menu fileMenu = new Menu("File");
        MenuItem openFileMenu = new MenuItem("Open a songs list");
        MenuBar menuBar = new MenuBar();
        fileMenu.getItems().add(openFileMenu);
        menuBar.getMenus().addAll(fileMenu);

        //další menu item - save výsledného listu

        ScrollPane scrollPane = new ScrollPane(content);
        scrollPane.setFitToHeight(true);
        scrollPane.setFitToWidth(true);


        list.setSpacing(5);

        if(!propsFile.exists()) {
            propsFile.createNewFile();
        }
        props.load(new FileReader(propsFile.getAbsoluteFile()));

        fr = new File(props.getProperty("file"));
        if(fr.isFile())
        {
            clearList(list);
            fillListFromFile(list);
        }


        openFileMenu.setOnAction(event -> {
            try
            {
                fr = chooseSongsList(stage);
                updateProperties("file", fr.getAbsolutePath());
                clearList(list);
                fillListFromFile(list);
            }
            catch (FileNotFoundException e)
            {
                fileError_LBL.setText("File not found");
            }
            catch (IOException e) {
                throw new RuntimeException(e);
            }
        });

        for(TextField textField : orderList)
        {
            textField.textProperty().addListener((observable, oldValue, newValue) -> { //TODO změnit to, že do prázdného TF se zadává nová pozice, tohle kurva nefunguje, protože když vymažeš číslo a napíšeš nové, zapíše se prázdná hodnota jako before, což je na piču
                if(newValue.matches("\\d+"))
                {
                    changePosInList(list, Integer.parseInt(textField.getPromptText()), Integer.parseInt(newValue));
                }
                textField.setText("");
            });
        }

        content.getChildren().addAll(menuBar, list);




        Scene scene = new Scene(new BorderPane(scrollPane), 500, 500);
        stage.setTitle("Songs Ranking");
        stage.getIcons().add(new Image("file:icon.png"));
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
    private static File chooseSongsList(Stage stage)
    {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("Text Files", "*.txt")
        );
        return fileChooser.showOpenDialog(stage);
    }
    private static void updateProperties(String setting, String value) throws IOException {
        BufferedWriter bw = new BufferedWriter(new FileWriter(propsFile));
        props.setProperty(setting, value);
        props.store(bw, "Konfigurace");
    }
    private static void fillListFromFile(VBox list) throws FileNotFoundException {

        Scanner sc = new Scanner(fr);
        String song;

        int i = 0;
        while(sc.hasNextLine())
        {
            song = sc.nextLine();
            songsList.add(new Label(song));
            orderList.add(new TextField());
            orderList.get(i).setPromptText(String.valueOf(i+1));
            addEntryToList(list, i);
            i++;
        }
        sc.close();
    }
    private static void fillListFromLists(VBox list) //použité jako "refresh" po změně pořadí
    {
        list.getChildren().clear();
        HBoxList.clear();
        for (int i = 0; i < orderList.size(); i++)
        {
            addEntryToList(list, i);
        }
    }
    private static void clearList(VBox list)
    {
        list.getChildren().clear();
        HBoxList.clear();
        songsList.clear();
        orderList.clear();
    }
    private static void changePosInList(VBox list, int before, int after)
    {
        after -= 1;
        before -= 1;
        Label pomLBL = songsList.get(before);
        songsList.set(before, songsList.get(after));
        songsList.set(after, pomLBL);
        fillListFromLists(list);
    }
    private static void addEntryToList(VBox list, int i)
    {
        HBoxList.add(new HBox(orderList.get(i), songsList.get(i)));
        list.getChildren().add(HBoxList.get(i));
    }
}