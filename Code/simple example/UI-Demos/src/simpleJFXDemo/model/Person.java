package simpleJFXDemo.model;

import java.time.LocalDate;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class Person {

	private StringProperty	firstNameProperty;
	private StringProperty	lastNameProperty;	
	private StringProperty	streetProperty;
	private StringProperty	cityProperty; 
	private IntegerProperty	postalCodeProperty;
	private ObjectProperty<LocalDate>	birthdayProperty;
	
	public Person() {
		this(null, null);
	}

	public Person(String fName, String lName) {
		firstNameProperty	= new SimpleStringProperty(fName);
		lastNameProperty	= new SimpleStringProperty(lName);
		
		streetProperty		= new SimpleStringProperty("blank");
		cityProperty		= new SimpleStringProperty("blank");
		postalCodeProperty	= new SimpleIntegerProperty(1234);
		birthdayProperty	= new SimpleObjectProperty<LocalDate>(LocalDate.now());
	}

	public StringProperty getFirstNameProperty() {
		return firstNameProperty;
	}
	
	public String getFirstName(){
		return firstNameProperty.get();
	}

	public void setFirstNameProperty(StringProperty firstNameProperty) {
		this.firstNameProperty = firstNameProperty;
	}

	public StringProperty getLastNameProperty() {
		return lastNameProperty;
	}
	
	public String getlastName(){
		return lastNameProperty.get();
	}

	public void setLastNameProperty(StringProperty lastNameProperty) {
		this.lastNameProperty = lastNameProperty;
	}

	public StringProperty getStreetProperty() {
		return streetProperty;
	}
	
	public String getStreet(){
		return streetProperty.get();
	}

	public void setStreetProperty(StringProperty streetProperty) {
		this.streetProperty = streetProperty;
	}

	public StringProperty getCityProperty() {
		return cityProperty;
	}
	
	public String getCity(){
		return cityProperty.get();
	}

	public void setCityProperty(StringProperty cityProperty) {
		this.cityProperty = cityProperty;
	}

	public IntegerProperty getPostalCodeProperty() {
		return postalCodeProperty;
	}
	
	public Integer getPostalCode(){
		return postalCodeProperty.get();
	}

	public void setPostalCodeProperty(IntegerProperty postalCodeProperty) {
		this.postalCodeProperty = postalCodeProperty;
	}

	public ObjectProperty<LocalDate> getBirthdayProperty() {
		return birthdayProperty;
	}
	
	public LocalDate getBirthday(){
		return birthdayProperty.get();
	}

	public void setBirthdayProperty(ObjectProperty<LocalDate> birthdayProperty) {
		this.birthdayProperty = birthdayProperty;
	}
	
	

	
}
