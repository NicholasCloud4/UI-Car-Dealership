/**
 * Nicholas Dhannie
 * CEN 3024C - Software Development 1
 * March 14, 2024
 * CarDealershipSystemController.java
 * This class is focusing on all the logic of the application. This is what
 * will be listening to the users input with all options that they have available.
 * It will then handle all the interactions and show the changes or error messages.
 */

package org.nicholas.guicardealershipsystem;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

public class CarDealershipController {
    @FXML
    private TableView<Car> tableView;
    @FXML
    private Button addCarButton;
    @FXML
    private TextField yearFilterField;
    @FXML
    private TextField makeFilterField;
    @FXML
    private TextField modelFilterField;
    @FXML
    private TextField colorFilterField;
    private ObservableList<Car> allCars;

    public void initialize() {
        //The user cannot add a car without uploading a valid .txt file
        addCarButton.setDisable(true);
    }

    /**
     * Name: loadCarsFile
     * @param file
     *
     * This will load all the cars from the list that the user chooses to upload
     * and sorts them into the category from the list. This will be then seen in
     * the tableView.
     *
     */
    public void loadCarsFile(File file) {
        try {
            ObservableList<Car> carList = FXCollections.observableArrayList();
            BufferedReader reader = new BufferedReader(new FileReader(file));
            String line;
            while ((line = reader.readLine()) != null) {
                // Parse each line and create Car object
                String[] parts = line.split(",");
                if (parts.length == 9) {
                    int id = Integer.parseInt(parts[0]);
                    int year = Integer.parseInt(parts[1]);
                    String make = parts[2];
                    String model = parts[3];
                    String color = parts[4];
                    String engine = parts[5];
                    String transmission = parts[6];
                    double price = Double.parseDouble(parts[7]);
                    boolean sold = Boolean.parseBoolean(parts[8]);

                    Car car = new Car(id, year, make, model, color, engine, transmission, price);
                    carList.add(car);
                    addCarButton.setDisable(false);
                }
            }
            reader.close();
            // Populate the allCars list with the loaded cars
            allCars = FXCollections.observableArrayList(carList);
            // Set the TableView items to the allCars list
            tableView.setItems(allCars);

        } catch (IOException ex) {
            ex.printStackTrace();
            // Show an error message
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("Failed to Load File");
            alert.setContentText("Please make sure it's a valid text file.");
            alert.showAndWait();
        }
    }

    /**
     * Name: removeSelectedCar
     *
     * This will remove the selected car that the user has selected from
     * the view on the file that was uploaded.
     */
    @FXML
    private void removeSelectedCar() {
        Car selectedCar = tableView.getSelectionModel().getSelectedItem();
        if (selectedCar != null) {
            tableView.getItems().remove(selectedCar);
            updateTextFile();
        }
    }

    /**
     * Name: updateTextFile
     *
     * This will allow for a new text file to be made with all
     * the changes that the user has done
     */
    private void updateTextFile() {
        try (PrintWriter writer = new PrintWriter("cars.txt")) {
            for (Car car : tableView.getItems()) {
                String line = car.getId() + "," + car.getYear() + "," + car.getMake() + "," + car.getModel() + "," + car.getColor() + "," + car.getEngine() + "," + car.getTransmissionType() + "," + car.getPrice() + "," + car.isSold();
                writer.println(line);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }


    /**
     * Name: editCarDialog
     * @param selectedCar
     *
     * This will allow the user to edit the car that they have selected
     * from the view. I made it to where the user has to enter numbers
     * for the year and price field. They will be able to save the changes
     * that were made or cancel them.
     */
    @FXML
    private void editCarDialog(Car selectedCar) {
        Dialog<Car> dialog = new Dialog<>();
        dialog.setTitle("Edit Car");
        dialog.setHeaderText("Edit Fields");

        // Save Button
        ButtonType saveButton = new ButtonType("Save", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveButton, ButtonType.CANCEL);

        // input fields for each attribute
        TextField yearField = new TextField(Integer.toString(selectedCar.getYear()));
        TextField makeField = new TextField(selectedCar.getMake());
        TextField modelField = new TextField(selectedCar.getModel());
        TextField colorField = new TextField(selectedCar.getColor());
        TextField engineField = new TextField(selectedCar.getEngine());
        TextField transmissionField = new TextField(selectedCar.getTransmissionType());
        TextField priceField = new TextField(Double.toString(selectedCar.getPrice()));
        CheckBox soldCheckbox = new CheckBox("Sold");
        soldCheckbox.setSelected(selectedCar.isSold());

        // Make sure that year and price are numbers and not string
        yearField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("\\d*")) {
                yearField.setText(newValue.replaceAll("[^\\d]", ""));
            }
        });

        priceField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("\\d*(\\.\\d*)?")) {
                priceField.setText(oldValue);
            }
        });

        dialog.getDialogPane().setContent(new VBox(8, yearField, makeField, modelField, colorField, engineField, transmissionField, priceField, soldCheckbox));
        // Convert the result to a car object when the save button is clicked
        dialog.setResultConverter(buttonType -> {
            if (buttonType == saveButton) {
                if (yearField.getText().isEmpty() || makeField.getText().isEmpty() || modelField.getText().isEmpty() || colorField.getText().isEmpty() || engineField.getText().isEmpty() || transmissionField.getText().isEmpty() || priceField.getText().isEmpty()) {
                    // Show an error message to the user
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Error");
                    alert.setHeaderText("Incomplete Fields");
                    alert.setContentText("Please fill in all fields.");
                    alert.showAndWait();
                    return null;
                }
                try {
                    int year = Integer.parseInt(yearField.getText());
                    String make = makeField.getText();
                    String model = modelField.getText();
                    String color = colorField.getText();
                    String engine = engineField.getText();
                    String transmissionType = transmissionField.getText();
                    double price = Double.parseDouble(priceField.getText());
                    boolean sold = soldCheckbox.isSelected();

                    selectedCar.setYear(year);
                    selectedCar.setMake(make);
                    selectedCar.setModel(model);
                    selectedCar.setColor(color);
                    selectedCar.setEngine(engine);
                    selectedCar.setTransmissionType(transmissionType);
                    selectedCar.setPrice(price);
                    selectedCar.setSold(sold);

                    tableView.refresh();
                } catch (NumberFormatException e) {
                    //error message
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Error");
                    alert.setHeaderText("Invalid Input");
                    alert.setContentText("Please enter valid numbers for the year and price fields.");
                    alert.showAndWait();
                }
            }
            return null;
        });

        dialog.showAndWait();
    }

    /**
     * Name: uploadFile
     *
     * This is what the user will be seeing when they select the upload File
     * button. It is made to where the user can only select a txt file.
     */
    @FXML
    private void uploadFile() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open Car Data File");

        // Only allow txt files
        FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("Text files (*.txt)", "*.txt");
        fileChooser.getExtensionFilters().add(extFilter);

        File selectedFile = fileChooser.showOpenDialog(null);

        if (selectedFile != null) {
            loadCarsFile(selectedFile);
        }
    }


    /**
     * Name: addCarDialog
     *
     * This will allow users to see the fields when they select the add car
     * button. This is basically the same as edit car.
     */
    @FXML
    private void addCarDialog() {
        Dialog<Car> dialog = new Dialog<>();
        dialog.setTitle("Add Car");
        dialog.setHeaderText("Enter Car Details");

        // Set the button types
        ButtonType addButton = new ButtonType("Add", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(addButton, ButtonType.CANCEL);

        // Create text fields for each attribute
        TextField yearField = new TextField();
        TextField makeField = new TextField();
        TextField modelField = new TextField();
        TextField colorField = new TextField();
        TextField engineField = new TextField();
        TextField transmissionField = new TextField();
        TextField priceField = new TextField();

        yearField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("\\d*")) {
                yearField.setText(newValue.replaceAll("[^\\d]", ""));
            }
        });

        priceField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("\\d*(\\.\\d*)?")) {
                priceField.setText(oldValue);
            }
        });

        dialog.getDialogPane().setContent(new VBox(8, yearField, makeField, modelField, colorField, engineField, transmissionField, priceField)); // Add other text fields...

        dialog.setResultConverter(buttonType -> {
            if (buttonType == addButton) {
                // Validate that all fields are not empty
                if (yearField.getText().isEmpty() || makeField.getText().isEmpty() || modelField.getText().isEmpty() || colorField.getText().isEmpty() || engineField.getText().isEmpty() || transmissionField.getText().isEmpty() || priceField.getText().isEmpty()) {
                    // Show an error message
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Error");
                    alert.setHeaderText("Incomplete Fields");
                    alert.setContentText("Please fill in all fields.");
                    alert.showAndWait();
                    return null;
                }
                try {
                    int year = Integer.parseInt(yearField.getText());
                    String make = makeField.getText();
                    String model = modelField.getText();
                    String color = colorField.getText();
                    String engine = engineField.getText();
                    String transmissionType = transmissionField.getText();
                    double price = Double.parseDouble(priceField.getText());


                    int nextId = findNextId();
                    Car car = new Car(nextId + 1, year, make, model, color, engine, transmissionType, price);
                    tableView.getItems().add(car);

                } catch (NumberFormatException e) {
                    // Show an error message
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Error");
                    alert.setHeaderText("Invalid Input");
                    alert.setContentText("Please enter valid numbers for the year and price fields.");
                    alert.showAndWait();
                }
            }
            return null;
        });

        dialog.showAndWait();
    }

    /**
     * Name: findNextId
     * @return nextId
     *
     * This will get the next number for the Id field when the
     * user goes to add a car.
     */
    @FXML
    private int findNextId() {
        int nextId = 0;
        ObservableList<Car> carList = tableView.getItems();
        for (Car car : carList) {
            if (car.getId() > nextId) {
                nextId = car.getId();
            }
        }
        return nextId;
    }

    /**
     * Name: editSelectedCar
     *
     * this is to make sure that the user can be able to select
     * a car and edit it with the button. It will then go through
     * the editCarDialog in which the user has to fill out.
     */
    @FXML
    public void editSelectedCar() {
        Car selectedCar = tableView.getSelectionModel().getSelectedItem();
        if (selectedCar != null) {
            editCarDialog(selectedCar);
        } else {
        }
    }

    /**
     * Name: removeFilteredCars
     *
     * This will allow the user to remove the car that they want by
     * entering something in one of the options for the filter
     * and be able to remove based on what they have entered.
     */
    @FXML
    private void removeFilteredCars() {
        String yearFilter = yearFilterField.getText().trim();
        String makeFilter = makeFilterField.getText().trim();
        String modelFilter = modelFilterField.getText().trim();
        String colorFilter = colorFilterField.getText().trim();
        // Create a list to hold all filter predicates
        List<Predicate<Car>> filterPredicates = new ArrayList<>();
        // Add filter predicates based on the values in filter fields
        if (!yearFilter.isEmpty()) {
            filterPredicates.add(car -> String.valueOf(car.getYear()).equals(yearFilter));
        }

        if (!makeFilter.isEmpty()) {
            filterPredicates.add(car -> car.getMake().equalsIgnoreCase(makeFilter));
        }

        if (!modelFilter.isEmpty()) {
            filterPredicates.add(car -> car.getModel().equalsIgnoreCase(modelFilter));
        }

        if (!colorFilter.isEmpty()) {
            filterPredicates.add(car -> car.getColor().equalsIgnoreCase(colorFilter));
        }
        // Combine all predicates with AND logic
        Predicate<Car> combinedPredicate = filterPredicates.stream().reduce(Predicate::and).orElse(car -> true);

        // Apply the combined filter to get filtered cars
        ObservableList<Car> filteredCars = allCars.filtered(combinedPredicate);

        // Check if any cars match the filter
        if (!filteredCars.isEmpty()) {
            // Remove the filtered cars from the existing list
            allCars.removeAll(filteredCars);

            // Update the TableView items
            tableView.setItems(allCars);

            // Clear the filter fields
            yearFilterField.clear();
            makeFilterField.clear();
            modelFilterField.clear();
            colorFilterField.clear();
        }
    }
}