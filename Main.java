import javafx.application.Application;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.SnapshotParameters;
import javafx.scene.chart.*;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.BorderPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;

public class Main extends Application {

    private Stage stage = new Stage();
    XYChart chart = null;
    BorderPane bp = new BorderPane();

    public void start(Stage primaryStage){
        stage.setTitle("Laboratoire 7");
        stage.setScene(scene());
        stage.setWidth(600);
        stage.setHeight(600);
        stage.show();
    }

    private Scene scene() {
        MenuBar menuBar = new MenuBar();

        Menu importer = new Menu("Importer");
        Menu exporter = new Menu("Exporter");

        MenuItem lignes = new MenuItem("Lignes");
        MenuItem regions = new MenuItem("Régions");
        MenuItem barres = new MenuItem("Barres");
        MenuItem png = new MenuItem("PNG");
        MenuItem gif = new MenuItem("GIF");

        importer.getItems().addAll(lignes, regions, barres);
        exporter.getItems().addAll(png, gif);
        menuBar.getMenus().addAll(importer, exporter);

        lignes.setOnAction(event -> {
            graph(1);
        });
        regions.setOnAction(event -> {
            graph(2);
        });
        barres.setOnAction(event -> {
            graph(3);
        });
        png.setOnAction(event -> save("png"));
        gif.setOnAction(event -> save("gif"));


        bp.setTop(menuBar);

        return new Scene(bp);
    }

    private void graph(int nb) {
        File file = open(stage);

        final CategoryAxis xAxis = new CategoryAxis();
        final NumberAxis yAxis = new NumberAxis();
        xAxis.setLabel("Mois");
        yAxis.setLabel("Temperature (C)");

        XYChart.Series series = new XYChart.Series();
        series.setName("Donnees");

        try{
            List<String> lines = Files.readAllLines(file.toPath());
            String[] x = lines.get(0).split(",");
            String[] y = lines.get(1).split(",");

            for (int i=0;i<x.length;i++){
                series.getData().add(new XYChart.Data(x[i],Integer.parseInt(y[i].trim())));
            }
        }
        catch(Exception exception){
            System.out.println("Impossible de charger le fichier");
        }

        if (nb == 1) {
            chart = new LineChart<String,Number>(xAxis,yAxis);
        }
        if (nb == 2) {
            chart = new AreaChart<String,Number>(xAxis,yAxis);
        }
        if (nb == 3) {
            chart = new BarChart<String,Number>(xAxis,yAxis);
        }

        chart.getData().addAll(series);

        bp.setCenter(chart);
    }

    public File open(Stage stage) {
        FileChooser fc = new FileChooser();
        fc.setTitle("Veuillez selectionner un fichier");
        fc.getExtensionFilters().add(new FileChooser.ExtensionFilter("Fichiers DAT", "*.dat"));
        File file = fc.showOpenDialog(stage);
        return file;
    }

    private void save(String type) {
        FileChooser fc = new FileChooser();
        fc.setTitle("Enregistrez sous");

        if (chart!=null){
            WritableImage image = chart.snapshot(new SnapshotParameters(), null);
            fc.getExtensionFilters().add(new FileChooser.ExtensionFilter("Fichier " + type.toUpperCase(),"*." + type));
            File fichier = fc.showSaveDialog(stage);
            try {
                ImageIO.write(SwingFXUtils.fromFXImage(image, null), type, fichier);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
