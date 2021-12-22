module org.moron.tictactoe {
    requires javafx.controls;
    requires javafx.fxml;
    requires kotlin.stdlib;


    opens org.moron.tictactoe to javafx.fxml;
    exports org.moron.tictactoe;
}