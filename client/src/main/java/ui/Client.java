package ui;

import exception.ResponseException;

public interface Client {
    /**
     * Evaluates the input command and returns the result as a String.
     *
     * @param input The command input to evaluate.
     * @return The result of the command.
     */
    String eval(String input);

    /**
     * Clears the server or session state.
     *
     * @return Message indicating the clear operation was successful.
     * @throws ResponseException if the clear operation fails.
     */
    String clear() throws ResponseException;

    /**
     * Provides help information for using the client commands.
     *
     * @return Help message as a String.
     */
    String help();

    /**
     * Provides help information for using the client commands.
     *
     * @return Start message as a String.
     */
    String startMsg();
}
