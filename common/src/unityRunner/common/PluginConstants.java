package unityRunner.common;

import org.jetbrains.annotations.NonNls;

import java.util.Arrays;
import java.util.List;

public interface PluginConstants {
    @NonNls final String RUN_TYPE = "unityRunner";
    @NonNls final String RUNNER_DISPLAY_NAME = "Unity";
    final String RUNNER_DESCRIPTION = "Unity runner";

    final String PROPERTY_UNITY_EXECUTABLE_PATH = "unity.executable";
    final String PROPERTY_BATCH_MODE = "argument.batch_mode";
    final String PROPERTY_EXECUTE_METHOD = "argument.execute_method";
    final String PROPERTY_NO_GRAPHICS = "argument.no_graphics";
    final String PROPERTY_QUIT = "argument.quit";
    final String PROPERTY_PROJECT_PATH = "argument.project_path";
    final String PROPERTY_LINELIST_PATH = "argument.line_list_path";
    final String PROPERTY_BUILD_PATH = "argument.build_path";
    final String PROPERTY_BUILD_PLAYER = "argument.build_player";
    final String PROPERTY_BUILD_TARGET = "argument.build_target";
    final String PROPERTY_BUILD_EXTRA = "argument.build_extra";
    final String PROPERTY_UNITY_VERSION = "argument.unity_version";

    final String PROPERTY_UNITY_LICENSE_CONFIG_FILE_PATH = "argument.unity_license_config_file_path";

    final String PROPERTY_CLEAR_OUTPUT_BEFORE = "argument.clear_output_before";
    final String PROPERTY_CLEAN_OUTPUT_AFTER = "argument.clean_output_after";
    final String PROPERTY_WARNINGS_AS_ERRORS = "argument.warnings_as_errors";
    final String PROPERTY_ERRORS_AS_WARNINGS = "argument.errors_as_warnings";

    final String PROPERTY_LOG_PATH = "argument.log_path";
    final String PROPERTY_CREATE_CLEANED_LOG = "argument.create_cleaned_log";
    final String PROPERTY_TAIL_CLEANED_LOG = "argument.tail_cleaned_log";
    final String PROPERTY_CLEANED_LOG_PATH = "argument.cleaned_log_path";

    final String PROPERTY_LOG_IGNORE = "argument.log_ignore";
    final String PROPERTY_LOG_IGNORE_TEXT = "argument.log_ignore_text";

    final List<String> PROPERTY_BUILD_TARGETS = Arrays.asList("buildWindowsPlayer", "buildOSXPlayer!", "buildWebPlayer");

    final String REPORT_TAB_CODE = "unityReportTab";

    final String CONFIGPARAM_UNITY_BASE_VERSION = "unity.";
    final String CONFIGPARAM_UNITY_LOG_PATH = "unity.log";
    final String CONFIGPARAM_UNITY_LATEST_VERSION = "unity.latest";
}
