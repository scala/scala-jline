/*
 * Copyright (c) 2002-2012, the original author or authors.
 *
 * This software is distributable under the BSD license. See the terms of the
 * BSD license in the documentation provided with this software.
 *
 * http://www.opensource.org/licenses/bsd-license.php
 */
package scala.tools.jline.internal;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Tests for the {@link TerminalLineSettings}.
 *
 * @author <a href="mailto:jbonofre@apache.org">Jean-Baptiste Onofré</a>
 */
public class TerminalLineSettingsTest
{
    private TerminalLineSettings settings;

    private final String linuxSttySample = "speed 38400 baud; rows 85; columns 244; line = 0;\n" +
            "intr = ^C; quit = ^\\; erase = ^?; kill = ^U; eof = ^D; eol = M-^?; eol2 = M-^?; swtch = M-^?; start = ^Q; stop = ^S; susp = ^Z; rprnt = ^R; werase = ^W; lnext = ^V; flush = ^O; min = 1; time = 0;\n" +
            "-parenb -parodd cs8 hupcl -cstopb cread -clocal -crtscts\n" +
            "-ignbrk brkint -ignpar -parmrk -inpck -istrip -inlcr -igncr icrnl ixon -ixoff -iuclc ixany imaxbel iutf8\n" +
            "opost -olcuc -ocrnl onlcr -onocr -onlret -ofill -ofdel nl0 cr0 tab0 bs0 vt0 ff0\n" +
            "isig icanon iexten echo echoe echok -echonl -noflsh -xcase -tostop -echoprt echoctl echoke";

    private final String solarisSttySample = "speed 38400 baud; \n" +
            "rows = 85; columns = 244; ypixels = 0; xpixels = 0;\n" +
            "csdata ?\n" +
            "eucw 1:0:0:0, scrw 1:0:0:0\n" +
            "intr = ^c; quit = ^\\; erase = ^?; kill = ^u;\n" +
            "eof = ^d; eol = -^?; eol2 = -^?; swtch = <undef>;\n" +
            "start = ^q; stop = ^s; susp = ^z; dsusp = ^y;\n" +
            "rprnt = ^r; flush = ^o; werase = ^w; lnext = ^v;\n" +
            "-parenb -parodd cs8 -cstopb -hupcl cread -clocal -loblk -crtscts -crtsxoff -parext \n" +
            "-ignbrk brkint -ignpar -parmrk -inpck -istrip -inlcr -igncr icrnl -iuclc \n" +
            "ixon ixany -ixoff imaxbel \n" +
            "isig icanon -xcase echo echoe echok -echonl -noflsh \n" +
            "-tostop echoctl -echoprt echoke -defecho -flusho -pendin iexten \n" +
            "opost -olcuc onlcr -ocrnl -onocr -onlret -ofill -ofdel tab3";

    private final String aixSttySample = "speed 38400 baud; 85 rows; 244 columns;\n" +
            "eucw 1:1:0:0, scrw 1:1:0:0:\n" +
            "intr = ^C; quit = ^\\; erase = ^?; kill = ^U; eof = ^D; eol = <undef>\n" +
            "eol2 = <undef>; start = ^Q; stop = ^S; susp = ^Z; dsusp = ^Y; reprint = ^R\n" +
            "discard = ^O; werase = ^W; lnext = ^V\n" +
            "-parenb -parodd cs8 -cstopb -hupcl cread -clocal -parext \n" +
            "-ignbrk brkint -ignpar -parmrk -inpck -istrip -inlcr -igncr icrnl -iuclc \n" +
            "ixon ixany -ixoff imaxbel \n" +
            "isig icanon -xcase echo echoe echok -echonl -noflsh \n" +
            "-tostop echoctl -echoprt echoke -flusho -pending iexten \n" +
            "opost -olcuc onlcr -ocrnl -onocr -onlret -ofill -ofdel tab3";

    private final String macOsSttySample = "speed 9600 baud; 47 rows; 155 columns;\n" +
            "lflags: icanon isig iexten echo echoe -echok echoke -echonl echoctl\n" +
            "-echoprt -altwerase -noflsh -tostop -flusho pendin -nokerninfo\n" +
            "-extproc\n" +
            "iflags: -istrip icrnl -inlcr -igncr ixon -ixoff ixany imaxbel iutf8\n" +
            "-ignbrk brkint -inpck -ignpar -parmrk\n" +
            "oflags: opost onlcr -oxtabs -onocr -onlret\n" +
            "cflags: cread cs8 -parenb -parodd hupcl -clocal -cstopb -crtscts -dsrflow\n" +
            "-dtrflow -mdmbuf\n" +
            "cchars: discard = ^O; dsusp = ^Y; eof = ^D; eol = <undef>;\n" +
            "eol2 = <undef>; erase = ^?; intr = ^C; kill = ^U; lnext = ^V;\n" +
            "min = 1; quit = ^\\; reprint = ^R; start = ^Q; status = ^T;\n" +
            "stop = ^S; susp = ^Z; time = 0; werase = ^W;";

    private final String netBsdSttySample = "speed 38400 baud; 85 rows; 244 columns;\n" +
            "lflags: icanon isig iexten echo echoe echok echoke -echonl echoctl\n" +
            "        -echoprt -altwerase -noflsh -tostop -flusho pendin -nokerninfo\n" +
            "        -extproc\n" +
            "iflags: -istrip icrnl -inlcr -igncr ixon -ixoff ixany imaxbel -ignbrk\n" +
            "        brkint -inpck -ignpar -parmrk\n" +
            "oflags: opost onlcr -ocrnl oxtabs onocr onlret\n" +
            "cflags: cread cs8 -parenb -parodd hupcl -clocal -cstopb -crtscts -mdmbuf\n" +
            "        -cdtrcts\n" +
            "cchars: discard = ^O; dsusp = ^Y; eof = ^D; eol = <undef>;\n" +
            "        eol2 = <undef>; erase = ^?; intr = ^C; kill = ^U; lnext = ^V;\n" +
            "        min = 1; quit = ^\\; reprint = ^R; start = ^Q; status = ^T;\n" +
            "        stop = ^S; susp = ^Z; time = 0; werase = ^W;";

    private final String freeBsdSttySample = "speed 9600 baud; 32 rows; 199 columns;\n" +
            "lflags: icanon isig iexten echo echoe echok echoke -echonl echoctl\n" +
            "        -echoprt -altwerase -noflsh -tostop -flusho -pendin -nokerninfo\n" +
            "        -extproc\n" +
            "iflags: -istrip icrnl -inlcr -igncr ixon -ixoff ixany imaxbel -ignbrk\n" +
            "        brkint -inpck -ignpar -parmrk\n" +
            "oflags: opost onlcr -ocrnl tab0 -onocr -onlret\n" +
            "cflags: cread cs8 -parenb -parodd hupcl -clocal -cstopb -crtscts -dsrflow\n" +
            "        -dtrflow -mdmbuf\n" +
            "cchars: discard = ^O; dsusp = ^Y; eof = ^D; eol = <undef>;\n" +
            "        eol2 = <undef>; erase = ^?; erase2 = ^H; intr = ^C; kill = ^U;\n" +
            "        lnext = ^V; min = 1; quit = ^\\; reprint = ^R; start = ^Q;\n" +
            "        status = ^T; stop = ^S; susp = ^Z; time = 0; werase = ^W;";

    private final String hpuxSttySample = "speed 38400 baud; line = 0;\n" +
            "rows = 52; columns = 151\n" +
            "min = 4; time = 0;\n" +
            "intr = DEL; quit = ^\\; erase = #; kill = @\n" +
            "eof = ^D; eol = ^@; eol2 <undef>; swtch = ^@\n" +
            "stop = ^S; start = ^Q; susp <undef>; dsusp <undef>\n" +
            "werase <undef>; lnext <undef>\n" +
            "-parenb -parodd cs8 -cstopb hupcl cread -clocal -loblk -crts\n" +
            "-ignbrk brkint ignpar -parmrk -inpck istrip -inlcr -igncr icrnl -iuclc\n" +
            "ixon ixany -ixoff -imaxbel -rtsxoff -ctsxon -ienqak\n" +
            "isig icanon -iexten -xcase echo -echoe echok -echonl -noflsh\n" +
            "-echoctl -echoprt -echoke -flusho -pendin\n" +
            "opost -olcuc onlcr -ocrnl -onocr -onlret -ofill -ofdel -tostop";

    @Before
    public void setUp() throws Exception {
    }

    @Test
    public void testGetConfig() throws Exception {
        if (!Configuration.getOsName().contains("win")) {
            TerminalLineSettings settings = new TerminalLineSettings();
            String config = settings.getConfig();
            System.out.println(config);
        }
    }

    @Test
    public void testLinuxSttyParsing() {
        assertEquals(0x7f, TerminalLineSettings.getProperty("erase", linuxSttySample));
        assertEquals(244, TerminalLineSettings.getProperty("columns", linuxSttySample));
        assertEquals(85, TerminalLineSettings.getProperty("rows", linuxSttySample));
    }

    @Test
    public void testSolarisSttyParsing() {
        assertEquals(0x7f, TerminalLineSettings.getProperty("erase", solarisSttySample));
        assertEquals(244, TerminalLineSettings.getProperty("columns", solarisSttySample));
        assertEquals(85, TerminalLineSettings.getProperty("rows", solarisSttySample));
    }

    @Test
    public void testAixSttyParsing() {
        assertEquals(0x7f, TerminalLineSettings.getProperty("erase", aixSttySample));
        assertEquals(244, TerminalLineSettings.getProperty("columns", aixSttySample));
        assertEquals(85, TerminalLineSettings.getProperty("rows", aixSttySample));
    }

    @Test
    public void testMacOsSttyParsing() {
        assertEquals(0x7f, TerminalLineSettings.getProperty("erase", macOsSttySample));
        assertEquals(155, TerminalLineSettings.getProperty("columns", macOsSttySample));
        assertEquals(47, TerminalLineSettings.getProperty("rows", macOsSttySample));
    }

    @Test
    public void testNetBsdSttyParsing() {
        assertEquals(0x7f, TerminalLineSettings.getProperty("erase", netBsdSttySample));
        assertEquals(244, TerminalLineSettings.getProperty("columns", netBsdSttySample));
        assertEquals(85, TerminalLineSettings.getProperty("rows", netBsdSttySample));
    }

    @Test
    public void testFreeBsdSttyParsing() {
        assertEquals(0x7f, TerminalLineSettings.getProperty("erase", freeBsdSttySample));
        assertEquals(199, TerminalLineSettings.getProperty("columns", freeBsdSttySample));
        assertEquals(32, TerminalLineSettings.getProperty("rows", freeBsdSttySample));
    }

    @Test
    public void testHpuxSttyParsing() {
        assertEquals(0x23, TerminalLineSettings.getProperty("erase", hpuxSttySample));
        assertEquals(151, TerminalLineSettings.getProperty("columns", hpuxSttySample));
        assertEquals(52, TerminalLineSettings.getProperty("rows", hpuxSttySample));
    }

}
