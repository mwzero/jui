import it.jui.JuiApp;
import it.jui.UIContext;

public class DashboardExample implements JuiApp {
    @Override
    public void run(UIContext ui) {
        ui.title("Team Dashboard", "chart-bar");
        ui.columns(
                () -> ui.metric("Users", 128),
                () -> ui.metric("Tasks", 42),
                () -> ui.metric("Completion", "87%"));
        ui.progressBar("Sprint", 87);
    }
}
