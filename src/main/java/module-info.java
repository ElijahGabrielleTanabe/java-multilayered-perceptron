module com.github.elijahgabrielletanabe {
    requires javafx.controls;
    requires javafx.fxml;

    opens com.github.elijahgabrielletanabe.Controllers to javafx.fxml;
    exports com.github.elijahgabrielletanabe;
    exports com.github.elijahgabrielletanabe.Controllers;
}
