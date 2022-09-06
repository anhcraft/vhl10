package dev.anhcraft.vhvl10.views;

import com.sun.javafx.scene.control.skin.TableHeaderRow;
import dev.anhcraft.jvmkit.helpers.PaginationHelper;
import dev.anhcraft.jvmkit.utils.CollectionUtil;
import dev.anhcraft.neep.struct.container.NeepList;
import dev.anhcraft.vhvl10.gui.Prompt;
import dev.anhcraft.vhvl10.objects.test.TestRecord;
import dev.anhcraft.vhvl10.objects.test.TestSession;
import dev.anhcraft.vhvl10.resources.Config;
import dev.anhcraft.vhvl10.resources.Storage;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.util.Callback;
import org.apache.commons.lang3.time.DurationFormatUtils;

import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Comparator;
import java.util.Date;
import java.util.ResourceBundle;
import java.util.concurrent.ThreadLocalRandom;

public class ReviewMenuView implements Initializable {
    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");

    @FXML
    private TableView<TestRecord> records;
    @FXML
    private TableColumn<TestRecord, Long> recordDate;
    @FXML
    private TableColumn<TestRecord, Long> recordTime;
    @FXML
    private TableColumn<TestRecord, Integer> recordScore;

    @FXML
    public void start() {
        TestSession ts = Storage.userData.getTestSession();
        if (ts != null) {
            if (System.currentTimeMillis() < ts.getTimeEnd()) {
                View.REVIEW_MULTI_CHOICES.load();
                return;
            } else {
                Prompt.info("Lần kiểm tra trước đã quá hạn! Bạn phải làm lại bài mới.");
            }
        }
        NeepList<?> list = Config.REVIEW.getList("multi_choices");
        if (list != null) {
            ThreadLocalRandom random = ThreadLocalRandom.current();
            TestSession testSession = new TestSession((long) Config.REVIEW.getExpression("test_time"));
            int max = Config.REVIEW.getInt("test_questions");
            for (int i = 0; i < max; i++) {
                int x;
                do {
                    x = random.nextInt(list.size());
                } while (testSession.getQuestionList().contains(x));
                testSession.getQuestionList().add(x);
            }
            testSession.setCurrentQuestion(testSession.getQuestionList().get(0));
            testSession.setQuestionCount(1);
            Storage.userData.setTestSession(testSession);
        }
        View.REVIEW_MULTI_CHOICES.load();
    }

    @FXML
    public void quit() {
        View.MAIN_MENU.load();
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        PaginationHelper<TestRecord> testRecords = new PaginationHelper<>(CollectionUtil.toArray(Storage.userData.getTestRecords(), TestRecord.class), 30);
        records.widthProperty().addListener((source, oldWidth, newWidth) -> {
            TableHeaderRow header = (TableHeaderRow) records.lookup("TableHeaderRow");
            header.reorderingProperty().addListener((observable, oldValue, newValue) -> header.setReordering(false));
        });
        recordDate.setComparator(Comparator.reverseOrder());
        recordDate.setCellValueFactory(new PropertyValueFactory<>("date"));
        recordDate.setCellFactory(new Callback<TableColumn<TestRecord, Long>, TableCell<TestRecord, Long>>() {
            @Override
            public TableCell<TestRecord, Long> call(TableColumn<TestRecord, Long> testRecordStringTableColumn) {
                return new TableCell<TestRecord, Long>() {
                    @Override
                    protected void updateItem(Long item, boolean empty) {
                        super.updateItem(item, empty);
                        if (item == null || empty) {
                            setGraphic(null);
                        } else {
                            setGraphic(new Label(dateFormat.format(new Date(item))));
                        }
                    }
                };
            }
        });
        recordTime.setComparator(Comparator.reverseOrder());
        recordTime.setCellValueFactory(new PropertyValueFactory<>("time"));
        recordTime.setCellFactory(new Callback<TableColumn<TestRecord, Long>, TableCell<TestRecord, Long>>() {
            @Override
            public TableCell<TestRecord, Long> call(TableColumn<TestRecord, Long> testRecordStringTableColumn) {
                return new TableCell<TestRecord, Long>() {
                    @Override
                    protected void updateItem(Long item, boolean empty) {
                        super.updateItem(item, empty);
                        if (item == null || empty) {
                            setGraphic(null);
                        } else {
                            setGraphic(new Label(DurationFormatUtils.formatDuration(item, "HH:mm:ss")));
                        }
                    }
                };
            }
        });
        recordScore.setComparator(Comparator.reverseOrder());
        recordScore.setCellValueFactory(new PropertyValueFactory<>("score"));
        if (testRecords.getData().length > 0) {
            records.getItems().addAll(testRecords.collect());
        }
        records.getSortOrder().add(recordDate);
        records.sort();
    }
}
