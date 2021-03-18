import com.mongodb.MongoClient;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import org.bson.Document;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;



public class TrainStation extends Application {
    private Passenger[] waitingRoom = new Passenger[42];
    private int waitingRoomNext = 0;
    private  PassengerQueue passengerQueue = new PassengerQueue();
    private Passenger[] train = new Passenger[42];
    private int trainNext = 0;
    private int waitingTime = 0;

    public static void main(String[] args) {
        launch();
    }
    @Override
    public void start (Stage primaryStage) throws Exception{
        Logger mongoLogger = Logger.getLogger("org.mongodb.driver");
        mongoLogger.setLevel(Level.SEVERE);
        MongoClient mongoClient = new MongoClient("localhost", 27017);
        MongoDatabase mongoDatabase = mongoClient.getDatabase("DenuwaraMenikeTrainSeatBookingSystem");
        MongoCollection<Document> ColombotoBadulla = mongoDatabase.getCollection("ColomboToBadulla");
        MongoCollection<Document>  BadullaToColombo = mongoDatabase.getCollection("BadullaToColombo");
        MongoCollection<Document> TrainQueue = mongoDatabase.getCollection("Queue");


        HashMap<Integer, String> customerDetailsColomboToBadulla = new HashMap<Integer, String>(); //hashmap 1
        HashMap<Integer, String> customerDetailsBadullaToColombo = new HashMap<Integer, String>(); //hashmap 2

        loadBookingData(customerDetailsColomboToBadulla, ColombotoBadulla); //loading Data
        loadBookingData(customerDetailsBadullaToColombo, BadullaToColombo); //Loading Data

        System.out.println("Enter No.1 If You Departing Station Is Colombo");
        System.out.println("Enter No.2 If You Departing Station Is Badulla");

        Scanner sc = new Scanner(System.in); //Checking the departure station
        String userInput = sc.next();

        if (userInput.equalsIgnoreCase("1")){
            addToWaitingRoom(customerDetailsColomboToBadulla);
        } else if (userInput.equalsIgnoreCase("2")){
            addToWaitingRoom(customerDetailsBadullaToColombo);
        } else {
            System.out.println("Invalid input.Please Read The Instructions Again");
        }

        //MENU
        menu:
        while (true){
            System.out.println("--------Dear Sir/Madam Welcome To Train Station--------");
            System.out.println("---------------------------------------------");
            System.out.println("-----PLease Follow The Below Instructions-----");
            System.out.println("---------------------------------------------");
            System.out.println("(01) Enter \"A\" To add to Train Queue");
            System.out.println("---------------------------------------------");
            System.out.println("(02) Enter \"V\" To View The Train Queue");
            System.out.println("---------------------------------------------");
            System.out.println("Enter \"D\" To Delete Passenger From The TrainQueue");
            System.out.println("---------------------------------------------");
            System.out.println("Enter \"S\" To Store Data Into A File ");
            System.out.println("---------------------------------------------");
            System.out.println("Enter \"L\" To Load Data From The File Into The TrainQueue");
            System.out.println("---------------------------------------------");
            System.out.println("(03) Enter \"R\" To To Run Simulation And View Report");
            System.out.println("---------------------------------------------");
            System.out.println("(04) Enter \"Q\" To Exit The Programme");
            System.out.println("---------------------------------------------");

        //Getting the input
            System.out.print("Enter Your Choice :");
            String option = sc.next();

            switch (option){
                case "A":
                case "a":
                    addToTrainQueue();
                    break ;
                case "R":
                case "r":
                    runSimulation();
                    break ;
                case "V":
                case "v":
                    viewTrainQueue();
                    break ;
                case "D":
                case "d":
                    System.out.println("Enter a name");
                    String name=sc.next();
                    deletePassenger(name);
                    break;
                case "S":
                case "s":
                    saveQueueDetails(TrainQueue);
                    System.out.println("Stored The data Sucssesfully---------------");
                    break;
                case "L":
                case "l":
                    loadQueueDetails(TrainQueue);
                    System.out.println("Loaded data from a file--------------------");
                    break ;
                case "Q":
                case "q":
                    System.out.println("You Are Exiting.....Have A Nice Day");
                    break menu;
            }
        }

    }

    //viewTrainQueue method..............................................................................
    private void viewTrainQueue() {
        System.out.println("Waiting Room");
        System.out.println("---------------------------------------------");
        for (int i=0; i<waitingRoomNext;i++){
            System.out.println(waitingRoom[i].getName() + " ~ " + waitingRoom[i].getSeat());
        }


        System.out.println("Train Queue");
        System.out.println("---------------------------------------------");
        for (int i=0; i<passengerQueue.getLast(); i++){
            System.out.println(passengerQueue.getQueueArray()[i].getName() + " ~ " + passengerQueue.getQueueArray()[i].getSeat());
        }

        System.out.println("Train");
        System.out.println("---------------------------------------------");
        for (int i=0; i<trainNext; i++){
            System.out.println(train[i].getName() + " " + train[i].getSeat());
        }

        Stage stage = new Stage();

        VBox vBox = new VBox();
        vBox.setSpacing(10);
        VBox vBox1 = new VBox();
        vBox1.setSpacing(10);
        VBox vBox2 = new VBox();
        vBox2.setSpacing(10);
        VBox vBox3 = new VBox();
        vBox3.setSpacing(10);
        VBox vBox4 = new VBox();
        vBox4.setSpacing(10);
        VBox vBox5 = new VBox();
        vBox5.setSpacing(10);
        VBox vBox6 = new VBox();
        vBox6.setSpacing(10);


        for (int i=0;i<waitingRoomNext;i++){
            Label label3 = new Label((i+1) + " - " +waitingRoom[i].getName());
            label3.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");
            if (i>21){
                vBox1.getChildren().add(label3);
            } else {
                vBox.getChildren().add(label3);
            }

        }

        for (int i=0;i<passengerQueue.getLast();i++){
            Label label4 = new Label((i+1) + " - " +passengerQueue.getQueueArray()[i].getName());
            label4.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");
            if (i>21){
                vBox3.getChildren().add(label4);
            } else {
                vBox2.getChildren().add(label4);
            }

        }

        Button[] seat = new Button[42];
        for (int i=0;i<42;i++){
            Button button = new Button((i+1) + " Empty");
            button.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-background-color: #5ecc65; -fx-background-radius: 20");
            button.setPrefWidth(160);
            button.setId(String.valueOf(i+1));
            seat[i]=button;

            if (i>27){
                vBox6.getChildren().add(button);
            } else if (i>13){
                vBox5.getChildren().add(button);
            } else {
                vBox4.getChildren().add(button);
            }
        }

        for (int i=0;i<trainNext;i++){
            seat[Integer.valueOf(train[i].getSeat())-1].setText(train[i].getSeat() + " - " + train[i].getName());
            seat[Integer.valueOf(train[i].getSeat())-1].setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-background-color: #2f9982; -fx-background-radius: 20");
        }


        HBox hBox = new HBox();
        hBox.setSpacing(20);
        HBox hBox1 = new HBox();
        hBox1.setSpacing(20);
        HBox hBox2 = new HBox();
        hBox2.setSpacing(20);

        Label label3 = new Label("Waiting Room");
        label3.setLayoutX(40);
        label3.setStyle("-fx-font-size: 35px; -fx-font-weight: bold;-fx-font-family: 'Comic Sans MS';");
        Label label4 = new Label("Train Queue");
        label4.setLayoutX(340);
        label4.setStyle("-fx-font-size: 35px; -fx-font-weight: bold;-fx-font-family: 'Comic Sans MS';");
        Label label5 = new Label("Seat Arrangment");
        label5.setLayoutX(520);
        label5.setStyle("-fx-font-size: 35px; -fx-font-weight: bold;-fx-font-family: 'Comic Sans MS';");
        HBox hBox3 = new HBox();
        hBox3.getChildren().addAll(label3,label4,label5);

        hBox3.setMargin(label3, new Insets(10, 0, 10, 10));
        hBox3.setMargin(label4, new Insets(10, 0, 10, 220));
        hBox3.setMargin(label5, new Insets(10, 0, 10, 380));

        hBox.getChildren().addAll(vBox,vBox1);
        hBox1.getChildren().addAll(vBox2,vBox3);
        hBox2.getChildren().addAll(vBox4,vBox5,vBox6);

        hBox.setPrefWidth(460);
        hBox1.setPrefWidth(500);
        hBox2.setPrefWidth(600);

        BorderPane borderPane = new BorderPane();
        borderPane.setLeft(hBox);
        borderPane.setCenter(hBox1);
        borderPane.setRight(hBox2);
        borderPane.setTop(hBox3);


        stage.setScene(new Scene(borderPane, 1500, 850));
        stage.setTitle("Denuwara Menike Train Station");
        stage.showAndWait();

    }

    //addToTrainQueue method..............................................................................
    private void addToTrainQueue() {
        Random random = new Random();
        int randomNum = random.nextInt(6) + 1;

        if (randomNum>waitingRoomNext){
            randomNum=waitingRoomNext;
        }
        System.out.println("Random num :- " + randomNum);

        for (int i=0; i<randomNum; i++){
            int waitingT = random.nextInt(6)+random.nextInt(6)+random.nextInt(6);
            waitingTime = waitingTime+waitingT;
            waitingRoom[i].setWaitingTime(waitingTime);
            passengerQueue.add(waitingRoom[i]);
        }
        for (int j=0; j<randomNum; j++) {

            for (int i = 0; i < waitingRoomNext; i++) {
                if ((i+1)<42){
                    waitingRoom[i] = waitingRoom[i + 1];
                }

            }
        }
        waitingRoomNext = waitingRoomNext - randomNum;
        System.out.println(" ");
        System.out.println("Train Queue");
        System.out.println("---------------------------------------------");
        for (int i=0; i<passengerQueue.getLast(); i++){
            System.out.println(passengerQueue.getQueueArray()[i].getName() + " ~ " + passengerQueue.getQueueArray()[i].getSeat());
        }
        Stage stage = new Stage();

        VBox vBox = new VBox();
        vBox.setSpacing(10);
        VBox vBox1 = new VBox();
        vBox1.setSpacing(10);
        VBox vBox2 = new VBox();
        vBox2.setSpacing(10);
        VBox vBox3 = new VBox();
        vBox3.setSpacing(10);

        for (int i=0;i<waitingRoomNext;i++){
            Label label3 = new Label((i+1) + " - " +waitingRoom[i].getName());
            label3.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");
            if (i>21){
                vBox1.getChildren().add(label3);
            } else {
                vBox.getChildren().add(label3);
            }

        }

        for (int i=0;i<passengerQueue.getLast();i++){
            Label label4 = new Label((i+1) + " - " +passengerQueue.getQueueArray()[i].getName());
            label4.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");
            if (i>21){
                vBox3.getChildren().add(label4);
            } else {
                vBox2.getChildren().add(label4);
            }

        }

        HBox hBox = new HBox();
        hBox.setSpacing(20);
        HBox hBox1 = new HBox();
        hBox1.setSpacing(20);
        HBox hBox2 = new HBox();
        hBox2.setSpacing(20);

        Label label3 = new Label("Waiting Room");
        label3.setLayoutX(40);
        label3.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-background-color: #2f9982; -fx-font-family: 'Comic Sans MS'; -fx-background-radius: 40");
        Label label4 = new Label("Train Queue");
        label4.setLayoutX(340);
        label4.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-background-color: #2f9982; -fx-font-family: 'Comic Sans MS'; -fx-background-radius: 40");


        HBox hBox3 = new HBox();
        hBox3.getChildren().addAll(label3,label4);

        hBox3.setMargin(label3, new Insets(10, 0, 10, 10));
        hBox3.setMargin(label4, new Insets(10, 0, 10, 220));

        hBox.getChildren().addAll(vBox,vBox1);
        hBox1.getChildren().addAll(vBox2,vBox3);


        hBox.setPrefWidth(360);
        hBox1.setPrefWidth(200);


        BorderPane borderPane = new BorderPane();
        borderPane.setLeft(hBox);
        borderPane.setCenter(hBox1);
        borderPane.setRight(hBox2);
        borderPane.setTop(hBox3);


        stage.setScene(new Scene(borderPane, 500, 650));
        stage.setTitle("Denuwara Menike Train Station");
        stage.showAndWait();

    }

    //deletePassenger method..............................................................................
    private void deletePassenger(String name)
    {
        Passenger passenger = new Passenger();
        passenger.setName(name);
        passengerQueue.remove(passenger);
    }

    //runSimulation method..............................................................................
    private void runSimulation() {
        for (int i=0; i<passengerQueue.getLast();i++){
            train[trainNext++] = passengerQueue.getQueueArray()[i];
        }

        System.out.println("Report");
        System.out.println("---------------------------------------------");
        System.out.println("Maximum Length - " + passengerQueue.getLast());
        System.out.println("Maximum Waiting Time - " + passengerQueue.getQueueArray()[passengerQueue.getLast()-1].getWaitingTime());
        System.out.println("Minimum Waiting Time - " + passengerQueue.getQueueArray()[0].getWaitingTime());
        System.out.println("Average Waiting Time - " + passengerQueue.getQueueArray()[passengerQueue.getLast()-1].getWaitingTime()/(double)passengerQueue.getLast());
        System.out.println("---------------------------------------------");
        passengerQueue.setLast(0);
        waitingTime=0;
    }


    //addToWaitingRoom method..............................................................................
    private void addToWaitingRoom(HashMap<Integer, String> customerDetailsColomboToBadulla) {
        for (int i : customerDetailsColomboToBadulla.keySet()){
            Passenger passenger = new Passenger();
            passenger.setSeat(String.valueOf(i));
            passenger.setName(customerDetailsColomboToBadulla.get(i));
            waitingRoom[waitingRoomNext] = passenger;
            waitingRoomNext = waitingRoomNext + 1;
        }
    }

    //loadBookingData method..............................................................................
    private void loadBookingData(HashMap<Integer, String> customerDetailsColomboToBadulla, MongoCollection<Document> ColombotoBadulla) {
        FindIterable<Document> Doc = ColombotoBadulla.find();

        for (Document record : Doc){
            String fName = (String) record.get("Name");

            int num = (int) record.get("Seat Number");
            customerDetailsColomboToBadulla.put(num, fName);

        }
    }

    //saveQueue method..............................................................................
    private void saveQueueDetails(MongoCollection<Document> TrainQueue) {
        try {
            List<Document> Names = new ArrayList<Document>();
            for (int i = 0; i < passengerQueue.getLast(); i++) {
                Document document = new Document();
                document.append("Name", passengerQueue.getQueueArray()[i].getName());
                document.append("Seat", passengerQueue.getQueueArray()[i].getSeat());
                Names.add(document);
            }
            TrainQueue.insertMany(Names);
        } catch (Exception e){}
    }

    //loadQueue method..............................................................................
    private void loadQueueDetails(MongoCollection<Document> TrainQueue) {
        FindIterable<Document> Doc = TrainQueue.find();

        for (Document record : Doc){
            String firstName = (String) record.get("Name");
            String seatNo =String.valueOf(record.get("Seat"));

            Passenger passenger = new Passenger();
            passenger.setName(firstName);
            passenger.setSeat(seatNo);
            passengerQueue.add(passenger);
        }
    }
}
