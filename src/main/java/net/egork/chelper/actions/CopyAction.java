package net.egork.chelper.actions;

import com.intellij.execution.RunnerAndConfigurationSettings;
import com.intellij.execution.configurations.RunConfiguration;
import com.intellij.execution.impl.RunManagerImpl;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import net.egork.chelper.configurations.TaskConfiguration;
import net.egork.chelper.task.Task;
import net.egork.chelper.util.FileUtilities;
import net.egork.chelper.util.Utilities;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.awt.datatransfer.StringSelection;

/**
 * @author Egor Kulikov (kulikov@devexperts.com)
 */
public class CopyAction extends TaskBasedAction {
	@Override
	public void taskActionPerformed(@NotNull AnActionEvent e, TaskConfiguration configuration) {
		Task task = configuration.getConfiguration();
		Project project = Utilities.getProject(e.getDataContext());
		VirtualFile file = FileUtilities.getFile(project, Utilities.getData(project).outputDirectory + "/" + task.mainClass + ".java");
		if (file == null) {
			return;
		}
		String content = FileUtilities.readTextFile(file);
		if (content == null) {
			return;
		}
		StringSelection selection = new StringSelection(content);
		Toolkit.getDefaultToolkit().getSystemClipboard().setContents(selection, selection);
	}
}
