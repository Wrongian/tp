package seedu.address.logic.parser;

import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseFailure;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseSuccess;
import static seedu.address.testutil.TypicalIndexes.INDEX_FIRST_PERSON;
import static seedu.address.testutil.TypicalIndexes.INDEX_SECOND_PERSON;
import static seedu.address.testutil.TypicalIndexes.INDEX_THIRD_PERSON;

import java.util.Set;

import org.junit.jupiter.api.Test;

import seedu.address.logic.commands.DeleteCommand;

/**
 * As we are only doing white-box testing, our test cases do not cover path variations
 * outside of the DeleteCommand code. For example, inputs "1" and "1 abc" take the
 * same path through the DeleteCommand, and therefore we test only one of them.
 * The path variation for those two cases occur inside the ParserUtil, and
 * therefore should be covered by the ParserUtilTest.
 */
public class DeleteCommandParserTest {

    private DeleteCommandParser parser = new DeleteCommandParser();

    @Test
    public void parse_validArgs_returnsDeleteCommand() {
        assertParseSuccess(parser, "1", new DeleteCommand(INDEX_FIRST_PERSON));
    }

    @Test
    public void parse_invalidArgs_throwsParseException() {
        assertParseFailure(parser, "a", String.format(MESSAGE_INVALID_COMMAND_FORMAT, DeleteCommand.MESSAGE_USAGE));
        assertParseFailure(parser, "i/123", String.format(MESSAGE_INVALID_COMMAND_FORMAT, DeleteCommand.MESSAGE_USAGE));
    }

    @Test
    public void parse_validMassOpsArgs_returnsDeleteCommand() {
        DeleteCommand expectedCommand = new DeleteCommand(
                Set.of(INDEX_FIRST_PERSON, INDEX_SECOND_PERSON, INDEX_THIRD_PERSON));

        // EP: valid spaced format
        assertParseSuccess(parser, " i/1 2 3", expectedCommand);
        // EP: valid spaced format with trailing
        assertParseSuccess(parser, " i/ 1 2 3  ", expectedCommand);
        // EP: valid range format
        assertParseSuccess(parser, " i/1-3 ", expectedCommand);
        // EP: valid spaced format single index
        assertParseSuccess(parser, " i/3 ", new DeleteCommand(INDEX_THIRD_PERSON));
    }

    @Test
    public void parse_invalidMassOpsArgs_throwsException() {
        assertParseFailure(parser, " i/12- 2 3 4",
                String.format(MESSAGE_INVALID_COMMAND_FORMAT, DeleteCommand.MESSAGE_USAGE));
        assertParseFailure(parser, " i/1-3-4",
                String.format(MESSAGE_INVALID_COMMAND_FORMAT, DeleteCommand.MESSAGE_USAGE));
        assertParseFailure(parser, " i/1-3 4",
                String.format(MESSAGE_INVALID_COMMAND_FORMAT, DeleteCommand.MESSAGE_USAGE));
        assertParseFailure(parser, " i/1-3 4-5",
                String.format(MESSAGE_INVALID_COMMAND_FORMAT, DeleteCommand.MESSAGE_USAGE));

        // out-of-range index
        assertParseFailure(parser, " i/1243474758943-234875783495789345",
                String.format(MESSAGE_INVALID_COMMAND_FORMAT, DeleteCommand.MESSAGE_USAGE));
    }

    @Test
    public void parse_extraPrefixesMassOpsArgs_throwsException() {
        // EP: extra prefix after i/
        assertParseFailure(parser, " i/1 2 3 t/4",
                String.format(MESSAGE_INVALID_COMMAND_FORMAT, DeleteCommand.MESSAGE_USAGE));

        // EP: extra prefix before i/
        assertParseFailure(parser, " asdkjp/4 i/1 2 3",
                String.format(MESSAGE_INVALID_COMMAND_FORMAT, DeleteCommand.MESSAGE_USAGE));

        // EP: multiple extra prefixes before i/
        assertParseFailure(parser, " a/asdjkasd asdkjp/4 i/1-2",
                String.format(MESSAGE_INVALID_COMMAND_FORMAT, DeleteCommand.MESSAGE_USAGE));
    }

    @Test
    public void parse_extraPrefixesSingleDelete_throwsException() {
        // EP: extra prefix after INDEX
        assertParseFailure(parser, " p/askdsakdj 1",
                String.format(MESSAGE_INVALID_COMMAND_FORMAT, DeleteCommand.MESSAGE_USAGE));

        // EP: extra prefix before INDEX
        assertParseFailure(parser, " 1 p/asjdk",
                String.format(MESSAGE_INVALID_COMMAND_FORMAT, DeleteCommand.MESSAGE_USAGE));
    }
}
