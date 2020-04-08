package net.egork.chelper.actions;

import com.intellij.execution.RunnerAndConfigurationSettings;
import com.intellij.execution.configurations.RunConfiguration;
import com.intellij.execution.impl.RunManagerImpl;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.project.Project;
import net.egork.chelper.configurations.TaskConfiguration;
import net.egork.chelper.configurations.TopCoderConfiguration;
import net.egork.chelper.task.Task;
import net.egork.chelper.task.TopCoderTask;
import net.egork.chelper.ui.CreateTaskDialog;
import net.egork.chelper.ui.EditTCDialog;
import net.egork.chelper.util.FileUtilities;
import net.egork.chelper.util.Utilities;
import org.jetbrains.annotations.NotNull;

/**
 * @author egorku@yandex-team.ru
 */
public class EditTask extends TaskBasedAction {
	@Override
	public void taskActionPerformed(@NotNull AnActionEvent e, TaskConfiguration configuration) {
		Project project = Utilities.getProject(e.getDataContext());
		Task task = configuration.getConfiguration();
		task = CreateTaskDialog.showDialog(
			FileUtilities.getPsiDirectory(project, task.location), task.name, task, false);
		if (task != null) configuration.setConfiguration(task);
	}

	@Override
	public void topCoderActionPerformed(@NotNull AnActionEvent e, TopCoderConfiguration configuration) {
		Project project = Utilities.getProject(e.getDataContext());
		TopCoderTask task = configuration.getConfiguration();
		configuration.setConfiguration(EditTCDialog.show(project, task));
	}
}
