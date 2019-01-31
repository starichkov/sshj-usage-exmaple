SSHJ usage example
=
[SSHJ](https://github.com/hierynomus/sshj) library makes working with SSH very simple!

This example performs following steps:
1. Connect to remote server using SSH
1. Authenticate using selected key and user name
1. SFTP - Create directories (path) on the remote server
1. SFTP - Print contents of this remote path (no contents)
1. SFTP - Copy first file to target folder using `net.schmizz.sshj.xfer.FileSystemFile`
1. SFTP - Copy second file to target folder from byte array, using custom `org.starichkov.java.ssh.ByteArraySourceFile`
1. SFTP - Print contents of this remote path (shows both files)
1. SFTP - Removes both files
1. SFTP - Print contents of this remote path (no contents)
1. Disconnects from remote server
