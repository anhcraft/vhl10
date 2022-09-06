package dev.anhcraft.vhvl10.views;

import dev.anhcraft.jvmkit.kits.geometry.Point2d;
import dev.anhcraft.neep.utils.MathUtil;
import dev.anhcraft.vhvl10.gui.GUIApp;
import dev.anhcraft.vhvl10.gui.Prompt;
import dev.anhcraft.vhvl10.objects.lab.LinearMotionExp;
import dev.anhcraft.vhvl10.objects.lab.MotionPeriod;
import dev.anhcraft.vhvl10.objects.lab.Vector;
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

public class LabLinearMotion implements Initializable {
    @FXML
    public TextField friction;
    @FXML
    public TextField weight;
    @FXML
    public TextField traction;
    @FXML
    public TextField originSpeed;
    @FXML
    public Button runBtn;
    @FXML
    public Canvas canvas;
    @FXML
    public Button delPeriod;

    private MotionPeriod period;
    private MotionPeriod nearest;
    private MotionPeriod highlight;
    private boolean preventSelect;
    private Point2d vehicle;
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
        preventSelect = false;
        nearest = null;
        switchSelection();
        task = TaskManager.runAsync(() -> {
            GraphicsContext ctx = canvas.getGraphicsContext2D();
            LinearMotionExp exp = Storage.userData.getLinearMotionExp();
            double mS = exp.getVehicleOriginSpeed();
            long deltaTime = 50;
            outer:
            for (MotionPeriod p : exp.getMotionPeriods()) {
                highlight = p;
                if (vehicle == null) {
                    vehicle = new Point2d(p.getOriginX(), p.getOriginY());
                }
                double angle = p.getAngle();
                double acc = exp.calculateAcceleration(p);
                double len = p.getLength();
                double cos = Math.cos(angle);
                double sin = Math.sin(angle);
                double deltaMs = acc * (deltaTime / 1000d);
                for (double i = 0; true; i += mS) {
                    i = Math.min(i, len);
                    mS += deltaMs; // v = a * t + vo
                    if (mS <= 0) break outer;
                    double speed = mS;
                    vehicle.setX(p.getOriginX() + i * cos);
                    vehicle.setY(p.getOriginY() - i * sin);
                    TaskManager.runSync(() -> {
                        clear(ctx);
                        renderRoads(ctx);
                        renderVehicle(ctx, acc, speed, p.getLength());
                    });
                    try {
                        Thread.sleep(deltaTime);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    if (i >= len) break;
                }
            }
            vehicle = null;
            highlight = null;
            task = null;
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            TaskManager.runSync(() -> {
                clear(ctx);
                renderRoads(ctx);
                runBtn.setDisable(false);
            });
        });
    }

    @FXML
    public void deletePeriod() {
        Storage.userData.getLinearMotionExp().getMotionPeriods().remove(nearest);
        nearest = null;
        switchSelection();
        GraphicsContext ctx = canvas.getGraphicsContext2D();
        clear(ctx);
        renderRoads(ctx);
    }

    @FXML
    public void deleteAll() {
        Storage.userData.getLinearMotionExp().getMotionPeriods().clear();
        nearest = null;
        switchSelection();
        GraphicsContext ctx = canvas.getGraphicsContext2D();
        clear(ctx);
        renderRoads(ctx);
    }

    private boolean save() {
        if (nearest != null && preventSelect) {
            String s1 = friction.getText().replaceAll("[^\\d.]", "");
            if (!s1.isEmpty() && MathUtil.isNumber(s1)) {
                double d = Double.parseDouble(s1);
                if (d >= 0) {
                    nearest.setFriction(d);
                } else {
                    Prompt.err(s1 + " phải từ 0.0 trở lên.");
                    return false;
                }
            } else {
                Prompt.err(s1 + " không phải là một con số hợp lệ.");
                return false;
            }
        }
        String s2 = weight.getText().replaceAll("[^\\d.]", "");
        if (!s2.isEmpty() && MathUtil.isNumber(s2)) {
            double d = Double.parseDouble(s2);
            if (d >= 0) {
                Storage.userData.getLinearMotionExp().setVehicleWeight(d);
            } else {
                Prompt.err(s2 + " phải từ 0.0 trở lên.");
                return false;
            }
        } else {
            Prompt.err(s2 + " không phải là một con số hợp lệ.");
            return false;
        }
        String s3 = traction.getText().replaceAll("[^\\d.]", "");
        if (!s3.isEmpty() && MathUtil.isNumber(s3)) {
            double d = Double.parseDouble(s3);
            if (d >= 0) {
                Storage.userData.getLinearMotionExp().setVehicleTraction(d);
            } else {
                Prompt.err(s3 + " phải từ 0.0 trở lên.");
                return false;
            }
        } else {
            Prompt.err(s3 + " không phải là một con số hợp lệ.");
            return false;
        }
        String s4 = originSpeed.getText().replaceAll("[^\\d.]", "");
        if (!s4.isEmpty() && MathUtil.isNumber(s4)) {
            double d = Double.parseDouble(s4);
            if (d >= 0) {
                Storage.userData.getLinearMotionExp().setVehicleOriginSpeed(d);
            } else {
                Prompt.err(s4 + " phải từ 0.0 trở lên.");
                return false;
            }
        } else {
            Prompt.err(s4 + " không phải là một con số hợp lệ.");
            return false;
        }
        return true;
    }

    private void clear(GraphicsContext ctx) {
        ctx.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
    }

    private void renderRoads(GraphicsContext ctx) {
        for (MotionPeriod p : Storage.userData.getLinearMotionExp().getMotionPeriods()) {
            if (p.equals(highlight)) {
                ctx.setStroke(Color.GREEN);
            } else if (period == null && p.equals(nearest)) {
                ctx.setStroke(Color.RED);
            } else {
                ctx.setStroke(Color.BLACK);
            }
            draw(ctx, p, p.getTargetX(), p.getTargetY());
        }
    }

    private void renderVehicle(GraphicsContext ctx, double acc, double speed, double length) {
        if (vehicle == null) return;
        ctx.beginPath();
        ctx.setFill(Color.BLACK);
        ctx.fillRect(vehicle.getX() - 7, vehicle.getY() - 7, 7, 7);
        ctx.fill();

        ctx.fillText(String.format("X: %.2f | Y: %.2f", vehicle.getX(), vehicle.getY()), 20, 30);
        ctx.fill();
        ctx.fillText(String.format("Gia tốc %.2f m/s^2", acc), 20, 50);
        ctx.fillText(String.format("Vận tốc %.2f m/s", speed), 20, 70);
        ctx.fillText(String.format("Quãng đường %.2f m", length), 20, 90);
        ctx.fill();

        ctx.closePath();
    }

    private void draw(GraphicsContext ctx, MotionPeriod period, int x, int y) {
        ctx.beginPath();
        ctx.moveTo(period.getOriginX(), period.getOriginY());
        ctx.lineTo(x, y);
        ctx.stroke();
        ctx.closePath();
    }

    private void switchSelection() {
        if (!preventSelect && nearest != null) {
            friction.setText(Double.toString(nearest.getFriction()));
            preventSelect = true;
        } else {
            friction.setText("");
            preventSelect = false;
        }
        friction.setDisable(!preventSelect);
        delPeriod.setDisable(!preventSelect);
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        friction.setDisable(true);
        delPeriod.setDisable(true);
        LinearMotionExp exp = Storage.userData.getLinearMotionExp();
        weight.setText(Double.toString(exp.getVehicleWeight()));
        traction.setText(Double.toString(exp.getVehicleTraction()));
        originSpeed.setText(Double.toString(exp.getVehicleOriginSpeed()));
        GraphicsContext ctx = canvas.getGraphicsContext2D();
        for (MotionPeriod p : Storage.userData.getLinearMotionExp().getMotionPeriods()) {
            draw(ctx, p, p.getTargetX(), p.getTargetY());
        }
        canvas.setOnMouseClicked(mouseEvent -> {
            int x = (int) mouseEvent.getX();
            int y = (int) mouseEvent.getY();
            if (period != null) {
                period.setTargetX(x);
                period.setTargetY(y);
                if (GUIApp.control) {
                    double length = period.getLength();
                    double deg = Math.toDegrees(period.getAngle());
                    double angle = (deg >= 0 && deg < 15.0 / 2.0) ? 0 : dev.anhcraft.jvmkit.utils.MathUtil.nextMultiple(deg, 15);
                    angle = Math.toRadians(angle);
                    period.setTargetX(period.getOriginX() + (int) (length * Math.cos(angle)));
                    period.setTargetY(period.getOriginY() + (int) (length * -Math.sin(angle)));
                }
                Storage.userData.getLinearMotionExp().getMotionPeriods().add(period);
                period = null;
                return;
            }
            if (GUIApp.shift) {
                if (nearest != null) {
                    double x1 = nearest.getOriginX() - x;
                    double x2 = nearest.getTargetX() - x;
                    double y1 = nearest.getOriginY() - y;
                    double y2 = nearest.getTargetY() - y;
                    double dst1 = Math.sqrt(x1 * x1 + y1 * y1);
                    double dst2 = Math.sqrt(x2 * x2 + y2 * y2);
                    if (dst1 <= 100 && dst2 <= 100) {
                        if (dst1 < dst2) {
                            x = nearest.getOriginX();
                            y = nearest.getOriginY();
                        } else {
                            x = nearest.getTargetX();
                            y = nearest.getTargetY();
                        }
                    } else if (dst1 <= 100) {
                        x = nearest.getOriginX();
                        y = nearest.getOriginY();
                    } else if (dst2 <= 100) {
                        x = nearest.getTargetX();
                        y = nearest.getTargetY();
                    }
                }
                period = new MotionPeriod(x, y, x, y, 1);
            } else if (nearest != null) {
                switchSelection();
            }
        });
        canvas.setOnMouseMoved(mouseEvent -> {
            int x = (int) mouseEvent.getX();
            int y = (int) mouseEvent.getY();
            if (period != null) {
                clear(ctx);
                draw(ctx, period, x, y);
                ctx.beginPath();
                ctx.fillText(String.format("Góc: %.2f độ", Math.toDegrees(new Vector(period.getOriginX(), period.getOriginY(), x, y).getAngle())), 20, 30);
                ctx.fill();
                ctx.closePath();
            } else if (!preventSelect) {
                MotionPeriod lastNearest = nearest;
                double minDst = -1;
                for (MotionPeriod p : Storage.userData.getLinearMotionExp().getMotionPeriods()) {
                    // nx = -uy
                    int a = -(p.getTargetY() - p.getOriginY());
                    // ny = ux
                    int b = p.getTargetX() - p.getOriginX();
                    // c = -a*x-b*y
                    int c = -a * p.getOriginX() - b * p.getOriginY();
                    // axo + byo + c
                    double f = a * x + b * y + c;
                    double len = Math.sqrt(a * a + b * b);
                    double dst = Math.abs(f) / len;
                    if (minDst != -1 && dst >= minDst) {
                        continue;
                    }
                    nearest = p;
                    minDst = dst;
                }
                if (nearest != lastNearest) {
                    clear(ctx);
                } else {
                    return;
                }
            } else {
                clear(ctx);
            }
            renderRoads(ctx);
        });
        friction.setOnKeyPressed(keyEvent -> {
            if (keyEvent.getCode().getName().equals("Enter")) save();
        });
        weight.setOnKeyPressed(keyEvent -> {
            if (keyEvent.getCode().getName().equals("Enter")) save();
        });
        traction.setOnKeyPressed(keyEvent -> {
            if (keyEvent.getCode().getName().equals("Enter")) save();
        });
        originSpeed.setOnKeyPressed(keyEvent -> {
            if (keyEvent.getCode().getName().equals("Enter")) save();
        });
    }
}
