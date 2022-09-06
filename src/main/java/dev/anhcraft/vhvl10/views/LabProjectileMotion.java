package dev.anhcraft.vhvl10.views;

import dev.anhcraft.jvmkit.kits.geometry.Point2d;
import dev.anhcraft.neep.utils.MathUtil;
import dev.anhcraft.vhvl10.Constants;
import dev.anhcraft.vhvl10.gui.GUIApp;
import dev.anhcraft.vhvl10.gui.Prompt;
import dev.anhcraft.vhvl10.objects.lab.ProjectileMotionExp;
import dev.anhcraft.vhvl10.resources.Storage;
import dev.anhcraft.vhvl10.tasks.TaskManager;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.paint.Color;

import java.net.URL;
import java.util.ResourceBundle;
import java.util.concurrent.Future;

public class LabProjectileMotion implements Initializable {
    private static final double RAD_90 = Math.toRadians(90);
    private static final double RECT_LEN = 5;
    private static final long TIME_OFFSET = 50;

    @FXML
    public Button runBtn;
    @FXML
    public Canvas canvas;
    @FXML
    public TextField initSpeed;

    private Point2d cursor;
    private Point2d projectile;
    private Future<?> task;

    @FXML
    public void quit() {
        if (task != null) {
            task.cancel(false);
        }
        View.LAB_MENU.load();
    }

    @FXML
    public void run() {
        if (runBtn.isDisable()) return;
        if (!save()) return;
        runBtn.setDisable(true);
        task = TaskManager.runAsync(() -> {
            projectile = new Point2d(0, 0);
            GraphicsContext ctx = canvas.getGraphicsContext2D();
            ProjectileMotionExp exp = Storage.userData.getProjectileMotionExp();
            double angle = -exp.getDirection().getAngle();
            double cos = Math.cos(angle);
            double sin = Math.sin(angle);
            double time = 0;
            while (true) {
                double deltaTime = (time += TIME_OFFSET) / 1000; // millis -> secs
                // x = v0 * cos * t
                double x = exp.getDirection().getOriginX() + exp.getInitialSpeed() * cos * deltaTime;
                // y = v0 * sin * t - 1/2 * g * t^2
                double y = exp.getDirection().getOriginY() + exp.getInitialSpeed() * sin * deltaTime - 0.5 * Constants.EARTH_GRAVITY * deltaTime * deltaTime;
                if (y <= 0) {
                    break;
                }
                projectile.setX(x);
                projectile.setY(y);
                TaskManager.runSync(() -> {
                    clear(ctx);
                    render(ctx);
                });
                try {
                    Thread.sleep(TIME_OFFSET);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            projectile = null;
            task = null;
            clear(ctx);
            render(ctx);
            runBtn.setDisable(false);
        });
    }

    private void clear(GraphicsContext ctx) {
        ctx.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
    }

    private void render(GraphicsContext ctx) {
        ProjectileMotionExp exp = Storage.userData.getProjectileMotionExp();
        double x2 = exp.getDirection().getTargetX();
        double y2 = canvas.getHeight() - exp.getDirection().getTargetY();

        ctx.beginPath();
        ctx.setFill(Color.BLACK);
        ctx.fillRect(exp.getDirection().getOriginX(), canvas.getHeight() - exp.getDirection().getOriginY(), 10, 10);
        ctx.fill();

        ctx.setStroke(Color.RED);
        ctx.moveTo(exp.getDirection().getOriginX(), canvas.getHeight() - exp.getDirection().getOriginY());
        ctx.lineTo(x2, y2);
        ctx.stroke();

        double angle = -exp.getDirection().getAngle();

        double cx1 = x2 - Math.sin(angle) * RECT_LEN;
        double cx2 = x2 + Math.sin(angle) * RECT_LEN;
        double wc = Math.sin(RAD_90 - angle) * RECT_LEN * 2;
        double cx3 = (cx1 + cx2) / 2 + wc;

        double cy1 = y2 - Math.cos(angle) * RECT_LEN;
        double cy2 = y2 + Math.cos(angle) * RECT_LEN;

        double hc = Math.cos(RAD_90 - angle) * RECT_LEN * 2;
        double cy3 = (cy1 + cy2) / 2 - hc;

        ctx.moveTo(cx1, cy1);
        ctx.lineTo(cx2, cy2);
        ctx.stroke(); // front

        ctx.moveTo(cx1, cy1);
        ctx.lineTo(cx3, cy3);
        ctx.stroke(); // top

        ctx.moveTo(cx2, cy2);
        ctx.lineTo(cx3, cy3);
        ctx.stroke(); // bottom

        if (cursor != null) {
            ctx.setStroke(Color.BLUE);
            ctx.moveTo(exp.getDirection().getOriginX(), canvas.getHeight() - exp.getDirection().getOriginY());
            ctx.lineTo(cursor.getX(), cursor.getY());
            ctx.stroke();
        }

        if (projectile != null) {
            ctx.setFill(Color.LIME);
            ctx.fillRect(projectile.getX(), canvas.getHeight() - projectile.getY(), 10, 10);
            ctx.fill();

            ctx.setFill(Color.BLACK);
            ctx.fillText(String.format("X: %.2f | Y: %.2f", projectile.getX(), projectile.getY()), 20, 30);
            ctx.fill();
        }

        ctx.closePath();
    }

    private boolean save() {
        String s1 = initSpeed.getText().replaceAll("[^\\d.]", "");
        if (!s1.isEmpty() && MathUtil.isNumber(s1)) {
            double d = Double.parseDouble(s1);
            if (d >= 0) {
                Storage.userData.getProjectileMotionExp().setInitialSpeed(d);
            } else {
                Prompt.err(s1 + " phải từ 0.0 trở lên.");
                return false;
            }
        } else {
            Prompt.err(s1 + " không phải là một con số hợp lệ.");
            return false;
        }
        return true;
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        ProjectileMotionExp exp = Storage.userData.getProjectileMotionExp();
        initSpeed.setText(Double.toString(exp.getInitialSpeed()));
        GraphicsContext ctx = canvas.getGraphicsContext2D();
        render(ctx);
        canvas.setOnMouseClicked(mouseEvent -> {
            if (GUIApp.shift) {
                exp.getDirection().setTargetX((int) mouseEvent.getX());
                exp.getDirection().setTargetY((int) (canvas.getHeight() - mouseEvent.getY()));
                cursor = null;
            } else {
                exp.getDirection().setOriginX((int) mouseEvent.getX());
                exp.getDirection().setOriginY((int) (canvas.getHeight() - mouseEvent.getY()));
            }
            clear(ctx);
            render(ctx);
        });
        canvas.setOnMouseMoved(mouseEvent -> {
            if (GUIApp.shift) {
                cursor = new Point2d(mouseEvent.getX(), mouseEvent.getY());
                clear(ctx);
                render(ctx);
            } else if (cursor != null) {
                cursor = null;
                clear(ctx);
                render(ctx);
            }
        });
        initSpeed.setOnKeyPressed(keyEvent -> {
            if (keyEvent.getCode().getName().equals("Enter")) save();
        });
    }
}
