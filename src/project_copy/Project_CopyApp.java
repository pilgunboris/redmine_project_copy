/*
 * Project_CopyApp.java
 */
package project_copy;

import org.jdesktop.application.Application;
import org.jdesktop.application.SingleFrameApplication;

/**
 * The main class of the application.
 */
public class Project_CopyApp extends SingleFrameApplication {

    /**
     * At startup create and show the main frame of the application.
     */
    @Override
    protected void startup() {
        show(new Project_CopyView(this));
    }

    /**
     * This method is to initialize the specified window by injecting resources.
     * Windows shown in our application come fully initialized from the GUI
     * builder, so this additional configuration is not needed.
     */
    @Override
    protected void configureWindow(java.awt.Window root) {
    }

    /**
     * A convenient static getter for the application instance.
     * @return the instance of Project_CopyApp
     */
    public static Project_CopyApp getApplication() {
        return Application.getInstance(Project_CopyApp.class);
    }

    /**
     * Main method launching the application.
     */
    public static void main(String[] args) {
        launch(Project_CopyApp.class, args);
    }
}
