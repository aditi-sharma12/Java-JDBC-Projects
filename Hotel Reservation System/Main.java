import java.sql.DriverManager;
import java.sql.Connection;
import java.sql.Statement;
import java.sql.SQLException;
import java.util.Scanner;
import java.sql.ResultSet;

public class Main {

    private static final String url = "jdbc:mysql://localhost:3306/hotel_db";

    private static final String username = "your_username";

    private static final String password = "your_password";

    public static void main(String []args) throws ClassNotFoundException, SQLException {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        }
        catch(ClassNotFoundException e) {
            System.out.println(e.getMessage());
        }
        try {
            Connection connection = DriverManager.getConnection(url, username, password);
            while(true) {
                System.out.println();
                System.out.println("1. Reserve a room ");
                System.out.println("2. View Reservation ");
                System.out.println("3. Get Room Number ");
                System.out.println("4. Update Reservation ");
                System.out.println("5. Delete Reservation ");
                System.out.println("0. Exit ");
                System.out.print("Choose an option : ");
                Scanner scanner = new Scanner(System.in);
                int choice = scanner.nextInt();

                switch(choice) {
                    case 1:
                        reserveRoom(connection, scanner);
                        break;

                    case 2:
                        viewReservation(connection);
                        break;

                    case 3:
                        getRoomNumber(connection, scanner);
                        break;

                    case 4:
                        updateReservation(connection, scanner);
                        break;

                    case 5:
                        deleteReservation(connection, scanner);
                        break;

                    case 0:
                        exit();
                        scanner.close();
                        return;

                    default:
                        System.out.println("Invalid Choice. Try Again");
                }
            }
        }
        catch(SQLException e) {
            System.out.println(e.getMessage());
        }
        catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
    private static void reserveRoom(Connection connection, Scanner scanner) {
        try{
            Statement statement = connection.createStatement();
            System.out.print("Enter Guest Name : ");
            String guestName = scanner.next();
            System.out.print("Enter Room Number : ");
            int roomNumber = scanner.nextInt();
            System.out.print("Enter Contact Number : ");
            String contactNumber = scanner.next();

            String query = String.format("INSERT INTO reservations(guest_name, room_number, contact_number) VALUES ('%s', %d, '%s')", guestName, roomNumber, contactNumber);
            int affectedRow = statement.executeUpdate(query);
            if(affectedRow>0) {
                System.out.println("Reservaton Successfully!!!");
            }
            else {
                System.out.println("Reservation Failed!!!");
            }
        }
        catch(SQLException e) {
            System.out.println(e.getMessage());
        }
    }
    private static void viewReservation(Connection connection) {
        try {
            Statement statement = connection.createStatement();
            System.out.println("Current Reservations:");
            System.out.println("+----------------+-----------------+---------------+----------------------+-------------------------+");
            System.out.println("| Reservation ID | Guest           | Room Number   | Contact Number       | Reservation Date        |");
            System.out.println("+----------------+-----------------+---------------+----------------------+-------------------------+");


            String query = "SELECT * FROM reservations";
            ResultSet resultSet = statement.executeQuery(query);
            while(resultSet.next()) {
                int reservationID = resultSet.getInt("reservation_id");
                String guestName = resultSet.getString("guest_name");
                int roomNumber = resultSet.getInt("room_number");
                String contactNumber = resultSet.getString("contact_number");
                String reservationDate = resultSet.getTimestamp("reservation_date").toString();

                System.out.printf("| %-14d | %-15s | %-13d | %-20s | %-19s   |\n", reservationID, guestName, roomNumber, contactNumber, reservationDate);
            }
            System.out.println("+----------------+-----------------+---------------+----------------------+-------------------------+");
        }
        catch(SQLException e) {
            System.out.println(e.getMessage());
        }
    }
    private static void getRoomNumber(Connection connection, Scanner scanner) {
        try {
            Statement statement = connection.createStatement();
            System.out.print("Enter Reservation ID : ");
            int reservationID = scanner.nextInt();
            System.out.print("Enter Guest Name : ");
            String guestName = scanner.next();

            String query = String.format("SELECT room_number FROM reservations WHERE reservation_id = %d  AND guest_name = '%s'",reservationID, guestName);
            ResultSet resultSet = statement.executeQuery(query);
            while(resultSet.next()) {
                int roomNumber = resultSet.getInt("room_number");
                System.out.println("Room Number : "+roomNumber);
            }
        }
        catch(SQLException e) {
            System.out.println(e.getMessage());
        }
    }
    private static void updateReservation(Connection connection, Scanner scanner) {
        try {
            Statement statement = connection.createStatement();
            System.out.print("Enter reservation ID to update : ");
            int reservationID = scanner.nextInt();

            if(!reservationExists(connection,reservationID)) {
                System.out.println("Reservation not found for the given ID.");
                return;
            }
            else {
                System.out.print("Enter new guest name : ");
                String guestName = scanner.next();
                System.out.print("Enter new room number : ");
                int roomNumber = scanner.nextInt();
                System.out.print("Enter new contact number : ");
                String contactNumber = scanner.next();

                String query = String.format("UPDATE reservations SET guest_name = '%s', room_number = %d, contact_number = '%s' WHERE reservation_id = %d",guestName, roomNumber, contactNumber, reservationID);
                int affectedRow = statement.executeUpdate(query);
                if(affectedRow>0) {
                    System.out.println("Reservation Updated Successfully!!!");
                }
                else {
                    System.out.println("Reservation Updated Failed!!!");
                }
            }
        }
        catch(SQLException e) {
            System.out.println(e.getMessage());
        }
    }
    private static void deleteReservation(Connection connection, Scanner scanner) {
        try {
            Statement statement = connection.createStatement();
            System.out.print("Enter reservation ID to delete : ");
            int reservationID = scanner.nextInt();

            if(!reservationExists(connection, reservationID)) {
                System.out.println("Reservation not found for the given ID");
                return;
            }
            else {
                String query = String.format("DELETE FROM reservations WHERE reservation_id = %d",reservationID);
                int affectedRow = statement.executeUpdate(query);
                if(affectedRow>0) {
                    System.out.println("Reservation Deleted Successfully!!!");
                }
                else {
                    System.out.println("Reservation Deletion Failed!!!");
                }
            }
        }
        catch(SQLException e) {
            System.out.println(e.getMessage());
        }
    }
    private static boolean reservationExists(Connection connection, int reservationID) {
        try {
            Statement statement = connection.createStatement();
            String sql = "SELECT reservation_id FROM reservations WHERE reservation_id = " + reservationID;
            ResultSet resultSet = statement.executeQuery(sql);
            return resultSet.next();
        }
        catch (SQLException e) {
            System.out.println(e.getMessage());
            return false;
        }
    }
    private static void exit() throws InterruptedException {
        System.out.print("Exiting System");
        int i=5;
        while(i!=0) {
            System.out.print(".");
            Thread.sleep(1000);
            i--;
        }
        System.out.println();
        System.out.println("ThankYou For Using Hotel Reservation System!!!");
    }
}