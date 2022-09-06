package dev.anhcraft.vhvl10.objects;

import dev.anhcraft.config.annotations.Configurable;
import dev.anhcraft.config.annotations.Path;
import dev.anhcraft.config.annotations.Setting;
import dev.anhcraft.config.annotations.Validation;
import dev.anhcraft.vhvl10.objects.lab.LinearMotionExp;
import dev.anhcraft.vhvl10.objects.lab.ProjectileMotionExp;
import dev.anhcraft.vhvl10.objects.test.TestRecord;
import dev.anhcraft.vhvl10.objects.test.TestSession;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

@Configurable
public class UserData {
    @Setting
    @Path("test_records")
    @Validation(notNull = true, silent = true)
    private List<TestRecord> testRecords = new ArrayList<>();

    @Setting
    @Path("test_session")
    private TestSession testSession;

    @Setting
    @Path("lab_linear_motion")
    @Validation(notNull = true, silent = true)
    private LinearMotionExp linearMotionExp = new LinearMotionExp();

    @Setting
    @Path("lab_projectile_motion")
    @Validation(notNull = true, silent = true)
    private ProjectileMotionExp projectileMotionExp = new ProjectileMotionExp();

    @Setting
    @Path("wiki_slider_index")
    private int sliderIndex;

    @NotNull
    public List<TestRecord> getTestRecords() {
        return testRecords;
    }

    @Nullable
    public TestSession getTestSession() {
        return testSession;
    }

    public void setTestSession(@Nullable TestSession testSession) {
        this.testSession = testSession;
    }

    @NotNull
    public LinearMotionExp getLinearMotionExp() {
        return linearMotionExp;
    }

    @NotNull
    public ProjectileMotionExp getProjectileMotionExp() {
        return projectileMotionExp;
    }

    public int getSliderIndex() {
        return sliderIndex;
    }

    public void setSliderIndex(int sliderIndex) {
        this.sliderIndex = sliderIndex;
    }
}
