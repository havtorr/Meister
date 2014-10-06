package simpleJFXMLDemo.view;

import simpleJFXMLDemo.MainApp;
import simpleJFXMLDemo.model.Person;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

public class PersonOverviewController {

	@FXML
	private TableView<Person>	personTable;
	@FXML
	private	TableColumn<Person, String>	firstNameColumn;
	@FXML
	private	TableColumn<Person, String>	lastNameColumn;
	
	@FXML
	private	Label firstNameLabel;
	@FXML
	private	Label lastNameLabel;
	@FXML
	private	Label streetLabel;
	@FXML
	private	Label postalCodeLabel;
	@FXML
	private	Label cityLabel;
	@FXML
	private	Label birthdayLabel;
	
	
	private MainApp mainApp;
	
	public PersonOverviewController() {
		// TODO Auto-generated constructor stub
	}
	
	@FXML
	private void initialize(){
		firstNameColumn.setCellValueFactory(cellData -> cellData.getValue().getFirstNameProperty());
		lastNameColumn.setCellValueFactory(cellData -> cellData.getValue().getLastNameProperty());
		
	}

	public void setMainApp(MainApp mainApp) {
		this.mainApp = mainApp;
		
		personTable.setItems(mainApp.getPersonList());
	}
}
