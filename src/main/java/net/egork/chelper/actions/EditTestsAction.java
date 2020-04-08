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
import net.egork.chelper.ui.EditTestsDialog;
import net.egork.chelper.ui.TopCoderEditTestsDialog;
import net.egork.chelper.util.Utilities;
import org.jetbrains.annotations.NotNull;

/**
 * @author Egor Kulikov (kulikov@devexperts.com)
 */
public class EditTestsAction extends TaskBasedAction {
	@Override
	public void taskActionPerformed(@NotNull AnActionEvent e, TaskConfiguration configuration) {
		Project project = Utilities.getProject(e.getDataContext());
		Task task = configuration.getConfiguration();
		configuration.setConfiguration(task.setTests(EditTestsDialog.editTests(task.tests, project)));
	}

	@Override
	public void topCoderActionPerformed(@NotNull AnActionEvent e, TopCoderConfiguration configuration) {
		Project project = Utilities.getProject(e.getDataContext());
		TopCoderTask task = configuration.getConfiguration();
		configuration.setConfiguration(task.setTests(TopCoderEditTestsDialog.editTests(task, project)));
	}
}
