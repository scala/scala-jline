/*
 * Copyright (c) 2002-2012, the original author or authors.
 *
 * This software is distributable under the BSD license. See the terms of the
 * BSD license in the documentation provided with this software.
 *
 * http://www.opensource.org/licenses/bsd-license.php
 */
package scala.tools.jline.console;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import scala.tools.jline.TerminalFactory;
import scala.tools.jline.WindowsTerminal;
import scala.tools.jline.console.history.History;
import scala.tools.jline.console.history.MemoryHistory;
import scala.tools.jline.internal.Configuration;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static scala.tools.jline.console.ConsoleReaderTest.WindowsKey.*;
import static org.junit.Assert.*;
import static org.junit.Assume.*;

/**
 * Tests for the {@link ConsoleReader}.
 */
public class ConsoleReaderTest
{
    
    private ByteArrayOutputStream output;
    
    @Before
    public void setUp() throws Exception {
        TerminalFactory.configure(TerminalFactory.AUTO);
        TerminalFactory.reset();
        System.setProperty(Configuration.JLINE_CONFIGURATION, "/no-such-file");
        System.setProperty(WindowsTerminal.DIRECT_CONSOLE, "false");
        System.setProperty(ConsoleReader.JLINE_INPUTRC, "/no/such/file");
        Configuration.reset();
    }

    @After
    public void tearDown() throws Exception {
        TerminalFactory.get().restore();
        TerminalFactory.reset();
    }

    private void assertWindowsKeyBehavior(String expected, char[] input) throws Exception {
        StringBuilder buffer = new StringBuilder();
        buffer.append(input);
        ConsoleReader reader = createConsole(buffer.toString());
        assertNotNull(reader);
        String line = reader.readLine();
        assertEquals(expected, line);
    }

    private ConsoleReader createConsole() throws Exception {
        return createConsole("");
    }

    private ConsoleReader createConsole(String chars) throws Exception {
        return createConsole(chars.getBytes(Configuration.getEncoding()));
    }

    private ConsoleReader createConsole(byte[] bytes) throws Exception {
        return createConsole(null, bytes);
    }

    private ConsoleReader createConsole(String appName, byte[] bytes) throws Exception {
        InputStream in = new ByteArrayInputStream(bytes);
        output = new ByteArrayOutputStream();
        ConsoleReader reader = new ConsoleReader(appName, in, output, null);
        reader.setHistory(createSeededHistory());
        return reader;
    }

    private History createSeededHistory() {
        History history = new MemoryHistory();
        history.add("dir");
        history.add("cd c:\\");
        history.add("mkdir monkey");
        return history;
    }

    @Test
    public void testReadline() throws Exception {
        ConsoleReader consoleReader = createConsole("Sample String\r\n");
        assertNotNull(consoleReader);
        String line = consoleReader.readLine();
        assertEquals("Sample String", line);
    }

    @Test
    public void testReadlineWithUnicode() throws Exception {
        System.setProperty("input.encoding", "UTF-8");
        ConsoleReader consoleReader = createConsole("\u6771\u00E9\u00E8\r\n");
        assertNotNull(consoleReader);
        String line = consoleReader.readLine();
        assertEquals("\u6771\u00E9\u00E8", line);
    }
    
    @Test
    public void testReadlineWithMask() throws Exception {
        ConsoleReader consoleReader = createConsole("Sample String\r\n");
        assertNotNull(consoleReader);
        String line = consoleReader.readLine('*');
        assertEquals("Sample String", line);
        assertEquals("*************", output.toString().trim());
    }

    @Test
    public void testDeleteOnWindowsTerminal() throws Exception {
        // test only works on Windows
        assumeTrue(TerminalFactory.get() instanceof WindowsTerminal);

        char[] characters = new char[]{
            'S', 's',
            (char) SPECIAL_KEY_INDICATOR.code,
            (char) LEFT_ARROW_KEY.code,
            (char) SPECIAL_KEY_INDICATOR.code,
            (char) DELETE_KEY.code, '\r', 'n'
        };
        assertWindowsKeyBehavior("S", characters);
    }

    @Test
    public void testNumpadDeleteOnWindowsTerminal() throws Exception {
        // test only works on Windows
        assumeTrue(TerminalFactory.get() instanceof WindowsTerminal);

        char[] characters = new char[]{
            'S', 's',
            (char) NUMPAD_KEY_INDICATOR.code,
            (char) LEFT_ARROW_KEY.code,
            (char) NUMPAD_KEY_INDICATOR.code,
            (char) DELETE_KEY.code, '\r', 'n'
        };
        assertWindowsKeyBehavior("S", characters);
    }

    @Test
    public void testHomeKeyOnWindowsTerminal() throws Exception {
        // test only works on Windows
        assumeTrue(TerminalFactory.get() instanceof WindowsTerminal);

        char[] characters = new char[]{
            'S', 's',
            (char) SPECIAL_KEY_INDICATOR.code,
            (char) HOME_KEY.code, 'x', '\r', '\n'
        };
        assertWindowsKeyBehavior("xSs", characters);

    }

    @Test
    public void testEndKeyOnWindowsTerminal() throws Exception {
        // test only works on Windows
        assumeTrue(TerminalFactory.get() instanceof WindowsTerminal);

        char[] characters = new char[]{
            'S', 's',
            (char) SPECIAL_KEY_INDICATOR.code,
            (char) HOME_KEY.code, 'x',
            (char) SPECIAL_KEY_INDICATOR.code, (char) END_KEY.code,
            'j', '\r', '\n'
        };
        assertWindowsKeyBehavior("xSsj", characters);
    }

    @Test
    public void testPageUpOnWindowsTerminal() throws Exception {
        // test only works on Windows
        assumeTrue(TerminalFactory.get() instanceof WindowsTerminal);

        char[] characters = new char[]{
            (char) SPECIAL_KEY_INDICATOR.code,
            (char) PAGE_UP_KEY.code, '\r', '\n'
        };
        assertWindowsKeyBehavior("dir", characters);
    }

    @Test
    public void testPageDownOnWindowsTerminal() throws Exception {
        // test only works on Windows
        assumeTrue(TerminalFactory.get() instanceof WindowsTerminal);

        char[] characters = new char[]{
            (char) SPECIAL_KEY_INDICATOR.code,
            (char) PAGE_DOWN_KEY.code, '\r', '\n'
        };
        assertWindowsKeyBehavior("mkdir monkey", characters);
    }

    @Test
    public void testEscapeOnWindowsTerminal() throws Exception {
        // test only works on Windows
        assumeTrue(TerminalFactory.get() instanceof WindowsTerminal);

        char[] characters = new char[]{
            's', 's', 's',
            (char) SPECIAL_KEY_INDICATOR.code,
            (char) ESCAPE_KEY.code, '\r', '\n'
        };
        assertWindowsKeyBehavior("", characters);
    }

    @Test
    public void testInsertOnWindowsTerminal() throws Exception {
        // test only works on Windows
        assumeTrue(TerminalFactory.get() instanceof WindowsTerminal);

        char[] characters = new char[]{
            'o', 'p', 's',
            (char) SPECIAL_KEY_INDICATOR.code,
            (char) HOME_KEY.code,
            (char) SPECIAL_KEY_INDICATOR.code,
            (char) INSERT_KEY.code, 'o', 'o', 'p', 's', '\r', '\n'
        };
        assertWindowsKeyBehavior("oops", characters);
    }

    @Test
    public void testExpansion() throws Exception {
        ConsoleReader reader = createConsole();
        MemoryHistory history = new MemoryHistory();
        history.setMaxSize(3);
        history.add("foo");
        history.add("dir");
        history.add("cd c:\\");
        history.add("mkdir monkey");
        reader.setHistory(history);

        assertEquals("echo a!", reader.expandEvents("echo a!"));
        assertEquals("mkdir monkey ; echo a!", reader.expandEvents("!! ; echo a!"));
        assertEquals("echo ! a", reader.expandEvents("echo ! a"));
        assertEquals("echo !\ta", reader.expandEvents("echo !\ta"));

        assertEquals("mkdir barey", reader.expandEvents("^monk^bar^"));
        assertEquals("mkdir barey", reader.expandEvents("^monk^bar"));
        assertEquals("a^monk^bar", reader.expandEvents("a^monk^bar"));

        assertEquals("mkdir monkey", reader.expandEvents("!!"));
        assertEquals("echo echo a", reader.expandEvents("echo !#a"));

        assertEquals("mkdir monkey", reader.expandEvents("!mk"));
        try {
            reader.expandEvents("!mz");
            fail("expected IllegalArgumentException");
        } catch (IllegalArgumentException e) {
            assertEquals("!mz: event not found", e.getMessage());
        }

        assertEquals("mkdir monkey", reader.expandEvents("!?mo"));
        assertEquals("mkdir monkey", reader.expandEvents("!?mo?"));

        assertEquals("mkdir monkey", reader.expandEvents("!-1"));
        assertEquals("cd c:\\", reader.expandEvents("!-2"));
        assertEquals("cd c:\\", reader.expandEvents("!3"));
        assertEquals("mkdir monkey", reader.expandEvents("!4"));
        try {
            reader.expandEvents("!20");
            fail("expected IllegalArgumentException");
        } catch (IllegalArgumentException e) {
            assertEquals("!20: event not found", e.getMessage());
        }
        try {
            reader.expandEvents("!-20");
            fail("expected IllegalArgumentException");
        } catch (IllegalArgumentException e) {
            assertEquals("!-20: event not found", e.getMessage());
        }
    }

    @Test
    public void testNumericExpansions() throws Exception {
        ConsoleReader reader = createConsole();
        MemoryHistory history = new MemoryHistory();
        history.setMaxSize(3);

        // Seed history with three entries:
        // 1 history1
        // 2 history2
        // 3 history3
        history.add("history1");
        history.add("history2");
        history.add("history3");
        reader.setHistory(history);

        // Validate !n
        assertExpansionIllegalArgumentException(reader, "!0");
        assertEquals("history1", reader.expandEvents("!1"));
        assertEquals("history2", reader.expandEvents("!2"));
        assertEquals("history3", reader.expandEvents("!3"));
        assertExpansionIllegalArgumentException(reader, "!4");

        // Validate !-n
        assertExpansionIllegalArgumentException(reader, "!-0");
        assertEquals("history3", reader.expandEvents("!-1"));
        assertEquals("history2", reader.expandEvents("!-2"));
        assertEquals("history1", reader.expandEvents("!-3"));
        assertExpansionIllegalArgumentException(reader, "!-4");

        // Validate !!
        assertEquals("history3", reader.expandEvents("!!"));

        // Add two new entries. Because maxSize=3, history is:
        // 3 history3
        // 4 history4
        // 5 history5
        history.add("history4");
        history.add("history5");

        // Validate !n
        assertExpansionIllegalArgumentException(reader, "!0");
        assertExpansionIllegalArgumentException(reader, "!1");
        assertExpansionIllegalArgumentException(reader, "!2");
        assertEquals("history3", reader.expandEvents("!3"));
        assertEquals("history4", reader.expandEvents("!4"));
        assertEquals("history5", reader.expandEvents("!5"));
        assertExpansionIllegalArgumentException(reader, "!6");

        // Validate !-n
        assertExpansionIllegalArgumentException(reader, "!-0");
        assertEquals("history5", reader.expandEvents("!-1"));
        assertEquals("history4", reader.expandEvents("!-2"));
        assertEquals("history3", reader.expandEvents("!-3"));
        assertExpansionIllegalArgumentException(reader, "!-4");

        // Validate !!
        assertEquals("history5", reader.expandEvents("!!"));
    }

    @Test
    public void testArgsExpansion() throws Exception {
        ConsoleReader reader = createConsole();
        MemoryHistory history = new MemoryHistory();
        history.setMaxSize(3);
        reader.setHistory(history);

        // we can't go back to previous arguments if there are none
        try {
            reader.expandEvents("!$");
            fail("expected IllegalArgumentException");
        } catch (IllegalArgumentException e) {
            assertEquals("!$: event not found", e.getMessage());
        }

        // if no arguments were given, it should expand to the command itself
        history.add("ls");
        assertEquals("ls", reader.expandEvents("!$"));

        // now we can expand to the last argument
        history.add("ls /home");
        assertEquals("/home", reader.expandEvents("!$"));

        //we always take the last argument
        history.add("ls /home /etc");
        assertEquals("/etc", reader.expandEvents("!$"));

        //make sure we don't add spaces accidentally
        history.add("ls /home  /foo ");
        assertEquals("/foo", reader.expandEvents("!$"));
    }

	/**
	 * Validates that an 'event not found' IllegalArgumentException is thrown
	 * for the expansion event.
	 */
    protected void assertExpansionIllegalArgumentException(ConsoleReader reader, String event) throws Exception {
        try {
            reader.expandEvents(event);
            fail("Expected IllegalArgumentException for " + event);
        } catch (IllegalArgumentException e) {
            assertEquals(event + ": event not found", e.getMessage());
        }
    }

    @Test
    public void testIllegalExpansionDoesntCrashReadLine() throws Exception {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        InputStream in = new ByteArrayInputStream("!f\r\n".getBytes());
        ConsoleReader reader = new ConsoleReader(in, baos);
        reader.setExpandEvents(true);
        reader.setBellEnabled(true);
        MemoryHistory history = new MemoryHistory();
        reader.setHistory(history);

        String line = reader.readLine();

        assertEquals("", line);
        assertEquals(0, history.size());
    }

    @Test
    public void testStoringHistory() throws Exception {
        ConsoleReader reader = createConsole("foo ! bar\r\n");
        MemoryHistory history = new MemoryHistory();
        reader.setHistory(history);
        reader.setExpandEvents(true);

        String line = reader.readLine();
        assertEquals("foo ! bar", line);

        history.previous();
        assertEquals("foo \\! bar", history.current());

        reader = createConsole("cd c:\\docs\r\n");
        history = new MemoryHistory();
        reader.setHistory(history);
        reader.setExpandEvents(true);

        line = reader.readLine();
        assertEquals("cd c:\\docs", line);

        history.previous();
        assertEquals("cd c:\\docs", history.current());
    }

    @Test
    public void testExpansionAndHistoryWithEscapes() throws Exception {

        /*
         * Tests the results of the ConsoleReader.readLine() call and the line
         * stored in history. For each input, it tests the with-expansion and
         * without-expansion case.
         */

        ConsoleReader reader = null;

        // \! (escaped expansion v1)
        reader = createConsole("echo ab\\!ef", true, "cd");
        assertReadLine("echo ab!ef", reader);
        assertHistory("echo ab\\!ef", reader);

        reader = createConsole("echo ab\\!ef", false, "cd");
        assertReadLine("echo ab\\!ef", reader);
        assertHistory("echo ab\\!ef", reader);

        // \!\! (escaped expansion v2)
        reader = createConsole("echo ab\\!\\!ef", true, "cd");
        assertReadLine("echo ab!!ef", reader);
        assertHistory("echo ab\\!\\!ef", reader);

        reader = createConsole("echo ab\\!\\!ef", false, "cd");
        assertReadLine("echo ab\\!\\!ef", reader);
        assertHistory("echo ab\\!\\!ef", reader);

        // !! (expansion)
        reader = createConsole("echo ab!!ef", true, "cd");
        assertReadLine("echo abcdef", reader);
        assertHistory("echo abcdef", reader);

        reader = createConsole("echo ab!!ef", false, "cd");
        assertReadLine("echo ab!!ef", reader);
        assertHistory("echo ab!!ef", reader);

        // \G (backslash no expansion)
        reader = createConsole("echo abc\\Gdef", true, "cd");
        assertReadLine("echo abc\\Gdef", reader);
        assertHistory("echo abc\\Gdef", reader);

        reader = createConsole("echo abc\\Gdef", false, "cd");
        assertReadLine("echo abc\\Gdef", reader);
        assertHistory("echo abc\\Gdef", reader);

        // \^ (escaped expansion)
        reader = createConsole("\\^abc^def", true, "echo abc");
        assertReadLine("^abc^def", reader);
        assertHistory("\\^abc^def", reader);

        reader = createConsole("\\^abc^def", false, "echo abc");
        assertReadLine("\\^abc^def", reader);
        assertHistory("\\^abc^def", reader);

        // ^^ (expansion)
        reader = createConsole("^abc^def", true, "echo abc");
        assertReadLine("echo def", reader);
        assertHistory("echo def", reader);

        reader = createConsole("^abc^def", false, "echo abc");
        assertReadLine("^abc^def", reader);
        assertHistory("^abc^def", reader);
    }

    private ConsoleReader createConsole(String input, boolean expandEvents, String... historyItems) throws Exception {
        ConsoleReader consoleReader = createConsole(input + "\r\n");
        MemoryHistory history = new MemoryHistory();
        if (historyItems != null) {
            for (String historyItem : historyItems) {
                history.add(historyItem);
            }
        }
        consoleReader.setHistory(history);
        consoleReader.setExpandEvents(expandEvents);
        return consoleReader;
    }

    private void assertReadLine(String expected, ConsoleReader consoleReader) throws Exception {
        assertEquals(expected, consoleReader.readLine());
    }

    private void assertHistory(String expected, ConsoleReader consoleReader) {
        History history = consoleReader.getHistory();
        history.previous();
        assertEquals(expected, history.current());
    }

    @Test
    public void testStoringHistoryWithExpandEventsOff() throws Exception {
        ConsoleReader reader = createConsole("foo ! bar\r\n");
        MemoryHistory history = new MemoryHistory();
        reader.setHistory(history);
        reader.setExpandEvents(false);

        String line = reader.readLine();
        assertEquals("foo ! bar", line);

        history.previous();
        assertEquals("foo ! bar", history.current());
    }

    @Test
    public void testMacro() throws Exception {
        ConsoleReader consoleReader = createConsole("\u0018(foo\u0018)\u0018e\r\n");
        assertNotNull(consoleReader);
        String line = consoleReader.readLine();
        assertEquals("foofoo", line);
    }

    @Test
    public void testInput() throws Exception {
        System.setProperty(ConsoleReader.JLINE_INPUTRC, getClass().getResource("/scala/tools/jline/internal/config1").toExternalForm());
        try {
            ConsoleReader consoleReader = createConsole("\u0018(foo\u0018)\u0018e\r\n");
            assertNotNull(consoleReader);

            assertEquals(Operation.UNIVERSAL_ARGUMENT, consoleReader.getKeys().getBound("" + ((char)('U' - 'A' + 1))));
            assertEquals("Function Key \u2671", consoleReader.getKeys().getBound("\u001b[11~"));
            assertEquals(null, consoleReader.getKeys().getBound(((char)('X' - 'A' + 1)) + "q"));

            consoleReader = createConsole("bash", new byte[0]);
            assertNotNull(consoleReader);
            assertEquals("\u001bb\"\u001bf\"", consoleReader.getKeys().getBound(((char)('X' - 'A' + 1)) + "q"));
        } finally {
            System.clearProperty(ConsoleReader.JLINE_INPUTRC);
        }
    }

    @Test
    public void testInput2() throws Exception {
        System.setProperty(ConsoleReader.JLINE_INPUTRC, getClass().getResource("/scala/tools/jline/internal/config2").toExternalForm());
        try {
            ConsoleReader consoleReader = createConsole("Bash", new byte[0]);
            assertNotNull(consoleReader);
            assertNotNull(consoleReader.getKeys().getBound("\u001b" + ((char)('V' - 'A' + 1))));

        } finally {
            System.clearProperty(ConsoleReader.JLINE_INPUTRC);
        }
    }

    @Test
    public void testInputBadConfig() throws Exception {
        System.setProperty(ConsoleReader.JLINE_INPUTRC, getClass().getResource("/scala/tools/jline/internal/config-bad").toExternalForm());
        try {
            ConsoleReader consoleReader = createConsole("Bash", new byte[0]);
            assertNotNull(consoleReader);
            assertEquals("\u001bb\"\u001bf\"", consoleReader.getKeys().getBound(((char)('X' - 'A' + 1)) + "q"));
        } finally {
            System.clearProperty(ConsoleReader.JLINE_INPUTRC);
        }
    }

    @Test
    public void testBell() throws Exception {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ConsoleReader consoleReader = new ConsoleReader(System.in, baos);

        assertFalse("default bell should be disabled", consoleReader.getBellEnabled());

        consoleReader.beep();

        assertEquals("out should not have received bell", 0, baos.toByteArray().length);

        consoleReader.setBellEnabled(true);

        assertTrue("bell should have been enabled", consoleReader.getBellEnabled());

        consoleReader.beep();

        assertEquals("out should have received bell", 1, baos.toByteArray().length);
        assertEquals("out should have received bell", ConsoleReader.KEYBOARD_BELL, baos.toByteArray()[0]);
    }

    /**
     * Windows keys.
     * <p/>
     * Constants copied <tt>wincon.h</tt>.
     */
    public static enum WindowsKey
    {
        /**
         * On windows terminals, this character indicates that a 'special' key has
         * been pressed. This means that a key such as an arrow key, or delete, or
         * home, etc. will be indicated by the next character.
         */
        SPECIAL_KEY_INDICATOR(224),

        /**
         * On windows terminals, this character indicates that a special key on the
         * number pad has been pressed.
         */
        NUMPAD_KEY_INDICATOR(0),

        /**
         * When following the SPECIAL_KEY_INDICATOR or NUMPAD_KEY_INDICATOR,
         * this character indicates an left arrow key press.
         */
        LEFT_ARROW_KEY(75),

        /**
         * When following the SPECIAL_KEY_INDICATOR or NUMPAD_KEY_INDICATOR
         * this character indicates an
         * right arrow key press.
         */
        RIGHT_ARROW_KEY(77),

        /**
         * When following the SPECIAL_KEY_INDICATOR or NUMPAD_KEY_INDICATOR
         * this character indicates an up
         * arrow key press.
         */
        UP_ARROW_KEY(72),

        /**
         * When following the SPECIAL_KEY_INDICATOR or NUMPAD_KEY_INDICATOR
         * this character indicates an
         * down arrow key press.
         */
        DOWN_ARROW_KEY(80),

        /**
         * When following the SPECIAL_KEY_INDICATOR or NUMPAD_KEY_INDICATOR
         * this character indicates that
         * the delete key was pressed.
         */
        DELETE_KEY(83),

        /**
         * When following the SPECIAL_KEY_INDICATOR or NUMPAD_KEY_INDICATOR
         * this character indicates that
         * the home key was pressed.
         */
        HOME_KEY(71),

        /**
         * When following the SPECIAL_KEY_INDICATOR or NUMPAD_KEY_INDICATOR
         * this character indicates that
         * the end key was pressed.
         */
        END_KEY(79),

        /**
         * When following the SPECIAL_KEY_INDICATOR or NUMPAD_KEY_INDICATOR
         * this character indicates that
         * the page up key was pressed.
         */
        PAGE_UP_KEY(73),

        /**
         * When following the SPECIAL_KEY_INDICATOR or NUMPAD_KEY_INDICATOR
         * this character indicates that
         * the page down key was pressed.
         */
        PAGE_DOWN_KEY(81),

        /**
         * When following the SPECIAL_KEY_INDICATOR or NUMPAD_KEY_INDICATOR
         * this character indicates that
         * the insert key was pressed.
         */
        INSERT_KEY(82),

        /**
         * When following the SPECIAL_KEY_INDICATOR or NUMPAD_KEY_INDICATOR,
         * this character indicates that the escape key was pressed.
         */
        ESCAPE_KEY(0),;

        public final int code;

        WindowsKey(final int code) {
            this.code = code;
        }

        private static final Map<Integer, WindowsKey> codes;

        static {
            Map<Integer, WindowsKey> map = new HashMap<Integer, WindowsKey>();

            for (WindowsKey key : WindowsKey.values()) {
                map.put(key.code, key);
            }

            codes = map;
        }

        public static WindowsKey valueOf(final int code) {
            return codes.get(code);
        }
    }
}
