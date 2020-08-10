import java.io.FileDescriptor;
import java.net.InetAddress;
import java.security.Permission;

public class ExitSecurityManager {
    private static SecurityManager oldSecurityManager = null;
    protected static void forbidSystemExitCall() {
        oldSecurityManager = System.getSecurityManager();
        final SecurityManager securityManager = new SecurityManager() {
            @Override
            public void checkExit(int status) {
                if (status == 135) throw new SecurityException();
                if (oldSecurityManager != null) oldSecurityManager.checkExit(status);
            }

            @Override
            public void checkPermission(Permission perm) {
                if (oldSecurityManager != null) oldSecurityManager.checkPermission(perm);
            }

            @Override
            public void checkPermission(Permission perm, Object context) {
                if (oldSecurityManager != null) oldSecurityManager.checkPermission(perm, context);
            }

            @Override
            public void checkCreateClassLoader() {
                if (oldSecurityManager != null) oldSecurityManager.checkCreateClassLoader();
            }

            @Override
            public void checkAccess(Thread t) {
                if (oldSecurityManager != null) oldSecurityManager.checkAccess(t);
            }

            @Override
            public void checkAccess(ThreadGroup g) {
                if (oldSecurityManager != null) oldSecurityManager.checkAccess(g);
            }

            @Override
            public void checkExec(String cmd) {
                if (oldSecurityManager != null) oldSecurityManager.checkExec(cmd);
            }

            @Override
            public void checkLink(String lib) {
                if (oldSecurityManager != null) oldSecurityManager.checkLink(lib);
            }

            @Override
            public void checkRead(FileDescriptor fd) {
                if (oldSecurityManager != null) oldSecurityManager.checkRead(fd);
            }

            @Override
            public void checkRead(String file) {
                if (oldSecurityManager != null) oldSecurityManager.checkRead(file);
            }

            @Override
            public void checkRead(String file, Object context) {
                if (oldSecurityManager != null) oldSecurityManager.checkRead(file, context);
            }

            @Override
            public void checkWrite(FileDescriptor fd) {
                if (oldSecurityManager != null) oldSecurityManager.checkWrite(fd);
            }

            @Override
            public void checkWrite(String file) {
                if (oldSecurityManager != null) oldSecurityManager.checkWrite(file);
            }

            @Override
            public void checkDelete(String file) {
                if (oldSecurityManager != null) oldSecurityManager.checkDelete(file);
            }

            @Override
            public void checkConnect(String host, int port) {
                if (oldSecurityManager != null) oldSecurityManager.checkConnect(host, port);
            }

            @Override
            public void checkConnect(String host, int port, Object context) {
                if (oldSecurityManager != null) oldSecurityManager.checkConnect(host, port, context);
            }

            @Override
            public void checkListen(int port) {
                if (oldSecurityManager != null) oldSecurityManager.checkListen(port);
            }

            @Override
            public void checkAccept(String host, int port) {
                if (oldSecurityManager != null) oldSecurityManager.checkAccept(host, port);
            }

            @Override
            public void checkMulticast(InetAddress maddr) {
                if (oldSecurityManager != null) oldSecurityManager.checkMulticast(maddr);
            }

            @Override
            public void checkPropertiesAccess() {
                if (oldSecurityManager != null) oldSecurityManager.checkPropertiesAccess();
            }

            @Override
            public void checkPropertyAccess(String key) {
                if (oldSecurityManager != null) oldSecurityManager.checkPropertyAccess(key);
            }

            @Override
            public void checkPrintJobAccess() {
                if (oldSecurityManager != null) oldSecurityManager.checkPrintJobAccess();
            }

            @Override
            public void checkPackageAccess(String pkg) {
                if (oldSecurityManager != null) oldSecurityManager.checkPackageAccess(pkg);
            }

            @Override
            public void checkPackageDefinition(String pkg) {
                if (oldSecurityManager != null) oldSecurityManager.checkPackageDefinition(pkg);
            }

            @Override
            public void checkSetFactory() {
                if (oldSecurityManager != null) oldSecurityManager.checkSetFactory();
            }

            @Override
            public void checkSecurityAccess(String target) {
                if (oldSecurityManager != null) oldSecurityManager.checkSecurityAccess(target);
            }
        };
        System.setSecurityManager(securityManager);
    }

    protected static void enableSystemExitCall() {
        System.setSecurityManager(oldSecurityManager) ;
        oldSecurityManager = null;
    }
}

