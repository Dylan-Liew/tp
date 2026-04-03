package cms.logic.commands;

import static java.util.Objects.requireNonNull;

import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import cms.model.Model;

/**
 * Format full help instructions for every command for display.
 */
public class HelpCommand extends Command {

    public static final String COMMAND_WORD = "help";

    public static final String MESSAGE_USAGE = COMMAND_WORD + ": Shows program usage instructions.\n"
            + "Parameters: [COMMAND]\n"
            + "Example: " + COMMAND_WORD + " "
            + "or " + COMMAND_WORD + " " + AddCommand.COMMAND_WORD;

    public static final String SHOWING_HELP_MESSAGE = "Opened help window.";

    private static final Map<String, String> HELP_MESSAGES = createHelpMessages();

    private final Optional<String> targetCommandWord;

    /**
     * Creates a HelpCommand that shows an overview for all supported commands.
     */
    public HelpCommand() {
        this.targetCommandWord = Optional.empty();
    }

    /**
     * Creates a HelpCommand that shows help for a specific command word.
     */
    public HelpCommand(String targetCommandWord) {
        requireNonNull(targetCommandWord);
        this.targetCommandWord = Optional.of(targetCommandWord.toLowerCase(Locale.ROOT));
    }

    private static Map<String, String> createHelpMessages() {
        Map<String, String> helpMessages = new LinkedHashMap<>();
        // Core data operations
        helpMessages.put(AddCommand.COMMAND_WORD, AddCommand.MESSAGE_USAGE);
        helpMessages.put(EditCommand.COMMAND_WORD, EditCommand.MESSAGE_USAGE);
        helpMessages.put(DeleteCommand.COMMAND_WORD, DeleteCommand.MESSAGE_USAGE);

        // Navigation and discovery
        helpMessages.put(ListCommand.COMMAND_WORD, ListCommand.MESSAGE_USAGE);
        helpMessages.put(FindCommand.COMMAND_WORD, FindCommand.MESSAGE_USAGE);
        helpMessages.put(FilterCommand.COMMAND_WORD, FilterCommand.MESSAGE_USAGE);
        helpMessages.put(SortCommand.COMMAND_WORD, SortCommand.MESSAGE_USAGE);

        // Privacy controls
        helpMessages.put(MaskCommand.COMMAND_WORD, MaskCommand.MESSAGE_USAGE);
        helpMessages.put(UnmaskCommand.COMMAND_WORD, UnmaskCommand.MESSAGE_USAGE);

        // Data interchange
        helpMessages.put(ExportCommand.COMMAND_WORD, ExportCommand.MESSAGE_USAGE);
        helpMessages.put(ImportCommand.COMMAND_WORD, ImportCommand.MESSAGE_USAGE);

        // Utility
        helpMessages.put(HelpCommand.COMMAND_WORD, HelpCommand.MESSAGE_USAGE);
        helpMessages.put(ClearCommand.COMMAND_WORD, ClearCommand.MESSAGE_USAGE);
        helpMessages.put(ExitCommand.COMMAND_WORD, ExitCommand.MESSAGE_USAGE);
        return helpMessages;
    }

    /**
     * Returns true if the command word is supported by help.
     */
    public static boolean isValidCommandWord(String commandWord) {
        requireNonNull(commandWord);
        return HELP_MESSAGES.containsKey(commandWord.toLowerCase(Locale.ROOT));
    }

    static String getAllHelpMessages() {
        String header = "Available commands (use help COMMAND for full usage):";
        return HELP_MESSAGES.values().stream()
                .map(HelpCommand::toOneLineHelp)
                .collect(Collectors.joining("\n", header + "\n", ""));
    }

    static String getHelpMessage(String commandWord) {
        requireNonNull(commandWord);
        return HELP_MESSAGES.get(commandWord.toLowerCase(Locale.ROOT));
    }

    static String toOneLineHelp(String fullHelpMessage) {
        int firstLineBreak = fullHelpMessage.indexOf('\n');
        String oneLine = firstLineBreak >= 0
                ? fullHelpMessage.substring(0, firstLineBreak)
                : fullHelpMessage;
        return "- " + oneLine;
    }

    @Override
    public CommandResult execute(Model model) {
        requireNonNull(model);
        String helpMessage = targetCommandWord
                .map(HelpCommand::getHelpMessage)
                .orElseGet(HelpCommand::getAllHelpMessages);
        return new CommandResult(SHOWING_HELP_MESSAGE, true, false, helpMessage);
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        if (!(other instanceof HelpCommand)) {
            return false;
        }

        HelpCommand otherHelpCommand = (HelpCommand) other;
        return targetCommandWord.equals(otherHelpCommand.targetCommandWord);
    }
}
