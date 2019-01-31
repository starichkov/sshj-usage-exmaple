package org.starichkov.java.ssh;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

import lombok.extern.slf4j.Slf4j;

import net.schmizz.sshj.SSHClient;
import net.schmizz.sshj.common.IOUtils;
import net.schmizz.sshj.connection.channel.direct.Session;
import net.schmizz.sshj.connection.channel.direct.Session.Command;
import net.schmizz.sshj.sftp.RemoteResourceInfo;
import net.schmizz.sshj.sftp.SFTPClient;
import net.schmizz.sshj.transport.verification.PromiscuousVerifier;
import net.schmizz.sshj.xfer.FileSystemFile;

/**
 * @author Vadim Starichkov
 * @since 31.01.2019 12:18
 */
@Slf4j
public class Example {

    private final String serverAddress = "some-cool-server.com";
    private final String sshPublicKeyFileName = "test-key";
    private final String sshPublicKeyUserName = "administrator";
    private final String remoteBaseDirectory = "/shared/folder/";
    private final String remoteDirectory = remoteBaseDirectory + "work/";
    private final String fileName1 = "first.txt";
    private final String fileName2 = "second.txt";
    private final String executableFileName = "remote_execution_test.bat";

    public void run() {
        final String base = System.getProperty("user.home") + File.separator + ".ssh-custom" + File.separator;
        log.info(base);

        try (SSHClient sshClient = new SSHClient()) {
            // turn off verifying fingerprint
            sshClient.addHostKeyVerifier(new PromiscuousVerifier());

            // connect to server
            sshClient.connect(serverAddress);

            // authenticate with public key
            sshClient.authPublickey(sshPublicKeyUserName, base + sshPublicKeyFileName);

            // upload files
            try (SFTPClient sftpClient = sshClient.newSFTPClient()) {

                ls(sftpClient, remoteBaseDirectory);

                try (Session session = sshClient.startSession()) {
                    final Command cmd = session
                            .exec(remoteBaseDirectory + File.separator + executableFileName + " 10 100500");
                    log.info(IOUtils.readFully(cmd.getInputStream()).toString());
                    log.info("Execution result: {}", cmd.getExitStatus());
                }

                ls(sftpClient, remoteBaseDirectory);

                sftpClient.mkdirs(remoteDirectory);

                uploadFiles(sftpClient, remoteDirectory, fileName1, fileName2);

                ls(sftpClient, remoteDirectory);

                removeFiles(sftpClient, remoteDirectory, fileName1, fileName2);

                ls(sftpClient, remoteDirectory);

                sftpClient.rmdir(remoteDirectory);

                ls(sftpClient, remoteDirectory);
            }

        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

    private void uploadFiles(SFTPClient sftpClient, String remoteDirectory, String file1, String file2) throws IOException {
        ClassLoader classLoader = getClass().getClassLoader();

        // first file will be send using net.schmizz.sshj.xfer.FileSystemFile
        sftpClient.put(new FileSystemFile(classLoader.getResource(file1).getFile()), remoteDirectory + "/" + file1);

        // second file we will send using byte array
        byte[] fileData = Files.readAllBytes(Paths.get(classLoader.getResource(file2).getFile()));
        sftpClient.put(new ByteArraySourceFile(file2, fileData), remoteDirectory + "/" + file2);
    }

    private void removeFiles(SFTPClient sftpClient, String remoteDirectory, String... files)
            throws IOException {
        for (String file : files) {
            sftpClient.rm(remoteDirectory + "/" + file);
        }
    }

    private void ls(SFTPClient sftpClient, String path) throws IOException {
        log.info("Contents of the '{}' directory:", path);
        List<RemoteResourceInfo> remoteResourceInfos = sftpClient.ls(path);
        for (RemoteResourceInfo info : remoteResourceInfos) {
            log.info("File name: {}, Full path: {}", info.getName(), info.getPath());
        }
    }
}
