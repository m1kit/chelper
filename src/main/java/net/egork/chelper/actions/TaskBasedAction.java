package net.egork.chelper.actions;

import com.intellij.execution.RunManager;
import com.intellij.execution.RunnerAndConfigurationSettings;
import com.intellij.execution.configurations.RunConfiguration;
import com.intellij.execution.impl.RunManagerImpl;
import com.intellij.notification.Notification;
import com.intellij.notification.NotificationType;
import com.intellij.notification.Notifications;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.project.Project;
import net.egork.chelper.configurations.TaskConfiguration;
import net.egork.chelper.configurations.TopCoderConfiguration;
import net.egork.chelper.util.Messenger;
import net.egork.chelper.util.Utilities;
import org.jetbrains.annotations.NotNull;

public abstract class TaskBasedAction extends AnAction {
	@Override
	public void actionPerformed(AnActionEvent e) {
		if (!Utilities.isEligible(e.getDataContext())) {
			return;
		}
		final Project project = Utilities.getProject(e.getDataContext());
		RunnerAndConfigurationSettings selectedConfiguration = RunManager.getInstance(project).getSelectedConfiguration();
		if (selectedConfiguration == null) {
			showUnsupportedTaskHint();
			return;
		}
		RunConfiguration configuration = selectedConfiguration.getConfiguration();
		if (configuration instanceof TaskConfiguration) {
			taskActionPerformed(e, (TaskConfiguration) configuration);
		} else if (configuration instanceof TopCoderConfiguration) {
			topCoderActionPerformed(e, (TopCoderConfiguration) configuration);
		} else {
			showUnsupportedTaskHint();
		}
	}

	public void taskActionPerformed(@NotNull AnActionEvent e, TaskConfiguration configuration) {
		showUnsupportedTaskHint();
	}

	public void topCoderActionPerformed(@NotNull AnActionEvent e, TopCoderConfiguration configuration) {
		showUnsupportedTaskHint();
	}

	private void showUnsupportedTaskHint() {
		Messenger.publishMessage("Configuration not selected or selected configuration not supported", NotificationType.ERROR);
	}
}
