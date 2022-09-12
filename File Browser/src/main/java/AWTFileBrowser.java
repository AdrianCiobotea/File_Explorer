import org.apache.commons.io.FileUtils;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.stream.Collectors;

public class AWTFileBrowser extends Frame {
    Frame frame;
    private List fileList;
    private Button btnBack;
    private Button btnDeleteFile;
    private Panel listPanel;
    private Button btnZipFiles;
    private Button btnRenameFile;

    public AWTFileBrowser() {
        frame = new Frame();
        frame.setLayout(new BoxLayout(frame, BoxLayout.X_AXIS));
        fileList = new List();
        listPanel = new Panel();
        final CurrentPath[] currentPath = {null};
        try {
            currentPath[0] = new CurrentPath(Path.of(System.getProperty("user.dir")));
        } catch (IOException e) {
            e.printStackTrace();
        }
        currentPath[0].getFilesFoundInPath().forEach(filePath -> {
            fileList.add(filePath.toString());
        });
        fileList.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);
                if (e.getClickCount() == 2) {
                    // double click
                    String newPath = fileList.getSelectedItem();
                    if (new File(newPath).isDirectory()) {
                        updateList(fileList, Path.of(newPath), currentPath);
                    }
                }
            }
        });
        btnBack = new Button("Back");
        btnBack.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (currentPath[0].getCurrentPath().getParent() != null) {
                    updateList(fileList, currentPath[0].getCurrentPath().getParent(), currentPath);
                } else {
                    fileList.removeAll();
                    Arrays.stream(File.listRoots()).collect(Collectors.toList()).forEach(path -> {
                        fileList.add(String.valueOf(path));
                    });
                }
            }
        });
        System.out.println(currentPath[0].getCurrentPath());

        frame.add(fileList);
        fileList.setMaximumSize(new Dimension(250, 500));
        frame.add(btnBack);
        btnBack.setMaximumSize(new Dimension(200, 50));
        btnBack.setLocation(350, 450);

        btnDeleteFile = new Button("Delete");
        btnDeleteFile.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String selectedPath = fileList.getSelectedItem();
                if (selectedPath != null) {
                    int choice = JOptionPane.showConfirmDialog(fileList, "Are you sure you want to delete " + selectedPath + " ?");
                    File selectedFile = new File(selectedPath);
                    switch (choice) {
                        case 0:
                            if (selectedFile.isFile()) {
                                try {
                                    FileUtils.delete(selectedFile);
                                    updateList(fileList, currentPath[0].getCurrentPath(), currentPath);
                                } catch (IOException ex) {
                                    ex.printStackTrace();
                                }
                            } else {
                                if (selectedFile.getParent() != null) {
                                    try {
                                        FileUtils.deleteDirectory(selectedFile);
                                        updateList(fileList, currentPath[0].getCurrentPath(), currentPath);
                                    } catch (IOException ex) {
                                        ex.printStackTrace();
                                    }
                                }
                            }
                            break;
                        case 1:
                        case 2:
                            break;
                    }
                }
            }
        });
        btnDeleteFile.setMaximumSize(new Dimension(200, 50));
        frame.add(btnDeleteFile);

        btnZipFiles = new Button("Zip Files");
        btnZipFiles.setMaximumSize(new Dimension(200, 50));
        btnZipFiles.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (fileList.getSelectedItems().length != 0) {
                    String zipName = JOptionPane.showInputDialog("Please enter a zip name");
                    if (!(zipName.contains(".zip") || zipName.contains(".rar"))) {
                        zipName += ".zip";
                    }
                    if (fileList.getSelectedItems().length == 1) {
                        String selectedFile = fileList.getSelectedItem();
                        if (new File(selectedFile).isFile()) {
                            ZipHelper.zipSingleFile(new File(selectedFile), zipName);
                        } else {
                            File[] filesToZip = new File[fileList.getSelectedItems().length];
                            int counter = 0;
                            for (String filePath : fileList.getSelectedItems()) {
                                filesToZip[counter] = new File(filePath);
                            }

                            ZipHelper.zipDirectory(filesToZip, zipName);
                        }
                    } else {

                        File[] filesToZip = new File[fileList.getSelectedItems().length];
                        int counter = 0;
                        for (String filePath : fileList.getSelectedItems()) {
                            filesToZip[counter] = new File(filePath);
                        }

                        ZipHelper.zipDirectory(filesToZip, zipName);
                    }
                    updateList(fileList, currentPath[0].getCurrentPath(), currentPath);
                }
            }
        });
        frame.add(btnZipFiles);

        btnRenameFile = new Button("Rename");
        btnZipFiles.setMaximumSize(new Dimension(200, 50));
        btnRenameFile.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                String[] selectedItems = fileList.getSelectedItems();
                if (selectedItems.length == 1) {
                    Path selectedFile = Path.of(selectedItems[0]);
                    String newFileName = JOptionPane.showInputDialog("Please select a new name for the selected file");
                    try {
                        Files.move(selectedFile, selectedFile.resolveSibling(newFileName));
                        updateList(fileList, currentPath[0].getCurrentPath(), currentPath);
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }
                }
            }
        });
        btnRenameFile.setMaximumSize(new Dimension(200, 50));
        frame.add(btnRenameFile);

        frame.setSize(500, 500);
        frame.setVisible(true);
        frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                super.windowClosing(e);
                frame.dispose();
            }
        });
    }

    public static void main(String[] args) {
        AWTFileBrowser awtFileBrowser = new AWTFileBrowser();

    }

    public static void updateList(List fileList, Path newPath, CurrentPath[] currentPath) {
        try {
            currentPath[0] = new CurrentPath(newPath);
            fileList.removeAll();
            currentPath[0].getFilesFoundInPath().forEach(filePath -> {
                fileList.add(filePath.toString());
            });
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

}
