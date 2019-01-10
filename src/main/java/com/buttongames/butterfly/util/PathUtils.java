package com.buttongames.butterfly.util;

import com.buttongames.butterfly.Main;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.swing.filechooser.FileSystemView;
import java.io.File;
import java.nio.file.Paths;

/**
 * Class to abstract away local paths on the computer. Abstracts the OS-specific pathing
 * semantics.
 * @author skogaby (skogabyskogaby@gmail.com)
 */
@Component
public class PathUtils {

    /** Name of the flag file to designate the program is in nonportable mode */
    public static final String NONPORTABLE_FLAG = "nonportable.flag";

    /** Whether or not the program is running in portable mode */
    private final boolean portable;

    /** The directory of the running program itself */
    public final String programDirectory;

    /** The directory where we store userdata -- for portable mode, this is the same as internalDirectory */
    public final String externalDirectory;

    @Autowired
    public PathUtils() {
        // really ugly hack to make sure we get the path of the program itself, and not the path
        // of wherever the process was invoked from; we want it to always go to the installation
        // directory
        // TODO: Test from a JAR, rather than from IDE testing. This should be more robust in the future
        this.programDirectory = new File(Main.class.getProtectionDomain().getCodeSource().getLocation().getPath())
                .getParentFile().getParentFile().getParentFile().getParentFile().toPath().toAbsolutePath().toString();

        // portable mode is enabled if there is a portable.flag file in the main program directory
        final File nonportableFlag = new File(Paths.get(programDirectory, NONPORTABLE_FLAG).toString());
        this.portable = nonportableFlag.exists() ? false : true;

        // the external directory is either the user's home/documents directory (nonportable mode) or
        // the installation directory (portable mode)
        this.externalDirectory = portable ? programDirectory :
                Paths.get(FileSystemView.getFileSystemView().getDefaultDirectory().getPath(),
                        Constants.APP_NAME).toString();
    }

    public boolean isPortable() {
        return portable;
    }
}
