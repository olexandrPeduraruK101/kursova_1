package com.example.kursova_train_003;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.net.URL;
import java.sql.*;
import java.util.ResourceBundle;

public class TableTicketsView_Controller implements Initializable {
    Connection con = null;
    PreparedStatement st = null;
    ResultSet rs = null;
    Tickets tickets = new Tickets();
    Methods methods = new Methods();

    @FXML
    private TableColumn<Tickets, Integer> colid;

    @FXML
    private TableColumn<Tickets, Integer> coltrain_id;

    @FXML
    private TableColumn<Tickets, Integer> colnumber_train;

    @FXML
    private TableColumn<Tickets, Integer> colseat_id;

    @FXML
    private TableColumn<Tickets, Integer> colseat_number;

    @FXML
    private TableColumn<Tickets, String> coldepartureStationName;

    @FXML
    private TableColumn<Tickets, String> colarrivalStationName;

    @FXML
    private TableColumn<Tickets, Date> coldeparture_date;

    @FXML
    private TableColumn<Tickets, Time> coldeparture_time;

    @FXML
    private TableColumn<Tickets, Date> colarrival_date;

    @FXML
    private TableColumn<Tickets, Time> colarrival_time;

    @FXML
    private TableColumn<Tickets, String> collogin;

    @FXML
    private TableColumn<Tickets, String> colfirstname;

    @FXML
    private TableColumn<Tickets, String> collastname;

    @FXML
    private TableColumn<Tickets, String> colprice;

    @FXML
    private TableView<Tickets> table;
    //----------------------------------------------------------------------------------------
    //tab додати квиток
    @FXML
    private Button btnPlus0;

    @FXML
    private Button btnPlus1;

    @FXML
    private Button btnPlus2;

    @FXML
    private Button btnPlusOk;

    @FXML
    private Button btnReset;

    @FXML
    private TextField tPlus0;

    @FXML
    private Label warning1;

    @FXML
    private Label warning2;

    @FXML
    private Label warning3;

    @FXML
    private Label warning4;

    @FXML
    private ComboBox<Integer> cmbPlus0;

    @FXML
    private ComboBox<Integer> cmbPlus1;

    @FXML
    private ComboBox<Integer> cmbPlus2;


    @FXML
    void btnPlus0A(ActionEvent event) {
        warning1.setText("");
        if (cmbPlus0.getSelectionModel().isEmpty()) {
            warning1.setText("*дані не обрані");
        } else {
            tickets.setTrainId(cmbPlus0.getValue());
            methods.addInformationToTicket(tickets);
            fillComboBoxWithAvailableSeats(tickets.getTrainId());
        }

    }

    @FXML
    void btnPlus1A(ActionEvent event) {
        warning2.setText("");
        if (cmbPlus1.getSelectionModel().isEmpty()) {
            warning2.setText("*дані не обрані");
        } else {
            tickets.setSeatNumber(cmbPlus1.getValue());
            methods.getSeatsBuyIdBySeatAndTrain(tickets);
            fillComboBoxWithUserId();
        }
    }

    @FXML
    void btnPlus2A(ActionEvent event) {
        warning3.setText("");
        if (cmbPlus2.getSelectionModel().isEmpty()) {
            warning3.setText("*дані не обрані");
        } else {
            int id = cmbPlus2.getValue();
                methods.showUserInfo(tickets,id);
        }
    }

    @FXML
    void btnPlusOkA(ActionEvent event) {
        warning4.setText("");

        if(tPlus0.getText().equals("")){
            warning4.setText("*Ви не вказали дані");
        }
        else {
            String input = tPlus0.getText();
            if (input.matches("^\\d+(\\.\\d+)?$")) {
                tickets.setPrice(input);
                addTickets();

            }
            else{
                warning4.setText("*Ви не коректно дані");
            }

        }
    }

   


//----------------------------------------------------------------------------------------
    //tab Видалити  по id



    @FXML
    private Label warningdelete;

    @FXML
    private TextField tDelete;

    @FXML
    void btnDeleteA(ActionEvent event) {
        warningdelete.setText("");
        if (tDelete.getText().equals("")) {
            warningdelete.setText("*ви не ввели дані");
        } else {
                tickets.setId(Integer.parseInt(tDelete.getText()));
            String query = "DELETE FROM Tickets WHERE id = ?";

            con = DBConnextion.getCon();
            try {
                st = con.prepareStatement(query);
                st.setInt(1, tickets.getId());
                st.executeUpdate();
                warningdelete.setText("Запис успішно видалено.");
            } catch (SQLException e) {
                e.printStackTrace();
            } finally {
                closeConnections();
            }
        }


    }






    //-------------------------------------------------------------------------------------------


    private void setupCellValueFactory() {
        colid.setCellValueFactory(new PropertyValueFactory<>("id"));
        coltrain_id.setCellValueFactory(new PropertyValueFactory<>("trainId"));
        colnumber_train.setCellValueFactory(new PropertyValueFactory<>("numberTrain"));
        colseat_id.setCellValueFactory(new PropertyValueFactory<>("seatId"));
        colseat_number.setCellValueFactory(new PropertyValueFactory<>("seatNumber"));
        coldepartureStationName.setCellValueFactory(new PropertyValueFactory<>("departureStationName"));
        colarrivalStationName.setCellValueFactory(new PropertyValueFactory<>("arrivalStationName"));
        coldeparture_date.setCellValueFactory(new PropertyValueFactory<>("departureDate"));
        coldeparture_time.setCellValueFactory(new PropertyValueFactory<>("departureTime"));
        colarrival_date.setCellValueFactory(new PropertyValueFactory<>("arrivalDate"));
        colarrival_time.setCellValueFactory(new PropertyValueFactory<>("arrivalTime"));
        collogin.setCellValueFactory(new PropertyValueFactory<>("login"));
        colfirstname.setCellValueFactory(new PropertyValueFactory<>("firstName"));
        collastname.setCellValueFactory(new PropertyValueFactory<>("lastName"));
        colprice.setCellValueFactory(new PropertyValueFactory<>("price"));
    }

    private void showTickets() {
        ObservableList<Tickets> tickets = getTickets();
        table.setItems(tickets);
    }

    private ObservableList<Tickets> getTickets() {
        ObservableList<Tickets> tickets = FXCollections.observableArrayList();
        String query = "SELECT t.*, s1.name_staishon AS departure_station_name, s2.name_staishon AS arrival_station_name " +
                "FROM Tickets t " +
                "INNER JOIN Staishon s1 ON t.departure_station_id = s1.id " +
                "INNER JOIN Staishon s2 ON t.arrival_station_id = s2.id";

        try {
            con = DBConnextion.getCon();
            st = con.prepareStatement(query);
            rs = st.executeQuery();
            while (rs.next()) {
                Tickets ticket = new Tickets();
                ticket.setId(rs.getInt("id"));
                ticket.setTrainId(rs.getInt("train_id"));
                ticket.setNumberTrain(rs.getInt("number_train"));
                ticket.setSeatId(rs.getInt("seat_id"));
                ticket.setSeatNumber(rs.getInt("seat_number"));
                ticket.setDepartureStationName(rs.getString("departure_station_name"));
                ticket.setArrivalStationName(rs.getString("arrival_station_name"));
                ticket.setDepartureDate(rs.getDate("departure_date"));
                ticket.setDepartureTime(rs.getTime("departure_time"));
                ticket.setArrivalDate(rs.getDate("arrival_date"));
                ticket.setArrivalTime(rs.getTime("arrival_time"));
                ticket.setLogin(rs.getString("login"));
                ticket.setFirstName(rs.getString("firstname"));
                ticket.setLastName(rs.getString("lastname"));
                ticket.setPrice(rs.getString("price"));
                tickets.add(ticket);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            closeConnections();
        }
        return tickets;
    }

    private void closeConnections() {
        try {
            if (rs != null) {
                rs.close();
            }
            if (st != null) {
                st.close();
            }
            if (con != null) {
                con.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    //-----------------------------------------------------------------------------------------
    //Combobox+id потягів
    public void fillComboBoxWithTrainid() {
        String query = "SELECT id FROM train_database_001.train";
        con = DBConnextion.getCon();

        try {
            st = con.prepareStatement(query);
            rs = st.executeQuery();

            cmbPlus0.getItems().clear(); // Очищення списку варіантів chbPTMarshutsB

            while (rs.next()) {
                String trainid = rs.getString("id");
                cmbPlus0.getItems().add(Integer.valueOf(trainid)); // Додавання нових варіантів до tPoshuk2
                    }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    //Combobox+id місця

    public void fillComboBoxWithAvailableSeats(int trainId) {
        String query = "SELECT seat_number FROM seats_buy WHERE train_id = ? AND booked = false";
        con = DBConnextion.getCon();

        try {
            st = con.prepareStatement(query);
            st.setInt(1, trainId);
            rs = st.executeQuery();

            cmbPlus1.getItems().clear(); // Очищення списку варіантів

            while (rs.next()) {
                int seatNumber = rs.getInt("seat_number");
                cmbPlus1.getItems().add((seatNumber)); // Додавання нового варіанту
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }





    public void fillComboBoxWithUserId() {
        String query = "SELECT id FROM train_database_001.users";
        con = DBConnextion.getCon();

        try {
            st = con.prepareStatement(query);
            rs = st.executeQuery();

            cmbPlus2.getItems().clear(); // Очищення списку варіантів

            while (rs.next()) {
                int usersId = rs.getInt("id");
                cmbPlus2.getItems().add(usersId); // Додавання нових варіантів
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void addTickets() {
        try {
            con = DBConnextion.getCon();

            // Додавання інформації до таблиці "train"
            String tiketsadd = "INSERT INTO train_database_001.tickets (train_id, number_train, seat_id, seat_number, departure_station_id, arrival_station_id, departure_date, departure_time, arrival_date, arrival_time, login, firstname, lastname, price) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
            st = con.prepareStatement(tiketsadd);

            // Заповнення параметрів для додавання інформації
            st.setInt(1, tickets.getTrainId());
            st.setInt(2, tickets.getNumberTrain());
            st.setInt(3, tickets.getSeatId());
            st.setFloat(4, tickets.getSeatNumber());
            st.setInt(5, tickets.getDepartureStationId());
            st.setInt(6, tickets.getArrivalStationId());
            st.setDate(7, tickets.getDepartureDate());
            st.setTime(8, tickets.getDepartureTime());
            st.setDate(9, tickets.getArrivalDate());
            st.setTime(10, tickets.getArrivalTime());
            st.setString(11, tickets.getLogin());
            st.setString(12, tickets.getFirstName());
            st.setString(13, tickets.getLastName());
            st.setString(14, tickets.getPrice());

            // Виконання запиту на додавання інформації до таблиці "train"
            st.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

        //-----------------------------------------------------------------------------------------
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        setupCellValueFactory();
        showTickets();
        fillComboBoxWithTrainid();
        fillComboBoxWithUserId();

    }
}
