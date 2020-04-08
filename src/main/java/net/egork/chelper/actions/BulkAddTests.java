package net.egork.chelper.actions;

import com.intellij.execution.RunnerAndConfigurationSettings;
import com.intellij.execution.configurations.RunConfiguration;
import com.intellij.execution.impl.RunManagerImpl;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.project.Project;
import net.egork.chelper.configurations.TaskConfiguration;
import net.egork.chelper.task.Task;
import net.egork.chelper.ui.BulkAddTestsDialog;
import net.egork.chelper.util.Utilities;
import org.jetbrains.annotations.NotNull;

/**
 * @author egorku@yandex-team.ru
 */
public class BulkAddTests extends TaskBasedAction {

    @Override
    public void taskActionPerformed(@NotNull AnActionEvent e, TaskConfiguration configuration) {
        Task task = configuration.getConfiguration();
        Project project = Utilities.getProject(e.getDataContext());
        configuration.setConfiguration(task.setTests(BulkAddTestsDialog.show(project, task.tests)));
    }
}
