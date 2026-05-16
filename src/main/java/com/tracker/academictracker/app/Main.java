package com.tracker.academictracker.app;

import com.tracker.academictracker.ui.controller.DashboardController;
import com.tracker.academictracker.domain.model.Student;
import com.tracker.academictracker.persistence.DataManager;
import com.tracker.academictracker.ui.util.SmoothScrollUtil;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class Main extends Application {

    // Giữ lại tham chiếu của cửa sổ chính để có thể đổi Scene
    private static Stage primaryStage;

    @Override
    public void start(Stage stage) throws Exception {
        primaryStage = stage;
        primaryStage.setTitle("Academic Tracker");

        // Sử dụng DataManager để kiểm tra và tải dữ liệu!
        if (DataManager.saveFileExists()) {
            System.out.println("📁 Đã tìm thấy file save. Đang tải dữ liệu...");

            Student loadedStudent = DataManager.loadStudent();

            if (loadedStudent != null) {
                showDashboard(loadedStudent); // Truyền dữ liệu thật vào UI
            } else {
                // Nếu file tồn tại nhưng bị rỗng/lỗi, quay về màn hình Welcome
                showWelcomeScreen();
            }
        } else {
            System.out.println("⚠️ Không tìm thấy file save. Mở màn hình tạo Profile...");
            showWelcomeScreen();
        }
    }

    /**
     * Mở màn hình Khởi tạo Profile (Welcome.fxml)
     */
    public static void showWelcomeScreen() throws IOException {
        FXMLLoader loader = new FXMLLoader(Main.class.getResource("/com/tracker/academictracker/Welcome.fxml"));
        Parent root = loader.load();
        SmoothScrollUtil.applyTo(root);

        Scene scene = new Scene(root, 1100, 700);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    /**
     * Mở màn hình Dashboard chính (Dashboard.fxml) và truyền dữ liệu Student vào
     */
    public static void showDashboard(Student student) throws IOException {
        FXMLLoader loader = new FXMLLoader(Main.class.getResource("/com/tracker/academictracker/Dashboard.fxml"));
        Parent root = loader.load();

        // Lấy Controller và truyền dữ liệu
        DashboardController controller = loader.getController();
        controller.initData(student);
        SmoothScrollUtil.applyTo(root);

        Scene scene = new Scene(root, 1100, 700);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
