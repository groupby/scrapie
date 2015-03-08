package com.wp.scrapie;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.junit.Rule;
import org.junit.Test;
import org.junit.contrib.java.lang.system.RestoreSystemProperties;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.StringWriter;

import static org.junit.Assert.assertEquals;

public class EmitterTest extends JsLoader {

    @Rule
    public final RestoreSystemProperties restoreSystemProperties = new RestoreSystemProperties(
            "workingDir");


    @Test
    public void findMaxIndexTest() throws IOException {
        final Emitter emitter = new Emitter();
        Emitter.setSourceDirectory(new File("target/findMaxIndex"));
        Emitter.setSourceFileName("temp2.js");
        emitter.resetMaxIndex();
        assertEquals(0, emitter.findMaxIndex("http://localhost:38/somefile.html?page="));
        emitter.saveMaxIndex("http://localhost:38/somefile.html?page=", 299);
        assertEquals(299, emitter.findMaxIndex("http://localhost:38/somefile.html?page="));
        emitter.setNoSkipToMaxIndex(true);
        assertEquals(0, emitter.findMaxIndex("http://localhost:38/somefile.html?page="));
        emitter.loadMaxIndexes();
        emitter.setNoSkipToMaxIndex(false);
        assertEquals(299, emitter.findMaxIndex("http://localhost:38/somefile.html?page="));
    }

    @Test
    public void saveMaxIndexTest() throws IOException {
        final Emitter emitter = new Emitter();
        Emitter.setSourceDirectory(new File("target/maxIndex"));
        Emitter.setSourceFileName("temp.js");
        emitter.saveMaxIndex("http://localhost:38/somefile.html?page=", 231);
        assertEquals("http://localhost:38/somefile.html?page=", emitter.getMaxIndexes().keySet().iterator().next());
        assertEquals(new Long(231), emitter.getMaxIndexes().values().iterator().next());
        assertEquals("{\"http://localhost:38/somefile.html?page=\":231}", FileUtils.readFileToString(new File("target/maxIndex/temp.js_data/maxIndexes.json")));
    }

    @Test
    public void testSimple() throws Exception {

        server.setResource("/index.html?id=0", createWebPage("0", ""));
        server.setResource("/index.html?id=1", createWebPage("1", ""));
        StringWriter writer = run(new Emitter(), "src/test/js/iterateLow.js");
        assertEquals("{\"title\":[\"title 0\"]}\n"
                + "{\"title\":[\"title 1\"]}\n", writer.getBuffer().toString());
    }

    @Test
    public void testGetUrl() throws Exception {
        server.setResource("/index.html?id=0", createWebPage("0", ""));
        server.setResource("/index.html?id=1", createWebPage("1", ""));
        StringWriter writer = run(new Emitter(), "src/test/js/iterateLowUrl.js");
        assertEquals("{\"url\":[\"http://localhost:" + getPort()
                + "/index.html?id=0\"]}\n" + "{\"url\":[\"http://localhost:"
                + getPort() + "/index.html?id=1\"]}\n", writer.getBuffer()
                .toString());
    }

    @Test
    public void testRecord() throws Exception {
        Emitter.setRecordCount(0);
        Emitter.setMaxRecords(2);
        Emitter.setSourceFileName("iterateLow.js");
        Emitter.setSourceDirectory(new File("target/"));
        testSimple();

        server = null;
        Emitter.setRecordCount(0);
        Emitter.setMaxRecords(1);
        Emitter emitter = new Emitter();
        StringWriter writer = new StringWriter();
        emitter.run(
                IOUtils.toString(
                        new FileInputStream("src/test/js/iterateLow.js"))
                        .replaceAll("####", "80"), writer);
        assertEquals("{\"title\":[\"title 0\"]}\n", writer.getBuffer()
                .toString());

        Emitter.setMaxRecords(2);
        Emitter.setRecordCount(0);
        emitter = new Emitter();
        writer = new StringWriter();
        emitter.run(
                IOUtils.toString(
                        new FileInputStream("src/test/js/iterateLow.js"))
                        .replaceAll("####", "80"), writer);
        assertEquals("{\"title\":[\"title 0\"]}\n"
                + "{\"title\":[\"title 1\"]}\n", writer.getBuffer().toString());
    }

    @Test
    public void testFileIterator() throws Exception {
        Emitter.setRecordCount(0);
        Emitter.setMaxRecords(10);
        Emitter.setSourceFileName("iterateFile.js");
        Emitter.setSourceDirectory(new File("target/"));
        testSimple();

        server = null;
        Emitter.setRecordCount(0);
        Emitter.setMaxRecords(10);
        Emitter.setSourceFileName("iterateFile.js");
        Emitter emitter = new Emitter();
        StringWriter writer = new StringWriter();
        FileUtils.copyFile(new File("src/test/js/iterateFile.js"), new File(
                "target/iterateFile.js"));
        FileUtils.copyFile(new File("src/test/js/idList.txt"), new File(
                "target/idList.txt"));
        emitter.runFile("target/iterateFile.js", writer);
        assertEquals("{\"title\":[\"title 0\"]}\n"
                + "{\"title\":[\"title 1\"]}\n", writer.getBuffer().toString());
    }

    @Test
    public void testEmit() throws Exception {
        Emitter test = new Emitter();
        Emitter.setWriter(new StringWriter());
        test.emit("key1", "value1");
        Emitter.flush();
        assertEquals("{\"key1\":[\"value1\"]}\n", Emitter.getWriter()
                .toString());
    }

    @Test
    public void testEmitForWorkingId() throws Exception {
        Emitter test = new Emitter();
        test.setWorkingId("someId");
        Emitter.setWriter(new StringWriter());
        test.emitForWorkingId("key1", "value1");
        Emitter.flush();
        assertEquals("{\"key1\":[\"value1\"]}\n", Emitter.getWriter()
                .toString());
    }

    @Test
    public void testEmitForWorkingIdParentContext() throws Exception {
        Emitter test = new Emitter();
        Emitter parent = new Emitter();
        test.setParent(parent);
        parent.setWorkingId("bob");
        StringWriter writer = new StringWriter();
        Emitter.setWriter(writer);
        test.emitForWorkingId("key1", "value1");
        parent.emitForWorkingId("key2", "value2");
        Emitter.flush();
        assertEquals("{\"key1\":[\"value1\"],\"key2\":[\"value2\"]}\n", Emitter
                .getWriter().toString());
    }

    @Test
    public void testHigh() throws Exception {
        createListPages();
        server.setResource("/detail.jsp?id=abc",
                createWebPage("0", "<div id='price'>$29.99</div>"));
        server.setResource("/detail.jsp?id=bcd",
                createWebPage("0", "<div id='price'>$39.99</div>"));
        server.setResource("/detail.jsp?id=cde",
                createWebPage("0", "<div id='price'>$49.99</div>"));
        server.setResource("/detail.jsp?id=def",
                createWebPage("0", "<div id='price'>$59.99</div>"));
        StringWriter writer = run(new Emitter(), "src/test/js/iterateHigh.js");
        assertEquals(
                "{\"detailUrl\":[\"http://localhost:"
                        + getPort()
                        + "/detail.jsp?id=abc\"],\"href\":[\"/detail.jsp?id=abc\"],\"id\":[\"abc\"],\"listUrl\":[\"http://localhost:"
                        + getPort()
                        + "/list?page=0\"],\"price\":[\"$29.99\"],\"title\":[\"link 1\"]}\n"
                        + "{\"detailUrl\":[\"http://localhost:"
                        + getPort()
                        + "/detail.jsp?id=bcd\"],\"href\":[\"/detail.jsp?id=bcd\"],\"id\":[\"bcd\"],\"listUrl\":[\"http://localhost:"
                        + getPort()
                        + "/list?page=0\"],\"price\":[\"$39.99\"],\"title\":[\"link 2\"]}\n"
                        + "{\"detailUrl\":[\"http://localhost:"
                        + getPort()
                        + "/detail.jsp?id=cde\"],\"href\":[\"/detail.jsp?id=cde\"],\"id\":[\"cde\"],\"listUrl\":[\"http://localhost:"
                        + getPort()
                        + "/list?page=1\"],\"price\":[\"$49.99\"],\"title\":[\"link 3\"]}\n"
                        + "{\"detailUrl\":[\"http://localhost:"
                        + getPort()
                        + "/detail.jsp?id=def\"],\"href\":[\"/detail.jsp?id=def\"],\"id\":[\"def\"],\"listUrl\":[\"http://localhost:"
                        + getPort()
                        + "/list?page=1\"],\"price\":[\"$59.99\"],\"title\":[\"link 4\"]}\n",
                writer.getBuffer().toString());

    }

    @Test
    public void testMedium() throws Exception {
        createListPages();
        StringWriter writer = run(new Emitter(), "src/test/js/iterateMedium.js");
        assertEquals("{\"id\":[\"abc\"],\"title\":[\"link 1\"]}\n"
                + "{\"id\":[\"bcd\"],\"title\":[\"link 2\"]}\n"
                + "{\"id\":[\"cde\"],\"title\":[\"link 3\"]}\n"
                + "{\"id\":[\"def\"],\"title\":[\"link 4\"]}\n", writer
                .getBuffer().toString());

    }

    private void createListPages() {
        server.setResource(
                "/list?page=0",
                createWebPage(
                        "0",
                        "<div class='item'><a href='/detail.jsp?id=abc'>link 1</a></div>"
                                + "<div class='item'><a href='/detail.jsp?id=bcd'>link 2</a></div>"));
        server.setResource(
                "/list?page=1",
                createWebPage(
                        "1",
                        "<div class='item'><a href='/detail.jsp?id=cde'>link 3</a></div>"
                                + "<div class='item'><a href='/detail.jsp?id=def'>link 4</a></div>"));
    }


}
