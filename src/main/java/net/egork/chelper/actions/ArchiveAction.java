package net.egork.chelper.actions;

import com.intellij.execution.RunManager;
import com.intellij.execution.configurations.RunConfiguration;
import com.intellij.execution.impl.RunManagerImpl;
import com.intellij.execution.impl.RunnerAndConfigurationSettingsImpl;
import com.intellij.notification.NotificationType;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VfsUtil;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.JavaPsiFacade;
import com.intellij.psi.PsiElement;
import com.intellij.psi.search.GlobalSearchScope;
import net.egork.chelper.codegeneration.CodeGenerationUtilities;
import net.egork.chelper.configurations.TaskConfiguration;
import net.egork.chelper.configurations.TaskConfigurationType;
import net.egork.chelper.configurations.TopCoderConfiguration;
import net.egork.chelper.configurations.TopCoderConfigurationType;
import net.egork.chelper.task.Task;
import net.egork.chelper.task.TopCoderTask;
import net.egork.chelper.util.FileUtilities;
import net.egork.chelper.util.Messenger;
import net.egork.chelper.util.TaskUtilities;
import net.egork.chelper.util.Utilities;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.List;

/**
 * @author Egor Kulikov (kulikov@devexperts.com)
 */
public class ArchiveAction extends TaskBasedAction {
	@Override
	public void taskActionPerformed(@NotNull AnActionEvent e, TaskConfiguration configuration) {
        final Task task = configuration.getConfiguration();
        final Project project = Utilities.getProject(e.getDataContext());
        final RunManagerImpl manager = RunManagerImpl.getInstanceImpl(project);
        String archiveDir = Utilities.getData(project).archiveDirectory;
        String dateAndContest = getDateAndContest(task);
        final VirtualFile directory = FileUtilities.createDirectoryIfMissing(project, archiveDir + "/" + dateAndContest);
        if (directory == null) {
            Messenger.publishMessage("Cannot create directory '" + archiveDir + "/" + dateAndContest + "' in archive",
                NotificationType.ERROR);
            return;
        }
        CodeGenerationUtilities.createUnitTest(task, project);
        ApplicationManager.getApplication().runWriteAction(new Runnable() {
            public void run() {
                try {
                    VirtualFile mainFile = FileUtilities.getFileByFQN(task.taskClass, project);
                    if (mainFile != null) {
                        VfsUtil.copyFile(this, mainFile, directory);
                        mainFile.delete(this);
                    }
                    PsiElement checker = JavaPsiFacade.getInstance(project).findClass(task.checkerClass, GlobalSearchScope.allScope(project));
                    VirtualFile checkerFile = checker == null ? null : checker.getContainingFile() == null ? null : checker.getContainingFile().getVirtualFile();
                    if (checkerFile != null && mainFile != null && checkerFile.getParent().equals(mainFile.getParent())) {
                        VfsUtil.copyFile(this, checkerFile, directory);
                        checkerFile.delete(this);
                    }
                    PsiElement interactor = task.interactor == null ? null : JavaPsiFacade.getInstance(project).findClass(task.interactor, GlobalSearchScope.allScope(project));
                    VirtualFile interactorFile = interactor == null ? null : interactor.getContainingFile() == null ? null : interactor.getContainingFile().getVirtualFile();
                    if (interactorFile != null && mainFile != null && interactorFile.getParent().equals(mainFile.getParent())) {
                        VfsUtil.copyFile(this, interactorFile, directory);
                        interactorFile.delete(this);
                    }
                    for (String testClass : task.testClasses) {
                        VirtualFile testFile = FileUtilities.getFileByFQN(testClass, project);
                        if (testFile != null) {
                            VfsUtil.copyFile(this, testFile, directory);
                            testFile.delete(this);
                        }
                    }
                    VirtualFile taskFile = FileUtilities.getFile(project, TaskUtilities.getTaskFileLocation(task.location, task.name));
                    if (taskFile != null) {
                        VfsUtil.copyFile(this, taskFile, directory);
                        taskFile.delete(this);
                    }
                    manager.removeConfiguration(manager.getSelectedConfiguration());
                    setOtherConfiguration(manager, task);
                    Messenger.publishMessage("Configuration '" + configuration.getName() + "' successfully archived",
                        NotificationType.INFORMATION);
                } catch (IOException e) {
                    Messenger.publishMessage("Error archiving configuration '" + configuration.getName() +
                        "' caused by " + e.getMessage(), NotificationType.ERROR);
                    Messenger.publishMessage("Configuration not deleted", NotificationType.WARNING);
                }
            }
        });
	}

	@Override
	public void topCoderActionPerformed(@NotNull AnActionEvent e, TopCoderConfiguration configuration) {
		final TopCoderTask task = configuration.getConfiguration();
		Project project = Utilities.getProject(e.getDataContext());
		final RunManagerImpl manager = RunManagerImpl.getInstanceImpl(project);
		String archiveDir = Utilities.getData(project).archiveDirectory;
		String dateAndContest = getDateAndContest(task);
		final VirtualFile directory = FileUtilities.createDirectoryIfMissing(project, archiveDir + "/" + dateAndContest);
		if (directory == null) {
			return;
		}
		CodeGenerationUtilities.createUnitTest(task, project);
		ApplicationManager.getApplication().runWriteAction(new Runnable() {
			public void run() {
				try {
					VirtualFile mainFile = FileUtilities.getFile(project, Utilities.getData(project).defaultDirectory
						+ "/" + task.name + ".java");
					if (mainFile != null) {
						VfsUtil.copyFile(this, mainFile, directory);
						mainFile.delete(this);
					}
					for (String testClass : task.testClasses) {
						VirtualFile testFile = FileUtilities.getFileByFQN(testClass, project);
						if (testFile != null) {
							VfsUtil.copyFile(this, testFile, directory);
							testFile.delete(this);
						}
					}
					VirtualFile taskFile = FileUtilities.getFile(project, TaskUtilities.getTaskFileLocation(Utilities.getData(project).defaultDirectory, task.name));
					if (taskFile != null) {
						VfsUtil.copyFile(this, taskFile, directory);
						taskFile.delete(this);
					}
					manager.removeConfiguration(manager.getSelectedConfiguration());
					setOtherConfiguration(manager, task);
					Messenger.publishMessage("Configuration " + configuration.getName() + " successfully archived",
						NotificationType.INFORMATION);
				} catch (IOException e) {
					Messenger.publishMessage("Error archiving configuration '" + configuration.getName() +
						"' caused by " + e.getMessage(), NotificationType.ERROR);
					Messenger.publishMessage("Configuration not deleted", NotificationType.WARNING);
				}
			}
		});
	}

	private String getDateAndContest(TopCoderTask task) {
		String yearAndMonth = task.date;
		int position = yearAndMonth.indexOf('.');
		if (position != -1) {
			position = yearAndMonth.indexOf('.', position + 1);
		}
		if (position != -1) {
			yearAndMonth = yearAndMonth.substring(0, position);
		}
		return TaskUtilities.canonize(yearAndMonth) + "/" + TaskUtilities.canonize(task.date + " - " + (task.contestName.length() == 0 ? "unsorted" : task.contestName));
	}

	private String getDateAndContest(Task task) {
		String yearAndMonth = task.date.trim();
		int position = yearAndMonth.indexOf('.');
		if (position != -1) {
			position = yearAndMonth.indexOf('.', position + 1);
		}
		if (position != -1) {
			yearAndMonth = yearAndMonth.substring(0, position);
		}
		if (yearAndMonth.length() == 0) {
			return TaskUtilities.canonize(task.contestName.length() == 0 ? "unsorted" : task.contestName);
		}
		return TaskUtilities.canonize(yearAndMonth) + "/" + TaskUtilities.canonize(task.date + " - " + (task.contestName.length() == 0 ? "unsorted" : task.contestName));
	}

	@Deprecated
	public static void setOtherConfiguration(RunManagerImpl manager, Task task) {
		RunConfiguration[] allConfigurations = manager.getAllConfigurations();
		for (RunConfiguration configuration : allConfigurations) {
			if (configuration instanceof TaskConfiguration) {
				Task other = ((TaskConfiguration) configuration).getConfiguration();
				if (!task.contestName.equals(other.contestName)) {
					continue;
				}
				manager.setActiveConfiguration(new RunnerAndConfigurationSettingsImpl(manager, configuration, false));
				return;
			}
		}
		for (RunConfiguration configuration : allConfigurations) {
			if (configuration instanceof TaskConfiguration || configuration instanceof TopCoderConfiguration) {
				manager.setActiveConfiguration(new RunnerAndConfigurationSettingsImpl(manager, configuration, false));
				return;
			}
		}
	}

	public static void setOtherConfiguration(RunManager manager, Task task) {
		List<RunConfiguration> allConfigurations = manager.getAllConfigurationsList();
		for (RunConfiguration configuration : allConfigurations) {
			if (configuration instanceof TaskConfiguration) {
				Task other = ((TaskConfiguration) configuration).getConfiguration();
				if (!task.contestName.equals(other.contestName)) {
					continue;
				}
				manager.setSelectedConfiguration(manager.createConfiguration(configuration, TaskConfigurationType.getInstance().getConfigurationFactory()));
				return;
			}
		}
		for (RunConfiguration configuration : allConfigurations) {
			if (configuration instanceof TaskConfiguration || configuration instanceof TopCoderConfiguration) {
				manager.setSelectedConfiguration(manager.createConfiguration(configuration, TopCoderConfigurationType.getInstance().getConfigurationFactory()));
				return;
			}
		}
	}

	public static void setOtherConfiguration(RunManagerImpl manager, TopCoderTask task) {
		List<RunConfiguration> allConfigurations = manager.getAllConfigurationsList();
		for (RunConfiguration configuration : allConfigurations) {
			if (configuration instanceof TopCoderConfiguration) {
				TopCoderTask other = ((TopCoderConfiguration) configuration).getConfiguration();
				if (!task.contestName.equals(other.contestName)) {
					continue;
				}
				manager.setSelectedConfiguration(new RunnerAndConfigurationSettingsImpl(manager, configuration, false));
				return;
			}
		}
		for (RunConfiguration configuration : allConfigurations) {
			if (configuration instanceof TaskConfiguration || configuration instanceof TopCoderConfiguration) {
				manager.setSelectedConfiguration(new RunnerAndConfigurationSettingsImpl(manager, configuration, false));
				return;
			}
		}
	}
}
