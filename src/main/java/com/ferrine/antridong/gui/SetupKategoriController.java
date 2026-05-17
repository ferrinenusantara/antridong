package com.ferrine.antridong.gui;

import com.ferrine.antridong.database.models.KategoriAntrian;
import com.ferrine.antridong.database.repository.KategoriAntrianRepository;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;

public class SetupKategoriController {
    @FXML private TableView<KategoriAntrian> table;
    @FXML private TextField codeField;
    @FXML private TextField nameField;
    @FXML private TextField startTimeField;
    @FXML private TextField endTimeField;

    private KategoriAntrian selectedItem = null;
    private final KategoriAntrianRepository repository = new KategoriAntrianRepository();

    @FXML
    public void initialize() {
        table.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) populateForm(newSelection);
        });
        refreshTable();
    }

    private void refreshTable() {
        List<KategoriAntrian> list = repository.query().findList();
        table.getItems().setAll(list);
    }

    private void populateForm(KategoriAntrian item) {
        selectedItem = item;
        codeField.setText(item.getCode());
        nameField.setText(item.getName());
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("HH:mm");
        startTimeField.setText(item.getStartTime() != null ? item.getStartTime().format(dtf) : "");
        endTimeField.setText(item.getEndTime() != null ? item.getEndTime().format(dtf) : "");
    }

    @FXML
    private void clearForm() {
        selectedItem = null;
        codeField.clear();
        nameField.clear();
        startTimeField.clear();
        endTimeField.clear();
        table.getSelectionModel().clearSelection();
    }

    @FXML
    private void saveItem() {
        if (codeField.getText().length() != 3) {
            showAlert("Kode harus tepat 3 karakter.");
            return;
        }
        
        LocalTime start, end;
        try {
            start = LocalTime.parse(startTimeField.getText());
            end = LocalTime.parse(endTimeField.getText());
        } catch (DateTimeParseException e) {
            showAlert("Format jam salah. Gunakan HH:mm (contoh 08:00)");
            return;
        }

        KategoriAntrian item = selectedItem != null ? selectedItem : new KategoriAntrian();
        item.setCode(codeField.getText().toUpperCase());
        item.setName(nameField.getText());
        item.setStartTime(start);
        item.setEndTime(end);
        
        item.save();
        clearForm();
        refreshTable();
    }

    @FXML
    private void deleteItem() {
        if (selectedItem != null) {
            selectedItem.delete();
            clearForm();
            refreshTable();
        }
    }

    private void showAlert(String msg) {
        new Alert(Alert.AlertType.WARNING, msg).show();
    }
}
