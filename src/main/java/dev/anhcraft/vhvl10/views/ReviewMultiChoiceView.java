package dev.anhcraft.vhvl10.views;

import dev.anhcraft.neep.struct.NeepElement;
import dev.anhcraft.neep.struct.container.NeepList;
import dev.anhcraft.neep.struct.container.NeepSection;
import dev.anhcraft.neep.struct.primitive.NeepInt;
import dev.anhcraft.neep.struct.primitive.NeepString;
import dev.anhcraft.vhvl10.gui.Prompt;
import dev.anhcraft.vhvl10.objects.test.TestRecord;
import dev.anhcraft.vhvl10.objects.test.TestSession;
import dev.anhcraft.vhvl10.resources.Config;
import dev.anhcraft.vhvl10.resources.Storage;
import dev.anhcraft.vhvl10.tasks.TaskManager;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.VBox;
import org.apache.commons.lang3.time.DurationFormatUtils;

import java.net.URL;
import java.util.Objects;
import java.util.ResourceBundle;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public class ReviewMultiChoiceView implements Initializable {
    private int answer;
    private ScheduledFuture<?> task;

    @FXML
    public Label question;
    @FXML
    public Label score;
    @FXML
    public Label questionCounter;
    @FXML
    public Label timeCounter;
    @FXML
    private VBox choices;

    private synchronized void end() {
        TestSession ts = Objects.requireNonNull(Storage.userData.getTestSession());
        TestRecord testRecord = new TestRecord();
        testRecord.setDate(ts.getTimeStart());
        testRecord.setTime(System.currentTimeMillis() - ts.getTimeStart());
        testRecord.setScore(ts.getScore());
        Storage.userData.setTestSession(null);
        Storage.userData.getTestRecords().add(testRecord);
        Prompt.info("Bạn đã đạt được " + ts.getScore() + " điểm!");
        quit();
    }

    @SuppressWarnings("unchecked")
    private synchronized void resetView() {
        if (Storage.userData.getTestSession() == null) return;
        choices.getChildren().clear();
        TestSession ts = Objects.requireNonNull(Storage.userData.getTestSession());
        int q = ts.getCurrentQuestion();
        NeepList<NeepElement> list = (NeepList<NeepElement>) Objects.requireNonNull(Config.REVIEW.getList("multi_choices"));
        NeepSection section = list.get(q).asSection();
        answer = ((NeepInt) Objects.requireNonNull(section.get("answer"))).getValueAsInt();
        question.setText(breakText(Objects.requireNonNull(section.get("question")).asPrimitive().stringifyValue(), 40));
        NeepList<NeepString> choiceList = (NeepList<NeepString>) Objects.requireNonNull(section.get("choices")).asList();
        ToggleGroup group = new ToggleGroup();
        int i = 0;
        for (NeepString string : choiceList) {
            RadioButton btn = new RadioButton(breakText(string.getValue(), 50));
            btn.setToggleGroup(group);
            btn.setFocusTraversable(false);
            btn.getProperties().put("answerId", i++);
            btn.selectedProperty().addListener((observableValue, aBoolean, t1) -> {
                TaskManager.runDelayAsync(() -> {
                    TaskManager.runSync(() -> {
                        if (ts.nextQuestion()) {
                            int x = (Integer) btn.getProperties().get("answerId");
                            if (x == answer) {
                                ts.setScore(ts.getScore() + 10);
                            }
                            resetView();
                        } else {
                            end();
                        }
                    });
                }, 1, TimeUnit.SECONDS);
            });
            choices.getChildren().add(btn);
        }
        questionCounter.setText("Câu " + ts.getQuestionCount() + " / " + ts.getQuestionList().size());
        score.setText(ts.getScore() + " điểm");
    }

    private String breakText(String x, int m) {
        StringBuilder stringBuilder = new StringBuilder();
        int i = 0;
        for (char c : x.toCharArray()) {
            if (i >= m && c == ' ') {
                stringBuilder.append("\n");
                i = 0;
                continue;
            }
            stringBuilder.append(c);
            i++;
        }
        return stringBuilder.toString();
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        resetView();
        task = TaskManager.runRepeatAsync(() -> {
            TestSession ts = Objects.requireNonNull(Storage.userData.getTestSession());
            if (System.currentTimeMillis() > ts.getTimeEnd()) {
                TaskManager.runSync(this::end);
                return;
            }
            String time = DurationFormatUtils.formatDuration(Math.max(0, ts.getTimeEnd() - System.currentTimeMillis()), "HH:mm:ss");
            TaskManager.runSync(() -> timeCounter.setText(time));
        }, 0, 1, TimeUnit.SECONDS);
    }

    @FXML
    public void quit() {
        if (task != null) {
            task.cancel(true);
        }
        View.REVIEW_MENU.load();
    }
}
