package com.oneandone.ejbcdiunit.simulators.sftpserver;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import org.jglue.cdiunit.AdditionalClasses;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;

import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.SftpException;
import com.oneandone.ejbcdiunit.EjbUnitRunner;
import com.oneandone.ejbcdiunit.util.LoggerProducer;

@RunWith(EjbUnitRunner.class)
@AdditionalClasses({ LoggerProducer.class })
public class SftpServerTest {

    private static final String HOST = "localhost";
    private static final Integer PORT = 12322;
    private static final String USER = "ejbcdiunituser";
    private static final String PASSWORD = "ejbcdiunitpassword";
    private static final String FILE_NAME = "myFile";

    private static final SftpServer SFTP_SERVER = new SftpServer(PORT, USER, PASSWORD, null);

    private List<Exception> exceptions = new CopyOnWriteArrayList<>();

    @Inject
    private Logger logger;

    @Before
    public void startServer() throws Exception {

        SFTP_SERVER.start();
    }

    @After
    public void stopServer() throws Exception {
        SFTP_SERVER.stop();
    }

    /**
     * This test creates multiple threads, which try to execute ls and one thread that executes rm concurrently on the SFTP server. This is repeated
     * 100 times. Sometimes a FileNotFoundException will be reported by the SFTP server. This is a known bug. We want to test the workaround for it,
     * which we also use in SimulatorJobMessageSenderSp109Telefonica.sendResponse. You can find it reproduced in this test in MyTask, where ls is
     * executed.
     */
    @Test
    public void testConcurrentLsAndRm() throws Exception {
        for (int run = 0; run < 100; run++) {
            logger.info("Run " + run);

            boolean skipRun = false;
            try {
                uploadFile();
            } catch (JSchException e) {
                logger.error("Skipping run because of JSchException during upload.", e);
                skipRun = true;
            }

            if (!skipRun) {
                int threadCount = 10;
                BlockingQueue<Runnable> workQueue = new ArrayBlockingQueue<>(threadCount);
                ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(threadCount, threadCount, 0, TimeUnit.SECONDS, workQueue);

                CountDownLatch countDownLatch = new CountDownLatch(threadCount);
                List<Future<Object>> futures = new ArrayList<>();

                int threadForRmId = (int) Math.floor(Math.random() * threadCount);

                for (int i = 0; i < threadCount; i++) {
                    futures.add(threadPoolExecutor.submit(new MyTask(i, countDownLatch, threadForRmId)));
                }

                for (Future<Object> future : futures) {
                    try {
                        future.get();
                    } catch (ExecutionException e) {
                        assertThat(isCausedByFileNotFoundException(e.getCause()), is(false));
                    }
                }

                for (Exception e : exceptions) {
                    if (!isCausedBySignatureException(e) && !isCausedByFileNotFoundException(e)) {
                        logger.info("failing exception", e);
                        fail();
                    }
                }
            }
        }

        // Aggregate logging of execptions at the end of the test
        for (Exception exception : exceptions) {
            logger.info("RECORDED EXCEPTION:", exception);
        }
    }

    /**
     * These are known security exceptions, that we are not interested in in this test.
     *
     * @see <a href="https://sourceforge.net/p/jsch/bugs/111/">https://sourceforge.net/p/jsch/bugs/111/</a>
     */
    private boolean isCausedBySignatureException(Throwable e) {
        return e.toString().contains("java.security.SignatureException");
    }

    /**
     * These are the exceptions, that the workaround, we want to test, is for.
     */
    private boolean isCausedByFileNotFoundException(Throwable e) {
        return e.toString().contains("2: ");
    }

    private JSch createJsch() {
        return new JSch();
    }

    private Session createSession(JSch jsch) throws Exception {
        Session session = jsch.getSession(USER, HOST, PORT);
        session.setPassword(PASSWORD);
        session.setConfig("StrictHostKeyChecking", "no");
        session.setTimeout(60000);
        session.connect();
        return session;
    }

    private ChannelSftp createChannel(Session session) throws Exception {
        ChannelSftp channel = (ChannelSftp) session.openChannel("sftp");
        channel.connect(60000);
        return channel;
    }

    private void uploadFile() throws Exception {
        JSch jsch = createJsch();
        Session session = createSession(jsch);
        try {
            ChannelSftp channel = createChannel(session);
            try {
                byte[] fileContent = "myContent".getBytes();

                try (InputStream inputStream = new ByteArrayInputStream(fileContent)) {
                    channel.put(inputStream, FILE_NAME);
                }
            } finally {
                channel.disconnect();
            }
        } finally {
            session.disconnect();
        }
    }

    class MyTask implements Callable<Object> {

        private CountDownLatch countDownLatch;
        private int id;
        private int threadForRmId;

        MyTask(int id, CountDownLatch countDownLatch, int threadForRmId) {
            this.id = id;
            this.countDownLatch = countDownLatch;
            this.threadForRmId = threadForRmId;
        }

        @Override
        public Void call() throws Exception {
            try {
                logger.info("Task " + id + ": Connect");
                JSch jsch = createJsch();
                Session session = createSession(jsch);
                try {
                    ChannelSftp channel = createChannel(session);
                    try {
                        logger.info("Task " + id + ": Await");
                        countDownLatch.countDown();
                        countDownLatch.await(100, TimeUnit.MILLISECONDS);

                        if (id == threadForRmId) {
                            logger.info("Task " + id + ": rm");
                            channel.rm(FILE_NAME);
                        } else {
                            logger.info("Task " + id + ": ls");

                            List<ChannelSftp.LsEntry> files;
                            try {
                                files = channel.ls("*");
                            } catch (SftpException e) {
                                // This is the same workaround as in SimulatorJobMessageSenderSp109Telefonica.sendResponse, which we want to test.
                                logger.error("ERROR! Now retrying.", e);
                                exceptions.add(e);
                                files = channel.ls("*");
                            }

                            String fileNames = "";
                            for (ChannelSftp.LsEntry file : files) {
                                fileNames += file.getFilename() + " ";
                            }

                            logger.info("Task " + id + " files: " + fileNames);
                        }
                    } finally {
                        channel.disconnect();
                    }
                } finally {
                    session.disconnect();
                }

                logger.info("Task " + id + ": Done");
            } catch (Exception e) {
                logger.error("LIVE EXCEPTION:", e);
                exceptions.add(e);

                throw new RuntimeException(e);
            }

            return null;
        }

    }

}
