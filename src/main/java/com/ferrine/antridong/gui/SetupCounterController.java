package com.ferrine.antridong.gui;

import com.ferrine.antridong.database.models.Counter;
import com.ferrine.antridong.database.models.KategoriAntrian;
import com.ferrine.antridong.database.repository.CounterRepository;
import com.ferrine.antridong.database.repository.KategoriAntrianRepository;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class SetupCounterController {
    @FXML private TableView<Counter> table;
    @FXML private TableColumn<Counter, String> colKategori;
    @FXML private TextField nameField;
    @FXML private ComboBox<String> statusBox;
    @FXML private ListView<KategoriAntrian> kategoriListView;

    private Counter selectedItem = null;
    private final CounterRepository counterRepository = new CounterRepository();
    private final KategoriAntrianRepository kategoriRepository = new KategoriAntrianRepository();

    @FXML
    public void initialize() {
        statusBox.getItems().addAll("active", "inactive");
        statusBox.setValue("active");

        kategoriListView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        kategoriListView.setCellFactory(param -> new ListCell<>() {
            @Override
            protected void updateItem(KategoriAntrian item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(item.getCode() + " - " + item.getName());
                }
            }
        });

        colKategori.setCellValueFactory(cellData -> {
            List<KategoriAntrian> kats = cellData.getValue().getKategoriList();
            if (kats == null || kats.isEmpty()) return new javafx.beans.property.SimpleStringProperty("-");
            String katStr = kats.stream().map(KategoriAntrian::getCode).collect(Collectors.joining(", "));
            return new javafx.beans.property.SimpleStringProperty(katStr);
        });

        table.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) populateForm(newSelection);
        });

        loadKategoriList();
        refreshTable();
    }

    private void loadKategoriList() {
        List<KategoriAntrian> kats = kategoriRepository.query().findList();
        kategoriListView.setItems(FXCollections.observableArrayList(kats));
    }

    private void refreshTable() {
        List<Counter> list = counterRepository.query().findList();
        table.getItems().setAll(list);
    }

    private void populateForm(Counter item) {
        selectedItem = item;
        nameField.setText(item.getName());
        statusBox.setValue(item.getStatus());
        
        kategoriListView.getSelectionModel().clearSelection();
        if (item.getKategoriList() != null) {
            for (KategoriAntrian kat : item.getKategoriList()) {
                kategoriListView.getItems().stream()
                    .filter(k -> k.getId().equals(kat.getId()))
                    .findFirst()
                    .ifPresent(k -> kategoriListView.getSelectionModel().select(k));
            }
        }
    }

    @FXML
    private void clearForm() {
        selectedItem = null;
        nameField.clear();
        statusBox.setValue("active");
        kategoriListView.getSelectionModel().clearSelection();
        table.getSelectionModel().clearSelection();
    }

    @FXML
    private void saveItem() {
        if (nameField.getText().isEmpty()) {
            showAlert("Nama Counter wajib diisi.");
            return;
        }

        Counter item = selectedItem != null ? selectedItem : new Counter();
        item.setName(nameField.getText());
        item.setStatus(statusBox.getValue());
        
        ObservableList<KategoriAntrian> selectedKats = kategoriListView.getSelectionModel().getSelectedItems();
        item.setKategoriList(new ArrayList<>(selectedKats));
        
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
