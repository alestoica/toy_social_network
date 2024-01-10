module org.example {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires java.sql;

    opens org.example to javafx.fxml;
    exports org.example;
    opens org.example.domain;
    exports org.example.domain;
    exports org.example.controller;
    opens org.example.controller to javafx.fxml;
    exports org.example.service;
    opens org.example.service;
    opens org.example.repository.paging;
    exports org.example.repository.paging;
    opens org.example.repository;
    exports org.example.repository;
    opens org.example.repository.db;
    exports org.example.repository.db;
    opens org.example.validators;
    exports org.example.validators;
    opens org.example.utils.observer;
    exports org.example.utils.observer;
    opens org.example.utils.events;
    exports org.example.utils.events;
}