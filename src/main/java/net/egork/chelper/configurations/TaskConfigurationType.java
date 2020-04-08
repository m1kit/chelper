package net.egork.chelper.configurations;

import com.intellij.execution.configurations.ConfigurationFactory;
import com.intellij.execution.configurations.ConfigurationType;
import com.intellij.execution.configurations.RunConfiguration;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.IconLoader;
import net.egork.chelper.util.Utilities;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;

/**
 * @author Egor Kulikov (kulikov@devexperts.com)
 */
public class TaskConfigurationType implements ConfigurationType {

    private static TaskConfigurationType INSTANCE;

    private static final Icon ICON = IconLoader.getIcon("/icons/taskIcon.png");
    private final ConfigurationFactory factory;

    private TaskConfigurationType() {
        factory = new ConfigurationFactory(this) {
            @NotNull
            @Override
            public RunConfiguration createTemplateConfiguration(@NotNull Project project) {
                return new TaskConfiguration("Task", project, Utilities.getDefaultTask(), factory);
            }
        };
    }

    @Override
    @NotNull
    public String getDisplayName() {
        return "Task";
    }

    @Override
    public String getConfigurationTypeDescription() {
        return "CHelper Task";
    }

    @Override
    public Icon getIcon() {
        return ICON;
    }

    @Override
    @NotNull
    public String getId() {
        return "Task";
    }

    public ConfigurationFactory getConfigurationFactory() {
        return factory;
    }

    @Override
    public ConfigurationFactory[] getConfigurationFactories() {
        return new ConfigurationFactory[]{factory};
    }

    public static TaskConfigurationType getInstance() {
        if (INSTANCE == null) INSTANCE = new TaskConfigurationType();
        return INSTANCE;
    }
}
