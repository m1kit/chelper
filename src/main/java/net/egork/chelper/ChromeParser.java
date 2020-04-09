package net.egork.chelper;

import com.intellij.notification.NotificationType;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.components.ProjectComponent;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.WindowManager;
import net.egork.chelper.actions.NewTaskDefaultAction;
import net.egork.chelper.parser.*;
import net.egork.chelper.task.Task;
import net.egork.chelper.util.ExecuteUtils;
import net.egork.chelper.util.Messenger;
import net.egork.chelper.util.Utilities;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.function.Supplier;

/**
 * @author egorku@yandex-team.ru
 */
public class ChromeParser implements ProjectComponent {
	private static final int PORT = 4243;


	private final Project project;
	private ServerSocket serverSocket;

	public ChromeParser(Project project) {
		this.project = project;
	}

	@Override
	public void initComponent() {
		// TODO: insert component initialization logic here
	}

	@Override
	public void disposeComponent() {
		// TODO: insert component disposal logic here
	}

	@Override
	@NotNull
	public String getComponentName() {
		return "ChromeParser";
	}

	@Override
	public void projectOpened() {
		if (ProjectData.load(project) == null) {
			return;
		}
		try {
			serverSocket = new ServerSocket(PORT);
			ApplicationManager.getApplication().executeOnPooledThread(() -> {
				while (!serverSocket.isClosed()) {
					try (Socket socket = serverSocket.accept()) {
						BufferedReader reader = new BufferedReader(
							new InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8));
						while (!reader.readLine().isEmpty()) ;
						final String type = reader.readLine();
						StringBuilder builder = new StringBuilder();
						String s;
						while ((s = reader.readLine()) != null)
							builder.append(s).append('\n');
						final String page = builder.toString();
						ExecuteUtils.executeStrictWriteAction(() -> {
							try {
								TaskParser parser = TaskParser.valueOf(type.toUpperCase());
								Collection<Task> tasks = parser.getInstance().parse(page);
								if (tasks.isEmpty()) {
									Messenger.publishMessage("Unable to parse task from " + type, NotificationType.WARNING);
									return;
								}
								JFrame projectFrame = WindowManager.getInstance().getFrame(project);
								if (projectFrame.getState() == JFrame.ICONIFIED) {
									projectFrame.setState(Frame.NORMAL);
								}
								tasks.forEach(task -> task.setTemplate(Utilities.getDefaultTask().template));
								for (Task task : tasks) {
									ApplicationManager.getApplication().invokeLater(
										() -> NewTaskDefaultAction.createTaskInDefaultDirectory(project, task)
									);
								}
							} catch(IllegalArgumentException ex) {
								Messenger.publishMessage("Unknown task type from Chrome parser: " + type,
									NotificationType.WARNING);
								System.err.println(page);
							}
						});
					} catch (Throwable ignored) {
					}
				}
			});
		} catch (IOException e) {
			Messenger.publishMessage("Could not create serverSocket for Chrome parser, probably another CHelper-" +
				"eligible project is running?", NotificationType.ERROR);
		}
	}

	@Override
	public void projectClosed() {
		if (serverSocket != null) {
			try {
				serverSocket.close();
			} catch (IOException ignored) {
			}
		}
	}

	public static void checkInstalled(Project project, ProjectData configuration) {
		if (!configuration.extensionProposed) {
			JPanel panel = new JPanel(new BorderLayout(15, 15));
			JLabel description = new JLabel("<html>You can now use new CHelper extension to parse<br>" +
				"tasks directly from Google Chrome<br>(currently supported - Yandex.Contest, Codeforces and HackerRank)<br><br>Do you want to install it?</html>");
			JButton download = new JButton("Download");
			JButton close = new JButton("Close");
			JPanel buttonPanel = new JPanel(new BorderLayout());
			buttonPanel.add(download, BorderLayout.WEST);
			buttonPanel.add(close, BorderLayout.EAST);
			panel.add(buttonPanel, BorderLayout.SOUTH);
			panel.add(description, BorderLayout.CENTER);
			final JDialog dialog = new JDialog();
			close.addActionListener(e -> dialog.setVisible(false));
			download.addActionListener(e -> {
				if (Desktop.isDesktopSupported()) {
					try {
						Desktop.getDesktop().browse(new URL("https://chrome.google.com/webstore/detail/chelper-extension/eicjndbmlajfjdhephbcjdeegmmoadip").toURI());
					} catch (IOException | URISyntaxException ignored) {
					}
				}
				dialog.setVisible(false);
			});
			panel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
			dialog.setContentPane(panel);
			dialog.pack();
			Point center = Utilities.getLocation(project, panel.getSize());
			dialog.setLocation(center);
			dialog.setVisible(true);
			configuration.completeExtensionProposal(project);
		}
	}

	private enum TaskParser {
		JSON(JSONParser::new),
		;
		private final Supplier<Parser> constructor;
		private Parser parser;

		TaskParser(Supplier<Parser> constructor) {
			this.constructor = constructor;
		}

		public Parser getInstance() {
			if (parser == null) parser = constructor.get();
			return parser;
		}
	}
}
