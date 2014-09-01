package simpleJFXDemo;

import java.io.IOException;

import simpleJFXDemo.model.Person;
import simpleJFXDemo.view.PersonOverviewController;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

public class MainApp extends Application {

	private Stage		primaryStage;
	private BorderPane	rootLayout;
	private	ObservableList<Person>	personList = FXCollections.observableArrayList();

	
	
	public MainApp() {
		//sampledata
		personList.add(new Person("sdfhj", "dfkj"));
		personList.add(new Person("hythj", "jffkj"));
		personList.add(new Person("kjh fdj", "dølkfghj"));
		personList.add(new Person("hasdj", "poi"));
	}

	
	@Override
	public void start(Stage primaryStage) {
		this.primaryStage = primaryStage;
		this.primaryStage.setTitle("AddressApp");

		initRootLayout();

		showPersonOverview();
	}

	private void showPersonOverview() {
		try {
			FXMLLoader loader = new FXMLLoader();
			loader.setLocation(MainApp.class.getResource("view/PersonOverview.fxml"));
			AnchorPane personOverview = (AnchorPane) loader.load();
			rootLayout.setCenter(personOverview);
			
			PersonOverviewController controller = loader.getController();
			controller.setMainApp(this);
			
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void initRootLayout() {
		try {
			FXMLLoader loader = new FXMLLoader();
			loader.setLocation(MainApp.class.getResource("view/RootLayout.fxml"));
			rootLayout = (BorderPane) loader.load();
			
			Scene scene = new Scene(rootLayout);
			primaryStage.setScene(scene);
			primaryStage.show();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}
	
	public Stage getPrimaryStage(){
		return primaryStage;
	}
	
	public ObservableList<Person> getPersonList() {
		return personList;
	}


	public static void main(String[] args) {
		launch(args);
	}
}
