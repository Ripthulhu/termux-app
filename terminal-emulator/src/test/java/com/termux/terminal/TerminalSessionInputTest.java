package com.termux.terminal;

import org.junit.Test;
import java.io.ByteArrayOutputStream;
import java.lang.reflect.Proxy;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.Assert.*;

public class TerminalSessionInputTest {
    private TerminalSession session(AtomicInteger rejections) {
        TerminalSessionClient client = (TerminalSessionClient) Proxy.newProxyInstance(
            getClass().getClassLoader(), new Class[]{TerminalSessionClient.class}, (proxy, method, args) -> {
                if (method.getName().equals("onInputRejected")) rejections.incrementAndGet();
                return null;
            });
        TerminalSession session = new TerminalSession("/system/bin/sh", "/", new String[0], new String[0], 100, client);
        session.mShellPid = 1;
        return session;
    }

    private byte[] drain(TerminalSession session) {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        byte[] chunk = new byte[4096];
        int count;
        while ((count = session.mTerminalToProcessIOQueue.read(chunk, false)) > 0) output.write(chunk, 0, count);
        return output.toByteArray();
    }

    @Test(timeout = 2000) public void aStalledReaderNeverBlocksInputAndKeepsAcceptedBytes() {
        AtomicInteger rejected = new AtomicInteger();
        TerminalSession session = session(rejected);
        byte[] paste = new byte[1024 * 1024];
        Arrays.fill(paste, (byte) 'a');
        session.write(paste, 0, paste.length);
        session.write("x");
        session.write("y");
        assertEquals(1, rejected.get());
        assertArrayEquals(paste, drain(session));
        session.writeCodePoint(false, 0x1F600);
        session.writeCodePoint(false, 'z');
        assertArrayEquals("😀z".getBytes(StandardCharsets.UTF_8), drain(session));
        session.write(new byte[1024 * 1024 + 1], 0, 1024 * 1024 + 1);
        assertEquals(2, rejected.get());
        assertEquals(0, drain(session).length);
    }

    @Test(timeout = 2000) public void rejectedPasteDoesNotSendHalfOfABracketedFrame() {
        AtomicInteger rejected = new AtomicInteger();
        TerminalSession session = session(rejected);
        TerminalEmulator emulator = new TerminalEmulator(session, 20, 5, 13, 15, 100, session.mClient);
        byte[] enable = "\033[?2004h".getBytes(StandardCharsets.UTF_8);
        emulator.append(enable, enable.length);
        byte[] pending = new byte[1024 * 1024 - 6];
        session.write(pending, 0, pending.length);
        emulator.paste("abc");
        assertEquals(1, rejected.get());
        assertArrayEquals(pending, drain(session));
        emulator.paste("a\nb\033c");
        session.write("Z");
        assertEquals("\033[200~a\rbc\033[201~Z", new String(drain(session), StandardCharsets.UTF_8));
    }

    @Test(timeout = 2000) public void closedQueueRejectsInputAndEmptyInputIsHarmless() {
        AtomicInteger rejected = new AtomicInteger();
        TerminalSession session = session(rejected);
        session.write("");
        assertEquals(0, rejected.get());
        session.mTerminalToProcessIOQueue.close();
        session.write("x");
        assertEquals(1, rejected.get());
    }
}
