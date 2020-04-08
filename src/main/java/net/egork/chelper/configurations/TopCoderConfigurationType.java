package net.egork.chelper.configurations;

import com.intellij.execution.configurations.ConfigurationFactory;
import com.intellij.execution.configurations.ConfigurationType;
import com.intellij.execution.configurations.RunConfiguration;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.IconLoader;
import net.egork.chelper.task.TopCoderTask;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;

/**
 * @author Egor Kulikov (kulikov@devexperts.com)
 */
public class TopCoderConfigurationType implements ConfigurationType {

    private static TopCoderConfigurationType INSTANCE;

    private static final Icon ICON = IconLoader.getIcon("/icons/topcoder.png");
    private final ConfigurationFactory factory;

    private TopCoderConfigurationType() {
        factory = new ConfigurationFactory(this) {
            @NotNull
            @Override
            public RunConfiguration createTemplateConfiguration(@NotNull Project project) {
                return new TopCoderConfiguration("TopCoderTask", project,
                        new TopCoderTask("TopCoderTask", null, null, "", "", new String[0], null, false, "256M"), factory);
            }
        };
    }

    @Override
    @NotNull
    public String getDisplayName() {
        return "TopCoder Task";
    }

    @Override
    public String getConfigurationTypeDescription() {
        return "CHelper TopCoder Task";
    }

    @Override
    public Icon getIcon() {
        return ICON;
    }

    @Override
    @NotNull
    public String getId() {
        return "TopCoderTask";
    }

    public ConfigurationFactory getConfigurationFactory() {
        return factory;
    }

    @Override
    public ConfigurationFactory[] getConfigurationFactories() {
        return new ConfigurationFactory[]{factory};
    }

    public static TopCoderConfigurationType getInstance() {
        if (INSTANCE == null) INSTANCE = new TopCoderConfigurationType();
        return INSTANCE;
    }
}
